package zeus.network.protocol;

import zeus.network.util.Constants;

/**
 * �������̨�������Ϣ��
 * 
 * @author frank
 *
 */
public class IntranetMsg extends ClientMsg implements IIntranetMsg {

	private byte[] sessionIdBytes;

	public IntranetMsg(byte[] sessionIdBytes, byte version, byte crc, byte gate, short main, short sub,
			byte[] data) {
		this.setVersion(version);
		this.setCrc(crc);
		if (data == null) {
			this.setLength(0);
		} else {
			this.setLength(data.length);
		}
		this.setGate(gate);
		this.setMain(main);
		this.setSub(sub);
		this.setData(data);

		this.sessionIdBytes = sessionIdBytes;
		if (sessionIdBytes != null) {
			this.sessionId = new String(sessionIdBytes, Constants.defaultCharset);
		}
	}

	public IntranetMsg(String sessionId, IClientMsg msg) {
		this.setVersion(msg.getVersion());
		this.setCrc(msg.getCrc());
		this.setLength(msg.getLength());
		this.setGate(msg.getGate());
		this.setMain(msg.getMain());
		this.setSub(msg.getSub());
		this.setServerKey(msg.getServerKey());
		this.setData(msg.getData());
		this.setSessionId(sessionId);
		if (sessionId != null) {
			sessionIdBytes = sessionId.getBytes(Constants.defaultCharset);
		}
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public byte[] getSessionIdBytes() {
		return sessionIdBytes;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
		if (sessionId != null) {
			sessionIdBytes = sessionId.getBytes(Constants.defaultCharset);
		}
	}

}
