package zeus.network.handler;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import zeus.network.protocol.ClientMsgDecoder;
import zeus.network.protocol.ClientMsgEncoder;
import zeus.network.protocol.GateClientMsgDecoder;
import zeus.network.protocol.GateClientMsgEncoder;
import zeus.network.protocol.HeartBeatHandler;
import zeus.network.threading.ITaskPool;

/**
 * 
 * @author frank
 *
 */
public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {
	private final SslContext sslCtx;
	private IBusinessHandler businessHandler;
	private ITaskPool taskPool;
	private boolean isGate;
	private boolean useBusinessTask;

	public TcpChannelInitializer(SslContext sslCtx, IBusinessHandler businessHandler, boolean useBusinessTask, ITaskPool taskPool,
			boolean isGate) {
		this.sslCtx = sslCtx;
		this.businessHandler = businessHandler;
		this.useBusinessTask = useBusinessTask;
		this.taskPool = taskPool;
		this.isGate = isGate;
		System.out.println("isGate:" + isGate);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		if (sslCtx != null){
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}
		if (isGate) {
			pipeline.addLast(new GateClientMsgEncoder());
			pipeline.addLast(new GateClientMsgDecoder());
		} else {
			pipeline.addLast(new ClientMsgEncoder());
			pipeline.addLast(new ClientMsgDecoder());
		}
		pipeline.addLast(new IdleStateHandler(15, 5, 15, TimeUnit.SECONDS));
		pipeline.addLast(new HeartBeatHandler());
		pipeline.addLast(new TcpChannelHandler(businessHandler, useBusinessTask, taskPool));
	}

}
