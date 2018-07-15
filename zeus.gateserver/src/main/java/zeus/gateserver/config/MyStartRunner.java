package zeus.gateserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.i5i58.util.JedisUtils;

import zeus.gateserver.server.GateServer;

@Component
public class MyStartRunner implements CommandLineRunner {

	@Autowired
	GateServer gateServer;
	@Override
	public void run(String... args) throws Exception {
		System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作<<<<<<<<<<<<<");
    	JedisUtils.init();
    	MyThreadPool.init();
    	gateServer.init();
    	gateServer.run();

//    	Timer timer = new HashedWheelTimer();
//    	timer.newTimeout((Timeout timeout)->{
//    		List<ServerInfo> serverList = new ArrayList<ServerInfo>();
//    		ServerInfo serverInfo = new ServerInfo();
//    		serverInfo.setServerKey("10000");
//    		serverInfo.setHost("127.0.0.1");
//    		serverInfo.setRemotePort(29493);
//    		serverInfo.setConnectorPort(9000);
//    		serverList.add(serverInfo);
//    		ConnectorManager.updateServerList(serverList);
//    	}, 1, TimeUnit.SECONDS);
    	
//    	gateServer.sync();
	}

}