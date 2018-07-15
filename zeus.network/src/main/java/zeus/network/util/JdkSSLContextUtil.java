package zeus.network.util;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import io.netty.util.internal.SystemPropertyUtil;

public class JdkSSLContextUtil {
	private static final String PROTOCOL = "TLS";
    private static final SSLContext SERVER_CONTEXT;
    private static final SSLContext CLIENT_CONTEXT;

    private static String StoreType = "JCEKS";
    private static String CLIENT_KEY_STORE = "E:\\keystore\\clientstore.jceks";
    
    private static String CLIENT_TRUST_KEY_STORE = "E:\\keystore\\clientstore.jceks";

    private static String SERVER_KEY_STORE = "E:\\keystore\\sslkeystore.jceks";

    private static String File_Certificate = "E:\\keystore\\securenetwork.cer";
    
    static {
        String algorithm = SystemPropertyUtil.get("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        SSLContext serverContext;
        SSLContext clientContext;
        try {
            KeyStore ks = KeyStore.getInstance(StoreType);
            ks.load(new FileInputStream(SERVER_KEY_STORE), "123456".toCharArray());

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            kmf.init(ks, "123456".toCharArray());

            // Initialize the SSLContext to work with our key managers.
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }

        try {
            KeyStore ks = KeyStore.getInstance(StoreType);
            ks.load(new FileInputStream(CLIENT_KEY_STORE), "123456".toCharArray());


            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, "123456".toCharArray());
            
            KeyStore tks = KeyStore.getInstance(StoreType);
            tks.load(new FileInputStream(CLIENT_TRUST_KEY_STORE), "123456".toCharArray());
            // Set up key manager factory to use our key store
            TrustManagerFactory tmf2 = TrustManagerFactory.getInstance("SunX509");
            tmf2.init(tks);
            
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(kmf.getKeyManagers(), tmf2.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }

        SERVER_CONTEXT = serverContext;
        CLIENT_CONTEXT = clientContext;
    }

    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }
}
