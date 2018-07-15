package zeus.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.SSLException;

import org.springframework.stereotype.Component;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import zeus.network.handler.IBusinessHandler;
import zeus.network.handler.WebsocketServerInitializer;
import zeus.network.threading.ITaskPool;
import zeus.network.util.NettySslContextUtil;

/**
 * websocket��������
 * 
 * @author songfeilong
 *
 */
@Component
// @ConfigurationProperties(prefix = "server.websocket")
public class WebsocketServerConfig extends BaseTcpServerConfig {

	private IBusinessHandler bunisnessHandler;
	private ITaskPool taskPool;

	public WebsocketServerConfig() {
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

	public void setBunisnessHandler(IBusinessHandler bunisnessHandler, ITaskPool taskPool) {
		this.bunisnessHandler = bunisnessHandler;
		this.taskPool = taskPool;
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
		childHandler = new WebsocketServerInitializer(sslCtx, bunisnessHandler, useBusinessTask, taskPool, this.isGate);
	}
}
