package zeus.network.handler;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import zeus.network.manager.IRemotPeer;
import zeus.network.protocol.HttpClientMsg;
import zeus.network.threading.HttpReadTask;
import zeus.network.threading.TaskQueue;
import zeus.network.util.Constants;

@io.netty.channel.ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	Logger logger = Logger.getLogger(getClass());
	
	private static final ByteBuf NOT_AUTH = Unpooled.copiedBuffer("{\"code\":\"not authed\",\"msg\":\"用户未认证\"}".getBytes(Constants.defaultCharset));
	private static final ByteBuf TEST_BUSINESS = Unpooled.copiedBuffer("test business".getBytes(Constants.defaultCharset));

	private IBusinessHandler businessHandler;

	public HttpRequestHandler(IBusinessHandler businessHandler) {
		this.businessHandler = businessHandler;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		String msg = cause.getMessage();
		if (cause instanceof IOException){
			if ( msg.compareTo(Constants.CLOSED_BY_REMOTE) == 0){
				
			}else{
				logger.error(cause.getMessage());
			}
		}else{
			logger.error(cause.getMessage());	
		}
		ctx.close();
		businessHandler.onExceptionCaught(ctx, cause);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		System.out.println("Full Http Request : \r\n" + msg.toString());
		HttpRequest req = (HttpRequest) msg;

		String content = msg.content().toString(Constants.defaultCharset);
		System.out.println("content : " + content);
		
		if (HttpUtil.is100ContinueExpected(req)) {
			ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
		}

		HttpClientMsg httpClientMsg = new HttpClientMsg(msg);
		businessHandler.httpAuth(ctx, httpClientMsg, new HttpAuthCallback() {
			@Override
			public void onCallback(ChannelHandlerContext httpCtx, IRemotPeer localPeer, HttpClientMsg httpClientMsg) {
				if (localPeer == null){
					sendNotAuthResponse(httpCtx, httpClientMsg);
				}else{
					TaskQueue queue = localPeer.getTaskQueue();
					if (queue != null){
						HttpReadTask task = new HttpReadTask(businessHandler, httpCtx, httpClientMsg, localPeer);
						queue.addTask(task);
					}else{
						sendNotAuthResponse(httpCtx, httpClientMsg);	
					}
				}
			}
		});
	}

	public void sendNotAuthResponse(ChannelHandlerContext ctx, HttpClientMsg msg) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.copiedBuffer(NOT_AUTH));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
		response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

		if (msg.isKeepAlive()) {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			ctx.writeAndFlush(response);
		} else {
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void testBusinessResponse(ChannelHandlerContext ctx, FullHttpRequest msg) {
		boolean keepAlive = HttpUtil.isKeepAlive(msg);
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(TEST_BUSINESS));
		HttpHeaders headers = response.headers();
		headers.set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
		headers.setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

		if (keepAlive) {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			ctx.writeAndFlush(response);
		} else {
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
	}
}
