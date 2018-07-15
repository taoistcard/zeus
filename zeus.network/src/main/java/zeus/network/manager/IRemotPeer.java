package zeus.network.manager;

import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import zeus.network.protocol.IIntranetMsg;
import zeus.network.threading.TaskQueue;

/**
 * �û�Զ�̴������ӿ�
 * @author frank
 *
 */
public interface IRemotPeer {

	void send(byte version, byte crc, byte gate, short main, short sub, Object data);

	void send(IIntranetMsg msg);
	
	Map<String, Object> getUserData();
	
	public TaskQueue getTaskQueue();

	public void setTaskQueue(TaskQueue taskQueue);

	public String getSessionId();
	
	/**
	 * ǿ�йر�����
	 * */
	public void close();
	
	/**
	 * �ر�ʱȷ�������Ѿ���ȫ����
	 * */
	public void closeGracefully();

	ChannelHandlerContext getContext();
	
	/**
	 * if the peer represents a client connected via a gateway, return true;
	 * otherwise return false;
	 * */
	boolean isVirtual();
}
