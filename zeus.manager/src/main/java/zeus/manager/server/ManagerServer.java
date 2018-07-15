package zeus.manager.server;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.i5i58.data.channel.ServerInfo;
import com.i5i58.util.JedisUtils;
import com.i5i58.util.ZookeeperConfig;

import zeus.manager.config.IdleThead;
import zeus.manager.config.ManagerTaskPool;
import zeus.manager.handler.MyWebSocketServerConfig;
import zeus.manager.zookeeper.ZookeeperService;
import zeus.manager.zookeeper.ZookeeperService.IServerStateListener;
import zeus.network.manager.RemotePeerManager;
import zeus.network.threading.ITaskPool;
import zeus.network.util.NetworkUtil;
import zeus.server.starter.ApplicationServer;

@Component
public class ManagerServer implements IServerStateListener {
	private static ManagerServer intance;

	Logger logger = Logger.getLogger(getClass());

	private RemotePeerManager peerManager;

	// @Autowired
	private MyWebSocketServerConfig managerWebsocketServerConfig;

	ApplicationServer websocketManagerServer;
	// private ReadWriteLock anchorDataLock = new ReentrantReadWriteLock(true);

	private ServerInfo serverInfo;
	@Autowired
	private ManagerDataService managerDataService;

	ITaskPool taskPool;

	@Autowired
	ZookeeperConfig zkConfig;

	ZookeeperService zookeeperService;

	IdleThead idleTask;

	@Autowired
	private JedisUtils jedisUtils;

	public ManagerServer() {
		intance = this;
	}

	/**
	 * start a idle thread to avoid spring to exit.
	 */
	public void runIdleTask() {
		idleTask = new IdleThead(this);
		Thread thread = new Thread(idleTask);
		thread.start();

	}

	/**
	 * stop idle thread when liveServer started.
	 */
	public void stopIdleTask() {
		if (idleTask != null) {
			idleTask.setExit(true);
		}

	}

	/**
	 * callback at idle thread finishing.
	 */
	public void idleTaskFinished() {
		idleTask = null;
	}

	public void init() {
		peerManager = new RemotePeerManager();
		taskPool = new ManagerTaskPool(16, 16);

		managerWebsocketServerConfig = new MyWebSocketServerConfig();

		managerWebsocketServerConfig.setName("manager-websocket-server");

		managerWebsocketServerConfig.setBunisnessHandler(taskPool);
		managerWebsocketServerConfig.setManagerDataService(managerDataService);
		managerWebsocketServerConfig.init();

		
		websocketManagerServer = new ApplicationServer(managerWebsocketServerConfig);

		serverInfo = new ServerInfo();
		String host = NetworkUtil.getLocalHost();
		serverInfo.setHost(host);
		zookeeperService = new ZookeeperService();
		zookeeperService.init(zkConfig);
		// zookeeperService.setServerStateLisener(this);
		// zookeeperService.registerServer(serverInfo);
	}

	public static ManagerServer getInstance() {
		return intance;
	}

	public static JedisUtils getJedisUtils() {
		return intance.jedisUtils;
	}

	public void setJedisUtils(JedisUtils jedisUtils) {
		this.jedisUtils = jedisUtils;
	}

	public void run() {
		websocketManagerServer.run();
		//managerDataService.InitDataRPC();
		postServerStart();
	}

	public boolean isRunning() {
		return websocketManagerServer.isRunning();
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	public RemotePeerManager getRemotePeerManager() {
		return peerManager;
	}

	/*
	 * public void lockAnchorData(boolean isRead) { if (isRead) {
	 * anchorDataLock.readLock().lock(); } else {
	 * anchorDataLock.writeLock().unlock(); } }
	 * 
	 * public void unlockAnchorData(boolean isRead) { if (isRead) {
	 * anchorDataLock.readLock().unlock(); } else {
	 * anchorDataLock.writeLock().unlock(); } }
	 */

	public ZookeeperService getZookeeperService() {
		return zookeeperService;
	}

	public void setZookeeperService(ZookeeperService zookeeperService) {
		this.zookeeperService = zookeeperService;
	}

	@Override
	public void serverActive(ServerInfo serverInfo) {
		// managerServerConfig.setPort(serverInfo.getConnectorPort());
		// managerTcpServerConfig.setPort(serverInfo.getDirectTcpPort());
		// managerWebsocketServerConfig.setPort(serverInfo.getDirectWebsocketPort());
		// if (serverInfo.getServerKey() == null ||
		// serverInfo.getServerKey().isEmpty()) {
		// // shutdown
		// this.serverInfo = serverInfo;
		// try {
		// managerServer.close();
		// tcpManagerServer.close();
		// websocketManagerServer.close();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// } else {
		// if (this.isRunning()) {
		// System.out.println("server is running, ignore this change");
		// } else {
		// this.serverInfo = serverInfo;
		// this.run();
		// }
		// }
	}

	/**
	 * call after server started.
	 */
	public void postServerStart() {
		if (!this.isRunning()) {
			throw new RuntimeException("start manager server failed");
		}
		// stopIdleTask();

		String host = NetworkUtil.getLocalHost();
		int websocketPort = websocketManagerServer.getTcpServerConfig().getPort();

		serverInfo.setHost(host);
		serverInfo.setDirectWebsocketPort(websocketPort);

		zookeeperService.registerServer(serverInfo);
	}
}
