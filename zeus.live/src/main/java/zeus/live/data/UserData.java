package zeus.live.data;

import org.springframework.stereotype.Component;

import com.i5i58.data.account.Wallet;
import com.i5i58.data.channel.HotChannelViewer;

@Component
public class UserData {
	private HotChannelViewer viewer;
	private Wallet wallet; 
	
	/**
	 * every time the data changes,the version will increases 
	 * */
	private volatile int walletVersion;
	
	public HotChannelViewer getViewer() {
		return viewer;
	}
	public void setViewer(HotChannelViewer viewer) {
		this.viewer = viewer;
	}
	public Wallet getWallet() {
		return wallet;
	}
	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}
	
	public void increaseWalletVersion(){
		++walletVersion;
	}
	
	public int getWalletVersion(){
		return walletVersion;
	}
}
