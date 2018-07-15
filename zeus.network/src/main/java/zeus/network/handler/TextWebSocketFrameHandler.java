package zeus.network.handler;

import java.util.Base64;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import zeus.network.manager.IRemotPeer;
import zeus.network.manager.RemotePeerManager;
import zeus.network.manager.WsRemotePeer;
import zeus.network.protocol.ClientMsg;
import zeus.network.protocol.IClientMsg;
import zeus.network.protocol.MessageFactory;
import zeus.network.protocol.TextClientMsg;
import zeus.network.threading.ChannelInactiveTask;
import zeus.network.threading.ChannelReadTask;
import zeus.network.threading.ITaskPool;
import zeus.network.threading.TaskQueue;
import zeus.network.util.NetworkUtil;

/**
 * websocketͨ��������
 * 
 * @author songfeilong
 *
 */
@io.netty.channel.ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	Logger logger = Logger.getLogger(getClass());

	private IBusinessHandler businessHandler;

	private ITaskPool taskPool;

	private boolean isGate;

	private boolean useBusinessTask;

	public TextWebSocketFrameHandler(IBusinessHandler businessHandler, boolean useBusinessTask, ITaskPool taskPool,
			boolean isGate) {
		this.businessHandler = businessHandler;
		this.taskPool = taskPool;
		this.isGate = isGate;
		this.useBusinessTask = useBusinessTask;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// String sessionId = ctx.channel().id().asLongText();
		// WsRemotePeer peer = new WsRemotePeer(sessionId, ctx, null);
		// RemotePeerManager.put(sessionId, peer);
		businessHandler.onChannelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String sessionId = ctx.channel().id().asLongText();
		IRemotPeer peer = RemotePeerManager.getClient(sessionId);
		if (peer != null) {
			TaskQueue queue = peer.getTaskQueue();
			if (queue != null) {
				ChannelInactiveTask task = new ChannelInactiveTask(businessHandler, ctx, peer);
				queue.addTask(task);
			}else{
				RemotePeerManager.removeClient(sessionId);
				NetworkUtil.closeGracefully(ctx);
			}
		}else{
			NetworkUtil.closeGracefully(ctx);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		businessHandler.onExceptionCaught(ctx, cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

		logger.info(msg.text());
		TextClientMsg textClientMsg = JSON.parseObject(msg.text(), TextClientMsg.class);
		// logger.info(String.format(
		// "channelRead0: channelId:%s, meg: [version:%s, crc:%s, gate:%s,
		// main:%s, sub:%s], data:%s",
		// ctx.channel().id().asLongText(), textClientMsg.getVersion(),
		// textClientMsg.getCrc(),
		// textClientMsg.getGate(), textClientMsg.getMain(),
		// textClientMsg.getSub(), textClientMsg.getData()));
		byte[] data = Base64.getDecoder().decode(textClientMsg.getData());
		ClientMsg clientMsg = null;
		if (isGate) {
			clientMsg = MessageFactory.createGateClientMsg(textClientMsg.getVersion(), textClientMsg.getCrc(),
					textClientMsg.getGate(), textClientMsg.getMain(), textClientMsg.getSub(),
					textClientMsg.getServerKey(), data);
		} else {
			clientMsg = MessageFactory.createClientMsg(textClientMsg.getVersion(), textClientMsg.getCrc(),
					textClientMsg.getGate(), textClientMsg.getMain(), textClientMsg.getSub(), data);
		}
		String sessionId = ctx.channel().id().asLongText();
		clientMsg.setSessionId(sessionId);

		businessHandler.auth(ctx, clientMsg, new ChannelReadCallback() {

			@Override
			public void OnDoAuthSuccess(ChannelHandlerContext ctx, IClientMsg msg, String key, Object userData) {
				if (useBusinessTask) {
					if (taskPool != null) {
						TaskQueue queue = taskPool.getMatchWorker();
						queue.incrementPeerCount();
						IRemotPeer peer = new WsRemotePeer(msg.getSessionId(), ctx, queue);
						peer.getUserData().put(key, userData);
						RemotePeerManager.put(msg.getSessionId(), peer);
						ChannelReadTask task = new ChannelReadTask(businessHandler, msg, peer);
						queue.addTask(task);
					} else {
						logger.error("用户工作任务队列null，关闭通道");
						ctx.close();
					}
				} else {
					IRemotPeer peer = new WsRemotePeer(msg.getSessionId(), ctx, null);
					peer.getUserData().put(key, userData);
					RemotePeerManager.put(msg.getSessionId(), peer);
					businessHandler.onChannelRead0(peer, msg);
				}
			}

			@Override
			public void OnDoAuthFailed(ChannelHandlerContext ctx, IClientMsg msg) {
				logger.error("用户需要验证并失败, 关闭通道");
				ctx.close();
			}

			@Override
			public void OnAuthSuccess(IRemotPeer peer, IClientMsg msg) {
				if (useBusinessTask) {
					ChannelReadTask task = new ChannelReadTask(businessHandler, msg, peer);
					peer.getTaskQueue().addTask(task);
				} else {
					businessHandler.onChannelRead0(peer, msg);
				}
			}

			@Override
			public void OnAuthFailed(ChannelHandlerContext ctx, IClientMsg msg) {
				logger.error("用户已验证并失败, 关闭通道");
				ctx.close();
			}
		});
	}

}
