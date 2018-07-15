package zeus.network.util;

import zeus.network.protocol.HttpClientMsg;

public class HttpClientMsgUtil {
	public static String getString(HttpClientMsg msg, String key){
		return msg.getParamMap().get(key);
	}
	
	public static Integer getInt(HttpClientMsg msg, String key){
		String sValue = msg.getParamMap().get(key);
		if (sValue == null || sValue.isEmpty()){
			return null;
		}
		try {
			Integer v = Integer.parseInt(sValue);
			return v;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Long getLong(HttpClientMsg msg, String key){
		String sValue = msg.getParamMap().get(key);
		if (sValue == null || sValue.isEmpty()){
			return null;
		}
		try {
			Long v = Long.parseLong(sValue);
			return v;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Boolean getBoolean(HttpClientMsg msg, String key){
		String sValue = msg.getParamMap().get(key);
		if (sValue == null || sValue.isEmpty()){
			return null;
		}
		
		try {
			Boolean v = Boolean.parseBoolean(sValue);
			return v;
		} catch (Exception e) {
			return null;
		}
	}
}
