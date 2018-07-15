package zeus.live.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my.threadpool")
public class MyThreadPool {

	private static int loginThreadCount;

	private static int lowPriorityThreadCount;

	private static ExecutorService loginTaskPool;

	private static int yunxinThreadCount;

	private static ExecutorService yunxinPool;

	private static int httpAuthThreadCount;

	private static ExecutorService httpAuthTaskPool;

	private static ExecutorService lowPrioritytPool;
	
	private static ExecutorService databaseThread;
	
	public static void init() {
		System.out.println(String.format("ThreadPool: loginThreadCount:%d, yunxinThreadCount:%d, httpAuthThreadCount%d",
				loginThreadCount, yunxinThreadCount, httpAuthThreadCount));
		lowPrioritytPool = Executors.newFixedThreadPool(lowPriorityThreadCount);
		loginTaskPool = Executors.newFixedThreadPool(loginThreadCount);
		yunxinPool = Executors.newFixedThreadPool(yunxinThreadCount);
		httpAuthTaskPool = Executors.newFixedThreadPool(httpAuthThreadCount);
		databaseThread = Executors.newSingleThreadExecutor();
	}

	public static int getYunxinThreadCount() {
		return yunxinThreadCount;
	}

	public static void setYunxinThreadCount(int yunxinThreadCount) {
		MyThreadPool.yunxinThreadCount = yunxinThreadCount;
	}

	public static int getLoginThreadCount() {
		return loginThreadCount;
	}

	public static void setLoginThreadCount(int loginThreadCount) {
		MyThreadPool.loginThreadCount = loginThreadCount;
	}

	public static int getHttpAuthThreadCount() {
		return httpAuthThreadCount;
	}

	public static void setHttpAuthThreadCount(int httpAuthThreadCount) {
		MyThreadPool.httpAuthThreadCount = httpAuthThreadCount;
	}

	public static ExecutorService getYunxinPool() {
		return yunxinPool;
	}

	public static Executor getLoginTaskPool() {
		return loginTaskPool;
	}

	public static ExecutorService getHttpAuthTaskPool() {
		return httpAuthTaskPool;
	}

	public static int getLowPriorityThreadCount() {
		return lowPriorityThreadCount;
	}

	public static void setLowPriorityThreadCount(int lowPriorityThreadCount) {
		MyThreadPool.lowPriorityThreadCount = lowPriorityThreadCount;
	}

	public static ExecutorService getLowPrioritytPool() {
		return lowPrioritytPool;
	}

	public static ExecutorService getDatabaseThread() {
		return databaseThread;
	}
}