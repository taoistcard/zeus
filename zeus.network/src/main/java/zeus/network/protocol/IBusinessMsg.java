package zeus.network.protocol;

/**
 * 业务消息接口
 * @author frank
 *
 */
public interface IBusinessMsg {
	
	/**
	 * 获取消息版本号
	 * @return
	 */
	byte getVersion();
	
	/**
	 * 获取消息中数据长度（不包括头）
	 * @return
	 */
	int getLength();
	
	/**
	 * 获取业务主命令
	 * @return
	 */
	short getMain();
	
	/**
	 * 获取业务次命令
	 * @return
	 */
	short getSub();
	
	/**
	 * 获取业务数据
	 * @return
	 */
	byte[] getData();
}
