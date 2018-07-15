package zeus.network.util;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponseUtil {
	
	public static void sendResponse(ChannelHandlerContext ctx, boolean keepAlive, Object object){
		if (object == null){
			NetworkUtil.closeGracefully(ctx);
			return;
		}
		String msg = JSON.toJSONString(object);
		ByteBuf content = Unpooled.wrappedBuffer(msg.getBytes(Constants.defaultCharset));
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
		
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		
		if (keepAlive){
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			ctx.writeAndFlush(response);
		}else{
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	public static void sendResponse(ChannelHandlerContext ctx, boolean keepAlive, String msg){
		if (msg == null){
			NetworkUtil.closeGracefully(ctx);
			return;
		}
		ByteBuf content = Unpooled.wrappedBuffer(msg.getBytes(Constants.defaultCharset));
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
		
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		
		if (keepAlive){
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			ctx.writeAndFlush(response);
		}else{
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
	}
}
