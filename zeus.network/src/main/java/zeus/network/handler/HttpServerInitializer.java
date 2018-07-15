package zeus.network.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
	private SslContext sslCtx = null;
	private IBusinessHandler bunisnessHandler;

    public HttpServerInitializer(SslContext sslCtx, IBusinessHandler bunisnessHandler) {
        this.sslCtx = sslCtx;
        this.bunisnessHandler = bunisnessHandler;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1024*1024));
        p.addLast(new HttpContentCompressor());
        p.addLast(new HttpContentDecompressor());
        p.addLast(new HttpRequestHandler(bunisnessHandler));
    }
}
