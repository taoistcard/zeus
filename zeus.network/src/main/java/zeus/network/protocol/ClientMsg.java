package zeus.network.protocol;

import java.util.Base64;

import zeus.network.util.Constants;

/**
 * ������ͻ��˼���Ϣ��
 * 
 * @author frank
 *
 */
public class ClientMsg extends BusinessMsg implements IClientMsg {

	private byte gate;

	private byte crc;

	protected String sessionId;

	protected String serverKey;

	protected byte[] serverKeyBytes;

	public void setData(String data) {
		this.data = Base64.getDecoder().decode(data);
	}

	@Override
	public byte getGate() {
		return gate;
	}

	@Override
	public byte getCrc() {
		// TODO Auto-generated method stub
		return crc;
	}

	public void setGate(byte gate) {
		this.gate = gate;
	}

	public void setCrc(byte crc) {
		this.crc = crc;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String getServerKey() {
		return serverKey;
	}

	public void setServerKeyBytes(byte[] serverKeyBytes) {
		if (serverKeyBytes != null) {
			this.serverKeyBytes = serverKeyBytes;
			this.serverKey = new String(serverKeyBytes, Constants.defaultCharset);
		}
	}

	public void setServerKey(String serverKey) {
		if (serverKey != null && !serverKey.isEmpty()) {
			this.serverKey = serverKey;
			this.serverKeyBytes = serverKey.getBytes(Constants.defaultCharset);
		}
	}

	@Override
	public byte[] getServerKeybytes() {
		// TODO Auto-generated method stub
		return this.serverKeyBytes;
	}

}
