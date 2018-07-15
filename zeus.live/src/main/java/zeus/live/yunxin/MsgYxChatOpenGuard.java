package zeus.live.yunxin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("static-access")
@JsonInclude(Include.NON_DEFAULT.NON_EMPTY)
public class MsgYxChatOpenGuard extends MsgYxChatIdentity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3422438287465629524L;

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
}
