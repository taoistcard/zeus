package zeus.live.yunxin;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.i5i58.data.im.YxCustomMsg;
import com.i5i58.util.JsonUtils;
import com.i5i58.yunxin.YunxinIM;
import com.i5i58.yunxin.Utils.YXResultSet;

public class TaskYxNoticeAudioStatus implements Runnable {

	private Logger logger = Logger.getLogger(getClass());
	private String fromAccId;
	private String toAccId;

	private JsonUtils jsonUtil;

	private MsgYxNoticeAudioStatus msg;

	private String content;

	public TaskYxNoticeAudioStatus(String fromAccId, String toAccId, Boolean audioFlag, JsonUtils jsonUtils) {
		super();
		this.jsonUtil = jsonUtils;
		this.fromAccId = fromAccId;
		this.toAccId = toAccId;
		this.msg = new MsgYxNoticeAudioStatus();
		if (audioFlag) {
			this.content = "语音连麦解禁";
		} else {
			this.content = "语音连麦禁用";
		} 
		this.msg.setAudioFlag(audioFlag);
		this.msg.setContent(this.content);

	}

	@Override
	public void run() {
		YXResultSet resultR;
		try {
			YxCustomMsg yxChatMsg = new YxCustomMsg();
			yxChatMsg.setCmd("audioMute");
			yxChatMsg.setData(this.msg);
			resultR = YunxinIM.sendAttachMessage(fromAccId, "0", toAccId, jsonUtil.toJson(yxChatMsg), content, "", "",
					"2", "");
			if (!"200".equals(resultR.getCode())) {
				/*
				 * logger.error(String.
				 * format("MsgYxChatOpenClub:{code:%s, roomId:%s, accId:%s, name:%s}"
				 * , CodeToString.getString(resultR.getCode()), roomId, accId,
				 * this.msg.getName()));
				 */
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}

}
