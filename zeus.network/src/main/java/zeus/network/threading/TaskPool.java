package zeus.network.threading;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * FIFO顺序任务线程池
 * @author frank
 *
 */
public abstract class TaskPool {
	public ExecutorService workPool;
	public ExecutorService allocationPool;
	public TaskQueue[] workers;
//	public int _workerCount;

	public TaskPool(int workerCount, int threadCount) {
//		_workerCount = workerCount;
		allocationPool = Executors.newSingleThreadExecutor();
		workPool = Executors.newFixedThreadPool(threadCount);
		workers = new TaskQueue[workerCount];
		for (int i = 0; i < workerCount; i++) {
			workers[i] = new TaskQueue();
			workers[i].setWorkerId(i);
			workers[i].setWorkPool(workPool);
			workers[i].setAllocationPool(allocationPool);
			workers[i].setTaskPool(this);
		}
	}
	
	public void onTaskCallback(int workerId) {
		workers[workerId].work();
	}
}
