package zeus.live.yunxin;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.i5i58.data.im.YxCustomMsg;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.StringUtils;
import com.i5i58.yunxin.YunxinIM;

public class TaskYxChatMicSeqChanged implements Runnable {

	private Logger logger = Logger.getLogger(getClass());

	private String roomId;
	private JsonUtils jsonUtil;
	private MicChangedMsg micMsg;

	public TaskYxChatMicSeqChanged(String roomId, String accId, int index, long time, JsonUtils jsonUtils) {
		super();
		jsonUtil = jsonUtils;
		this.roomId = roomId;
		micMsg = new MicChangedMsg();
		micMsg.setAccId(accId);
		micMsg.setIndex(index);
		micMsg.setTime(time);
	}

	@Override
	public void run() {
		YxCustomMsg msg = new YxCustomMsg();
		msg.setCmd("micSeqChanged");
		msg.setData(micMsg);
		try {
			YunxinIM.sendChatRoomMsg(roomId, StringUtils.createUUID(), micMsg.getAccId(), "100", "0", "",
					jsonUtil.toJson(msg));
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	class MicChangedMsg implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6940291551589717589L;

		private String accId;
		private int index;
		private long time;

		public String getAccId() {
			return accId;
		}

		public void setAccId(String accId) {
			this.accId = accId;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

	}
}
