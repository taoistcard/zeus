package zeus.manager.data;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.IZkChildListener;

import com.i5i58.util.ZKUtil;

import ch.ethz.ssh2.channel.Channel;
import zeus.manager.server.SSHUtil;

public class testZK implements IZkChildListener {

	public static final String activeRoomServer = "/ActiveLiveServer";
	public static final String gateServer = "/GateServer";
	public static final String dubboServer = "/dubbo";

	ZKUtil zkUtil;
	static SSHUtil ssh;
	static Channel channel;
	public static void main(String[] args) throws ParseException {
		
		try {  
		    ssh = new SSHUtil("116.62.35.78", "root", "Tiger2016");  
		    ssh.execCommand(" ps -ef|grep SNAPSHOT|grep -v grep");  
		   while (true) {  
		        String line = ssh.readLine();  
		        if (line == null ) {  
		            break;  
		        }  
		        System.out.println("line>>>:" + line);  
		   //     channel.publish(client, line, null);  
		    }  
		} catch (IOException e) {  
		    e.printStackTrace();  
		    throw new RuntimeException(e);  
		} finally {  
		    ssh.close();  
		}  

		/*String connectString = "114.55.141.98:2181,118.178.125.176:2181,118.178.185.111:2181";
		int sessionTimeout = 10000;
		Watcher watcher = new Watcher() {
			public void process(WatchedEvent event) { // System.out.println(event.getPath());
			}
		};
		try {
			ZooKeeper zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher); 
			List<String> list = zooKeeper.getChildren(dubboServer, true);
			Stat stat = new Stat(); 
			 byte[] data = zooKeeper.getData(dubboServer + "/com.i5i58.apis.account.IAccountPersonal/providers", true, stat);  
			System.out.println("================byte =====" +(data == null ? "null" : new String(data)));
			for (String path : list) {
				List<String> consumerList = zooKeeper.getChildren(dubboServer + "/" + path + "/consumers", true);
				System.out.println(path); 
				Stat data = zooKeeper.exists(dubboServer + "/" + path + "/consumers",true);  
				System.out.println("================getAversion =====" +(data == null ? "null" : data.getAversion()));
				System.out.println("================getClass =====" +(data == null ? "null" : data.getClass()));
				System.out.println("================getCtime =====" +(data == null ? "null" :new Date(data.getCtime()).toString()));
				System.out.println("================getCzxid =====" +(data == null ? "null" :Long.toHexString(data.getCzxid())));
				System.out.println("================getDataLength =====" +(data == null ? "null" : data.getDataLength()));
				System.out.println("================getEphemeralOwner =====" +(data == null ? "null" : Long.toHexString(data.getEphemeralOwner())));
				System.out.println("================getNumChildren =====" +(data == null ? "null" : data.getNumChildren()));
				System.out.println("================getPzxid =====" +(data == null ? "null" :Long.toHexString(data.getMzxid())));
				System.out.println("================getMtime =====" +(data == null ? "null" : new Date(data.getMtime()).toString()));
				System.out.println("================getMzxid =====" +(data == null ? "null" : Long.toHexString(data.getMzxid())));
				System.out.println("================getVersion =====" +(data == null ? "null" : data.getVersion()));
				System.out.println("================getCversion =====" +(data == null ? "null" : data.getCversion()));
				System.out.println("================signature =====" +(data == null ? "null" : data.signature()));
				
				for (String path2 : consumerList) { // URL URL.valueOf(path2);
					System.out.println(path2);
					// codeTest(path2);     
					Stat data = zooKeeper.exists(dubboServer + "/" + path + "/providers/" + path2,true);  
					System.out.println("================getAversion =====" +(data == null ? "null" : data.getAversion()));
					System.out.println("================getClass =====" +(data == null ? "null" : data.getClass()));
					System.out.println("================getCtime =====" +(data == null ? "null" :new Date(data.getCtime()).toString()));
					System.out.println("================getCzxid =====" +(data == null ? "null" :Long.toHexString(data.getCzxid())));
					System.out.println("================getDataLength =====" +(data == null ? "null" : data.getDataLength()));
					System.out.println("================getEphemeralOwner =====" +(data == null ? "null" : Long.toHexString(data.getEphemeralOwner())));
					System.out.println("================getNumChildren =====" +(data == null ? "null" : data.getNumChildren()));
					System.out.println("================getPzxid =====" +(data == null ? "null" :Long.toHexString(data.getMzxid())));
					System.out.println("================getMtime =====" +(data == null ? "null" : new Date(data.getMtime()).toString()));
					System.out.println("================getMzxid =====" +(data == null ? "null" : Long.toHexString(data.getMzxid())));
					System.out.println("================getVersion =====" +(data == null ? "null" : data.getVersion()));
					
					byte[] dataResult = zooKeeper.getData(dubboServer + "/" + path + "/providers/" + path2, true, data);
					
						System.out.println("this is data " + Arrays.toString(dataResult));
					  
			        String decode = URLDecoder.decode(path2, "UTF-8"); 
			        System.out.println("-----------------------"+decode);
				}
			}
			Stat stat = zooKeeper.exists(dubboServer, true);
			// System.out.println("-===================" +
			// zooKeeper.getData(dubboServer, watcher);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
*/
	}

	@Override
	public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
		// TODO Auto-generated method stub

	}

	public static void codeTest(String url) {
		if (url == null || (url = url.trim()).length() == 0) {
			throw new IllegalArgumentException("url == null");
		}
		String protocol = null;
		String username = null;
		String password = null;
		String host = null;
		int port = 0;
		String path = null;
		Map<String, String> parameters = null;
		int i = url.indexOf("?"); // seperator between body and parameters
		if (i >= 0) {
			String[] parts = url.substring(i + 1).split("\\&");
			parameters = new HashMap<String, String>();
			for (String part : parts) {
				part = part.trim();
				if (part.length() > 0) {
					int j = part.indexOf('=');
					if (j >= 0) {
						parameters.put(part.substring(0, j), part.substring(j + 1));
					} else {
						parameters.put(part, part);
					}
				}
			}
			url = url.substring(0, i);
		}
		i = url.indexOf("://");
		if (i >= 0) {
			if (i == 0)
				throw new IllegalStateException("url missing protocol: \"" + url + "\"");
			protocol = url.substring(0, i);
			url = url.substring(i + 3);
		} else {
			// case: file:/path/to/file.txt
			i = url.indexOf(":/");
			if (i >= 0) {
				if (i == 0)
					throw new IllegalStateException("url missing protocol: \"" + url + "\"");
				protocol = url.substring(0, i);
				url = url.substring(i + 1);
			}
		}

		i = url.indexOf("/");
		if (i >= 0) {
			path = url.substring(i + 1);
			url = url.substring(0, i);
		}
		i = url.indexOf("@");
		if (i >= 0) {
			username = url.substring(0, i);
			int j = username.indexOf(":");
			if (j >= 0) {
				password = username.substring(j + 1);
				username = username.substring(0, j);
			}
			url = url.substring(i + 1);
		}
		i = url.indexOf(":");
		if (i >= 0 && i < url.length() - 1) {
			port = Integer.parseInt(url.substring(i + 1));
			url = url.substring(0, i);
		}
		if (url.length() > 0)
			host = url;
		System.out.println("protocol" + protocol);
		System.out.println("username" + username);
		System.out.println("password" + password);
		System.out.println("host" + host);
		System.out.println("port" + port);
		System.out.println("path" + path);
		System.out.println("parameters" + parameters);
	}

}
