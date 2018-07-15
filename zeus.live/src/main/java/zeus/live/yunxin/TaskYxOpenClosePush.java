package zeus.live.yunxin;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.i5i58.data.im.YxCustomMsg;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.StringUtils;
import com.i5i58.yunxin.YunxinIM;
import com.i5i58.yunxin.Utils.CodeToString;
import com.i5i58.yunxin.Utils.YXResultSet;

public class TaskYxOpenClosePush implements Runnable {

	private Logger logger = Logger.getLogger(getClass());

	private String roomId;
	private String accId;
	private String cmd;
	private JsonUtils jsonUtil;

	public TaskYxOpenClosePush(String roomId, String accId, String cmd, JsonUtils jsonUtils) {
		super();
		jsonUtil = jsonUtils;
		this.roomId = roomId;
		this.accId = accId;
		this.cmd = cmd;
	}

	@Override
	public void run() {
		String uuid = StringUtils.createUUID();
		YXResultSet resultR;
		try {
			YxCustomMsg yxChatMsg = new YxCustomMsg();
			yxChatMsg.setCmd(cmd);
			resultR = YunxinIM.sendChatRoomMsg(roomId, uuid, accId, "100", "0", "", jsonUtil.toJson(yxChatMsg));
			if (!"200".equals(resultR.getCode())) {
				System.out.println(CodeToString.getString(resultR.getCode()));
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}
}
