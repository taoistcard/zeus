package zeus.live.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.i5i58.data.channel.ChannelAuth;
import com.i5i58.data.channel.ServerInfo;
import com.i5i58.util.AuthVerify;
import com.i5i58.util.JedisUtils;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.MountPresentUtil;
import com.i5i58.util.ZookeeperConfig;

import zeus.live.config.IdleThead;
import zeus.live.config.LiveTaskPool;
import zeus.live.data.HotDaoDelegate;
import zeus.live.data.PriDaoDelegate;
import zeus.live.data.SecDaoDelegate;
import zeus.live.zookeeper.ZookeeperService;
import zeus.live.zookeeper.ZookeeperService.IServerStateListener;
import zeus.network.handler.IBusinessHandler;
import zeus.network.manager.RemotePeerManager;
import zeus.network.threading.ITaskPool;
import zeus.network.util.NetworkUtil;
import zeus.server.config.HttpServerConfig;
import zeus.server.config.IntranetTcpServerConfig;
import zeus.server.config.TcpServerConfig;
import zeus.server.config.WebsocketServerConfig;
import zeus.server.starter.ApplicationServer;

@Component
public class LiveServer implements IServerStateListener {
	private static LiveServer intance;

	Logger logger = Logger.getLogger(getClass());

	private RemotePeerManager peerManager;

	// @Autowired
	private IntranetTcpServerConfig liveServerConfig;

	// @Autowired
	private TcpServerConfig liveTcpServerConfig;

	// @Autowired
	private WebsocketServerConfig liveWebsocketServerConfig;

	private HttpServerConfig liveHttpServerConfig;

//	@Autowired
//	private PriDaoDelegate priDaoDelegate;
//	@Autowired
//	private SecDaoDelegate secDaoDelegate;
//	@Autowired
//	private HotDaoDelegate hotDaoDelegate;
//
//	@Autowired
//	@Qualifier("entityManagerPrimary")
//	private EntityManager entityManager;
//
//	@Autowired
//	private JedisUtils jedisUtils;
//
//
//	@Autowired
//	AuthVerify<ChannelAuth> channelAdminAuthVerify;
//
//	@Autowired
//	JsonUtils jsonUtil;
//
//	@Autowired
//	MountPresentUtil mountPresentUtil;

	@Autowired
	private IBusinessHandler businessHandler;

	/**
	 * shared between multi-thread process
	 * 
	 */
	ConcurrentHashMap<Object, Object> customRoomData;

	ApplicationServer liveServer;
	ApplicationServer tcpLiveServer;
	ApplicationServer websocketLiveServer;
	ApplicationServer httpLiveServer;

	private ReadWriteLock anchorDataLock = new ReentrantReadWriteLock(true);

	private ServerInfo serverInfo;

	ITaskPool taskPool;

	@Autowired
	ZookeeperConfig zkConfig;

	ZookeeperService zookeeperService;

	IdleThead idleTask;

	public LiveServer() {
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
		taskPool = new LiveTaskPool(16, 16);
		liveServerConfig = new IntranetTcpServerConfig();
		liveTcpServerConfig = new TcpServerConfig();
		liveWebsocketServerConfig = new WebsocketServerConfig();
		liveHttpServerConfig = new HttpServerConfig();

//		businessHandler = new GLiveBusinessHandler(processor);

		liveServerConfig.setName("live-connector-server");
		liveTcpServerConfig.setName("live-tcp-server");
		liveWebsocketServerConfig.setName("live-websocket-server");
		liveHttpServerConfig.setName("live-http-server");

		liveServerConfig.setBunisnessHandler(businessHandler, taskPool);
		liveServerConfig.init();
		liveTcpServerConfig.setBunisnessHandler(businessHandler, taskPool);
		liveTcpServerConfig.init();
		liveWebsocketServerConfig.setBunisnessHandler(businessHandler, taskPool);
		liveWebsocketServerConfig.init();
		liveHttpServerConfig.setBunisnessHandler(businessHandler);
		liveHttpServerConfig.init();

		liveServer = new ApplicationServer(liveServerConfig);
		tcpLiveServer = new ApplicationServer(liveTcpServerConfig);
		websocketLiveServer = new ApplicationServer(liveWebsocketServerConfig);
		httpLiveServer = new ApplicationServer(liveHttpServerConfig);

		serverInfo = new ServerInfo();
		String host = NetworkUtil.getLocalHost();
		serverInfo.setHost(host);

		zookeeperService = new ZookeeperService();
		zookeeperService.init(zkConfig);
		zookeeperService.setServerStateLisener(this);
		zookeeperService.registerServer(serverInfo);
	}

	public static LiveServer getInstance() {
		return intance;
	}

	public void run() {
		if (!businessHandler.beforeServerStart())
			return;
		liveServer.run();
		tcpLiveServer.run();
		websocketLiveServer.run();
		httpLiveServer.run();

		businessHandler.afterServerStart();
		postServerStart();
	}

	public boolean isRunning() {
		return liveServer.isRunning() && tcpLiveServer.isRunning() && websocketLiveServer.isRunning();
	}

//	public static PriDaoDelegate getPriDaoDelegate() {
//		return intance.priDaoDelegate;
//	}
//
//	public static SecDaoDelegate getSecDaoDelegate() {
//		return intance.secDaoDelegate;
//	}
//
//	public static HotDaoDelegate getHotDaoDelegate() {
//		return intance.hotDaoDelegate;
//	}
//
//	public static EntityManager getEntityManager() {
//		return intance.entityManager;
//	}
//
//	
//	public static AuthVerify<ChannelAuth> getChannelAdminAuthVerify() {
//		return intance.channelAdminAuthVerify;
//	}
//
//	public static JedisUtils getJedisUtils() {
//		return intance.jedisUtils;
//	}
//
//	public static JsonUtils getJsonUtil() {
//		return intance.jsonUtil;
//	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

//	public  static MountPresentUtil getMountPresentUtil() {
//		return intance.mountPresentUtil;
//	}

	public RemotePeerManager getRemotePeerManager() {
		return peerManager;
	}

	public void lockAnchorData(boolean isRead) {
		if (isRead) {
			anchorDataLock.readLock().lock();
		} else {
			anchorDataLock.writeLock().unlock();
		}
	}

	public void unlockAnchorData(boolean isRead) {
		if (isRead) {
			anchorDataLock.readLock().unlock();
		} else {
			anchorDataLock.writeLock().unlock();
		}
	}

	public ConcurrentHashMap<Object, Object> getCustomRoomData() {
		return customRoomData;
	}

	public ZookeeperService getZookeeperService() {
		return zookeeperService;
	}

	public void setZookeeperService(ZookeeperService zookeeperService) {
		this.zookeeperService = zookeeperService;
	}

	@Override
	public void serverActive(ServerInfo serverInfo) {
		liveServerConfig.setPort(serverInfo.getConnectorPort());
		liveTcpServerConfig.setPort(serverInfo.getDirectTcpPort());
		liveWebsocketServerConfig.setPort(serverInfo.getDirectWebsocketPort());
		if (serverInfo.getServerKey() == null || serverInfo.getServerKey().isEmpty()) {
			// shutdown
			this.serverInfo = serverInfo;
			try {
				liveServer.close();
				tcpLiveServer.close();
				websocketLiveServer.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			if (this.isRunning()){
				System.out.println("server is running, ignore this change");
			}else{
				this.serverInfo = serverInfo;
				this.run();
			}
		}
	}

	/**
	 * call after server started.
	 */
	public void postServerStart() {
		if (!this.isRunning()) {
			throw new RuntimeException("start live server failed");
		}
		stopIdleTask();

		String host = NetworkUtil.getLocalHost();
		int livePort = liveServer.getTcpServerConfig().getPort();
		int tpcPort = tcpLiveServer.getTcpServerConfig().getPort();
		int websocketPort = websocketLiveServer.getTcpServerConfig().getPort();
		int httpPort = httpLiveServer.getTcpServerConfig().getPort();

		serverInfo.setHost(host);
		serverInfo.setConnectorPort(livePort);
		serverInfo.setDirectTcpPort(tpcPort);
		serverInfo.setDirectWebsocketPort(websocketPort);
		serverInfo.setHttpPort(httpPort);

		zookeeperService.registerServer(serverInfo);
	}
}
