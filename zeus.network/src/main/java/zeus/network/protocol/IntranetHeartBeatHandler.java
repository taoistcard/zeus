package zeus.network.protocol;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import zeus.network.util.NetworkUtil;

/**
 * ����������������
 * 
 * @author frank
 *
 */
public class IntranetHeartBeatHandler extends SimpleChannelInboundHandler<IIntranetMsg> {

	private Logger logger = Logger.getLogger(getClass());

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IIntranetMsg msg) throws Exception {
		// logger.info(String.format(
		// "channelRead0: channelId:%s, meg: [version:%s, crc:%s, length:%s,
		// gate:%s, main:%s, sub:%s]",
		// ctx.channel().id().asLongText(), msg.getVersion(), msg.getCrc(),
		// msg.getLength(), msg.getGate(),
		// msg.getMain(), msg.getSub()));
		if (msg.getGate() == GateCmd.PING) {
			// logger.info("gateServer ping... ");
			ctx.writeAndFlush(GateCmd.pongMsg_Intranet);
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			switch (event.state()) {
			case READER_IDLE:
				logger.info("Client is offline. Close Channel.");
				NetworkUtil.closeGracefully(ctx);
				break;
			case WRITER_IDLE:
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
}
