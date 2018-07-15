package zeus.manager.data;

import java.util.Map.Entry;

public class ConsumerListItem implements Entry<String, ConsumerData> {
	
	private String api;
	
	private ConsumerData consumerData;
	
	public ConsumerListItem(String api, ConsumerData consumerData) {
		this.api = api;
		this.consumerData = consumerData;
	}
	
	@Override
	public String getKey() { 
		return api;
	}

	@Override
	public ConsumerData getValue() {
		return consumerData;
	}

	@Override
	public ConsumerData setValue(ConsumerData value) {
		consumerData = value;
		return consumerData;
	}

}
