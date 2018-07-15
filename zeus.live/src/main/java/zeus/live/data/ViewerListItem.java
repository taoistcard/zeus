package zeus.live.data;

import java.util.Map.Entry;

import com.i5i58.data.channel.HotChannelViewer;

public class ViewerListItem implements Entry<Double, HotChannelViewer> {
	private Double hotScore;
	private HotChannelViewer hotViewer;
	public ViewerListItem(Double hotScore, HotChannelViewer hotViewer) {
		this.hotScore = hotScore;
		this.hotViewer = hotViewer;
	}

	@Override
	public Double getKey() {
		return hotScore;
	}

	@Override
	public HotChannelViewer getValue() {
		return hotViewer;
	}

	@Override
	public HotChannelViewer setValue(HotChannelViewer value) {
		hotViewer = value;
		return hotViewer;
	}

}
