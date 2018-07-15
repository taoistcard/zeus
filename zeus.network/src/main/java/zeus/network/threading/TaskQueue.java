package zeus.network.threading;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FIFO顺序任务队列
 * @author frank
 *
 */
public class TaskQueue extends LinkedBlockingQueue<ITask> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7018953549948797349L;

	private volatile boolean isTaskRunning;

	private ExecutorService workPool;

	private ExecutorService allocationPool;
	
	private TaskPool taskPool;
	
	private int workerId;
	
	private AtomicInteger peerCount = new AtomicInteger(0);

	public int getWorkerId() {
		return workerId;
	}

	public void setWorkerId(int workerId) {
		this.workerId = workerId;
	}
	
	public int getPeerCount() {
		return peerCount.get();
	}

	public void incrementPeerCount(){
		peerCount.incrementAndGet();	
	}
	
	public void decrementPeerCount(){
		peerCount.decrementAndGet();
	}
	
	public void setTaskPool(TaskPool taskPool) {
		this.taskPool = taskPool;
	}

	public boolean isTaskRunning() {
		return isTaskRunning;
	}

	public void setTaskRunning(boolean isTaskRunning) {
		this.isTaskRunning = isTaskRunning;
	}

	public void setAllocationPool(ExecutorService allocationPool) {
		this.allocationPool = allocationPool;
	}

	public void setWorkPool(ExecutorService workPool) {
		this.workPool = workPool;
	}

	public void addTask(ITask task) {
		AllocationTask t = new AllocationTask(task, this, workPool, taskPool);
		allocationPool.execute(t);
	}

	public void work() {
		CallbackTask t = new CallbackTask(this, workPool);
		allocationPool.execute(t);
	}

}
