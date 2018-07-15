package zeus.manager.zookeeper;

import java.util.List;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.i5i58.data.channel.ServerInfo;
import com.i5i58.util.IZkConfig;
import com.i5i58.util.StringUtils;
import com.i5i58.util.ZKUtil;

@Component
public class ZookeeperService implements IZkDataListener, IZkStateListener {
	private static Logger logger = Logger.getLogger(ZookeeperService.class);

	public static final String managerServer = "/ManagerServer";

	private ServerInfo serverInfo;
	private ZKUtil zkUtil;

	private String nodeName;
	private String nodeData;
	private String nodePath;

	IServerStateListener stateListener;
	IActiveManagerServerListener activeManagerServerListener;

	/**
	 * listen to current server's state.
	 */
	public interface IServerStateListener {
		/**
		 * Ready to active current server
		 */
		public void serverActive(ServerInfo serverInfo);
	}

	/**
	 * listener to all active live servers. invoke this function when a live
	 * server removed from(added to) "/ActiveLiveServer/"
	 */
	public interface IActiveManagerServerListener {
		public void onActiveManagerServerChanged(List<ServerInfo> serverInfos);
	}

	public void setServerStateLisener(IServerStateListener stateListener) {
		this.stateListener = stateListener;
	}

	public void init(IZkConfig zkConfig) {
		zkUtil = new ZKUtil(zkConfig.getHosts(), zkConfig.getSessionTimeout(), zkConfig.getConnectionTimeout());
		System.out.println("Hl's  host is ==============" + zkConfig.getHosts());
		zkUtil.init();
		zkUtil.subscribeStateChanges(this);
		if (!zkUtil.exists(managerServer)) {
			zkUtil.createPersistent(managerServer, null);
		}
	}

	/*
	 * public void registerServer(ServerInfo serverInfo) { this.serverInfo =
	 * serverInfo; String key = this.serverInfo.getServerKey(); if (key == null
	 * || key.isEmpty()) { registerManagerServer(); } else {
	 * registerActiveServer(); } }
	 */

	public void registerServer(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
		registerManagerServer();

	}

	public void registerManagerServer() {
		if (nodePath != null) {
			deleteNode(nodePath);
		}

		nodeName = String.format("%s:%s:[innet]%s:[dirtcp]%s:[dws]%s", serverInfo.getServerKey(), serverInfo.getHost(),
				serverInfo.getConnectorPort(), serverInfo.getDirectTcpPort(), serverInfo.getDirectWebsocketPort());
		nodeData = JSON.toJSONString(serverInfo);
		nodePath = managerServer + "/" + nodeName;
		if (zkUtil.exists(nodePath)) {
			deleteNode(nodePath);
		}
		zkUtil.createEphemeral(nodePath, nodeData);
		zkUtil.subscribeDataChanges(nodePath, this);
	}
	
	public void deleteNode(String nodePath) {
		if (nodePath != null) {
			zkUtil.unsubscribeDataChanges(nodePath, this);
			zkUtil.deleteRecursive(nodePath);
		}
	}

	/**
	 * inherit from IZkStateListener
	 */
	@Override
	public void handleStateChanged(KeeperState state) throws Exception {
		logger.debug("zookeeper handleStateChanged " + state.toString());
		/*
		 * if (state == KeeperState.SyncConnected) { if (serverInfo != null) {
		 * registerServer(serverInfo); } }
		 */
	}

	/**
	 * inherit from IZkStateListener
	 */
	@Override
	public void handleNewSession() throws Exception {
		logger.debug("zookeeper session new");
	}

	/**
	 * inherit from IZkDataListener
	 */
	@Override
	public void handleDataChange(String dataPath, Object data) throws Exception {
		logger.debug("zookeeper handleDataChange changed " + dataPath + "--" + data.toString());
		if (!nodePath.equals(dataPath)) {
			logger.error("Path error. current nodePath = " + nodePath);
			return;
		}
		String nodeData = data.toString();
		ServerInfo newData = JSON.parseObject(nodeData, ServerInfo.class);
		// deleteNode(nodePath);
		if (stateListener != null) {
			stateListener.serverActive(newData);
		}
	}

	/**
	 * inherit from IZkDataListener
	 */
	@Override
	public void handleDataDeleted(String dataPath) throws Exception {
		logger.debug("zookeeper handleDataDeleted changed " + dataPath);
	}

	// /**
	// * inherit from IZkChildListener
	// */
	// @Override
	// public void handleChildChange(String parentPath, List<String>
	// currentChilds) throws Exception {
	// if (currentChilds == null){
	// return;
	// }
	// logger.debug("zookeeper handleChildChange changed parent: " + parentPath
	// + ", children: "
	// + currentChilds.toString());
	// List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
	// for (String c : currentChilds){
	// String data = zkUtil.readData(parentPath + "/" + c);
	// if (data == null || data.isEmpty()){
	// // delete server
	// continue;
	// }
	// ServerInfo s = JSON.parseObject(data, ServerInfo.class);
	// serverInfos.add(s);
	// }
	// if (activeLiveServerListener != null){
	// activeLiveServerListener.onActiveLiveServerChanged(serverInfos);
	// }
	// }

	@Override
	public void handleSessionEstablishmentError(Throwable arg0) throws Exception {
		// TODO Auto-generated method stub

	}
}
