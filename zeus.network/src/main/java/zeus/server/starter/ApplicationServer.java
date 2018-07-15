package zeus.server.starter;

import java.net.InetSocketAddress;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import zeus.network.util.NetworkUtil;
import zeus.server.config.ITcpServerConfig;

/**
 * Ӧ�÷�����
 * @author songfeilong
 *
 */
public class ApplicationServer implements Runnable {

	protected ITcpServerConfig tcpServerConfig;

	protected Channel serverChannel;

	public ApplicationServer(ITcpServerConfig tcpServerConfig) {
		this.tcpServerConfig = tcpServerConfig;
	}

	public ITcpServerConfig getTcpServerConfig() {
		return tcpServerConfig;
	}

	public void setTcpServerConfig(ITcpServerConfig tcpServerConfig) {
		this.tcpServerConfig = tcpServerConfig;
	}

	public Channel getServerChannel() {
		return serverChannel;
	}

	public void setServerChannel(Channel serverChannel) {
		this.serverChannel = serverChannel;
	}

	public boolean isRunning() {
		if (serverChannel == null)
			return false;
		return serverChannel.isOpen() || serverChannel.isActive();
	}

	public void close() throws InterruptedException {
		if (serverChannel == null) {
			return;
		}
		serverChannel.close().sync();
	}

	public void sync() throws InterruptedException {
		if (serverChannel == null)
			return;
		serverChannel.closeFuture().sync();
	}

	private void setOption(ServerBootstrap b) {
		b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, tcpServerConfig.getConntimeout());
		// b.option(ChannelOption.TCP_NODELAY, tcpServerConfig.isNodelay());
		b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		b.childOption(ChannelOption.SO_KEEPALIVE, tcpServerConfig.isKeepalive());
		b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		b.childOption(ChannelOption.TCP_NODELAY, tcpServerConfig.isNodelay());
	}

	@Override
	public void run() {
		try {
			ServerBootstrap b = new ServerBootstrap();
			EventLoopGroup bossGroup, workerGroup;

			if (tcpServerConfig.isEpoll()) {
				bossGroup = new EpollEventLoopGroup(tcpServerConfig.getBossthread());
				workerGroup = new EpollEventLoopGroup(tcpServerConfig.getWorkerthread());
				b.channel(EpollServerSocketChannel.class);
			} else {
				bossGroup = new NioEventLoopGroup(tcpServerConfig.getBossthread());
				workerGroup = new NioEventLoopGroup(tcpServerConfig.getWorkerthread());
				b.channel(NioServerSocketChannel.class);
			}
			b.group(bossGroup, workerGroup);

			if (tcpServerConfig.getHandler() != null) {
				b.handler(tcpServerConfig.getHandler());
			}
			if (tcpServerConfig.getChildHandler() != null) {
				b.childHandler(tcpServerConfig.getChildHandler());
			}
			setOption(b);
			if (tcpServerConfig.getPort() <= 0){
				int port = NetworkUtil.getFreePort();
				tcpServerConfig.setPort(port);
			}
			serverChannel = b.bind(tcpServerConfig.getPort()).sync().channel();
			if (serverChannel.isActive()) {
				serverChannel.closeFuture().addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						// TODO Auto-generated method stub
						future.channel().parent().eventLoop().shutdownGracefully();
						future.channel().eventLoop().shutdownGracefully();
					}
				});
				InetSocketAddress inetSocketAddress = (InetSocketAddress) serverChannel.localAddress();
				tcpServerConfig.setPort(inetSocketAddress.getPort());
				System.out.println(
						tcpServerConfig.getName() + " start success. Listen at port " + tcpServerConfig.getPort());
			} else {
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
				System.err.println(tcpServerConfig.getName() + " start failed");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
	}
}
