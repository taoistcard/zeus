package zeus.manager.server;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.apache.zookeeper.KeeperException;
import org.aspectj.weaver.ast.Var;
import org.springframework.stereotype.Component;

import com.i5i58.data.channel.ServerInfo;
import com.i5i58.util.ZKUtil;

import zeus.manager.data.ConsumerData;
import zeus.manager.data.ConsumerListItem;
import zeus.manager.data.ProviderData;
import zeus.manager.data.ProviderListItem;
import zeus.manager.data.ResponseData;
import zeus.manager.data.ResultDataSet;
import zeus.network.manager.IRemotPeer;
import zeus.network.manager.WsRemotePeer;
import zeus.network.protocol.GateCmd;
import zeus.network.manager.RemotePeerManager;

@Component
public class ManagerDataService {

	private List<ConsumerListItem> consumersListRPC = new ArrayList<ConsumerListItem>();;
	private List<ProviderListItem> providersListRPC = new ArrayList<ProviderListItem>();
	private List<String> consumerList = new ArrayList<>();
	private List<String> providerList = new ArrayList<>();
	private List<String> childNodes = new ArrayList<String>();
	ZKUtil zooKeeper;
	String connectRPCProd = "114.55.141.98:2181,118.178.125.176:2181,118.178.185.111:2181";
	//String connectRPCProd = "10.26.253.201:2181,10.25.64.110:2181,10.25.68.153:2181";
	String connectRPCTest = "114.55.237.181:2181,116.62.35.78:2181";

	String[][] rpcServiceListProd = { { "rest001", "10.29.52.205", "root", "Tiger2016", "10.29.52.205", "" },
			{ "rest002", "10.29.52.138", "root", "Tiger2016", "10.29.52.138", "" },
			{ "rpc001", "10.27.14.27", "root", "Tiger2016", "10.27.14.27", "" },
			{ "rpc002", "10.27.106.106", "root", "Tiger2016", "10.27.106.106", "" },
			{ "rest003", "10.30.198.125", "root", "Tiger2016", "10.30.198.125", "" }, };
/*	String[][] rpcServiceListProd = { { "rest001", "118.178.185.181", "root", "Tiger2016", "10.29.52.205", "" },
			{ "rest002", "118.178.124.227", "root", "Tiger2016", "10.29.52.138", "" },
			{ "rpc001", "114.55.141.98", "root", "Tiger2016", "10.27.14.27", "" },
			{ "rpc002", "118.178.185.111", "root", "Tiger2016", "10.27.106.106", "" },
			{ "rest003", "116.62.105.0", "root", "Tiger2016", "10.30.198.125", "" }, };
*/
	String[][] rpcServiceListTest = { { "10.25.173.153", "10.25.173.153", "root", "Tiger2016", "10.25.173.153", "" },
			{ "10.27.4.91", "10.27.4.91", "root", "Tiger2016", "10.27.4.91", "" }, };
	/*String[][] rpcServiceListTest = { { "114.55.237.181", "114.55.237.181", "root", "Tiger2016", "10.25.173.153", "" },
			{ "116.62.35.78", "116.62.35.78", "root", "Tiger2016", "10.27.4.91", "" }, };*/
	// private SSHUtil ssh;
	String linuxPathTest = "/usr/i5i58test";
	String linuxPathProd = "/usr/i5i58test/bin";
	String[][] rpcServiceList = null;
	String connectRPC = null;
	String linuxPath = null;
	int sessionTimeoutRPC = 10000;
	int connectionTimeoutRPC = 10000;
	public static final String dubboServerRPC = "/dubbo";
	private static ManagerDataService service;

	public static ManagerDataService getInstance() {
		assert false : "spring create LiveDataService failed.";
		return service;
	}

	public ManagerDataService() {
		if (service != null) {
			throw new RuntimeException("liveDataService has been created.");
		}
		service = this;
	}

	IZkChildListener childListener = new IZkChildListener() {
		@Override
		public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
			System.out.println("changed=========" + parentPath + "22222222" + currentChilds);
			String[] path = parentPath.split("\\/");
			List<String> resuletList = new ArrayList<>();
 			for (String path2 : currentChilds) {
				String decodeNode = URLDecoder.decode(path2, "UTF-8");
				resuletList.add(decodeNode);
			}
			//InitDataRPC();
			/*if (path[2].equals("consumers")) {
				for (String path2 : currentChilds) {
					String decodeNode = URLDecoder.decode(path2, "UTF-8");
					String[] resultsProvider = decodeNode.split("\\//|/");
					List<String> methodsProvder = new ArrayList<>();
					String[] resultsProvider2 = decodeNode
							.substring(decodeNode.indexOf("&methods="), decodeNode.indexOf("&pid=")).split("=")[1]
									.split(",");
					for (String x : resultsProvider2) {
						methodsProvder.add(x);
					}
					ConsumerData node2 = new ConsumerData(resultsProvider[1], path[2], methodsProvder, "", 0L);
					for (int j = 0; j < consumersListRPC.size(); j++) {
						if (consumersListRPC.get(j).getValue().getIp().equals(resultsProvider[1])) {
							consumersListRPC.remove(j);
							consumersListRPC.add(j, new ConsumerListItem(path[2], node2));
						}
					}
				}
			} else {
				for (String path2 : currentChilds) {
					String decodeNode = URLDecoder.decode(path2, "UTF-8");
					String[] resultsProvider = decodeNode.split("\\//|/");
					List<String> methodsProvder = new ArrayList<>();
					String[] resultsProvider2 = null;
					if (decodeNode.indexOf("&optimize=") != -1) {
						resultsProvider2 = decodeNode
								.substring(decodeNode.indexOf("&methods="), decodeNode.indexOf("&optimize="))
								.split("=")[1].split(",");
					} else {
						resultsProvider2 = decodeNode
								.substring(decodeNode.indexOf("&methods="), decodeNode.indexOf("&pid="))
								.split("=")[1].split(",");
					}

					for (String x : resultsProvider2) {
						methodsProvder.add(x);
					}
					ProviderData node2 = new ProviderData(resultsProvider[1], path[2], methodsProvder, "", 0L);
					for (int j = 0; j < providersListRPC.size(); j++) {
						if (providersListRPC.get(j).getValue().getIp().equals(resultsProvider[1])) {
							System.out.println(providersListRPC.get(j));
							providersListRPC.remove(j);
							providersListRPC.add(j, new ProviderListItem(path[2], node2));
							System.out.println(providersListRPC.get(j));
						}
					}
				}
			}*/
			ResultDataSet rds = new ResultDataSet();
			rds.setCmd("rpcChanged");
			rds.setData(parentPath + resuletList);
			IRemotPeer[] peerList = RemotePeerManager.getAllPeer();
			for (IRemotPeer peer : peerList) {
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
			}

		}
	};
	IZkDataListener dataListener = new IZkDataListener() {

		@Override
		public void handleDataDeleted(String dataPath) throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public void handleDataChange(String dataPath, Object data) throws Exception {
			// TODO Auto-generated method stub

		}
	};

	public boolean InitDataRPC() {
		zooKeeper = new ZKUtil(connectRPC, sessionTimeoutRPC, connectionTimeoutRPC);
		zooKeeper.init();
		refreshNode();
		return true;
	}

	public void refreshNode() {
		consumersListRPC.clear();
		providersListRPC.clear();
		childNodes.clear();
		List<String> list = zooKeeper.getChildren(dubboServerRPC);
		for (String path : list) {
			childNodes.add(path);
			try {
				consumerList = zooKeeper.getChildren(dubboServerRPC + "/" + path + "/consumers");
				zooKeeper.subscribeChildChanges(dubboServerRPC + "/" + path + "/consumers", childListener);
				for (String path2 : consumerList) {
					String decodeNode = URLDecoder.decode(path2, "UTF-8");
					String[] results = decodeNode.split("\\//|/");
					List<String> methods = new ArrayList<>();
					String[] results2 = decodeNode
							.substring(decodeNode.indexOf("&methods="), decodeNode.indexOf("&pid=")).split("=")[1]
									.split(",");
					for (String x : results2) {
						methods.add(x);
					}
					ConsumerData node = new ConsumerData(results[1], path, methods, "", 0L);
					consumersListRPC.add(new ConsumerListItem(path, node));
				}
			} catch (Exception e) {
			}
			try {
				providerList = zooKeeper.getChildren(dubboServerRPC + "/" + path + "/providers");
				zooKeeper.subscribeChildChanges(dubboServerRPC + "/" + path + "/providers", childListener);
				for (String path2 : providerList) {
					String decodeNode = URLDecoder.decode(path2, "UTF-8");
					String[] resultsProvider = decodeNode.split("\\//|/");
					List<String> methodsProvder = new ArrayList<>();
					String[] resultsProvider2 = decodeNode
							.substring(decodeNode.indexOf("&methods="), decodeNode.indexOf("&pid=")).split("=")[1]
									.split(",");
					for (String x : resultsProvider2) {
						methodsProvder.add(x);
					}
					ProviderData node2 = new ProviderData(resultsProvider[1], path, methodsProvder, "", 0L);
					providersListRPC.add(new ProviderListItem(path, node2));
				}
			} catch (Exception e) {
			}
		}
	}

	public List<ConsumerListItem> getConsumersListRPC() {
		return consumersListRPC;
	}

	public List<ProviderListItem> getProvidersListRPC() {
		return providersListRPC;
	}

	public List<String> getChildNodes() {
		return childNodes;
	}

	public boolean deleteZookeeperNodes() throws Exception {
		try {
			deleteNode(dubboServerRPC, zooKeeper);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (KeeperException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public static void deleteNode(String path, ZKUtil zookeeper) throws Exception {

		// 获取路径下的节点
		List<String> children = zookeeper.getChildren(path);
		for (String pathCd : children) {
			// 获取父节点下面的子节点路径
			String newPath = "";
			// 递归调用,判断是否是根节点
			if (path.equals("/")) {
				newPath = "/" + pathCd;
			} else {
				newPath = path + "/" + pathCd;
			}
			deleteNode(newPath, zookeeper);
			// System.out.println("被删除的节点为：" + newPath);
		}
		// 删除节点,并过滤zookeeper节点和 /节点
		if (path != null && !path.trim().startsWith("/zookeeper") && !path.trim().equals("/")) {
			zookeeper.delete(path);
			// 打印删除的节点路径
			System.out.println("被删除的节点为：" + path);
		}
	}

	public String[][] getRpcServiceList() {
		return rpcServiceList;
	}

	public void setRpcServiceList(String xxx) {
		if (xxx.equals("test")) {
			this.rpcServiceList = rpcServiceListTest;
		} else
			this.rpcServiceList = rpcServiceListProd;
	}

	public String getConnectRPC() {
		return connectRPC;
	}

	public void setConnectRPC(String xxx) {
		if (xxx.equals("test")) {
			this.connectRPC = connectRPCTest;
		} else
			this.connectRPC = connectRPCProd;
	}

	public String getLinuxPath() {
		return linuxPath;
	}

	public void setLinuxPath(String xxx) {
		if (xxx.equals("test")) {
			this.linuxPath = linuxPathTest;
		} else
			this.linuxPath = linuxPathProd;
	}

}
