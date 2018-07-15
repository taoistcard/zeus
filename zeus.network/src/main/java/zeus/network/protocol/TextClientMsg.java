package zeus.network.protocol;

import zeus.network.util.Constants;

/**
 * �ı���ϢЭ�飨websocket��
 * 
 * @author frank
 *
 */
public class TextClientMsg {

	protected byte version;

	protected short main;

	protected short sub;

	protected String data;

	private byte gate;

	private String serverKey;

	private byte crc;

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public short getMain() {
		return main;
	}

	public void setMain(short main) {
		this.main = main;
	}

	public short getSub() {
		return sub;
	}

	public void setSub(short sub) {
		this.sub = sub;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public byte getGate() {
		return gate;
	}

	public void setGate(byte gate) {
		this.gate = gate;
	}

	public byte getCrc() {
		return crc;
	}

	public void setCrc(byte crc) {
		this.crc = crc;
	}

	public String getServerKey() {
		return serverKey;
	}

	public void setServerKey(String serverKey) {
		this.serverKey = serverKey;
	}

}
