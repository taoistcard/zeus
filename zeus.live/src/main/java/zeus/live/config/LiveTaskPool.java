package zeus.live.config;

import zeus.network.threading.ITaskPool;
import zeus.network.threading.TaskPool;
import zeus.network.threading.TaskQueue;

public class LiveTaskPool extends TaskPool implements ITaskPool {

	public LiveTaskPool(int workerCount, int threadCount) {
		super(workerCount, threadCount);
	}

	@Override
	public TaskQueue getMatchWorker() {
		int workerId = 0;
		int peerCount = workers[0].getPeerCount();
		for (int i = 0; i < workers.length; i++) {
			int peer = workers[i].getPeerCount();
			if (peer < peerCount) {
				workerId = i;
				peerCount = peer;
			}
		}
		return workers[workerId];
	}

}
