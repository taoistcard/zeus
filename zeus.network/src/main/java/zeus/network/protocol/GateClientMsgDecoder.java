package zeus.network.protocol;

import java.util.List;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import zeus.network.util.Constants;

public class GateClientMsgDecoder extends ByteToMessageDecoder {

	private Logger logger = Logger.getLogger(getClass());

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		logger.info("decode here");
		// int packageCount = 0;
		while (readPackage(ctx, in, out)) {
			// packageCount++;
			// logger.info("read packageCount: " + packageCount);
		}
	}

	private boolean readPackage(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		if (in.readableBytes() < Constants.protocolHeadLength) {
			return false;
		}
		in.markReaderIndex();

		// read header
		byte version = in.readByte();
		byte crc = in.readByte();
		int length = in.readInt();
		byte gate = in.readByte();
		short main = in.readShort();
		short sub = in.readShort();
		logger.info(String.format("version:%s, crc:%s, length:%s, gate:%s, main:%s, sub:%s", version, crc, length, gate,
				main, sub));
		byte serverKeyLen = in.readByte();
		if (in.readableBytes() < serverKeyLen + length) {
			in.resetReaderIndex();
			return false;
		}
		byte[] serverKeyBytes = null;
		if (serverKeyLen > 0) {
			serverKeyBytes = new byte[serverKeyLen];
			in.readBytes(serverKeyBytes);
		}
		// // ===============
		// if (in.readableBytes() < length) {
		// in.resetReaderIndex();
		// return false;
		// }
		byte[] data = null;
		if (length > 0) {
			data = new byte[length];
			in.readBytes(data);
		}
		ClientMsg msg = MessageFactory.createGateClientMsg(version, crc, gate, main, sub, serverKeyBytes, data);
		msg.setSessionId(ctx.channel().id().asLongText());
		out.add(msg);
		return true;
	}
}
