package zeus.live.yunxin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("static-access")
@JsonInclude(Include.NON_DEFAULT.NON_EMPTY)
public class MsgYxChatMajiaChanged extends MsgYxChatIdentity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1154458408229009414L;

	private int majia;
	
	private String operate;

	public int getMajia() {
		return majia;
	}

	public void setMajia(int majia) {
		this.majia = majia;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}
	
}
