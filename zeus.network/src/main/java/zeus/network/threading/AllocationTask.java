package zeus.network.threading;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

public class AllocationTask implements Runnable {

	Logger logger = Logger.getLogger(AllocationTask.class);
	
	ITask task;
	TaskQueue queue;
	ExecutorService pool;

	public AllocationTask(ITask workTask, TaskQueue taskQueue, ExecutorService workPool, TaskPool taskPool) {
		task = workTask;
		queue = taskQueue;
		pool = workPool;
		task.setWorkerId(queue.getWorkerId());
		task.setPool(taskPool);
	}

	@Override
	public void run() {
		logger.info(String.format("current task = %d, taskqueue is running %s", 
				queue.size(), queue.isTaskRunning()?"true":"false"));
		
		if (queue.size() > 0) {
			queue.add(task);
			return;
		} else {
			if (queue.isTaskRunning()) {
				queue.add(task);
				return;
			}
		}
		pool.execute(task);
		queue.setTaskRunning(true);
	}

}
