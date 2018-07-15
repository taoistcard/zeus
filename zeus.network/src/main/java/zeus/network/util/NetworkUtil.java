package zeus.network.util;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class NetworkUtil {
	public static String getLocalHost() {
		Enumeration<NetworkInterface> allNetInterfaces;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		InetAddress addr = null;
		String innerIp = "";
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//			System.out.println(netInterface.getName());
			Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				addr = (InetAddress) addresses.nextElement();
				if (addr != null && addr instanceof Inet4Address) {
					String ip = addr.getHostAddress();
					ip.trim();
					System.out.println("IP = " + addr.getHostAddress());
					if (ip.equals("127.0.0.1")){
						continue;
					}
					/**
					 * It is a internal address.
					 * */
					if (ip.indexOf("10.") == 0){
						innerIp = ip;
						continue;
					}
					
					return ip;
				}
			}
		}
		return innerIp;
	}
	
	public static int getFreePort(){
		ServerSocket serverSocket;
		try {
			for (int i=10000; i<65536; i++){
				try {
					serverSocket = new ServerSocket(i);
					int port = serverSocket.getLocalPort();
					serverSocket.close();
					return port;
				} catch (BindException e) {
					// TODO: handle exception
					continue;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 读取空闲的可用端口
		
		return 0;
	}
	
	public static void closeGracefully(ChannelHandlerContext ctx){
		if (ctx.channel() != null && ctx.channel().isActive()) {
			ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}
}
