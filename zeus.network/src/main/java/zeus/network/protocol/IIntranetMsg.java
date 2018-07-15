package zeus.network.protocol;

/**
 * 网关与后台业务服务消息协议接口
 * @author frank
 *
 */
public interface IIntranetMsg extends IClientMsg{

	/**
	 * 获取虚拟ChannelId(客户端在网关中的channelId，由网关channel赋值，在后端业务服务与网关之间匹配客户端)
	 * @return
	 */
//	String getVirtualChannelId();
	
	byte[] getSessionIdBytes();
	
//	void setVirtualChannelId(String virtualChannelId);
}
