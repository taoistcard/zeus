package zeus.network.protocol;

import java.io.UnsupportedEncodingException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import zeus.network.util.Constants;

/**
 * �������̨ҵ��������Ϣ������
 * 
 * @author frank
 *
 */
public class IntranetMsgDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

//		int packageCount = 0;
		while (readPackage(in, out)) {
//			packageCount++;
//			System.out.println("packageCount: " + packageCount);
		}
	}

	private boolean readPackage(ByteBuf in, List<Object> out) throws UnsupportedEncodingException {
		if (in.readableBytes() < 1) {
			return false;
		}
		in.markReaderIndex();
		// ��ȡ�ڲ���Ϣͷ==========
		byte virtualChannelIdLength = in.readByte();
		if (in.readableBytes() < virtualChannelIdLength) {
			in.resetReaderIndex();
			return false;
		}
		byte[] virtualChannelIdBytes = new byte[virtualChannelIdLength];
		in.readBytes(virtualChannelIdBytes);

		if (in.readableBytes() < Constants.protocolHeadLength) {
			in.resetReaderIndex();
			return false;
		}
		// ��ȡ�ͻ�����Ϣͷ==========
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
		byte[] data = new byte[length];
		in.readBytes(data);

		IntranetMsg msg = new IntranetMsg(virtualChannelIdBytes, version, crc, gate, main, sub, data);
		out.add(msg);
		return true;
	}
}
