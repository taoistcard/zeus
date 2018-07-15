package zeus.live.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.i5i58.primary.dao.account.AccountPriDao;
import com.i5i58.primary.dao.account.AccountPropertyPriDao;
import com.i5i58.primary.dao.account.AccountVipConfigPriDao;
import com.i5i58.primary.dao.account.MountStorePriDao;
import com.i5i58.primary.dao.account.WalletPriDao;
import com.i5i58.primary.dao.channel.ChannelAdminorPriDao;
import com.i5i58.primary.dao.channel.ChannelFansClubPriDao;
import com.i5i58.primary.dao.channel.ChannelGuardConfigPriDao;
import com.i5i58.primary.dao.channel.ChannelGuardPriDao;
import com.i5i58.primary.dao.channel.ChannelMountPriDao;
import com.i5i58.primary.dao.channel.ChannelPriDao;
import com.i5i58.primary.dao.channel.ChannelRecordPriDao;
import com.i5i58.primary.dao.channel.ChannelWatchingRecordPriDao;
import com.i5i58.primary.dao.record.RecordConsumptionPriDao;

@Component
public class PriDaoDelegate {

	@Autowired
	private ChannelPriDao channelPriDao;

	@Autowired
	private AccountPriDao accountPriDao;

	@Autowired
	private AccountPropertyPriDao accountPropertyPriDao;

	@Autowired
	private ChannelAdminorPriDao channelAdminorPriDao;

	@Autowired
	private ChannelGuardPriDao channelGuardPriDao;

	@Autowired
	private ChannelFansClubPriDao channelFansClubPriDao;

	@Autowired
	private ChannelWatchingRecordPriDao channelWatchingRecordPriDao;

	@Autowired
	private RecordConsumptionPriDao recordConsumptionPriDao;

	@Autowired
	private WalletPriDao walletPriDao;

	@Autowired
	private AccountVipConfigPriDao accountVipConfigPriDao;

	@Autowired
	private ChannelRecordPriDao channelRecordPriDao;

	@Autowired
	private MountStorePriDao mountStorePriDao;

	@Autowired
	private ChannelMountPriDao channelMountPriDao;

	@Autowired
	private ChannelGuardConfigPriDao channelGuardConfigPriDao; 

	public AccountPriDao getAccountPriDao() {
		return accountPriDao;
	}

	public AccountPropertyPriDao getAccountPropertyPriDao() {
		return accountPropertyPriDao;
	}

	public ChannelAdminorPriDao getChannelAdminorPriDao() {
		return channelAdminorPriDao;
	}

	public ChannelGuardPriDao getChannelGuardPriDao() {
		return channelGuardPriDao;
	}

	public ChannelFansClubPriDao getChannelFansClubPriDao() {
		return channelFansClubPriDao;
	}

	public ChannelWatchingRecordPriDao getChannelWatchingRecordPriDao() {
		return channelWatchingRecordPriDao;
	}

	public WalletPriDao getWalletPriDao() {
		return walletPriDao;
	}

	public ChannelPriDao getChannelPriDao() {
		return channelPriDao;
	}

	public ChannelRecordPriDao getChannelRecordPriDao() {
		return channelRecordPriDao;
	}

	public RecordConsumptionPriDao getRecordConsumptionPriDao() {
		return recordConsumptionPriDao;
	}

	public AccountVipConfigPriDao getAccountVipConfigPriDao() {
		return accountVipConfigPriDao;
	}

	public MountStorePriDao getMountStorePriDao() {
		return mountStorePriDao;
	}

	public ChannelGuardConfigPriDao getChannelGuardConfigPriDao() {
		return channelGuardConfigPriDao;
	}

	public ChannelMountPriDao getChannelMountPriDao() {
		return channelMountPriDao;
	}

}
