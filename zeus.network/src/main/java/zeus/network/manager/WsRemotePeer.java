package zeus.network.manager;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import zeus.network.protocol.IIntranetMsg;
import zeus.network.protocol.MessageFactory;
import zeus.network.protocol.TextClientMsg;
import zeus.network.threading.TaskQueue;
import zeus.network.util.Constants;
import zeus.network.util.NetworkUtil;

/**
 * websocket�û�Զ�̴������ӿ�
 * @author frank
 *
 */
public class WsRemotePeer implements IRemotPeer {

	protected String sessionId;
	protected ChannelHandlerContext ctx;
	protected Map<String, Object> userData;
	protected TaskQueue taskQueue;

	public WsRemotePeer(String sessionId, ChannelHandlerContext ctx, TaskQueue taskQueue) {
		this.sessionId = sessionId;
		this.ctx = ctx;
		this.taskQueue = taskQueue;
		userData = new HashMap<String, Object>();
	}

	@Override
	public void send(byte version, byte crc, byte gate, short main, short sub, Object data) {
		TextClientMsg textMsg = new TextClientMsg();
		textMsg.setVersion(version);
		textMsg.setCrc(crc);
		textMsg.setGate(gate);
		textMsg.setMain(main);
		textMsg.setSub(sub);
		textMsg.setData(JSON.toJSONString(data));
		String json = JSON.toJSONString(textMsg);
		ctx.writeAndFlush(new TextWebSocketFrame(json)); 
	}

	@Override
	public void send(IIntranetMsg msg) {
		TextClientMsg textMsg = new TextClientMsg();
		textMsg.setVersion(msg.getVersion());
		textMsg.setCrc(msg.getCrc());
		textMsg.setGate(msg.getGate());
		textMsg.setMain(msg.getMain());
		textMsg.setSub(msg.getSub());
		textMsg.setData(Base64.getEncoder().encodeToString(msg.getData()));
		String json = JSON.toJSONString(textMsg);
		ctx.writeAndFlush(new TextWebSocketFrame(json));
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
