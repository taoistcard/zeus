package zeus.network.threading;

public abstract class Task implements ITask{

	private int workerId;
	
	public void setWorkerId(int workerId) {
		this.workerId = workerId;
	}
	
	public int getWorkerId() {
		return workerId;
	}

	private TaskPool pool;

	public void setPool(TaskPool pool) {
		this.pool = pool;
	}

	@Override
	public void run() {
		try {			
			onRun();
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("run task crashed......");
		}finally{			
			pool.onTaskCallback(workerId);
		}
	}
	
	protected abstract void onRun();

}
