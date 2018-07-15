package zeus.network.protocol;

import java.util.List;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import zeus.network.util.Constants;

/**
 * �ͻ�����Ϣ������
 * 
 * @author frank
 *
 */
public class ClientMsgDecoder extends ByteToMessageDecoder {

	private Logger logger = Logger.getLogger(getClass());

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

//		int packageCount = 0;
		while (readPackage(ctx, in, out)) {
//			packageCount++;
//			logger.info("read packageCount: " + packageCount);
		}
	}

	private boolean readPackage(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		if (in.readableBytes() < Constants.protocolHeadLength) {
			return false;
		}
		in.markReaderIndex();
		// ��ȡͷ==========
		byte version = in.readByte();
		byte crc = in.readByte();
		int length = in.readInt();
		byte gate = in.readByte();
		short main = in.readShort();
		short sub = in.readShort();
		// ===============
		if (in.readableBytes() < length) {
			in.resetReaderIndex();
			return false;
		}
		byte[] data = null;
		if (length > 0) {
			data = new byte[length];
			in.readBytes(data);
		}
		ClientMsg msg = MessageFactory.createClientMsg(version, crc, gate, main, sub, data);
		msg.setSessionId(ctx.channel().id().asLongText());
		out.add(msg);
		return true;
	}
}
