package zeus.network.threading;

public interface ITask extends Runnable {

	void setWorkerId(int workerId);
	
	void setPool(TaskPool pool);
	
	int getWorkerId();
}
