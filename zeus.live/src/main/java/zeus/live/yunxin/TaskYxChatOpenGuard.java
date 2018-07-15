package zeus.live.yunxin;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.i5i58.data.im.YxCustomMsg;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.StringUtils;
import com.i5i58.yunxin.YunxinIM;
import com.i5i58.yunxin.Utils.CodeToString;
import com.i5i58.yunxin.Utils.YXResultSet;

public class TaskYxChatOpenGuard implements Runnable {

	private Logger logger = Logger.getLogger(getClass());

	private String roomId;
	private String accId;
	private JsonUtils jsonUtil;

	private MsgYxChatOpenGuard msg;

	public TaskYxChatOpenGuard(String roomId, String accId, String name, String face, int vip, long vipDeadLine,
			int guard, long guardDeadLine, long richScore, long score, int fansClub, String clubName, int clubLevel,
			long fansClubDeadLine, JsonUtils jsonUtils) {
		super();
		jsonUtil = jsonUtils;
		this.roomId = roomId;
		this.accId = accId;
		msg = new MsgYxChatOpenGuard();
		this.msg.setAccId(accId);
		this.msg.setName(name);
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
		switch (guard) {
		case 1:
			msg.setContent("成为 骑士");
			break;
		case 2:
			msg.setContent("成为 大骑士");
			break;
		case 3:
			msg.setContent("成为 圣骑士");
			break;
		}
	}

	@Override
	public void run() {
		String uuid = StringUtils.createUUID();
		YXResultSet resultR;
		try {
			YxCustomMsg yxChatMsg = new YxCustomMsg();
			yxChatMsg.setCmd("openGuard");
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
