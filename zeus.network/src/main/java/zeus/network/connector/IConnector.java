package zeus.network.connector;

import com.i5i58.data.channel.ServerInfo;

import io.netty.channel.Channel;
import zeus.network.protocol.IIntranetMsg;
import zeus.network.protocol.IntranetMsg;

/**
 * �������ӿ�
 * @author frank
 *
 */
public interface IConnector {

	boolean connect();

	void close() throws InterruptedException;

	boolean reconnect() throws Exception;

	Channel getChannel();

	/**
	 * ��ȡ��������Ϣ
	 * @return
	 */
	ServerInfo getServerInfo();

	/**
	 * ��������������
	 * @param msg ��������Ϣ
	 */
	void send(IIntranetMsg msg);

	/**
	 * ���ʹ�accId���������ݣ� accId��ƴ���ڰ�ͷ��data֮�䣬|accId���ȣ�1byte|accId,accId���ȵ�bytes|data...|
	 * @param msg ��������Ϣ
	 * @param accId accId
	 */
	void sendWithAccId(IntranetMsg msg, String accId);
	
	boolean isRunning();
	
	/**
	 * �Ƿ��ֶ��رյ�
	 * @return
	 */
	boolean isManullyClosed();
	
	public interface IConnectorClosedListener{
		public void connectorClosed(IConnector connector);
	}
}
