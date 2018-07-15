package zeus.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class IntranetMsgEncoder extends MessageToByteEncoder<IIntranetMsg> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IIntranetMsg msg, ByteBuf out) throws Exception {
		if (msg.getSessionId() == null || msg.getSessionId().isEmpty()) {
			out.writeByte(0);
		} else {
			out.writeByte(msg.getSessionId().length());
			out.writeBytes(msg.getSessionIdBytes());
		}
		out.writeByte(msg.getVersion());
		out.writeByte(msg.getCrc());
		out.writeInt(msg.getLength());
		out.writeByte(msg.getGate());
		out.writeShort(msg.getMain());
		out.writeShort(msg.getSub());
		if (msg.getLength() > 0) {
			out.writeBytes(msg.getData());
		}

	}
}
