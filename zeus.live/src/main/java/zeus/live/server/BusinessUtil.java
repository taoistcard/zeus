package zeus.live.server;

import java.io.IOException;
import java.text.ParseException;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.i5i58.clubTask.ClubTaskUtils;
import com.i5i58.data.channel.ChannelAuth;
import com.i5i58.data.channel.ChannelWatchingRecord;
import com.i5i58.data.channel.WeekOffer;
import com.i5i58.userTask.TaskUtil;
import com.i5i58.util.AuthVerify;
import com.i5i58.util.ChannelUtils;
import com.i5i58.util.Constant;
import com.i5i58.util.DateUtils;
import com.i5i58.util.JedisUtils;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.MountPresentUtil;

import zeus.live.data.HotDaoDelegate;
import zeus.live.data.PriDaoDelegate;
import zeus.live.data.SecDaoDelegate;

@Component
public class BusinessUtil {
	private static  Logger logger = Logger.getLogger(BusinessUtil.class);
	
	private static LiveDataService liveDataService;
	private static PriDaoDelegate priDaoDelegate;
	private static SecDaoDelegate secDaoDelegate;
	private static HotDaoDelegate hotDaoDelegate;
	private static EntityManager entityManager;
	private static JedisUtils jedisUtils;
	private static AuthVerify<ChannelAuth> channelAdminAuthVerify;
	private static JsonUtils jsonUtil;
	private static MountPresentUtil mountPresentUtil;
	private static ChannelUtils channelUtils;
	private static TaskUtil taskUtil;
	private static ClubTaskUtils clubTaskUtils;

	public static boolean beforeServerStart() {
		liveDataService = LiveDataService.getInstance();
		
		priDaoDelegate = liveDataService.getPriDaoDelegate();
		secDaoDelegate = liveDataService.getSecDaoDelegate();
		hotDaoDelegate = liveDataService.getHotDaoDelegate();
		entityManager = liveDataService.getEntityManager();
		jedisUtils = liveDataService.getJedisUtils();
		channelAdminAuthVerify = liveDataService.getChannelAdminAuthVerify();
		jsonUtil = liveDataService.getJsonUtil();
		mountPresentUtil = liveDataService.getMountPresentUtil();
		channelUtils = liveDataService.getChannelUtils();
		taskUtil = liveDataService.getTaskUtil();
		clubTaskUtils = liveDataService.getClubTaskUtils();
		
		return true;
	}
	
	public static void addSessionId(String accId, String sessionId) {
		liveDataService.getJedisUtils().hset(Constant.HOT_USERDATA + accId, Constant.SUB_USERDATA_VIRTUAL_SESSION_ID,
				sessionId);
		logger.info(String.format("add virtual session id accId %s, sessionId %s", accId, sessionId));
	}

	public static String getSessionId(String accId) {
		return liveDataService.getJedisUtils().hget(Constant.HOT_USERDATA + accId, Constant.SUB_USERDATA_VIRTUAL_SESSION_ID);
	}

	public static void delSessionId(String accId) {
		liveDataService.getJedisUtils().hdel(Constant.HOT_USERDATA + accId, Constant.SUB_USERDATA_VIRTUAL_SESSION_ID);
		logger.info(String.format("delete virtual session id accId %s", accId));
	}
	
	public static void channelWatchingStart(String cId, String accId) throws ParseException {
		long nowTime = DateUtils.getNowTime();
		ChannelWatchingRecord todayWatchingTime = liveDataService.getPriDaoDelegate().getChannelWatchingRecordPriDao()
				.findOne(cId + "_" + accId);
		if (todayWatchingTime == null) {
			todayWatchingTime = new ChannelWatchingRecord();
			todayWatchingTime.setId(cId + "_" + accId);
			todayWatchingTime.setAccId(accId);
			todayWatchingTime.setcId(cId);
			todayWatchingTime.setStartTime(nowTime);
		} else {
			try {
				long dayDiff;
				dayDiff = DateUtils.getDayInterval(todayWatchingTime.getStartTime(), nowTime);
				if (dayDiff != 0) {// 重置过期值
					todayWatchingTime.setStartTime(nowTime);
					todayWatchingTime.setFinishTime(0L);
					todayWatchingTime.setDuration(0L);
				}
				todayWatchingTime.setStartTime(nowTime);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}

		}
		liveDataService.getPriDaoDelegate().getChannelWatchingRecordPriDao().save(todayWatchingTime);
	}
	
	/**
	 * 获取娱乐积分对应的等级
	 * 
	 * @return
	 * @author frank
	 */
	public static int getRichScoreLevel(long richScore) {
		richScore = richScore / 100;
		if (richScore <= 10) {
			return 1;
		}
		if (richScore <= 100) {
			return 2;
		}
		if (richScore <= 200) {
			return 3;
		}
		if (richScore <= 500) {
			return 4;
		}
		if (richScore <= 800) {
			return 5;
		}
		if (richScore <= 2000) {
			return 6;
		}
		if (richScore <= 5000) {
			return 7;
		}
		if (richScore <= 10000) {
			return 8;
		}
		if (richScore <= 20000) {
			return 9;
		}
		if (richScore <= 50000) {
			return 10;
		}
		if (richScore <= 100000) {
			return 11;
		}
		if (richScore <= 200000) {
			return 12;
		}
		if (richScore <= 300000) {
			return 13;
		}
		if (richScore <= 400000) {
			return 14;
		}
		if (richScore <= 500000) {
			return 15;
		}
		if (richScore <= 600000) {
			return 16;
		}
		if (richScore <= 800000) {
			return 17;
		}
		if (richScore <= 1000000) {
			return 18;
		}
		if (richScore <= 2000000) {
			return 19;
		}
		if (richScore <= 3000000) {
			return 20;
		}
		return 20;
	}

	public static long addWeekOffer(String cId, String accId, final long amount, String name, String faceSmallUrl,
			final int vip, final int guardLevel, final long richScore) {
		long offer = 0;
		System.out.println(amount);
		Double score = liveDataService.getJedisUtils().zscore(Constant.HOT_CHANNEL_WEEKOFFER_SSET_KEY + cId, accId);
		if (score == null) {
			offer = amount;
		} else {
			System.out.println(score);
			offer = score.longValue() + amount;
		}
		System.out.println(offer);
		liveDataService.getJedisUtils().zadd(Constant.HOT_CHANNEL_WEEKOFFER_SSET_KEY + cId, offer, accId);// redis排序集合记录周榜排名
		// jedisUtils.hset(Constant.HOT_CHANNEL_WEEKOFFER_HSET_KEY + cId, accId,
		// Long.toString(offer));
		WeekOffer weekOffer = new WeekOffer();
		weekOffer.setAccId(accId);
		weekOffer.setOffer(offer);
		weekOffer.setName(name);
		weekOffer.setFaceSmallUrl(faceSmallUrl);
		weekOffer.setVip(vip);
		weekOffer.setGuardLevel(guardLevel);
		weekOffer.setRichScore(richScore);
		try {
			String value = liveDataService.getJsonUtil().toJson(weekOffer);
			System.out.println(value);
			liveDataService.getJedisUtils().hset(Constant.HOT_CHANNEL_WEEKOFFER_HSET_KEY + cId, accId, value);// redis
			// hash集合存放周榜用户对象json
		} catch (IOException e) {
			logger.error("", e);
		}
		return offer;
	}

	public static boolean giftAnmiCondition(int count, int continuous, int condition) {
		if (condition == 0) {
			return true;
		}
		if (count == condition) {
			return true;
		}
		return false;
	}
}
