package zeus.live.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.i5i58.util.JedisUtils;

import zeus.live.server.LiveDataService;
import zeus.live.server.LiveServer;

@Component
public class MyStartRunner implements CommandLineRunner {

	@Autowired
	LiveServer liveServer;
	
	@Autowired
	LiveDataService liveDataService;
	
	@Override
	public void run(String... args) throws Exception {
		System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作<<<<<<<<<<<<<");
		JedisUtils.init();
		MyThreadPool.init();
		liveServer.init();
		liveServer.runIdleTask();
	}
}