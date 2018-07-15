package zeus.network.util;

import java.nio.charset.Charset;

import io.netty.util.CharsetUtil;
import zeus.network.protocol.ClientMsg;
import zeus.network.protocol.GateCmd;
import zeus.network.protocol.MessageFactory;

/**
 * ����ͨ�ų���
 * 
 * @author frank
 *
 */
public class Constants {

	public static final byte PROTOCOL_VERSION = 0x00;

	/**
	 * �ͻ��������ؼ���ϢЭ��ͷЭ��
	 */
	public static final int protocolHeadLength = 11;

	public static final int gateProtocolHeadLength = 43;

	public static final Charset defaultCharset = CharsetUtil.UTF_8;

	public static final String WEBSOCKET_PATH = "/websocket";

	public static final ClientMsg pingMsg = MessageFactory.createClientMsg((byte) 0, (byte) 0, GateCmd.PING, (short) 0,
			(short) 0, null);

	public static final ClientMsg pongMsg = MessageFactory.createClientMsg((byte) 0, (byte) 0, GateCmd.PONG, (short) 0,
			(short) 0, null);

//	/**
//	 * user data key used on gate server.
//	 * record all the current live servers where the user is active.
//	 * */
//	public static final String CONNECTORS = "connectors";
	public static String CLOSED_BY_REMOTE = "远程主机强迫关闭了一个现有的连接。";
}
