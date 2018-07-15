package zeus.server.config;

import java.util.Map;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;

public interface ITcpServerConfig {
	
	public String getName() ;

	public void setName(String name) ;

	public int getPort() ;

	public void setPort(int port);

	public int getBossthread() ;

	public void setBossthread(int bossthread) ;

	public int getWorkerthread() ;

	public void setWorkerthread(int workerthread) ;

	public boolean isEpoll();

	public void setEpoll(boolean epoll) ;

	public boolean isKeepalive() ;

	public void setKeepalive(boolean keepalive);

	public boolean isNodelay() ;

	public void setNodelay(boolean nodelay) ;

	public boolean isSsl() ;

	public void setSsl(boolean ssl) ;

	public int getConntimeout() ;

	public void setConntimeout(int conntimeout) ;

	public ChannelHandler getHandler() ;

	public void setHandler(ChannelHandler handler) ;

	public ChannelHandler getChildHandler() ;

	public void setChildHandler(ChannelHandler childHandler) ;

	public Map<ChannelOption<?>, Object> getOptions() ;

	public void setOptions(Map<ChannelOption<?>, Object> options) ;

	public Map<ChannelOption<?>, Object> getChildOptions() ;

	public void setChildOptions(Map<ChannelOption<?>, Object> childOptions);
}

