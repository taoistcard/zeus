package zeus.live.yunxin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("static-access")
@JsonInclude(Include.NON_DEFAULT.NON_EMPTY)
public class MsgYxChatGift extends MsgYxChatIdentity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1320780686358713677L;
	/**
	 * 礼物id
	 */
	private int id;
	/**
	 * 礼物数量
	 */
	private int ct;
	
	/**
	 * 播放动画条件
	 */
	private boolean condition;
	/**
	 * 送礼物连击数
	 */
	private int ctis;
	
	private long weekOffer;
	
	private long offer;
	
	private long indexByViewer;
	
	private long indexByRicher;
	
	private boolean levelUp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCt() {
		return ct;
	}

	public void setCt(int ct) {
		this.ct = ct;
	}

	public int getCtis() {
		return ctis;
	}

	public void setCtis(int ctis) {
		this.ctis = ctis;
	}

	public long getWeekOffer() {
		return weekOffer;
	}

	public void setWeekOffer(long weekOffer) {
		this.weekOffer = weekOffer;
	}

	public long getOffer() {
		return offer;
	}

	public void setOffer(long offer) {
		this.offer = offer;
	}

	public long getIndexByViewer() {
		return indexByViewer;
	}

	public void setIndexByViewer(long indexByViewer) {
		this.indexByViewer = indexByViewer;
	}

	public long getIndexByRicher() {
		return indexByRicher;
	}

	public void setIndexByRicher(long indexByRicher) {
		this.indexByRicher = indexByRicher;
	}

	public boolean isCondition() {
		return condition;
	}

	public void setCondition(boolean condition) {
		this.condition = condition;
	}
	public boolean isLevelUp() {
		return levelUp;
	}

	public void setLevelUp(boolean levelUp) {
		this.levelUp = levelUp;
	}
}
