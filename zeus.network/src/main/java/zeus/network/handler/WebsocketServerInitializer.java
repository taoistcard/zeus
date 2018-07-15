package zeus.network.handler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import zeus.network.threading.ITaskPool;
import zeus.network.util.Constants;
import zeus.network.util.JdkSSLContextUtil;

/**
 * 
 * websocketͨ����ʼ����
 * 
 * @author songfeilong
 *
 */
public class WebsocketServerInitializer extends ChannelInitializer<SocketChannel> { // 1

	private final SslContext sslCtx;
	private IBusinessHandler businessHandler;
	private ITaskPool taskPool;
	private boolean isGate;
	private boolean useBusinessTask;

	public WebsocketServerInitializer(SslContext sslCtx, IBusinessHandler businessHandler, boolean useBusinessTask,
			ITaskPool taskPool, boolean isGate) {
		this.sslCtx = sslCtx;
		this.businessHandler = businessHandler;
		this.taskPool = taskPool;
		this.isGate = isGate;
		this.useBusinessTask = useBusinessTask;
	}

	public SslContext getSslCtx() {
		return sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {// 2
		ChannelPipeline pipeline = ch.pipeline();
		if (sslCtx != null){
//			SSLContext context = JdkSSLContextUtil.getServerContext();
//			SSLEngine engine = context.createSSLEngine();
//			
//			engine.setUseClientMode(false);
////			engine.setNeedClientAuth(false);
//			pipeline.addLast(new SslHandler(engine));
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(64 * 1024));
		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new WebsocketRequestHandler(Constants.WEBSOCKET_PATH));
		pipeline.addLast(new WebSocketServerProtocolHandler(Constants.WEBSOCKET_PATH));
		pipeline.addLast(new TextWebSocketFrameHandler(businessHandler, useBusinessTask, taskPool, isGate));

	}
}
