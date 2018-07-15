package zeus.live.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.i5i58.redis.all.HotAccountVipConfigDao;
import com.i5i58.redis.all.HotChannelDao;
import com.i5i58.redis.all.HotChannelGiftDao;
import com.i5i58.redis.all.HotChannelGuardConfigDao;
import com.i5i58.redis.all.HotChannelMicDao;
import com.i5i58.redis.all.HotChannelMountDao;
import com.i5i58.redis.all.HotChannelViewerDao;
import com.i5i58.redis.all.HotFansClubConfigDao;

@Component
public class HotDaoDelegate {

	@Autowired
	private HotChannelViewerDao hotChannelViewerDao;

	@Autowired
	private HotChannelMicDao hotChannelMicDao;

	@Autowired
	private HotChannelDao hotChannelDao;

	@Autowired
	private HotChannelGiftDao hotChannelGiftDao;

	@Autowired
	private HotAccountVipConfigDao hotAccountVipConfigDao;

	@Autowired
	private HotFansClubConfigDao hotFansClubConfigDao;

	@Autowired
	private HotChannelMountDao hotChannelMountDao;


	@Autowired
	private HotChannelGuardConfigDao hotChannelGuardConfigDao; 
	public HotChannelViewerDao getHotChannelViewerDao() {
		return hotChannelViewerDao;
	}

	public HotChannelMicDao getHotChannelMicDao() {
		return hotChannelMicDao;
	}

	public HotChannelDao getHotChannelDao() {
		return hotChannelDao;
	}

	public HotChannelGiftDao getHotChannelGiftDao() {
		return hotChannelGiftDao;
	}

	public HotFansClubConfigDao getHotFansClubConfigDao() {
		return hotFansClubConfigDao;
	}

	public HotAccountVipConfigDao getHotAccountVipConfigDao() {
		return hotAccountVipConfigDao;
	}

	public HotChannelGuardConfigDao getHotChannelGuardConfigDao() {
		return hotChannelGuardConfigDao;
	}

	public HotChannelMountDao getHotChannelMountDao() {
		return hotChannelMountDao;
	}

}
