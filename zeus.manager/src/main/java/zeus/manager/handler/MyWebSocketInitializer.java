package zeus.manager.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import zeus.manager.server.ManagerDataService;
import zeus.network.handler.IBusinessHandler;
import zeus.network.handler.WebsocketRequestHandler;
import zeus.network.threading.ITaskPool;
import zeus.network.util.Constants;

public class MyWebSocketInitializer extends ChannelInitializer<SocketChannel> { // 1

	private final SslContext sslCtx;
	private ITaskPool taskPool;
	private ManagerDataService managerDataService;

	public MyWebSocketInitializer(SslContext sslCtx, ITaskPool taskPool, ManagerDataService managerDataService) {
		this.sslCtx = sslCtx;
		this.taskPool = taskPool;
		this.managerDataService = managerDataService;
	}

	public SslContext getSslCtx() {
		return sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {// 2
		ChannelPipeline pipeline = ch.pipeline();
		if (sslCtx != null) {
			// SSLContext context = JdkSSLContextUtil.getServerContext();
			// SSLEngine engine = context.createSSLEngine();
			//
			// engine.setUseClientMode(false);
			//// engine.setNeedClientAuth(false);
			// pipeline.addLast(new SslHandler(engine));
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(64 * 1024));
		pipeline.addLast(new ChunkedWriteHandler());
		pipeline.addLast(new WebsocketRequestHandler(Constants.WEBSOCKET_PATH));
		pipeline.addLast(new WebSocketServerProtocolHandler(Constants.WEBSOCKET_PATH));
		pipeline.addLast(new MyTextWebSocketHandler(taskPool,managerDataService));

	}
}
