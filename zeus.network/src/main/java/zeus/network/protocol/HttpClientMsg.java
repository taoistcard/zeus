package zeus.network.protocol;

import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import zeus.network.util.Constants;

public class HttpClientMsg {
	private boolean keepAlive;
	private String uri;
	private String accId;
	private String token;
	private String clientIp;

	private Map<String, String> paramMap = new HashMap<String, String>();

	public HttpClientMsg(FullHttpRequest message){
		parseHeader(message);
		keepAlive = HttpUtil.isKeepAlive(message);
		String fullUri = message.uri();
		int first = fullUri.indexOf('?');
		if (first > 0){
			uri = fullUri.substring(0, first);
			String param = fullUri.substring(first + 1, fullUri.length());
			parseParams(param);
		}else if (first == 0){
			uri = ""; // ?
		}else{
			uri = fullUri;// 
		}
		
		String content = message.content().toString(Constants.defaultCharset);
		parseParams(content);
	}
	
	public void parseHeader(FullHttpRequest message){
		HttpHeaders headers = message.headers();
		accId = headers.get("accId");
		token = headers.get("token");
		clientIp = headers.get("clientIp");
	}
	
	public void parseParams(String param){
		String[] pairs = param.split("&");
		for (String pair : pairs){
			String key = "";
			String value = "";

			int equalIndex = pair.indexOf('=');
			if (equalIndex > 0 && equalIndex < pair.length()){
				key = pair.substring(0, equalIndex);
				value = pair.substring(equalIndex+1, pair.length());
				paramMap.put(key, value);
			}
		}
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public String getUri() {
		return uri;
	}
	public String getAccId() {
		return accId;
	}
	public String getToken() {
		return token;
	}
	public String getClientIp() {
		return clientIp;
	}
	public Map<String, String> getParamMap() {
		return paramMap;
	}
}
