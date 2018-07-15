

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientTest {
	private static final int POOL_SIZE_SEND = 1;
	
	public static void main(String[] args) throws InterruptedException {
		testTcp();
//		testWebsocket();
	}
	
	public static void testTcp() throws InterruptedException{
		Executor executor = Executors.newFixedThreadPool(POOL_SIZE_SEND);
		ArrayList<TCPClientTask> clients = new ArrayList<TCPClientTask>();
		for (int i = 0; i < POOL_SIZE_SEND; i++) {
			executor.execute(new TCPClientTask());
			System.out.println("start cilent i = " + i);
			Thread.sleep(10);
		}
		for (TCPClientTask c : clients){
			c.waitSync();
		}
	}

	public static void testWebsocket() throws InterruptedException{
		Executor executor = Executors.newFixedThreadPool(POOL_SIZE_SEND);
		ArrayList<WebSocketClient> clients = new ArrayList<WebSocketClient>();
		for (int i = 0; i < POOL_SIZE_SEND; i++) {
			WebSocketClient client = new WebSocketClient();
			executor.execute(client);
			clients.add(client);
			System.out.println("start cilent i = " + i);
			Thread.sleep(50);
		}
		for (WebSocketClient c : clients){
			c.waitSync();
		}
	}
}
