package zeus.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.SSLException;

import org.springframework.stereotype.Component;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import zeus.network.handler.IBusinessHandler;
import zeus.network.handler.TcpChannelInitializer;
import zeus.network.threading.ITaskPool;
import zeus.network.util.NettySslContextUtil;

/**
 * tcp��������
 * 
 * @author songfeilong
 *
 */
@Component
// @ConfigurationProperties(prefix = "server.tcp")
public class TcpServerConfig extends BaseTcpServerConfig {

	private IBusinessHandler bunisnessHandler;
	private ITaskPool taskPool;

	public TcpServerConfig() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("application.properties");
		Properties config = new Properties();
		try {
			config.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setName(config.getProperty("server.tcp.name"));
		this.setPort(Integer.parseInt(config.getProperty("server.tcp.port")));
		this.setBossthread(Integer.parseInt(config.getProperty("server.tcp.bossthread")));
		this.setWorkerthread(Integer.parseInt(config.getProperty("server.tcp.workerthread")));
		this.setEpoll(Boolean.parseBoolean(config.getProperty("server.tcp.epoll")));
		this.setKeepalive(Boolean.parseBoolean(config.getProperty("server.tcp.keepalive")));
		this.setNodelay(Boolean.parseBoolean(config.getProperty("server.tcp.nodelay")));
		this.setSsl(Boolean.parseBoolean(config.getProperty("server.tcp.ssl")));
		this.setConntimeout(Integer.parseInt(config.getProperty("server.tcp.conntimeout")));
		this.setGate(Boolean.parseBoolean(config.getProperty("server.tcp.isGate")));
		this.setUseBusinessTask(Boolean.parseBoolean(config.getProperty("server.tcp.useBusinessTask")));
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
//			sslCtx = NettySslContextUtil.getServerContext();
		} else {
			sslCtx = null;
		}
		childHandler = new TcpChannelInitializer(sslCtx, bunisnessHandler, useBusinessTask, taskPool, this.isGate);
	}
}
