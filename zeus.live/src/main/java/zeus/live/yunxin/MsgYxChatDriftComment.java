package zeus.live.yunxin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("static-access")
@JsonInclude(Include.NON_DEFAULT.NON_EMPTY)
public class MsgYxChatDriftComment extends MsgYxChatIdentity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1320780686358713677L;

	/**
	 * 弹幕内容
	 */
	private String content;
	
	private boolean levelUp;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public boolean isLevelUp() {
		return levelUp;
	}

	public void setLevelUp(boolean levelUp) {
		this.levelUp = levelUp;
	}
}
