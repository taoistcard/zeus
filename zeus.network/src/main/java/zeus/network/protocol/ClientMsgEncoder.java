package zeus.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 客户端消息编码器
 * 
 * @author frank
 *
 */
public class ClientMsgEncoder extends MessageToByteEncoder<IClientMsg> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IClientMsg msg, ByteBuf out) throws Exception {
		if (msg == null) {
			throw new Exception("msg is null, encode failed.");
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
