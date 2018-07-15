package zeus.live.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ChannelInfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6778016168167759396L;

	private String cId = ""; // 唯一主键频道ID

	private String channelId = "";

	private String ownerId = "";

	private String channelName = "";

	private int type = 0;

	private int status = 0;
	private String coverUrl = "";
	private String gId = "";
	private String channelNotice = "";
	private String yunXinCId = "";
	private String yunXinRId = "";
	private int pushDevice = 0;

	@JsonIgnore
	private String pushUrl = "";

	private String httpPullUrl = "";
	private String hlsPullUrl = "";
	private String rtmpPullUrl = "";

	@JsonIgnore
	private String ConnCid = "";

	private String location = "";

	private int playerCount = 0;

	private int playerTimes = 0;

	private long weekOffer = 0;

	private int heartCount = 0;
	private int heartUserCount = 0;

	private long brightness = 0;

	private String title = "";

	private String clubName = "";
	private long clubScore = 0L;
	private int clubLevel = 0;
	private String clubTitle = "";
	private String clubIcon = "";
	private int clubMemberCount = 0;

	public ChannelInfo(String cId, String channelId, String ownerId, String channelName, int type, int status,
			String coverUrl, String gId, String channelNotice, String yunXinCId, String yunXinRId, 
			String pushUrl, String httpPullUrl, String hlsPullUrl, String rtmpPullUrl, String ConnCid, String location,
			int playerCount, int playerTimes, long weekOffer, int heartCount, int heartUserCount, long brightness,
			String title, String clubName, long clubScore, int clubLevel, String clubTitle, String clubIcon,
			int clubMemberCount) {
		this.cId = cId;
		this.channelId = channelId;
		this.ownerId = ownerId;
		this.channelName = channelName;
		this.type = type;
		this.status = status;
		this.coverUrl = coverUrl;
		this.gId = gId;
		this.channelNotice = channelNotice;
		this.yunXinCId = yunXinCId;
		this.yunXinRId = yunXinRId;
		//this.pushDevice = pushDevice;
		this.pushUrl = pushUrl;
		this.httpPullUrl = httpPullUrl;
		this.hlsPullUrl = hlsPullUrl;
		this.rtmpPullUrl = rtmpPullUrl;
		this.ConnCid = ConnCid;
		this.location = location;
		this.playerCount = playerCount;
		this.playerTimes = playerTimes;
		this.weekOffer = weekOffer;
		this.heartCount = heartCount;
		this.heartUserCount = heartUserCount;
		this.brightness = brightness;
		this.title = title;
		this.clubName = clubName;
		this.clubScore = clubScore;
		this.clubLevel = clubLevel;
		this.clubTitle = clubTitle;
		this.clubIcon = clubIcon;
		this.clubMemberCount = clubMemberCount;

	}

	public String getcId() {
		return cId;
	}

	public void setcId(String cId) {
		this.cId = cId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getChannelNotice() {
		return channelNotice;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getYunXinCId() {
		return yunXinCId;
	}

	public void setYunXinCId(String yunXinCId) {
		this.yunXinCId = yunXinCId;
	}

	public String getYunXinRId() {
		return yunXinRId;
	}

	public void setYunXinRId(String yunXinRId) {
		this.yunXinRId = yunXinRId;
	}

	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}

	public String getHttpPullUrl() {
		return httpPullUrl;
	}

	public void setHttpPullUrl(String httpPullUrl) {
		this.httpPullUrl = httpPullUrl;
	}

	public String getHlsPullUrl() {
		return hlsPullUrl;
	}

	public void setHlsPullUrl(String hlsPullUrl) {
		this.hlsPullUrl = hlsPullUrl;
	}

	public String getRtmpPullUrl() {
		return rtmpPullUrl;
	}

	public void setRtmpPullUrl(String rtmpPullUrl) {
		this.rtmpPullUrl = rtmpPullUrl;
	}

	public String getgId() {
		return gId;
	}

	public void setgId(String gId) {
		this.gId = gId;
	}

	public void setChannelNotice(String channelNotice) {
		this.channelNotice = channelNotice;
	}

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public long getClubScore() {
		return clubScore;
	}

	public int getClubLevel() {
		return clubLevel;
	}

	public void setClubLevel(int clubLevel) {
		this.clubLevel = clubLevel;
	}

	public void setClubScore(long clubScore) {
		this.clubScore = clubScore;
	}

	public String getClubTitle() {
		return clubTitle;
	}

	public void setClubTitle(String clubTitle) {
		this.clubTitle = clubTitle;
	}

	public String getClubIcon() {
		return clubIcon;
	}

	public void setClubIcon(String clubIcon) {
		this.clubIcon = clubIcon;
	}

	public int getClubMemberCount() {
		return clubMemberCount;
	}

	public void setClubMemberCount(int clubMemberCount) {
		this.clubMemberCount = clubMemberCount;
	}

	public int getPushDevice() {
		return pushDevice;
	}

	public void setPushDevice(int pushDevice) {
		this.pushDevice = pushDevice;
	}

	public String getConnCid() {
		return ConnCid;
	}

	public void setConnCid(String connCid) {
		ConnCid = connCid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public int getPlayerTimes() {
		return playerTimes;
	}

	public void setPlayerTimes(int playerTimes) {
		this.playerTimes = playerTimes;
	}

	public long getWeekOffer() {
		return weekOffer;
	}

	public void setWeekOffer(long weekOffer) {
		this.weekOffer = weekOffer;
	}

	public int getHeartCount() {
		return heartCount;
	}

	public void setHeartCount(int heartCount) {
		this.heartCount = heartCount;
	}

	public int getHeartUserCount() {
		return heartUserCount;
	}

	public void setHeartUserCount(int heartUserCount) {
		this.heartUserCount = heartUserCount;
	}

	public long getBrightness() {
		return brightness;
	}

	public void setBrightness(long brightness) {
		this.brightness = brightness;
	}

}