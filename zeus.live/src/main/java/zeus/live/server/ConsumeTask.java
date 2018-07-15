package zeus.live.server;

import com.i5i58.data.account.Wallet;
import com.i5i58.data.channel.HotChannelViewer;
import com.i5i58.primary.dao.account.WalletPriDao;

import zeus.live.data.UserData;
import zeus.network.manager.IRemotPeer;
import zeus.network.threading.Task;
import zeus.network.util.LiveUserDataKey;

public class ConsumeTask implements Runnable {
	private String 			unit;
	private long 			delta;
	private long 			commission;
	private int 			walletVersion;
	private WalletPriDao 	walletPriDao;
	private IRemotPeer 		peer;
	private String 			anchorAccId;

	public ConsumeTask(String unit, 
			long  delta, 
			long commission, 
			int walletVersion, 
			WalletPriDao walletPriDao, 
			IRemotPeer peer,
			String anchorAccId){
		this.unit = unit;
		this.delta = delta;
		this.commission = commission;
		this.walletVersion = walletVersion;
		this.walletPriDao = walletPriDao;
		this.peer = peer;
		this.anchorAccId = anchorAccId;
	}
	
	@Override
	public void run() {
		if(peer == null || peer.getTaskQueue() == null){
			return;
		}
		UserData userData = (UserData) peer.getUserData().get(LiveUserDataKey.UserData);
		HotChannelViewer viewer = userData.getViewer();
		
		switch (unit) {
		case "iGold":
			walletPriDao.updateIGold(viewer.getAccId(), -delta);
			break;
		case "diamond":
			walletPriDao.updateDiamond(viewer.getAccId(), -delta);
			break;
		case "giftTicket":
			walletPriDao.updateGiftTicket(viewer.getAccId(), -delta);
			break;
		default:
			break;
		}
		
		walletPriDao.updateCommission(anchorAccId, commission);
		
		Wallet wallet = walletPriDao.findByAccId(viewer.getAccId());
		Wallet anchorWallet = walletPriDao.findByAccId(anchorAccId);
		peer.getTaskQueue().addTask(new ConsumeCallbackTask(walletVersion, peer, wallet, anchorWallet));
	}

	public class ConsumeCallbackTask extends Task{
		private int walletVersion;
		private IRemotPeer peer;
		private Wallet wallet;
		private Wallet anchorWallet;
		
		public ConsumeCallbackTask(int walletVersion,IRemotPeer peer,Wallet wallet, Wallet anchorWallet){
			this.walletVersion = walletVersion;
			this.peer = peer;
			this.wallet = wallet;
			this.anchorWallet = anchorWallet;
		}
		
		@Override
		protected void onRun() {
			UserData userData = (UserData) peer.getUserData().get(LiveUserDataKey.UserData);
			if (userData.getWalletVersion() == walletVersion){
				System.out.println(String.format("update wallet, user wallte igold = %s, diamond = %s, giftTicket = %s", 
						wallet.getiGold(), wallet.getDiamond(), wallet.getGiftTicket()));
				System.out.println(String.format("update wallet, anchor wallte igold = %s, diamond = %s, giftTicket = %s, commission = %s", 
						anchorWallet.getiGold(), anchorWallet.getDiamond(), anchorWallet.getGiftTicket(), anchorWallet.getCommission()));
				
				userData.setWallet(wallet);
				LiveDataService.getInstance().setAnchorWallet(anchorWallet);
			}else{
				System.out.println(String.format("begin walletVersion version = %d,\r\ncurrent walletVerson =  %d,\r\n "
						+ "donot replace in-memory cached user wallet.", 
						walletVersion, userData.getWalletVersion()));
			}
		}
		
	}
	
}
