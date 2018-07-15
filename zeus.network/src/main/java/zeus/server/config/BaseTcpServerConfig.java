package zeus.server.config;

import java.util.LinkedHashMap;
import java.util.Map;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;

/**
 * tcp���������û��
 * 
 * @author songfeilong
 *
 */
public abstract class BaseTcpServerConfig implements ITcpServerConfig {

	protected String name = "";
	protected int port = 0;
	protected int bossthread = 1;
	protected int workerthread = 0;
	protected boolean epoll = false;
	protected boolean keepalive = true;
	protected boolean nodelay = true;
	protected boolean ssl = false;
	protected int conntimeout = 3000;
	protected boolean isGate = false;
	protected boolean useBusinessTask=false;
	protected int backlog = 1024;

	protected ChannelHandler handler;
	protected ChannelHandler childHandler;

	protected Map<ChannelOption<?>, Object> options = new LinkedHashMap<ChannelOption<?>, Object>();
	protected Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap<ChannelOption<?>, Object>();

	public void init() {
		options.put(ChannelOption.CONNECT_TIMEOUT_MILLIS, conntimeout);
		options.put(ChannelOption.TCP_NODELAY, nodelay);
		options.put(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		options.put(ChannelOption.SO_BACKLOG, backlog);
		childOptions.put(ChannelOption.SO_KEEPALIVE, keepalive);
		childOptions.put(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		childOptions.put(ChannelOption.TCP_NODELAY, nodelay);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBossthread() {
		return bossthread;
	}

	public void setBossthread(int bossthread) {
		this.bossthread = bossthread;
	}

	public int getWorkerthread() {
		return workerthread;
	}

	public void setWorkerthread(int workerthread) {
		this.workerthread = workerthread;
	}

	public boolean isEpoll() {
		return epoll;
	}

	public void setEpoll(boolean epoll) {
		this.epoll = epoll;
	}

	public boolean isKeepalive() {
		return keepalive;
	}

	public void setKeepalive(boolean keepalive) {
		this.keepalive = keepalive;
	}

	public boolean isNodelay() {
		return nodelay;
	}

	public void setNodelay(boolean nodelay) {
		this.nodelay = nodelay;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public boolean isGate() {
		return isGate;
	}

	public void setGate(boolean isGate) {
		this.isGate = isGate;
	}

	public int getConntimeout() {
		return conntimeout;
	}

	public void setConntimeout(int conntimeout) {
		this.conntimeout = conntimeout;
	}

	public ChannelHandler getHandler() {
		return handler;
	}

	public void setHandler(ChannelHandler handler) {
		this.handler = handler;
	}

	public ChannelHandler getChildHandler() {
		return childHandler;
	}

	public void setChildHandler(ChannelHandler childHandler) {
		this.childHandler = childHandler;
	}

	public Map<ChannelOption<?>, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<ChannelOption<?>, Object> options) {
		this.options = options;
	}

	public Map<ChannelOption<?>, Object> getChildOptions() {
		return childOptions;
	}

	public void setChildOptions(Map<ChannelOption<?>, Object> childOptions) {
		this.childOptions = childOptions;
	}

	public boolean isUseBusinessTask() {
		return useBusinessTask;
	}

	public void setUseBusinessTask(boolean useBusinessTask) {
		this.useBusinessTask = useBusinessTask;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}
}
