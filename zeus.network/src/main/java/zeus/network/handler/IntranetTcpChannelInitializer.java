package zeus.network.handler;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import zeus.network.protocol.IntranetHeartBeatHandler;
import zeus.network.protocol.IntranetMsgDecoder;
import zeus.network.protocol.IntranetMsgEncoder;
import zeus.network.threading.ITaskPool;

/**
 * ������ͨ����ʼ����
 * @author frank
 *
 */
public class IntranetTcpChannelInitializer extends ChannelInitializer<SocketChannel> {

	private IBusinessHandler businessHandler;
	private ITaskPool taskPool;
	public IntranetTcpChannelInitializer(IBusinessHandler businessHandler, ITaskPool taskPool) {
		this.businessHandler = businessHandler;
		this.taskPool = taskPool;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new IntranetMsgEncoder());
		ch.pipeline().addLast(new IntranetMsgDecoder());
		ch.pipeline().addLast(new IdleStateHandler(15, 5, 15, TimeUnit.SECONDS));
		ch.pipeline().addLast(new IntranetHeartBeatHandler());
		ch.pipeline().addLast(new IntranetChannelHandler(businessHandler, taskPool));
	}

}
