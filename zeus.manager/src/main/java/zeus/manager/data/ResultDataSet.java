package zeus.manager.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Data Transfer Object for Response
 * 
 * @author frank
 * @param <T>
 *
 */
@SuppressWarnings("static-access")
@JsonInclude(Include.NON_DEFAULT.NON_EMPTY)
public class ResultDataSet implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2157145182878477072L;

	private String cmd;
	private String code;
	private String msg;
	private Object data;

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
