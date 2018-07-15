

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TCPClient {
	private String clinetName = "TCPClient";
	private Channel channel;
	private String remoteHost;
    private int remotePort;
    private EventLoopGroup workerGroup;
    private ChannelInitializer<SocketChannel> channelInitializer;
    
    public TCPClient(String clinetName, String remoteHost, int remotePort, EventLoopGroup workerGroup, 
    		ChannelInitializer<SocketChannel> channelInitializer){
    	if (clinetName != null){
    		this.clinetName = clinetName;
    	}
    	this.remoteHost = remoteHost;
    	this.remotePort = remotePort;
    	this.workerGroup = workerGroup;
    	this.channelInitializer = channelInitializer;
    }
    
    public Channel getChannel(){
    	return channel;
    }
   
    public void close() throws InterruptedException{
    	if (channel == null){
    		return;
    	}
    	channel.close().sync();
    }
    public void sync() throws InterruptedException{
    	if (channel == null)
    		return;
    	channel.closeFuture().sync();
    }
    
    public boolean run() {
    	if (remoteHost == null || remoteHost.isEmpty() || remotePort == 0){
    		System.out.println(clinetName + " start failed. remote host : " + remoteHost + ", remote Port : " + remotePort);
    		return false;
    	}
    	try {
    		// Start the connection attempt.
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(channelInitializer);
            b.option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.connect(remoteHost, remotePort).sync();
            channel = f.channel();
            if (channel.isActive()){
            	System.out.println(clinetName + " connect to " + remoteHost + " : " + remotePort +" successfully");
            	channel.closeFuture().addListener(new ChannelFutureListener() {
    				@Override
    				public void operationComplete(ChannelFuture future) throws Exception {
    					future.channel().eventLoop().shutdownGracefully();
    				}
    			});
            }else{
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
