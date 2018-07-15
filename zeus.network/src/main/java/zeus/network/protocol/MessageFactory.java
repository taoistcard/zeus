package zeus.network.protocol;

/**
 * ��Ϣ����
 * @author Administrator
 *
 */
public class MessageFactory {

	/**
	 * �����ͻ�����Ϣ
	 * @param version
	 * @param crc
	 * @param gate
	 * @param main
	 * @param sub
	 * @param data
	 * @return
	 */
	public static ClientMsg createClientMsg(byte version, byte crc, byte gate, short main, short sub, byte[] data) {
		ClientMsg msg = new ClientMsg();
		msg.setVersion(version);
		msg.setCrc(crc);
		if (data == null) {
			msg.setLength(0);
		} else {
			msg.setLength(data.length);
		}
		msg.setGate(gate);
		msg.setMain(main);
		msg.setSub(sub);
		msg.setData(data);
		return msg;
	}

	public static ClientMsg createGateClientMsg(byte version, byte crc, byte gate, short main, short sub,
			byte[] serverKeyBytes, byte[] data) {
		ClientMsg msg = new ClientMsg();
		msg.setVersion(version);
		msg.setCrc(crc);
		if (data == null) {
			msg.setLength(0);
		} else {
			msg.setLength(data.length);
		}
		msg.setGate(gate);
		msg.setMain(main);
		msg.setSub(sub);
		msg.setServerKeyBytes(serverKeyBytes);
		msg.setData(data);
		return msg;
	}

	public static ClientMsg createGateClientMsg(byte version, byte crc, byte gate, short main, short sub,
			String serverKey, byte[] data) {
		ClientMsg msg = new ClientMsg();
		msg.setVersion(version);
		msg.setCrc(crc);
		if (data == null) {
			msg.setLength(0);
		} else {
			msg.setLength(data.length);
		}
		msg.setGate(gate);
		msg.setMain(main);
		msg.setSub(sub);
		msg.setServerKey(serverKey);
		msg.setData(data);
		return msg;
	}

	/**
	 * ������������Ϣ
	 * @param virtualSessionId
	 * @param version
	 * @param crc
	 * @param gate
	 * @param main
	 * @param sub
	 * @param data
	 * @return
	 */
	public static IntranetMsg createIntranetMsg(byte[] virtualSessionId, byte version, byte crc, byte gate, short main,
			short sub, byte[] data) {
		IntranetMsg msg = new IntranetMsg(virtualSessionId, version, crc, gate, sub, sub, data);
		return msg;
	}
}
