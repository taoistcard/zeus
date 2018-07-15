package zeus.gateserver.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my.threadpool")
public class MyThreadPool {

	private static int authThreadCount;

	private static ExecutorService authTaskPool;
	
	private static ExecutorService updateOnlineThread;
	
	public static void init() {
		System.out.println("ThreadPool: " + authThreadCount);
		authTaskPool = Executors.newFixedThreadPool(authThreadCount);
		updateOnlineThread = Executors.newSingleThreadExecutor();
	}

	public static int getAuthThreadCount() {
		return authThreadCount;
	}

	public static void setAuthThreadCount(int authThreadCount) {
		MyThreadPool.authThreadCount = authThreadCount;
	}

	public static ExecutorService getAuthTaskPool() {
		return authTaskPool;
	}
	
	public static ExecutorService getUpdateOnlineThread() {
		return updateOnlineThread;
	}

}