package zeus.network.protocol;

import zeus.network.util.Constants;

/**
 * ��������
 * @author Administrator
 *
 */
public class GateCmd {

	public static final byte PING = 0x23;
	public static final byte PONG = 0x24;

	public static final IntranetMsg pingMsg_Intranet = new IntranetMsg(null, (byte) 0, (byte) 0, GateCmd.PING,
			(short) 0, (short) 0, null);

	public static final IntranetMsg pongMsg_Intranet = new IntranetMsg(null, (byte) 0, (byte) 0, GateCmd.PONG,
			(short) 0, (short) 0, null);

//	/**
//	 * ������֤�û��������
//	 */
//	public static final byte AUTH = 0x30;

	/**
	 * ����ת����Ϣ�����һ��������ҪЯ��accId��
	 */
	public static final byte TRANS_FIRST = 0x31;

	/**
	 * ����ת����Ϣ����
	 */
	public static final byte TRANS = 0x32;

	/**
	 * ���ضϿ����̨ҵ���������
	 */
	public static final byte DISCONNECT = 0x33;

	/**
	 * ������֤�û���ݲ�ת����Ϣ�����һ��������ҪЯ����̨serverKey��
	 */
	public static final byte AUTH_TRANS_FIRST = 0x34;

//	/**
//	 * ������֤�û���ݲ�ת����Ϣ����
//	 */
//	public static final byte AUTH_TRANS = 0x35;

	/**
	 * �����л���̨ҵ��������ͬ��ִ�У�������Ͽ�ԭ��̨�����������º�̨����
	 */
	public static final byte SWITCH = 0x36;

	/**
	 * �����л���̨ҵ��������º�̨ҵ�����ת����Ϣ����
	 */
	public static final byte SWITCH_TRANS = 0x37;

	// ======================================

	/**
	 * ҵ��������ر������������ӣ������ط�������ر����
	 */
	public static final byte VIRTUAL_CLOSE = 0x70;
	

	public static IntranetMsg createVirtualCloseMsg(String virtualChannelId) {
		return new IntranetMsg(virtualChannelId,
				MessageFactory.createClientMsg(Constants.PROTOCOL_VERSION, (byte) 0, VIRTUAL_CLOSE, (short) 0, (short) 0, null));
	}
	
	public static IntranetMsg createVirtualCloseMsg(byte[] virtualChannelIdBytes){
		return new IntranetMsg(virtualChannelIdBytes, Constants.PROTOCOL_VERSION, (byte) 0, VIRTUAL_CLOSE, (short) 0, (short) 0, null);
	}
}
