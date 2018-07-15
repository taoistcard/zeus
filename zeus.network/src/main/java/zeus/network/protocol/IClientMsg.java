package zeus.network.protocol;

import io.netty.buffer.ByteBuf;

/**
 * ������ͻ��˼��ͻ���ֱ��ҵ�������ϢЭ��ӿ�
 * @author frank
 *
 */
public interface IClientMsg extends IBusinessMsg{

	/**
	 * ��ȡ��������
	 * @return
	 */
	public byte getGate();
	
	/**
	 * ��ȡcrcУ���ֶ�
	 * @return
	 */
	public byte getCrc();
	
	String getSessionId();
	
	void setSessionId(String sessionId);

	public void setData(byte[] data);

	public void setLength(int i);
	
	public String getServerKey();

	public byte[] getServerKeybytes();
}
