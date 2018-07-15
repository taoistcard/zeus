package zeus.gateserver.config;

import zeus.network.threading.ITaskPool;
import zeus.network.threading.TaskPool;
import zeus.network.threading.TaskQueue;

public class GateTaskPool extends TaskPool implements ITaskPool{

	public GateTaskPool(int workerCount, int threadCount) {
		super(workerCount, threadCount);
	}

	@Override
	public TaskQueue getMatchWorker() {
		int workerId = 0;
		int workerTaskCount = workers[0].size();
		for (int i = 0; i < workers.length; i++) {
			int taskSize = workers[i].size();
			if (taskSize < workerTaskCount) {
				workerId = i;
				workerTaskCount = taskSize;
			}
		}
		return workers[workerId];
	}

}
