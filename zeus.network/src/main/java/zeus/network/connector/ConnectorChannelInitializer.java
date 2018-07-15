package zeus.network.connector;

import java.util.concurrent.TimeUnit;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import zeus.network.protocol.IntranetMsgDecoder;
import zeus.network.protocol.IntranetMsgEncoder;

public class ConnectorChannelInitializer extends ChannelInitializer<SocketChannel> {
	private String serverKey;
	public ConnectorChannelInitializer(String serverKey) {
		this.serverKey = serverKey;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new IntranetMsgEncoder());
    	ch.pipeline().addLast(new IntranetMsgDecoder());
    	ch.pipeline().addLast(new IdleStateHandler(5, 5, 5, TimeUnit.SECONDS));
    	ch.pipeline().addLast(new ConnectorChannelHandler(serverKey));
	}

}
