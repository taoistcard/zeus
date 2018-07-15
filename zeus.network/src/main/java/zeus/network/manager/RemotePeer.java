package zeus.network.manager;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import zeus.network.protocol.IIntranetMsg;
import zeus.network.threading.TaskQueue;
import zeus.network.util.NetworkUtil;

/**
 * tcp�û�Զ�̴������
 * @author frank
 *
 */
public class RemotePeer implements IRemotPeer {

	protected String sessionId;
	protected ChannelHandlerContext ctx;
	protected Map<String, Object> userData;
	protected TaskQueue taskQueue;

	public RemotePeer(String sessionId, ChannelHandlerContext ctx, TaskQueue taskQueue) {
		this.sessionId = sessionId;
		this.ctx = ctx;
		this.taskQueue = taskQueue;
		userData = new HashMap<String, Object>();
	}

	@Override
	public void send(byte version, byte crc, byte gate, short main, short sub, Object data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(IIntranetMsg msg) {
		ctx.writeAndFlush(msg);
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
		ctx.close();
	}

	@Override
	public void closeGracefully() {
		NetworkUtil.closeGracefully(ctx);
	}

	@Override
	public ChannelHandlerContext getContext() {
		return ctx;
	}

	@Override
	public boolean isVirtual() {
		return false;
	}
}
