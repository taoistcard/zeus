package zeus.network.threading;

/**
 * FIFO˳�������̳߳ؽӿ�
 * @author Administrator
 *
 */
public interface ITaskPool {
	
	/**
	 * ��ȡƥ���������
	 * @return
	 */
	public TaskQueue getMatchWorker();

}
