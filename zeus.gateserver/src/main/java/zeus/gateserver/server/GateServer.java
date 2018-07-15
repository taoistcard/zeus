package zeus.gateserver.server;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.i5i58.data.channel.ServerInfo;
import com.i5i58.util.Constant;
import com.i5i58.util.JedisUtils;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.ZookeeperConfig;

import zeus.gateserver.data.AccountData;
import zeus.gateserver.data.PriDaoDelegate;
import zeus.gateserver.data.SecDaoDelegate;
import zeus.gateserver.zookeeper.ZookeeperService;
import zeus.gateserver.zookeeper.ZookeeperService.IActiveServerListener;
import zeus.network.connector.ConnectorManager;
import zeus.network.handler.IBusinessHandler;
import zeus.network.manager.RemotePeerManager;
import zeus.network.util.NetworkUtil;
import zeus.server.config.ITcpServerConfig;
import zeus.server.config.TcpServerConfig;
import zeus.server.config.WebsocketServerConfig;
import zeus.server.starter.ApplicationServer;

@Component
@ComponentScan(basePackages="zeus.server.config")
public class GateServer implements IActiveServerListener{
	private static GateServer globalInstance;

	@Autowired
	TcpServerConfig tcpServerConfig;

	@Autowired
	WebsocketServerConfig websocketServerConfig;

	@Autowired
	PriDaoDelegate priDaoDelegate;
	@Autowired
	SecDaoDelegate secDaoDelegate;
	
	@Autowired
	private JedisUtils jedisUtils;
	
	@Autowired
	JsonUtils jsonUtil;

	@Autowired
	AccountData accountUtils;

	// @Autowired
	// JedisUtils jedisUtils;

	ApplicationServer tcpServer;
	ApplicationServer websocketServer;

//	ITaskPool taskPool;
	IBusinessHandler businessHandler;

	/**
	 * all users connected to gateserver.
	 */
	RemotePeerManager globalPeerManager;

	
	@Autowired
	ZookeeperConfig zkConfig;
	ZookeeperService zookeeperService;
	
	private String gateServerKey; //网关服务器唯一标志
	
	public static GateServer getInstance() {
		assert globalInstance != null : "gate server has not been created.";
		return globalInstance;
	}

	public GateServer() {
		globalInstance = this;
		globalPeerManager = new RemotePeerManager();
		businessHandler = new GateBusinessHandler();
	}

	public void init() {
//		taskPool = new GateTaskPool(16, 16);
		//tcpServerConfig = new TcpServerConfig();
		//websocketServerConfig = new WebsocketServerConfig();
		
		//tcpServerConfig.setName("gate tcp server");
		//websocketServerConfig.setName("gate websocket server");
		
		tcpServerConfig.setBunisnessHandler(businessHandler, null);
		websocketServerConfig.setBunisnessHandler(businessHandler, null);
		tcpServerConfig.init();
		websocketServerConfig.init();

		tcpServer = new ApplicationServer(tcpServerConfig);
		websocketServer = new ApplicationServer(websocketServerConfig);
		
		zookeeperService = new ZookeeperService();
		zookeeperService.init(zkConfig);
		zookeeperService.subscribeActiveServers(this);;
	}

	public void run() {
		if (!businessHandler.beforeServerStart()){
			return;
		}
		tcpServer.run();
		websocketServer.run();
		
		addOnlineCount(0);
		businessHandler.afterServerStart();
		zookeeperService.onServerStart();
	}

//	public void setOnlineCount(int count){
//		String gateServerKey = getGateServerKey();	
//		String gateServerValue = jedisUtils.hget(Constant.HOT_GATESERVER_INFO, gateServerKey);
//		if (gateServerValue == null){		
//			JSONObject object = new JSONObject();
//			object.put("onlineCount", count);
//			GateServer.getJedisUtils().hset(Constant.HOT_GATESERVER_INFO, gateServerKey, object.toJSONString());
//		}else{
//			JSONObject object = JSON.parseObject(gateServerValue);
//			object.put("onlineCount", count);
//			GateServer.getJedisUtils().hset(Constant.HOT_GATESERVER_INFO, gateServerKey, object.toJSONString());
//		}
//	}
	
	public void addOnlineCount(int count){
		String gateServerKey = getGateServerKey();	
		String gateServerValue = jedisUtils.hget(Constant.HOT_GATESERVER_INFO, gateServerKey);
		if (gateServerValue == null){		
			JSONObject object = new JSONObject();
			if(count < 0){
				count = 0;
			}
			object.put("onlineCount", count);
			GateServer.getJedisUtils().hset(Constant.HOT_GATESERVER_INFO, gateServerKey, object.toJSONString());
		}else{
			JSONObject object = JSON.parseObject(gateServerValue);
			count = object.getIntValue("onlineCount") + count;
			if (count < 0){
				count = 0;
			}
			object.put("onlineCount", count);
			GateServer.getJedisUtils().hset(Constant.HOT_GATESERVER_INFO, gateServerKey, object.toJSONString());
		}
	}
	
	
	public String getGateServerKey() {
		if (gateServerKey == null){
			ITcpServerConfig tcpServerConfig = GateServer.getInstance().getTcpServerConfig();
			ITcpServerConfig websocketServerConfig = GateServer.getInstance().getWebSocketConfig();
			int tcpPort = tcpServerConfig.getPort();
			int wsPort = websocketServerConfig.getPort();
			String host = NetworkUtil.getLocalHost();
			gateServerKey = host + "_" + tcpPort + "_" + wsPort;
		}
		return gateServerKey;
	}

	public TcpServerConfig getTcpServerConfig(){
		return tcpServerConfig;
	}
	
	public WebsocketServerConfig getWebSocketConfig(){
		return websocketServerConfig;
	}
	
	public RemotePeerManager getRemotePeerManager() {
		return globalPeerManager;
	}

	public PriDaoDelegate getPriDaoDelegate() {
		return priDaoDelegate;
	}

	public SecDaoDelegate getSecDaoDelegate() {
		return secDaoDelegate;
	}

	public static JedisUtils getJedisUtils() {
		return globalInstance.jedisUtils;
	}

	public void setJedisUtils(JedisUtils jedisUtils) {
		this.jedisUtils = jedisUtils;
	}

	public static JsonUtils getJsonUtil() {
		return globalInstance.jsonUtil;
	}

	public void setJsonUtil(JsonUtils jsonUtil) {
		this.jsonUtil = jsonUtil;
	}

	public AccountData getAccountUtils() {
		return accountUtils;
	}

	@Override
	public void activeLiveServerChanged(Map<String, ServerInfo> serverInfos) {
		ConnectorManager.updateServerList(serverInfos);
	}

	// public JedisUtils getJedisUtils() {
	// return jedisUtils;
	// }
}
