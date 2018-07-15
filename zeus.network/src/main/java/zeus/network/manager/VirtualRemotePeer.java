package zeus.network.manager;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelHandlerContext;
import zeus.network.protocol.GateCmd;
import zeus.network.protocol.IIntranetMsg;
import zeus.network.protocol.IntranetMsg;
import zeus.network.protocol.MessageFactory;
import zeus.network.threading.TaskQueue;
import zeus.network.util.Constants;

/**
 * �����û�Զ�̴������
 * @author frank
 *
 */
public class VirtualRemotePeer implements IRemotPeer {

	protected String sessionId;
	private byte[] sessionIdBytes;
	protected ChannelHandlerContext ctx;
	protected Map<String, Object> userData;
	protected TaskQueue taskQueue;

	public VirtualRemotePeer(String sessionId, ChannelHandlerContext ctx, TaskQueue taskQueue) {
		this.sessionId = sessionId;
		this.sessionIdBytes = this.sessionId.getBytes(Constants.defaultCharset);
		this.ctx = ctx;
		this.taskQueue = taskQueue;
		userData = new HashMap<String, Object>();
	}

	@Override
	public void send(byte version, byte crc, byte gate, short main, short sub, Object data) {

		this.ctx.writeAndFlush(MessageFactory.createIntranetMsg(sessionIdBytes, version, crc, gate, main, sub,
				JSON.toJSONString(data).getBytes(Constants.defaultCharset)));
	}

	@Override
	public void send(IIntranetMsg msg) {
		this.ctx.writeAndFlush(msg);
	}

	@Override
	public Map<String, Object> getUserData() {
		return userData;
	}

	@Override
	public TaskQueue getTaskQueue() {
		return taskQueue;
	}

	@Override
	public void setTaskQueue(TaskQueue taskQueue) {
		this.taskQueue = taskQueue;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}
	
	@Override
	public void close() {
		IntranetMsg msg = GateCmd.createVirtualCloseMsg(sessionId);
		ctx.writeAndFlush(msg);
	}

	@Override
	public void closeGracefully() {
		close();
	}

	@Override
	public ChannelHandlerContext getContext() {
		return ctx;
	}
	
	@Override
	public boolean isVirtual() {
		return true;
	}
}
