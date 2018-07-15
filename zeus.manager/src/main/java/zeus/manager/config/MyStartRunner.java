package zeus.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.i5i58.util.JedisUtils;

import zeus.manager.server.ManagerDataService;
import zeus.manager.server.ManagerServer;

@Component
public class MyStartRunner implements CommandLineRunner {
	@Autowired
	ManagerServer managerServer;

	@Autowired
	ManagerDataService managerDataService;

	@Override
	public void run(String... args) throws Exception {
		System.out.println(">>>>>>>>>>>>>>> manager server start<<<<<<<<<<<<<");
		JedisUtils.init();
		MyThreadPool.init();
		/*
		 * liveServer.init(); liveServer.runIdleTask();
		 */
		managerServer.init();
		managerServer.run();
		// managerServer.runIdleTask();
	}
}