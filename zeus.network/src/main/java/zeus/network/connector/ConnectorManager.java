package zeus.network.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.i5i58.data.channel.ServerInfo;

/**
 * ������������
 * @author frank
 *
 */
public class ConnectorManager {

	private static Logger logger = Logger.getLogger(ConnectorManager.class);

	static Map<String, IConnector> connectors = new HashMap<String, IConnector>();
	
	static Map<String, IConnector> reconnectors = new HashMap<String, IConnector>();
	static Map<String, Integer> reconnectCount = new HashMap<String, Integer>();

	static ExecutorService connectorThread = Executors.newSingleThreadExecutor();
	static ScheduledExecutorService reconnectExecutor;

	static ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

//	static Map<String, ServerInfo> currentServers = new HashMap<String, ServerInfo>();

	public static void updateServerList(Map<String, ServerInfo> serverInfoMap) {
		connectorThread.execute(() -> {
			if (serverInfoMap == null) {
				return;
			}
			logger.info("update server list");
			List<ServerInfo> toAdd = new ArrayList<ServerInfo>();
			List<String> toRemove = new ArrayList<String>();
			
			try {
				readWriteLock.readLock().lock();
				for (Map.Entry<String, IConnector> e : connectors.entrySet()) {
					if (serverInfoMap.get(e.getKey()) == null) {
						toRemove.add(e.getKey());
					}
				}
			}catch (Exception e) {
			}finally {				
				readWriteLock.readLock().unlock();
			}
			

			for (Map.Entry<String, ServerInfo> e : serverInfoMap.entrySet()) {
				IConnector connector;
				
				readWriteLock.readLock().lock();
				connector = connectors.get(e.getKey());
				readWriteLock.readLock().unlock();

				if (connector == null) {
					toAdd.add(e.getValue());
				} else {
					ServerInfo info = e.getValue();
					ServerInfo preInfo = connector.getServerInfo();
					if (preInfo.getConnectorPort() != info.getConnectorPort()
							|| !preInfo.getHost().equals(info.getHost())) {
						toAdd.add(e.getValue());
						toRemove.add(e.getKey());
					}
				}
			}

			for (String key : toRemove) {				
				try {
					readWriteLock.writeLock().lock();
					IConnector connector = connectors.remove(key);
					if (connector != null && connector.isRunning()) {
						connector.close();
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}catch (Exception e) {
				} finally {
					readWriteLock.writeLock().unlock();
				}
			}

			for (ServerInfo info : toAdd) {
				doConnect(info);
			}
		});
	}

	/**
	 * �첽���������ӷ��������
	 */
	private static void doConnect(ServerInfo info) {
		IConnector connector = new GateConnector(info);
		if (connector.connect()) {
			readWriteLock.writeLock().lock();
			if (info.getServerKey() != null){	
				connectors.put(info.getServerKey(), connector);
			}
			readWriteLock.writeLock().unlock();
		}else{
			reconnectors.put(info.getServerKey(), connector);
			reconnectCount.put(info.getServerKey(), 0);
			if (reconnectExecutor == null){
				reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
				reconnectExecutor.scheduleAtFixedRate(()->{
					doReconnect();
				}, 2, 5, TimeUnit.SECONDS);
			}
		}
	}

	public static IConnector get(String serverKey) {
		IConnector connector = null;
		readWriteLock.readLock().lock();
		if (serverKey != null){			
			connector = connectors.get(serverKey);
		}
		readWriteLock.readLock().unlock();
		return connector;
	}
	
	private static void doReconnect(){
		connectorThread.execute(()->{
			System.out.println("do reconnect now......");
			for (Map.Entry<String, IConnector> e : reconnectors.entrySet()){
				try {
					if (e.getValue().reconnect()){
						reconnectors.remove(e.getKey());
						reconnectCount.remove(e.getKey());
						
						readWriteLock.writeLock().lock();					
						connectors.put(e.getKey(), e.getValue());
						readWriteLock.writeLock().unlock();
					}else{
						int reconnect = reconnectCount.get(e.getKey()) + 1;
						if (reconnect >= 3){
							reconnectCount.remove(e.getKey());
							reconnectors.remove(e.getKey());
							System.out.println("reconnect to many times, connector to " + e.getKey() + " removed.");
						}else{
							reconnectCount.put(e.getKey(), reconnect);
						}
					}
				} catch (Exception e1) {
					logger.error(e1.getMessage());
					reconnectCount.remove(e.getKey());
					reconnectors.remove(e.getKey());
				}
			}
			
			if (reconnectors.size() == 0 && reconnectExecutor != null){
				reconnectExecutor.shutdownNow();
				reconnectExecutor = null;
			}
		});
		
	}
	
	public static void connectorClosed(IConnector connector){
		if (connector == null)
			return;
		connectorThread.execute(()->{
			ServerInfo info = connector.getServerInfo();
			if(connector != connectors.get(info.getServerKey()))
				return;
			connectors.remove(info.getServerKey());
			if (!connector.isManullyClosed())
			{
				reconnectors.put(info.getServerKey(), connector);
				reconnectCount.put(info.getServerKey(), 0);
				if (reconnectExecutor == null){
					reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
					reconnectExecutor.scheduleAtFixedRate(()->{
						doReconnect();
					}, 2, 5, TimeUnit.SECONDS);
				}
			}
		});
	}
}
