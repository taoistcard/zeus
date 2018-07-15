package zeus.network.protocol;

/**
 * 业务消息
 * @author frank
 *
 */
public class BusinessMsg implements IBusinessMsg{

	protected byte version;
	
	protected int length;
	
	protected short main;
	
	protected short sub;
	
	protected byte[] data;

	@Override
	public byte getVersion() {
		return version;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public short getMain() {
		return main;
	}

	@Override
	public short getSub() {
		return sub;
	}

	@Override
	public byte[] getData() {
		return data;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setMain(short main) {
		this.main = main;
	}

	public void setSub(short sub) {
		this.sub = sub;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
}
