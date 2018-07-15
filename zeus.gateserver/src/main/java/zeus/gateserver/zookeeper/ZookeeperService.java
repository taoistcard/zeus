package zeus.gateserver.zookeeper;

/**
 * subscribe active live servers. If any live server online/offline, 
 * we will receive a notification with all servers online now.
 * */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.i5i58.data.channel.ServerInfo;
import com.i5i58.util.IZkConfig;
import com.i5i58.util.ZKUtil;

import zeus.gateserver.server.GateServer;
import zeus.network.util.NetworkUtil;
import zeus.server.config.ITcpServerConfig;

public class ZookeeperService implements IZkChildListener, IZkDataListener {
	private static Logger logger = Logger.getLogger(ZookeeperService.class);

	public static final String activeRoomServer = "/ActiveLiveServer";
	public static final String gateServer = "/GateServer";

	private ZKUtil zkUtil;
	
//	private ITcpServerConfig tcpServerConfig;
//	private ITcpServerConfig websocketServerConfig;
	
	private IActiveServerListener activeServerListener;

	/**
	 * listener to all active live servers.
	 * invoke this function when a live server removed from(added to) "/ActiveLiveServer/"
	 * */
	public interface IActiveServerListener{
		public void activeLiveServerChanged(Map<String, ServerInfo> serverInfos);
	}
	
	public void subscribeActiveServers(IActiveServerListener activeServerListener){
		this.activeServerListener = activeServerListener;
	}
	
	public void init(IZkConfig zkConfig) {
		zkUtil = new ZKUtil(zkConfig.getHosts(), zkConfig.getSessionTimeout(), zkConfig.getConnectionTimeout());
		zkUtil.init();
		if (!zkUtil.exists(activeRoomServer)){
			zkUtil.createPersistent(activeRoomServer, null);
		}

		try {
			zkUtil.subscribeChildChanges(activeRoomServer, this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onServerStart(){
//		this.tcpServerConfig = tcpServerConfig;
//		this.websocketServerConfig = websocketServerConfig;
		
		readAllActiveServer();
		publishGateServer();
	}
	
	public void deleteNode(String nodePath) {
		if (nodePath != null) {
			zkUtil.unsubscribeDataChanges(nodePath, this);
			zkUtil.deleteRecursive(nodePath);
		}
	}
	
	/**
	 * 向zookeeper注册当前网关节点
	 * */
	public void publishGateServer(){
		ITcpServerConfig tcpServerConfig = GateServer.getInstance().getTcpServerConfig();
		ITcpServerConfig websocketServerConfig = GateServer.getInstance().getWebSocketConfig();
		
		int tcpPort = tcpServerConfig.getPort();
		int wsPort = websocketServerConfig.getPort();
		String host = NetworkUtil.getLocalHost();
		JSONObject object = new JSONObject();
		object.put("host", host);
		object.put("tcpPort", tcpPort);
		object.put("wsPort", wsPort);
		String name = host + "_" + tcpPort +"_" + wsPort;
		String path = gateServer + "/" + name;
		String data = object.toJSONString();
		if (zkUtil.exists(path)){
			deleteNode(path);
		}
		zkUtil.createEphemeral(path, data);
		
		zkUtil.unsubscribeDataChanges(path, this);
		zkUtil.subscribeDataChanges(path, this);
	}
	/**
	 * 获取当前激活的所有业务服务器
	 * */
	public void readAllActiveServer(){
		if (!zkUtil.exists(activeRoomServer))
			return;
		List<String> currentChilds = zkUtil.getChildren(activeRoomServer);
		fireChildren(activeRoomServer, currentChilds);
	}
	
	/**
	 * inherit from IZkChildListener
	 */
	@Override
	public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
		if (currentChilds == null){
			return;
		}
		logger.debug("zookeeper handleChildChange changed parent: " + parentPath + ", children: "
				+ currentChilds.toString());
		fireChildren(parentPath, currentChilds);
	}
	
	public void fireChildren(String parentPath, List<String> currentChilds){
		Map<String, ServerInfo> serverInfos = new HashMap<String, ServerInfo>();
		for (String c : currentChilds){
			String data = zkUtil.readData(parentPath + "/" + c);
			if (data == null || data.isEmpty()){
				// delete server
				continue;
			}
			ServerInfo s = JSON.parseObject(data, ServerInfo.class);
			serverInfos.put(s.getServerKey(), s);
		}
		if (activeServerListener != null){
			activeServerListener.activeLiveServerChanged(serverInfos);
		}
	}

	@Override
	public void handleDataChange(String dataPath, Object data) throws Exception {
		
	}

	@Override
	public void handleDataDeleted(String dataPath) throws Exception {
		// 关闭网关服务器
	}
}
