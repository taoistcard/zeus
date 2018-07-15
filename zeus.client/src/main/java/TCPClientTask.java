
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import zeus.network.protocol.ClientMsg;
import zeus.network.protocol.GateClientMsgDecoder;
import zeus.network.protocol.GateClientMsgEncoder;
import zeus.network.protocol.GateCmd;
import zeus.network.protocol.MessageFactory;
import zeus.network.util.Constants;

/**
 * ˵����
 *
 * @author <a href="http://www.waylau.com">waylau.com</a> 2015��11��5��
 */
public class TCPClientTask implements Runnable {

	private Channel channel;

	private boolean aaa = false;

	public void waitSync() {
		if (channel == null) {
			return;
		}
		if (!channel.isActive()) {
			return;
		}
		try {
			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean useGateway = true;
		String host;
		int port;

		if (useGateway) {
			host = "127.0.0.1";
			port = 8000;
		} else {
			host = "127.0.0.1";
			port = 9000;
		}

		// TestTCPClient client = new TestTCPClient("localhost",
		// ServerConfig.roomServerPortForClient);
		ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				// ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
				ch.pipeline().addLast(new GateClientMsgEncoder());
				ch.pipeline().addLast(new GateClientMsgDecoder());
				ch.pipeline().addLast(new IdleStateHandler(5, 5, 5, TimeUnit.SECONDS));
				ch.pipeline().addLast(new SimpleTCPClientHandler());
			}
		};

		TCPClient client = new TCPClient("GateServer-BackendClient", host, port, new NioEventLoopGroup(), initializer);
		try {
			client.run();

			channel = client.getChannel();

			byte[] bytesF = "{\"cmd\":\"enter\",\"name\":\"frank\",\"age\":33}".getBytes(Charset.forName("utf-8"));
			byte[] totalBytes = new byte[66 + bytesF.length];
			totalBytes[0] = (byte) 32;
			byte[] accid = "d9e40973589a42289d54bdb8d25f73fa".getBytes(Constants.defaultCharset);
			totalBytes[33] = (byte) 32;
			byte[] token = "ce4eed3bb27f5920c76e5be864531332".getBytes(Constants.defaultCharset);
			System.arraycopy(accid, 0, totalBytes, 1, 32);
			System.arraycopy(token, 0, totalBytes, 34, 32);
			System.arraycopy(bytesF, 0, totalBytes, 66, bytesF.length);

			ClientMsg msgF = MessageFactory.createGateClientMsg((byte) 0x01, (byte) 0x02,
					(byte) GateCmd.AUTH_TRANS_FIRST, (short) 111, (short) 112, "4158bd2d6b0a46c6890d175851e750b8",
					totalBytes);

			System.out.println("sending msg lenth " + msgF.getLength());
			channel.writeAndFlush(msgF).sync();
			Thread.sleep(3000);

//			byte[] bytesF2 = "{\"cmd\":\"enter\",\"name\":\"frank\",\"age\":33}".getBytes(Charset.forName("utf-8"));
//			byte[] totalBytes2 = new byte[66 + bytesF2.length];
//			totalBytes2[0] = (byte) 32;
//			byte[] accid2 = "d9e40973589a42289d54bdb8d25f73fa".getBytes(Constants.defaultCharset);
//			totalBytes2[33] = (byte) 32;
//			byte[] token2 = "ce4eed3bb27f5920c76e5be864531332".getBytes(Constants.defaultCharset);
//			System.arraycopy(accid2, 0, totalBytes2, 1, 32);
//			System.arraycopy(token2, 0, totalBytes2, 34, 32);
//			System.arraycopy(bytesF2, 0, totalBytes2, 66, bytesF2.length);
//
//			ClientMsg msgF2 = MessageFactory.createGateClientMsg((byte) 0x01, (byte) 0x02,
//					(byte) GateCmd.TRANS_FIRST, (short) 111, (short) 112, "0248bad961fc455492bc66dea85f28a1",
//					bytesF2);
//
//			System.out.println("sending msg lenth " + msgF2.getLength());
//			channel.writeAndFlush(msgF2).sync();
//			Thread.sleep(3000);

			while (true) {

				if (channel == null) {
					break;
				}
				if (!channel.isActive()) {
					// channel.close().sync();
					break;
				}

				String cid = "";
				if (aaa) {
					cid = "4158bd2d6b0a46c6890d175851e750b8";
					aaa = false;
				} else {
					cid = "0248bad961fc455492bc66dea85f28a1";
					aaa = true;
				}

				byte[] bytes = "{\"cmd\":\"gift\",\"giftId\":1,\"giftCount\":1,\"continuous\":1}"
						.getBytes(Charset.forName("utf-8"));
				ClientMsg msg = MessageFactory.createGateClientMsg((byte) 0x01, (byte) 0x02, (byte) GateCmd.TRANS,
						(short) 111, (short) 112, cid, bytes);

//				System.out.println("sending.....");
//				channel.writeAndFlush(msg).sync();
				Thread.sleep(2000);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("shutdown............");
		}
	}

}
