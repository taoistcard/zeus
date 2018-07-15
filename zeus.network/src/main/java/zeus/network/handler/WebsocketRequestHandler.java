package zeus.network.handler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import zeus.network.util.Constants;

/**
 * @author songfeilong
 *
 */
public class WebsocketRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> { // 1
	Logger logger = Logger.getLogger(getClass());
	
	private final String wsUri;
	private static final File INDEX;

	static {
		URL location = WebsocketRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			String path = location.toURI() + "websocket.html";
			path = !path.contains("file:") ? path : path.substring(5);
			INDEX = new File(path);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Unable to locate WebsocketChatClient.html", e);
		}
	}

	public WebsocketRequestHandler(String wsUri) {
		this.wsUri = wsUri;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		if (wsUri.equalsIgnoreCase(request.uri())) {
			ctx.fireChannelRead(request.retain()); 
		} else {
			ctx.close();
			return;
//			if (HttpUtil.is100ContinueExpected(request)) {
//				send100Continue(ctx);
//			}
//
//			RandomAccessFile file = new RandomAccessFile(INDEX, "r");
//
//			HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
//			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
//
//			boolean keepAlive = HttpUtil.isKeepAlive(request);
//
//			if (keepAlive) {
//				response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
//				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//			}
//			ctx.write(response);
//
//			if (ctx.pipeline().get(SslHandler.class) == null) {
//				ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
//			} else {
//				ctx.write(new ChunkedNioFile(file.getChannel()));
//			}
//			ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
//			if (!keepAlive) {
//				future.addListener(ChannelFutureListener.CLOSE);
//			}
//
//			file.close();
		}
	}

	private static void send100Continue(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
		ctx.writeAndFlush(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		String msg = cause.getMessage();
		if (cause instanceof IOException){
			if ( msg.compareTo(Constants.CLOSED_BY_REMOTE) == 0){
//				logger.error("客户端断开");
			}else{
				logger.error(cause.getMessage());
			}
		}else{
			logger.error(cause.getMessage());	
		}
		if (cause instanceof WebSocketHandshakeException) {
			ctx.fireExceptionCaught(cause);
		}else{
			ctx.close();
		}
	}
}
