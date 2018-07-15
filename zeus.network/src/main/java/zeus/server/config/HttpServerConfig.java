package zeus.server.config;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import zeus.network.handler.HttpServerInitializer;
import zeus.network.handler.IBusinessHandler;

public class HttpServerConfig extends BaseTcpServerConfig {
	private IBusinessHandler bunisnessHandler;

	public void setBunisnessHandler(IBusinessHandler bunisnessHandler) {
		this.bunisnessHandler = bunisnessHandler;
	}

	public void init() {
		super.init();
		handler = new LoggingHandler(LogLevel.DEBUG);
		super.init();
		handler = new LoggingHandler(LogLevel.DEBUG);
		SslContext sslCtx = null;
		if (isSsl()) {
			SelfSignedCertificate ssc;
			try {
				ssc = new SelfSignedCertificate();
				sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
			} catch (CertificateException | SSLException e) {
				e.printStackTrace();
			}
		} else {
			sslCtx = null;
		}
		
		childHandler = new HttpServerInitializer(sslCtx, bunisnessHandler);
	}
}
