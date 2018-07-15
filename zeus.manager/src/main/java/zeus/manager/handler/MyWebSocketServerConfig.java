package zeus.manager.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import zeus.manager.server.ManagerDataService;
import zeus.network.handler.IBusinessHandler;
import zeus.network.threading.ITaskPool;
import zeus.network.util.NettySslContextUtil;
import zeus.server.config.BaseTcpServerConfig;

public class MyWebSocketServerConfig extends BaseTcpServerConfig {
 
	private ITaskPool taskPool;
	private ManagerDataService managerDataService;

	public MyWebSocketServerConfig() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("application.properties");
		Properties config = new Properties();
		try {
			config.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setName(config.getProperty("server.websocket.name"));
		this.setPort(Integer.parseInt(config.getProperty("server.websocket.port")));
		this.setBossthread(Integer.parseInt(config.getProperty("server.websocket.bossthread")));
		this.setWorkerthread(Integer.parseInt(config.getProperty("server.websocket.workerthread")));
		this.setEpoll(Boolean.parseBoolean(config.getProperty("server.websocket.epoll")));
		this.setKeepalive(Boolean.parseBoolean(config.getProperty("server.websocket.keepalive")));
		this.setNodelay(Boolean.parseBoolean(config.getProperty("server.websocket.nodelay")));
		this.setSsl(Boolean.parseBoolean(config.getProperty("server.websocket.ssl")));
		this.setConntimeout(Integer.parseInt(config.getProperty("server.websocket.conntimeout")));
		this.setGate(Boolean.parseBoolean(config.getProperty("server.websocket.isGate")));
		this.setUseBusinessTask(Boolean.parseBoolean(config.getProperty("server.websocket.useBusinessTask")));
	}

	public void setBunisnessHandler( ITaskPool taskPool) {
		this.taskPool = taskPool;
	}

	public void setManagerDataService(ManagerDataService managerDataService){
		this.managerDataService = managerDataService;
	}
	public void init() {
		super.init();
		handler = new LoggingHandler(LogLevel.DEBUG);
		SslContext sslCtx = null;
		if (isSsl()) {
//			boolean isOpenssl = OpenSsl.isAvailable();
//			if (!isOpenssl){
//				Throwable throwable = OpenSsl.unavailabilityCause();
//				System.out.println(throwable.toString());
//			}
			sslCtx = NettySslContextUtil.getServerContext();
//			SelfSignedCertificate ssc;
//			try {
//				ssc = new SelfSignedCertificate();
//				sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
//			} catch (CertificateException | SSLException e) {
//				e.printStackTrace();
//			}
		} else {
			sslCtx = null;
		}
		childHandler = new MyWebSocketInitializer(sslCtx,taskPool,managerDataService);
	}
}