package zeus.live.yunxin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.i5i58.data.account.Account;
import com.i5i58.data.channel.HotChannel;
import com.i5i58.data.im.YxCustomMsg;
import com.i5i58.primary.dao.account.AccountPriDao;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.StringUtils;
import com.i5i58.yunxin.YunxinIM;

public class TaskYxChatConnMic implements Runnable {

	private Logger logger = Logger.getLogger(getClass());

	private AccountPriDao accountPriDao;

	private HotChannel requestHotChannel;

	private HotChannel targetHotChannel;

	private String requestAccId;

	private String targetAccId;

	private JsonUtils jsonUtil;

	public TaskYxChatConnMic(AccountPriDao accountPriDao, HotChannel requestHotChannel, HotChannel targetHotChannel,
			String requestAccId, String targetAccId, JsonUtils jsonUtil) {
		super();
		this.accountPriDao = accountPriDao;
		this.requestHotChannel = requestHotChannel;
		this.targetHotChannel = targetHotChannel;
		this.requestAccId = requestAccId;
		this.targetAccId = targetAccId;
		this.jsonUtil = jsonUtil;
	}

	@Override
	public void run() {
		Account requestHotAcc = accountPriDao.findOne(requestHotChannel.getOwnerId());
		Account targetHotAcc = accountPriDao.findOne(targetHotChannel.getOwnerId());

		try {
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("channel", targetHotChannel);
			requestMap.put("owner", targetHotAcc);
			YxCustomMsg requestMessage = new YxCustomMsg();
			requestMessage.setCmd("connMic");
			requestMessage.setData(requestMap);
			YunxinIM.sendChatRoomMsg(requestHotChannel.getYunXinRId(), StringUtils.createUUID(), targetAccId, "100",
					"0", "", jsonUtil.toJson(requestMessage));
		} catch (IOException e) {
			logger.error("", e);
		}

		try {
			Map<String, Object> targetMap = new HashMap<String, Object>();
			targetMap.put("channel", requestHotChannel);
			targetMap.put("owner", requestHotAcc);
			YxCustomMsg targetMessage = new YxCustomMsg();
			targetMessage.setCmd("connMic");
			targetMessage.setData(targetMap);
			YunxinIM.sendChatRoomMsg(targetHotChannel.getYunXinRId(), StringUtils.createUUID(), requestAccId, "100",
					"0", "", jsonUtil.toJson(targetMessage));
		} catch (IOException e) {
			logger.error("", e);
		}
	}

}
