package zeus.network.connector;

import com.i5i58.data.channel.ServerInfo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import zeus.network.protocol.IIntranetMsg;
import zeus.network.protocol.IntranetMsg;
import zeus.network.util.Constants;

/**
 * �������̨ҵ�����������
 * @author frank
 *
 */
public class GateConnector extends BaseConnector implements IConnector, ChannelFutureListener {

	/**
	 * ����ͻ��˵��������Ϣת��,����ͬһ����������пͻ��˹���һ���߳�
	 */
//	private ExecutorService transferThread = Executors.newSingleThreadExecutor();
	
	private boolean manullyClosed = false;
	
	public GateConnector(ServerInfo serverInfo) {
		super(serverInfo);
	}

	public boolean connect() {
		if (serverInfo == null)
			return false;
		super.workerGroup = new NioEventLoopGroup(1);
		super.channelInitializer = new ConnectorChannelInitializer(serverInfo.getServerKey());
		boolean running = super.run();
		Channel channel = getChannel();
		ChannelFuture future = channel.closeFuture();
		future.addListeners(this);
		return running;
	}

	public void close() throws InterruptedException {
		manullyClosed = true;
		super.close();
	}

	public boolean reconnect() throws Exception {
		if (serverInfo == null) {
			throw new Exception("before reconnect, please do connect first.");
		}
//		if (reconnectCount >= 3) {
//			System.out.println("reconnect to many times. serverKey = " + serverInfo.getServerKey());
//			return false;
//		}
		System.out.println("reconnect to " + serverInfo.getServerKey() + " " + serverInfo.getHost() + " " + serverInfo.getConnectorPort());
		
//		reconnectCount++;
		boolean isRunning = connect();
		if (isRunning) {
//			reconnectCount = 0;
			manullyClosed = false;
			Channel channel = getChannel();
			ChannelFuture future = channel.closeFuture();
			future.addListeners(this);
		}
		return isRunning;
	}

	public Channel getChannel() {
		return super.getChannel();
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

//	public ExecutorService getTransferThread() {
//		return transferThread;
//	}

	@Override
	public void send(IIntranetMsg msg) {
//		transferThread.execute(() -> {
			getChannel().writeAndFlush(msg);
//		});
	}
	@Override
	public boolean isRunning() {
		if (getChannel() != null && getChannel().isActive()){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean isManullyClosed() {
		return manullyClosed;
	}

	@Override
	public void sendWithAccId(IntranetMsg msg, String accId) {
//		transferThread.execute(() -> {
			byte[] accIdbytes = accId.getBytes(Constants.defaultCharset);
			byte[] newData = new byte[msg.getLength() + accIdbytes.length + 1];
			newData[0] = (byte) accIdbytes.length;
			System.arraycopy(accIdbytes, 0, newData, 1, accIdbytes.length);
			System.arraycopy(msg.getData(), 0, newData, accIdbytes.length + 1, msg.getData().length);
			msg.setLength(newData.length);
			msg.setData(newData);
			getChannel().writeAndFlush(msg);
			// getChannel().write((byte) msg.getVirtualChannelIdBytes().length);
			// getChannel().write(msg.getVirtualChannelIdBytes());
			// getChannel().write(msg.getVersion());
			// getChannel().write(msg.getCrc());
			// getChannel().write(msg.getLength() + accIdbytes.length);
			// getChannel().write(msg.getGate());
			// getChannel().write(msg.getMain());
			// getChannel().write(msg.getSub());
			// getChannel().write((byte) accIdbytes.length);
			// getChannel().write(accIdbytes);
			// getChannel().writeAndFlush(msg.getData());
//		});
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		System.out.println("connector closed......");
		ConnectorManager.connectorClosed(this);
	}
}
