
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;

import com.alibaba.fastjson.JSON;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import zeus.network.protocol.GateCmd;
import zeus.network.protocol.TextClientMsg;
import zeus.network.util.Constants;
import zeus.network.util.JdkSSLContextUtil;
import zeus.network.util.NettySslContextUtil;

public final class WebSocketClient implements Runnable {

	static final String URL = System.getProperty("url", "wss://127.0.0.1:8500/websocket");
	private Channel channel;

	public void waitSync() {
		if (channel == null) {
			return;
		}
		if (!channel.isActive()) {
			return;
		}
		try {
			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			URI uri = new URI(URL);
			String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
			final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
			final int port;
			if (uri.getPort() == -1) {
				if ("ws".equalsIgnoreCase(scheme)) {
					port = 80;
				} else if ("wss".equalsIgnoreCase(scheme)) {
					port = 443;
				} else {
					port = -1;
				}
			} else {
				port = uri.getPort();
			}
			if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
				System.err.println("Only WS(S) is supported.");
				return;
			}

			boolean ssl = "wss".equalsIgnoreCase(scheme);
			final SslContext sslCtx;
			if (ssl) {
				try {
					sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
				} catch (SSLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
//				sslCtx = NettySslContextUtil.getClientContext();
			} else {
				sslCtx = null;
			}

			// Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08
			// or V00.
			// If you change it to V00, ping is not supported and remember to
			// change
			// HttpResponseDecoder to WebSocketHttpResponseDecoder in the
			// pipeline.

			WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri,
					WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
			final WebSocketClientHandler handler = new WebSocketClientHandler(handshaker);

			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) {
					ChannelPipeline p = ch.pipeline();
					if (sslCtx != null) {
//						SSLContext context = JdkSSLContextUtil.getClientContext();
//						SSLEngine engine = context.createSSLEngine();
//						
//						engine.setUseClientMode(true);
//						
//						p.addLast(new SslHandler(engine));
//						
						p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
					}
					p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192),
							WebSocketClientCompressionHandler.INSTANCE, handler);
				}
			});

			channel = b.connect(uri.getHost(), port).sync().channel();
			handler.handshakeFuture().sync();

//			String enter = "{\"cmd\":\"enter\",\"params\":{\"accId\":\"a3a4900dfb3141a8984650a71a5f38a4\",\"token\":\"4ce70a8bf08f8cba879d190f8135e9cc\",\"device\":\"3\",\"cId\":\"0248bad961fc455492bc66dea85f28a1\",\"giftVersion\":\"3bf13d71ce2d4677agt1654d1ae5ee8a\",\"mountVersion\":\"3bf13d71ce2d4677agt1654d1ae5ee8a\"}}";
			
			// BufferedReader console = new BufferedReader(new
			// InputStreamReader(System.in));
			String msg1 = "hello i am a websocket message";
			String msg2 = "hello i am another websocket message";
			int count = 0;
			String sRoomId = String.format("%20d", 10000);

			byte[] bytesF = "{\"cmd\":\"enter\",\"name\":\"frank\",\"age\":33}".getBytes(Charset.forName("utf-8"));
			byte[] totalBytes = new byte[66 + bytesF.length];
			totalBytes[0] = (byte) 32;
			byte[] accid = "d9e40973589a42289d54bdb8d25f73fa".getBytes(Constants.defaultCharset);
			totalBytes[33] = (byte) 32;
			byte[] token = "d9e40973589a42289d54bdb8d25f73fa".getBytes(Constants.defaultCharset);
			System.arraycopy(accid, 0, totalBytes, 1, 32);
			System.arraycopy(token, 0, totalBytes, 34, 32);
			System.arraycopy(bytesF, 0, totalBytes, 66, bytesF.length);
			String strF = Base64.getEncoder().encodeToString(totalBytes);
			TextClientMsg msgF = new TextClientMsg();
			msgF.setVersion((byte) 0x01);
			msgF.setCrc((byte) 0x02);
			msgF.setGate(GateCmd.AUTH_TRANS_FIRST);
			msgF.setMain((short) 111);
			msgF.setSub((short) 112);
			msgF.setServerKey("4158bd2d6b0a46c6890d175851e750b8");
			msgF.setData(strF);
			WebSocketFrame frameF = new TextWebSocketFrame(JSON.toJSONString(msgF));
			channel.writeAndFlush(frameF);
			System.out.println("sending msg " + JSON.toJSONString(msgF));

			Thread.sleep(3000);
			while (true) {
				 if (channel == null || !channel.isActive())
					 break;
				// String msg = "";
				// String tmp;
				// if (++count % 2 == 0){
				// tmp = msg2;
				// }else{
				// tmp = msg1;
				// }
				// for (int i=0; i<1; ++i){
				// msg += tmp;
				// }
				// System.out.println("sending msg length " + msg.length());

				byte[] bytes = "{\"cmd\":\"gift\",\"name\":\"frank\",\"age\":33}".getBytes(Charset.forName("utf-8"));
				String str = Base64.getEncoder().encodeToString(bytes);
				TextClientMsg msg = new TextClientMsg();
				msg.setVersion((byte) 0x01);
				msg.setCrc((byte) 0x02);
				msg.setGate(GateCmd.TRANS);
				msg.setMain((short) 111);
				msg.setSub((short) 112);
				msg.setServerKey("4158bd2d6b0a46c6890d175851e750b8");
				msg.setData(str);

				System.out.println("sending msg 1" + JSON.toJSONString(msg));

				WebSocketFrame frame = new TextWebSocketFrame(JSON.toJSONString(msg));
				channel.writeAndFlush(frame);
				Thread.sleep(2000);
				// String msg = console.readLine();
				// if (msg == null) {
				// break;
				// } else if ("bye".equals(msg.toLowerCase())) {
				// ch.writeAndFlush(new CloseWebSocketFrame());
				// ch.closeFuture().sync();
				// break;
				// } else if ("ping".equals(msg.toLowerCase())) {
				// WebSocketFrame frame = new
				// PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[] { 8, 1,
				// 8, 1 }));
				// ch.writeAndFlush(frame);
				// } else {
				// WebSocketFrame frame = new TextWebSocketFrame(msg);
				// ch.writeAndFlush(frame);
				// }
			}
//		} catch (SSLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}
