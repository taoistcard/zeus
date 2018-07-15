package zeus.network.connector;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import zeus.network.manager.IRemotPeer;
import zeus.network.manager.RemotePeerManager;
import zeus.network.protocol.GateCmd;
import zeus.network.protocol.IntranetMsg;

public class ConnectorChannelHandler extends SimpleChannelInboundHandler<IntranetMsg> {
	String serverKey;
	private Logger logger = Logger.getLogger(getClass());

	public ConnectorChannelHandler(String serverKey) {
		this.serverKey = serverKey;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();

	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			switch (event.state()) {
			case READER_IDLE:
				break;
			case WRITER_IDLE:
				// logger.info("send ping... " + serverKey);
				ctx.writeAndFlush(GateCmd.pingMsg_Intranet);
				break;
			case ALL_IDLE:
				break;
			default:
				break;
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IntranetMsg msg) throws Exception {
		switch (msg.getGate()) {
		case GateCmd.PING:
			break;
		case GateCmd.PONG:
			break;
		case GateCmd.TRANS: {
			if (RemotePeerManager.containsKey(msg.getSessionId())) {
				msg.setServerKey(serverKey);
				RemotePeerManager.getClient(msg.getSessionId()).send(msg);
			}
		}
			break;
		case GateCmd.VIRTUAL_CLOSE:{
			logger.info("receive a virtual close from live, gate will close a client.");
			IRemotPeer peer = RemotePeerManager.removeClient(msg.getSessionId());
			if (peer == null){
				break;
			}
			if (peer.getContext() == null){
				break;
			}
			peer.close();
		}
			break;
		default:
			break;
		}
		return;
	}
}
