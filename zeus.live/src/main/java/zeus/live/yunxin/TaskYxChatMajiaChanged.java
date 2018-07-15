package zeus.live.yunxin;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.i5i58.data.im.YxCustomMsg;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.StringUtils;
import com.i5i58.yunxin.Utils.CodeToString;
import com.i5i58.yunxin.Utils.YXResultSet;
import com.i5i58.yunxin.YunxinIM;

public class TaskYxChatMajiaChanged implements Runnable {

	private Logger logger = Logger.getLogger(getClass());

	private String roomId;
	private String accId;
	private JsonUtils jsonUtil;

	private MsgYxChatMajiaChanged msg;

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

	public TaskYxChatMajiaChanged(String roomId, String accId, String fromName, String face, long richScore, long score,
			int guard, long guardDeadLine, int vip, long vipDeadLine, int fansClub, String clubName, int clubLevel,
			long fansClubDeadLine, String operate, int majia, JsonUtils jsonUtils) {
		super();
		jsonUtil = jsonUtils;
		this.roomId = roomId;
		this.accId = accId;
		this.msg = new MsgYxChatMajiaChanged();
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
		this.msg.setClubLevel(clubLevel);
		this.msg.setClubName(clubName);
		this.msg.setFansClubDeadLine(fansClubDeadLine);
		this.msg.setOperate(operate);
		this.msg.setMajia(majia);
	}

	@Override
	public void run() {

		String uuid = StringUtils.createUUID();
		YXResultSet resultR;
		try {
			YxCustomMsg yxChatMsg = new YxCustomMsg();
			yxChatMsg.setCmd("majiaChanged");
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
