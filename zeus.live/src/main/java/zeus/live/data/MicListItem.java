package zeus.live.data;

import java.util.Map.Entry;

import com.i5i58.data.channel.HotChannelMic;

public class MicListItem implements Entry<String, HotChannelMic> {
	private String accId;
	private HotChannelMic hotChannelMic;

	public MicListItem(String accId, HotChannelMic hotChannelMic) {
		this.accId = accId;
		this.hotChannelMic = hotChannelMic;
	}

	@Override
	public String getKey() {
		return accId;
	}

	@Override
	public HotChannelMic getValue() {
		return hotChannelMic;
	}

	@Override
	public HotChannelMic setValue(HotChannelMic value) {
		hotChannelMic = value;
		return hotChannelMic;
	}

}