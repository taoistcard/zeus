package zeus.network.threading;

/**
 * FIFO顺序任务线程池接口
 * @author Administrator
 *
 */
public interface ITaskPool {
	
	/**
	 * 获取匹配任务队列
	 * @return
	 */
	public TaskQueue getMatchWorker();

}
