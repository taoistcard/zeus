package zeus.live.yunxin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("static-access")
@JsonInclude(Include.NON_DEFAULT.NON_EMPTY)
public class MsgYxNoticeAudioStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1858911833758085363L;

	private Boolean audioFlag;

	private String content;

	public Boolean getAudioFlag() {
		return audioFlag;
	}

	public void setAudioFlag(Boolean audioFlag) {
		this.audioFlag = audioFlag;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}