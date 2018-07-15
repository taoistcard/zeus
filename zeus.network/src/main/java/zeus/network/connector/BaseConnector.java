package zeus.network.connector;

import org.apache.log4j.Logger;

import com.i5i58.data.channel.ServerInfo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

//�������̨ҵ���������������
public class BaseConnector {
	private Logger logger = Logger.getLogger(getClass());
	protected ServerInfo serverInfo;
	private Channel channel;
	protected EventLoopGroup workerGroup;
	protected ChannelInitializer<SocketChannel> channelInitializer;

	public BaseConnector(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	public Channel getChannel() {
		return channel;
	}

	public void close() throws InterruptedException {
		if (channel == null) {
			return;
		}
		channel.close().sync();
		channel = null;
	}

	public void sync() throws InterruptedException {
		if (channel == null)
			return;
		channel.closeFuture().sync();
		channel = null;
	}

	public boolean run() {
		if (serverInfo.getHost() == null || serverInfo.getHost().isEmpty() || serverInfo.getConnectorPort() == 0) {
			logger.error(
					String.format("%s start failed. remote host : %s, remote Port : %s",
							serverInfo.getConnectorName(), serverInfo.getHost(), serverInfo.getConnectorPort()));
			return false;
		}
		try {
			// Start the connection attempt.
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.handler(channelInitializer);
			b.option(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.connect(serverInfo.getHost(), serverInfo.getConnectorPort()).sync();
			channel = f.channel();
			if (channel.isActive()) {
				logger.info(
						String.format("%s connect to %s:%s  successfully",
								serverInfo.getConnectorName(), serverInfo.getHost(), serverInfo.getConnectorPort()));
				channel.closeFuture().addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						future.channel().eventLoop().shutdownGracefully();
						logger.info("connector closed gracefully.");
					}
				});
			} else {
				workerGroup.shutdownGracefully();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {

		}
		if (channel != null && channel.isActive())
			return true;
		else
			return false;
	}
}
