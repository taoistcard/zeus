package zeus.manager.data;

import java.util.List;

public class ProviderData implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8836698540176222946L;

	private String ip = "";

	private String interfaceName = "";

	private List<String> methods = null;

	private String revision = "";

	private Long timestamp = 0L;

	public ProviderData(String ip, String interfaceName, List<String> methods, String revision, Long timestamp) {
		this.ip = ip;
		this.interfaceName = interfaceName;
		this.methods = methods;
		this.revision = revision;
		this.timestamp = timestamp;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public List<String> getMethods() {
		return methods;
	}

	public void setMethods(List<String> methods) {
		this.methods = methods;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
