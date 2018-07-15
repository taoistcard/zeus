package zeus.live.yunxin;

import com.i5i58.data.account.AccountProperty;
import com.i5i58.data.account.Wallet;
import com.i5i58.data.channel.ChannelRecord;
import com.i5i58.primary.dao.account.AccountPropertyPriDao;
import com.i5i58.primary.dao.account.WalletPriDao;
import com.i5i58.util.DataSaveThread;
import com.i5i58.util.StringUtils;

public class TaskDataBaseGiveGift implements Runnable {

	private String accId;

	private String ownerId;

	private WalletPriDao walletPriDao;

	private long amount;

	private long anchorAmount;

	private AccountPropertyPriDao accountPropertyPriDao;

	private DataSaveThread<ChannelRecord, Long> dataSaveThread;

	public TaskDataBaseGiveGift(String accId, String ownerId, WalletPriDao walletPriDao, long amount, long anchorAmount,
			AccountPropertyPriDao accountPropertyPriDao, DataSaveThread<ChannelRecord, Long> dataSaveThread) {
		super();
		this.accId = accId;
		this.ownerId = ownerId;
		this.walletPriDao = walletPriDao;
		this.amount = amount;
		this.anchorAmount = anchorAmount;
		this.accountPropertyPriDao = accountPropertyPriDao;
		this.dataSaveThread = dataSaveThread;
	}

	@Override
	public void run() {
//		Wallet wallet = walletPriDao.findByAccId(accId);
//		
//		if (wallet.getGiftTicket() >= amount) {
//			wallet.setGiftTicket(wallet.getGiftTicket() - amount);
//			walletPriDao.save(wallet);
//			anchorAmount = 0L; //扣礼物券时，不加佣金
//		} else if (wallet.getDiamond() >= amount) {
//			wallet.setDiamond(wallet.getDiamond() - amount);
//			walletPriDao.save(wallet);
//		} else if (wallet.getiGold() >= amount) {
//			wallet.setiGold(wallet.getiGold() - amount);
//			walletPriDao.save(wallet);
//		}else{
//			return;
//		}
		
//		if (!StringUtils.StringIsEmptyOrNull(ownerId) && anchorAmount > 0L) {
//			Wallet ownerWallet = walletPriDao.findByAccId(ownerId);
//			ownerWallet.setCommission(ownerWallet.getCommission() + anchorAmount);
//			walletPriDao.save(ownerWallet);
//		}
//		AccountProperty accountProperty = accountPropertyPriDao.findByAccId(accId);
//		accountProperty.setRichScore(accountProperty.getRichScore() + amount);
//		accountPropertyPriDao.save(accountProperty);
		dataSaveThread.Save();
	}

}
