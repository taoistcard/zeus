package zeus.network.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.net.ssl.SSLException;

import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class NettySslContextUtil {
	private static SslContext clientSslContext;
	
	private static SslContext serverSslContext;
	static {
		try {
			clientSslContext = SslContextBuilder.forClient().trustManager(new FileInputStream("E:\\keystore\\ca\\server.pem")).build();
			
//			clientSslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			serverSslContext = SslContextBuilder.forServer(new FileInputStream("E:\\keystore\\ca\\server.pem"), 
					new FileInputStream("E:\\keystore\\ca\\server-key-pkcs8.pem")).build();
		} catch (SSLException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static SslContext getClientContext() {
		return clientSslContext;
	}
	public static SslContext getServerContext() {
		return serverSslContext;
	}
}
