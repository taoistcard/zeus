package zeus.network.protocol;

/**
 * �������̨ҵ�������ϢЭ��ӿ�
 * @author frank
 *
 */
public interface IIntranetMsg extends IClientMsg{

	/**
	 * ��ȡ����ChannelId(�ͻ����������е�channelId��������channel��ֵ���ں��ҵ�����������֮��ƥ��ͻ���)
	 * @return
	 */
//	String getVirtualChannelId();
	
	byte[] getSessionIdBytes();
	
//	void setVirtualChannelId(String virtualChannelId);
}
