package zeus.network.handler;

import java.io.IOException;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import zeus.network.manager.IRemotPeer;
import zeus.network.manager.RemotePeer;
import zeus.network.manager.RemotePeerManager;
import zeus.network.protocol.IClientMsg;
import zeus.network.threading.ChannelInactiveTask;
import zeus.network.threading.ChannelReadTask;
import zeus.network.threading.ITaskPool;
import zeus.network.threading.TaskQueue;
import zeus.network.util.Constants;
import zeus.network.util.NetworkUtil;

/**
 * tcpͨ��������
 * 
 * @author songfeilong
 *
 */
@io.netty.channel.ChannelHandler.Sharable
public class TcpChannelHandler extends SimpleChannelInboundHandler<IClientMsg> {

	private Logger logger = Logger.getLogger(getClass());

	private IBusinessHandler businessHandler;
	private ITaskPool taskPool;
	private boolean useBusinessTask;

	public TcpChannelHandler(IBusinessHandler businessHandler, boolean useBusinessTask, ITaskPool taskPool) {
		this.businessHandler = businessHandler;
		this.taskPool = taskPool;
		this.useBusinessTask = useBusinessTask;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelActive: " + ctx.channel().id().asLongText());
		businessHandler.onChannelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelInactive: " + ctx.channel().id().asLongText());
		String sessionId = ctx.channel().id().asLongText();
		IRemotPeer peer = RemotePeerManager.getClient(sessionId);
		if (peer != null) {
			if (useBusinessTask) {
				TaskQueue queue = peer.getTaskQueue();
				if (queue != null) {
					ChannelInactiveTask task = new ChannelInactiveTask(businessHandler, ctx, peer);
					queue.addTask(task);
				} else {
					RemotePeerManager.removeClient(sessionId);
					NetworkUtil.closeGracefully(ctx);
				}
			}else{
				businessHandler.onChannelInactive(ctx, peer);
				RemotePeerManager.removeClient(sessionId);
				NetworkUtil.closeGracefully(ctx);
			}
		} else {
			NetworkUtil.closeGracefully(ctx);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		String msg = cause.getMessage();
		if (cause instanceof IOException){
			if ( msg.compareTo(Constants.CLOSED_BY_REMOTE) == 0){
//				logger.error("客户端断开");
			}else{
				logger.error(cause.getMessage());
			}
		}else{
			logger.error(cause.getMessage());	
		}
		ctx.close();
		businessHandler.onExceptionCaught(ctx, cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IClientMsg msg) throws Exception {
		// logger.info(String.format(
		// "channelRead0: channelId:%s, meg: [version:%s, crc:%s, length:%s,
		// gate:%s, main:%s, sub:%s], data:%s",
		// ctx.channel().id().asLongText(), msg.getVersion(), msg.getCrc(),
		// msg.getLength(), msg.getGate(),
		// msg.getMain(), msg.getSub(), new String(msg.getData(), "utf-8")));
		businessHandler.auth(ctx, msg, new ChannelReadCallback() {

			@Override
			public void OnDoAuthSuccess(ChannelHandlerContext ctx, IClientMsg msg, String key, Object userData) {
				if (useBusinessTask) {
					if (taskPool != null) {
						TaskQueue queue = taskPool.getMatchWorker();
						queue.incrementPeerCount();
						IRemotPeer peer = new RemotePeer(msg.getSessionId(), ctx, queue);
						peer.getUserData().put(key, userData);
						RemotePeerManager.put(msg.getSessionId(), peer);
						ChannelReadTask task = new ChannelReadTask(businessHandler, msg, peer);
						queue.addTask(task);
					} else {
						logger.error("用户工作任务队列null，关闭通道");
						ctx.close();
					}
				} else {
					IRemotPeer peer = new RemotePeer(msg.getSessionId(), ctx, null);
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
