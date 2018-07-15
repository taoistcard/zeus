package zeus.live.yunxin;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.i5i58.data.channel.HotChannel;
import com.i5i58.data.channel.HotChannelViewer;
import com.i5i58.data.im.YxCustomMsg;
import com.i5i58.redis.all.HotChannelDao;
import com.i5i58.util.ChannelUtils;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.StringUtils;
import com.i5i58.yunxin.YunxinIM;
import com.i5i58.yunxin.Utils.CodeToString;
import com.i5i58.yunxin.Utils.YXResultSet;

import zeus.live.server.LiveDataService;

public class TaskYxChatGift implements Runnable {

	private Logger logger = Logger.getLogger(getClass());

	private String roomId;
	private String accId;
	private String cId;
	private boolean broadcast;
	private JsonUtils jsonUtil;
	private ChannelUtils channelUtils;
	private HotChannelViewer hotViewer;

	private MsgYxChatGift msg;

	HotChannelDao hotChannelDao;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getAccId() {
		return accId;
	}

	public void setAccId(String accId) {
		this.accId = accId;
	}

	public TaskYxChatGift(String roomId, String accId, String fromName, String face, int vip, long vipDeadLine,
			int guard, long guardDeadLine, long richScore, long score, int fansClub, String clubName, int clubLevel,
			long fansClubDeadLine, int giftId, int giftCount, int continuous, boolean condition, long weekOffer,
			long offer, long indexByViewer, long indexByRicher, boolean levelUp, HotChannelDao hotChannelDao,
			boolean broadcast, JsonUtils jsonUtils, ChannelUtils channelUtil, HotChannelViewer hotViewer, String cId) {
		super();
		this.jsonUtil = jsonUtils;
		this.channelUtils = channelUtil;
		this.hotViewer = hotViewer;
		this.hotChannelDao = hotChannelDao;
		this.roomId = roomId;
		this.accId = accId;
		this.cId = cId;
		this.broadcast = broadcast;
		this.msg = new MsgYxChatGift();
		this.msg.setAccId(accId);
		this.msg.setName(fromName);
		this.msg.setFace(face);
		this.msg.setVip(vip);
		this.msg.setVipDeadLine(vipDeadLine);
		this.msg.setGuard(guard);
		this.msg.setGuardDeadLine(guardDeadLine);
		this.msg.setRichScore(richScore);
		this.msg.setScore(score);
		this.msg.setFansClub(fansClub);
		this.msg.setClubName(clubName);
		this.msg.setClubLevel(clubLevel);
		this.msg.setFansClubDeadLine(fansClubDeadLine);
		this.msg.setId(giftId);
		this.msg.setCt(giftCount);
		this.msg.setCtis(continuous);
		this.msg.setCondition(condition);
		this.msg.setWeekOffer(weekOffer);
		this.msg.setOffer(offer);
		this.msg.setIndexByViewer(indexByViewer);
		this.msg.setIndexByRicher(indexByRicher);
		this.msg.setLevelUp(levelUp);
	}

	/*
	 * 
	 * 
	 * */
	private void broadcastGiftMsg() {
		YxCustomMsg yxChatMsg = new YxCustomMsg();
		yxChatMsg.setCmd("giftBroadcast");
		yxChatMsg.setData(this.msg);
		try {
			String jsonMsg = jsonUtil.toJson(yxChatMsg);
			Iterable<HotChannel> hotChannels = hotChannelDao.findAll();
			for (HotChannel c : hotChannels) {
				String roomId = c.getYunXinRId();
				String uuid = StringUtils.createUUID();
				YXResultSet resultR;
				resultR = YunxinIM.sendChatRoomMsg(roomId, uuid, accId, "100", "0", "", jsonMsg);
				if (!"200".equals(resultR.getCode())) {
					System.out.println(CodeToString.getString(resultR.getCode()));
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void run() {
		channelUtils.addViewer(cId, hotViewer);
		channelUtils.addRichman(cId, hotViewer);
		String uuid = StringUtils.createUUID();
		YXResultSet resultR;
		try {
			if (this.broadcast) {
				broadcastGiftMsg();
			} else {
				YxCustomMsg yxChatMsg = new YxCustomMsg();
				yxChatMsg.setCmd("gift");
				yxChatMsg.setData(this.msg);
				resultR = YunxinIM.sendChatRoomMsg(roomId, uuid, accId, "100", "0", "", jsonUtil.toJson(yxChatMsg));
				if (!"200".equals(resultR.getCode())) {
					System.out.println(CodeToString.getString(resultR.getCode()));
				}
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}
}
