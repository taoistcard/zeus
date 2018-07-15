package zeus.live.yunxin;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.i5i58.data.im.YxCustomMsg;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.StringUtils;
import com.i5i58.yunxin.YunxinIM;
import com.i5i58.yunxin.Utils.CodeToString;
import com.i5i58.yunxin.Utils.YXResultSet;

public class TaskYxChatSetMute implements Runnable {

	private Logger logger = Logger.getLogger(getClass());

	private String roomId;
	private String accId;
	private JsonUtils jsonUtil;

	private MsgYxChatSetMute msg;

	public TaskYxChatSetMute(String roomId, String accId, String toName, String muted, JsonUtils jsonUtils) {
		super();
		jsonUtil = jsonUtils;
		this.roomId = roomId;
		this.accId = accId;
		this.msg = new MsgYxChatSetMute();
		this.msg.setContent("管理员已将 " + toName + " 禁言");
		this.msg.setAccId(accId);
		this.msg.setMuted(muted);

	}

	@Override
	public void run() {
		String uuid = StringUtils.createUUID();
		YXResultSet resultR;
		try {

			YxCustomMsg yxChatMsg = new YxCustomMsg();
			yxChatMsg.setCmd("setMute");
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