package zeus.live.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.i5i58.clubTask.ClubTaskUtils;
import com.i5i58.data.account.HotAccount1;
import com.i5i58.data.account.Wallet;
import com.i5i58.data.channel.ChannelAuth;
import com.i5i58.data.channel.HotChannel;
import com.i5i58.data.channel.HotChannelGift;
import com.i5i58.data.channel.HotChannelMic;
import com.i5i58.data.channel.HotChannelMount;
import com.i5i58.data.channel.HotChannelViewer;
import com.i5i58.primary.dao.account.WalletPriDao;
import com.i5i58.redis.all.HotChannelDao;
import com.i5i58.redis.all.HotChannelGiftDao;
import com.i5i58.redis.all.HotChannelMountDao;
import com.i5i58.userTask.TaskUtil;
import com.i5i58.util.AuthVerify;
import com.i5i58.util.ChannelUtils;
import com.i5i58.util.Constant;
import com.i5i58.util.JedisUtils;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.MountPresentUtil;
import com.i5i58.util.StringUtils;

import zeus.live.data.HotDaoDelegate;
import zeus.live.data.MicListItem;
import zeus.live.data.MutedUserManager;
import zeus.live.data.PriDaoDelegate;
import zeus.live.data.SecDaoDelegate;
import zeus.live.data.ViewerListItem;

@Component
// @EnableScheduling
public class LiveDataService {

	private HotChannel channel;

	private HotAccount1 hotOwner;

	private Wallet walletAnchor;

	// private AtomicLong commission;

	private static Map<Integer, HotChannelGift> hotChannelGifts;

	private Map<Integer, HotChannelMount> hotChannelMounts;

	private MutedUserManager mutedUers;

	private List<ViewerListItem> viewerList;

	private List<ViewerListItem> richerList;

	private List<MicListItem> micList;

	private ReadWriteLock anchorWalletLock = new ReentrantReadWriteLock(true);

	private ReadWriteLock richerListLock = new ReentrantReadWriteLock(true);

	private ReadWriteLock viewerListLock = new ReentrantReadWriteLock(true);

	// private ReadWriteLock micListLock = new ReentrantReadWriteLock(true);

	@Autowired
	private PriDaoDelegate priDaoDelegate;
	@Autowired
	private SecDaoDelegate secDaoDelegate;
	@Autowired
	private HotDaoDelegate hotDaoDelegate;

	@Autowired
	@Qualifier("entityManagerPrimary")
	private EntityManager entityManager;

	@Autowired
	private JedisUtils jedisUtils;

	@Autowired
	private AuthVerify<ChannelAuth> channelAdminAuthVerify;

	@Autowired
	private JsonUtils jsonUtil;

	@Autowired
	private MountPresentUtil mountPresentUtil;

	@Autowired
	private ChannelUtils channelUtils;

	@Autowired
	private TaskUtil taskUtil;

	@Autowired
	private ClubTaskUtils clubTaskUtils;

	// /**
	// * Sets the walletPriDao. This method should never be called except by
	// * spring
	// *
	// * @param walletPriDao
	// */
	// @Autowired(required = true)
	// public void setWalletPriDao(WalletPriDao walletPriDao) {
	// LiveDataService.walletPriDao = walletPriDao;
	// }
	//
	// @Autowired(required = true)
	// public void setHotChannelGiftDao(HotChannelGiftDao hotChannelGiftDao) {
	// LiveDataService.hotChannelGiftDao = hotChannelGiftDao;
	// }
	//
	// @Autowired(required = true)
	// public void setHotChannelDao(HotChannelDao hotChannelDao) {
	// LiveDataService.hotChannelDao = hotChannelDao;
	// }
	//
	// @Autowired(required = true)
	// public void ChannelAdminorPriDao(ChannelAdminorPriDao
	// channelAdminorPriDao) {
	// LiveDataService.channelAdminorPriDao = channelAdminorPriDao;
	// }

	private static LiveDataService service;

	public static LiveDataService getInstance() {
		assert false : "spring create LiveDataService failed.";
		return service;
	}

	@Transactional
	public void test() {
		List<Wallet> wallets = priDaoDelegate.getWalletPriDao().findMineAndAnchor("10000", "20000");
		if (wallets.size() != 2) {
			System.out.println("found error");
			return;
		} else {
			System.out.println("found mine and anchor");
			return;
		}
	}

	public LiveDataService() {
		if (service != null) {
			throw new RuntimeException("liveDataService has been created.");
		}
		service = this;
	}

	public boolean beforeServerStart(String cId) {
		return InitData(cId);
	}

	public static int findInsertPos(List<ViewerListItem> list, ViewerListItem item) {
		if (list.size() == 0)
			return 0;
		Double key = item.getKey();
		// if(list.size() <= 10){
		// int index = 0;
		// for (; index < list.size(); index++){
		// if (list.get(index).getKey() < key){
		// break;
		// }
		// }
		// return index;
		// }
		int l = 0;
		int r = list.size() - 1;
		ViewerListItem ritem = list.get(r);
		ViewerListItem litem = list.get(l);

		if (ritem.getKey() >= key) {
			return r; // key is smaller than the smallest.
		} else if (litem.getKey() < key) {
			return l; // key is bigger than the biggest.
		}

		while (l < r) {
			int m = (l + r + 1) / 2;
			Double mk = list.get(m).getKey();
			if (mk >= key) {
				l = m;
			} else {
				r = m;
			}
		}

		if (list.get(r).getKey() >= key) {
			return r + 1;
		} else {
			return r;
		}
	}

	public int addViewer(HotChannelViewer hotViewer) {
		Double hotScore = new Double(hotViewer.getVip()) * Constant.VIP_SCORE_RATE
				+ new Double(hotViewer.getGuardLevel()) * Constant.GUARD_SCORE_RATE;
		viewerListLock.writeLock().lock();
		try {
			ViewerListItem item = new ViewerListItem(hotScore, hotViewer);
			int idx = findInsertPos(viewerList, item);
			viewerList.add(idx, item);
		} catch (Exception e) {

		} finally {
			viewerListLock.writeLock().unlock();
		}
		return viewerList.size() - 1;
	}

	public int updateViewer(HotChannelViewer hotViewer) {
		Double hotScore = new Double(hotViewer.getVip()) * Constant.VIP_SCORE_RATE
				+ new Double(hotViewer.getGuardLevel()) * Constant.GUARD_SCORE_RATE;
		int res = 0;
		viewerListLock.writeLock().lock();
		try {
			if (viewerList.remove(hotViewer)) {
				ViewerListItem item = new ViewerListItem(hotScore, hotViewer);
				int idx = findInsertPos(viewerList, item);
				viewerList.add(idx, item);
			}
		} catch (Exception e) {
		} finally {
			viewerListLock.writeLock().unlock();
		}
		return res;
	}

	public boolean removeViewer(HotChannelViewer hotViewer) {
		viewerListLock.writeLock().lock();
		try {
			viewerList.remove(hotViewer);
		} catch (Exception e) {
			return false;
		} finally {
			viewerListLock.writeLock().unlock();
		}
		return true;

	}

	public int updateRicher(HotChannelViewer hotViewer) {
		Double hotScore = new Double(hotViewer.getRichScore()) * Constant.VIP_SCORE_RATE
				+ new Double(hotViewer.getGuardLevel()) * Constant.GUARD_SCORE_RATE;
		int res = 0;
		richerListLock.writeLock().lock();
		try {
			if (richerList.remove(hotViewer)) {
				ViewerListItem item = new ViewerListItem(hotScore, hotViewer);
				int idx = findInsertPos(richerList, item);
				richerList.add(idx, item);
			}
		} catch (Exception e) {
		} finally {
			richerListLock.writeLock().unlock();
		}
		return res;
	}

	public int addRichman(HotChannelViewer hotViewer) {
		Double hotScore = new Double(hotViewer.getRichScore()) * Constant.VIP_SCORE_RATE
				+ new Double(hotViewer.getGuardLevel()) * Constant.GUARD_SCORE_RATE;
		richerListLock.writeLock().lock();

		try {
			ViewerListItem item = new ViewerListItem(hotScore, hotViewer);
			int idx = findInsertPos(richerList, item);
			richerList.add(idx, item);
		} catch (Exception e) {
		} finally {
			richerListLock.writeLock().unlock();
		}

		return richerList.size() - 1;
	}

	public boolean removeRichman(HotChannelViewer hotViewer) {
		richerListLock.writeLock().lock();
		try {
			richerList.remove(hotViewer);
		} catch (Exception e) {
			return false;
		} finally {
			richerListLock.writeLock().unlock();
		}
		return true;
	}

	public boolean addMic(Integer index, HotChannelMic newMic) {
		try {
			micList.add(index, new MicListItem(newMic.getAccId(), newMic));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean setMic(Integer index, HotChannelMic newMic) {
		try {
			micList.set(index, new MicListItem(newMic.getAccId(), newMic));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean deleteMic(HotChannelMic findMic) {
		try {
			int index = getInstance().micList.indexOf(findMic);
			getInstance().micList.remove(index);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean updateMic(Integer index, HotChannelMic newMic) {
		try {
			if (!richerList.remove(newMic)) {
				micList.add(index, new MicListItem(newMic.getAccId(), newMic));
				return true;
			}
		} catch (Exception e) {
		}
		return false;

	}

	public boolean InitData(String cId) {
		try {
			WalletPriDao walletPriDao = priDaoDelegate.getWalletPriDao();
			HotChannelGiftDao hotChannelGiftDao = hotDaoDelegate.getHotChannelGiftDao();
			HotChannelMountDao hotChannelMountDao = hotDaoDelegate.getHotChannelMountDao();
			HotChannelDao hotChannelDao = hotDaoDelegate.getHotChannelDao();

			viewerList = new ArrayList<ViewerListItem>();
			richerList = new ArrayList<ViewerListItem>();
			micList = new ArrayList<MicListItem>();
			mutedUers = new MutedUserManager();
			hotChannelGifts = new HashMap<Integer, HotChannelGift>();
			List<HotChannelGift> giftList = (List<HotChannelGift>) hotChannelGiftDao.findAll();
			if (giftList != null) {
				for (HotChannelGift gift : giftList) {
					hotChannelGifts.put(gift.getMainId(), gift);
				}
			}

			hotChannelMounts = new HashMap<Integer, HotChannelMount>();
			List<HotChannelMount> mountList = (List<HotChannelMount>) hotChannelMountDao.findAll();
			if (mountList != null) {
				for (HotChannelMount mount : mountList) {
					hotChannelMounts.put(mount.getId(), mount);
				}
			}
			channel = hotChannelDao.findOne(cId);
			if (channel == null) {
				return false;
			}

			String accId = channel.getOwnerId();
			try {
				Query htAccQuery = entityManager.createQuery(
						"SELECT new com.i5i58.data.account.HotAccount1(c.accId, c.openId, c.phoneNo,c.nickName, c.stageName, c.anchor, c.gender, c.birthDate, c.faceSmallUrl, c.faceOrgUrl , c.version ,  g.vip , g.vipDeadline, g.richScore, g.score, g.mountsId, g.mountsName,g.clubCid, g.clubName,g.fansCount, g.focusCount, g.essayCount ,  g.medals,  c.location, c.signature, c.personalBrief) FROM Account c ,AccountProperty g WHERE c.accId= g.accId AND c.accId='"
								+ accId + "'");
				hotOwner = (HotAccount1) htAccQuery.getSingleResult();
			} catch (Exception e) {
				return false;
			}
			walletAnchor = walletPriDao.findOne(accId);
			if (StringUtils.StringIsEmptyOrNull(cId) || StringUtils.StringIsEmptyOrNull(accId) || channel == null
					|| hotOwner == null || walletAnchor == null || giftList == null) {
				return false;
			}
			return true;
		} catch (Exception e) {
			System.out.println("初始化直播间数据错误");
			System.out.println(e);
			return false;
		}

	}

	// @Scheduled(fixedRate = 5000)
	// public void updateDatabase() {
	// anchorWalletLock.writeLock().lock();
	// if (walletAnchor != null) {
	// Wallet wallet = new Wallet();
	// wallet.setAccId(walletAnchor.getAccId());
	// wallet.setiGold(walletAnchor.getiGold());
	// wallet.setDiamond(walletAnchor.getDiamond());
	// wallet.setGiftTicket(walletAnchor.getGiftTicket());
	// wallet.setTicket(walletAnchor.getTicket());
	// wallet.setCommission(walletAnchor.getCommission());
	// walletPriDao.save(wallet);
	// }
	// anchorWalletLock.writeLock().unlock();
	// }

	public void setAnchorWallet(Wallet wallet) {
		anchorWalletLock.writeLock().lock();
		walletAnchor = wallet;
		anchorWalletLock.writeLock().unlock();
	}

	public HotChannel getChannel() {
		return channel;
	}

	public HotAccount1 getHotOwner() {
		return hotOwner;
	}

	public HotChannelGift getGift(int giftId) {
		return hotChannelGifts.get(giftId);
	}

	public HotChannelMount getMount(int mountId) {
		return getInstance().hotChannelMounts.get(mountId);
	}

	public ChannelUtils getChannelUtils() {
		return channelUtils;
	}

	public int getMicIndexBuyAccId(String accId) {
		for (int i = 0; i < micList.size(); i++) {
			if (accId.equals(micList.get(i).getKey())) {
				return i;
			}
		}
		return -1;
	}

	public HotChannelMic getHotChannelMicByAccId(String accId) {
		for (int i = 0; i < micList.size(); i++) {
			if (accId.equals(micList.get(i).getKey())) {
				return micList.get(i).getValue();
			}
		}
		return null;
	}

	public HotChannelViewer getHotViewerByAccId(String accId) {
		for (int i = 0; i < viewerList.size(); i++) {
			if (accId.equals(viewerList.get(i).getValue().getAccId())) {
				return viewerList.get(i).getValue();
			}
		}
		return null;
	}

	public PriDaoDelegate getPriDaoDelegate() {
		return priDaoDelegate;
	}

	public SecDaoDelegate getSecDaoDelegate() {
		return secDaoDelegate;
	}

	public HotDaoDelegate getHotDaoDelegate() {
		return hotDaoDelegate;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public AuthVerify<ChannelAuth> getChannelAdminAuthVerify() {
		return channelAdminAuthVerify;
	}

	public JedisUtils getJedisUtils() {
		return jedisUtils;
	}

	public JsonUtils getJsonUtil() {
		return jsonUtil;
	}

	public MountPresentUtil getMountPresentUtil() {
		return mountPresentUtil;
	}

	public TaskUtil getTaskUtil() {
		return taskUtil;
	}

	public ClubTaskUtils getClubTaskUtils() {
		return clubTaskUtils;
	}

	public MutedUserManager getMutedUers() {
		return mutedUers;
	}

	public void setMutedUers(MutedUserManager mutedUers) {
		this.mutedUers = mutedUers;
	}

	/*
	 * private static void TreeMapSortByValue() { // 将map.entrySet()转换成list //
	 * List<Entry<String, HotViewer>> viewerList = new //
	 * ArrayList<Map.Entry<String, HotViewer>>( getInstance().viewerList = new
	 * ArrayList<Map.Entry<Integer,
	 * HotViewer>>(getInstance().viewerMapList.entrySet()); while (true) {
	 * 
	 * } // 通过比较器来实现排序
	 * 
	 * Collections.sort(getInstance().viewerList, new
	 * Comparator<Map.Entry<String, HotViewer>>() {
	 * 
	 * @Override public int compare(Entry<String, HotViewer> o1, Entry<String,
	 * HotViewer> o2) { // 升序排序 return (int) (new Double(o1.getValue().getVip())
	 * * Constant.VIP_SCORE_RATE + new Double(o1.getValue().getGuardLevel()) *
	 * Constant.GUARD_SCORE_RATE - new Double(o2.getValue().getVip()) *
	 * Constant.VIP_SCORE_RATE - new Double(o2.getValue().getGuardLevel()) *
	 * Constant.GUARD_SCORE_RATE);
	 * 
	 * } });
	 * 
	 * // arrayList.set(index, element);
	 * 
	 * for (Entry<String, HotViewer> mapping : list) { //
	 * System.out.println(mapping.getKey() + ":" + mapping.getValue());
	 * getInstance().viewerList.put(mapping.getKey(), mapping.getValue()); }
	 * 
	 * 
	 * }
	 */
}
