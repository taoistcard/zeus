package zeus.live.yunxin;

import com.i5i58.data.account.Wallet;
import com.i5i58.data.channel.ChannelRecord;
import com.i5i58.primary.dao.account.AccountPropertyPriDao;
import com.i5i58.primary.dao.account.WalletPriDao;
import com.i5i58.util.DataSaveThread;

public class TaskDataBaseDriftComment implements Runnable {

	private String accId;

	private WalletPriDao walletPriDao;

	private AccountPropertyPriDao accountPropertyPriDao;

	private long amount;

	private DataSaveThread<ChannelRecord, Long> dataSaveThread;

	public TaskDataBaseDriftComment(String accId, WalletPriDao walletPriDao,
			AccountPropertyPriDao accountPropertyPriDao, long amount,
			DataSaveThread<ChannelRecord, Long> dataSaveThread) {
		super();
		this.accId = accId;
		this.walletPriDao = walletPriDao;
		this.accountPropertyPriDao = accountPropertyPriDao;
		this.amount = amount;
		this.dataSaveThread = dataSaveThread;
	}

	@Override
	public void run() {
//		AccountProperty accountProperty = accountPropertyPriDao.findByAccId(accId);
//		if (accountProperty != null) {
//			accountProperty.setRichScore(accountProperty.getRichScore() + Constant.DRIFT_COMMENT_PRICE);
//			accountPropertyPriDao.save(accountProperty);
//		}

		Wallet wallet = walletPriDao.findByAccId(accId);
		wallet.setiGold(wallet.getiGold() - amount);
		walletPriDao.save(wallet);
		this.dataSaveThread.Save();
	}

}
