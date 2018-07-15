package zeus.network.protocol;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import zeus.network.util.Constants;
import zeus.network.util.NetworkUtil;

/**
 * tcp心跳处理器
 * @author songfeilong
 *
 */
public class HeartBeatHandler extends SimpleChannelInboundHandler<IClientMsg> {

	private Logger logger = Logger.getLogger(getClass());

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IClientMsg msg) throws Exception {
		if (msg.getGate() == GateCmd.PING) {
			logger.info("from client ping... ");
			ctx.writeAndFlush(Constants.pongMsg);
		} else if (msg.getGate() == GateCmd.PONG) {
			logger.info("from client pong");
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
				logger.error("==============WRITER_IDLE====================");
				ctx.writeAndFlush(Constants.pingMsg);
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
