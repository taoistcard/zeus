
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import zeus.network.protocol.ClientMsg;
import zeus.network.protocol.GateCmd;
import zeus.network.protocol.MessageFactory;
import zeus.network.util.Constants;

public class SimpleTCPClientHandler extends SimpleChannelInboundHandler<ClientMsg> {

	private static ClientMsg pongMsg = MessageFactory.createClientMsg((byte) 0, (byte) 0, GateCmd.PONG, (short) 0,
			(short) 0, null);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelActive();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientMsg msg) throws Exception {
		Channel incoming = ctx.channel();
		if (msg.getGate() == GateCmd.PING) {
			System.out.println("from gate ping");
			ctx.writeAndFlush(pongMsg);
		} else if (msg.getGate() == GateCmd.PONG) {
			System.out.println("from gate pong");
		} else {
			System.out.println(String.format(
					"channelRead0: channelId:%s, meg: [version:%s, crc:%s, length:%s, gate:%s, main:%s, sub:%s], data:%s",
					ctx.channel().id().asLongText(), msg.getVersion(), msg.getCrc(), msg.getLength(), msg.getGate(),
					msg.getMain(), msg.getSub(), new String(msg.getData(), "utf-8")));
		}
		// String message = new String(msg.getBytes(),
		// ConstantUtil.defaultCharset);
		// System.out.println("Server->Client: " + incoming.remoteAddress() + "
		// " + message);
		// if (msg.getMsg().equals("PONG")) {
		//
		// } else {
		//
		// }
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelInactive();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.fireExceptionCaught(cause);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			switch (event.state()) {
			case READER_IDLE:
				break;
			case WRITER_IDLE:
//				ClientMsg pongMsg = MessageFactory.createGateClientMsg((byte) 0, (byte) 0, 
//						GateCmd.PING, (short) 0,(short) 0, "", null);
//				ctx.writeAndFlush(pongMsg);
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
