package zeus.manager.data;

import java.util.Map.Entry;

public class ProviderListItem implements Entry<String, ProviderData> {

	private String api;

	private ProviderData providerData;

	public ProviderListItem(String api, ProviderData providerData) {
		this.api = api;
		this.providerData = providerData;
	}

	@Override
	public String getKey() {
		return api;
	}

	@Override
	public ProviderData getValue() {
		return providerData;
	}

	@Override
	public ProviderData setValue(ProviderData value) {
		providerData = value;
		return providerData;
	}

}
