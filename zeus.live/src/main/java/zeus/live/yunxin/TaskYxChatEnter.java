package zeus.live.yunxin;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.i5i58.data.channel.Channel;
import com.i5i58.data.channel.HotChannelViewer;
import com.i5i58.data.im.YxCustomMsg;
import com.i5i58.util.ChannelUtils;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.StringUtils;
import com.i5i58.yunxin.YunxinIM;
import com.i5i58.yunxin.Utils.CodeToString;
import com.i5i58.yunxin.Utils.YXResultSet;

public class TaskYxChatEnter implements Runnable {

	private Logger logger = Logger.getLogger(getClass());

	private String roomId;
	private String accId;
	private String cId;
	private JsonUtils jsonUtil;
	private ChannelUtils channelUtils;
	private HotChannelViewer hotViewer;

	private MsgYxChatEnter msg;

	public String getRoomId() {
		return roomId;
	}

	public String getcId() {
		return cId;
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

	public TaskYxChatEnter(String roomId, String accId, String fromName, String face, long richScore, long score,
			int guard, long guardDeadLine, int vip, long vipDeadLine, int fansClub, String clubName, int clubLevel,
			long fansClubDeadLine, int guradMountsId, int mountsId, long indexByViewer, long indexByRicher,
			JsonUtils jsonUtils, ChannelUtils channelUtil, HotChannelViewer hotViewer, String cId) {
		super();
		jsonUtil = jsonUtils;
		channelUtils = channelUtil;
		this.hotViewer = hotViewer;
		this.roomId = roomId;
		this.accId = accId;
		this.cId = cId;
		this.msg = new MsgYxChatEnter();
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
		this.msg.setgMtsId(guradMountsId);
		this.msg.setMtsId(mountsId);
		this.msg.setIndexByViewer(indexByViewer);
		this.msg.setIndexByRicher(indexByRicher);
		this.hotViewer = hotViewer;
	}

	@Override
	public void run() {
		channelUtils.addViewer(cId, hotViewer);
		channelUtils.addRichman(cId, hotViewer);
		String uuid = StringUtils.createUUID();
		YXResultSet resultR;
		try {
			YxCustomMsg yxChatMsg = new YxCustomMsg();
			yxChatMsg.setCmd("enter");
			yxChatMsg.setData(this.msg);
			resultR = YunxinIM.sendChatRoomMsg(roomId, uuid, accId, "100", "0", "", jsonUtil.toJson(yxChatMsg));
			if (!"200".equals(resultR.getCode())) {
				System.out.println(CodeToString.getString(resultR.getCode()));
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}

}
