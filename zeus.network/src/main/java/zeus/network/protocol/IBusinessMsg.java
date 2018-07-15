package zeus.network.protocol;

/**
 * ҵ����Ϣ�ӿ�
 * @author frank
 *
 */
public interface IBusinessMsg {
	
	/**
	 * ��ȡ��Ϣ�汾��
	 * @return
	 */
	byte getVersion();
	
	/**
	 * ��ȡ��Ϣ�����ݳ��ȣ�������ͷ��
	 * @return
	 */
	int getLength();
	
	/**
	 * ��ȡҵ��������
	 * @return
	 */
	short getMain();
	
	/**
	 * ��ȡҵ�������
	 * @return
	 */
	short getSub();
	
	/**
	 * ��ȡҵ������
	 * @return
	 */
	byte[] getData();
}
