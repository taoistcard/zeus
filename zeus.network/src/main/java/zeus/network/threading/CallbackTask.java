package zeus.network.threading;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

public class CallbackTask implements Runnable {
	Logger logger = Logger.getLogger(CallbackTask.class);
	TaskQueue queue;
	ExecutorService pool;

	public CallbackTask(TaskQueue taskQueue, ExecutorService workPool) {
		queue = taskQueue;
		pool = workPool;
	}

	@Override
	public void run() {
//		logger.info(String.format("current task = %d", queue.size()));
		if (queue.size() > 0) {
			ITask task = queue.poll();
			pool.execute(task);
		} else {
			queue.setTaskRunning(false);
		}
	}

}
