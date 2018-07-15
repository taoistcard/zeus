package zeus.manager.handler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.patterns.PointcutEvaluationExpenseComparator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import zeus.manager.data.ResponseData;
import zeus.manager.data.ResultCode;
import zeus.manager.data.ResultDataSet;
import zeus.manager.server.ManagerDataService;
import zeus.manager.server.SSHUtil;
import zeus.network.handler.IBusinessHandler;
import zeus.network.manager.IRemotPeer;
import zeus.network.manager.RemotePeerManager;
import zeus.network.manager.WsRemotePeer;
import zeus.network.protocol.GateCmd;
import zeus.network.threading.ITaskPool;
import zeus.network.threading.TaskQueue;

@io.netty.channel.ChannelHandler.Sharable
public class MyTextWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	Logger logger = Logger.getLogger(getClass());
	private ManagerDataService managerDataService;
	private IBusinessHandler businessHandler;
	private ITaskPool taskPool;
	String[][] rpcServiceList;
	String linuxPath;

	public MyTextWebSocketHandler(ITaskPool taskPool, ManagerDataService managerDataService) {
		this.taskPool = taskPool;
		this.managerDataService = managerDataService;
	}

	public void setManagerDataService(ManagerDataService managerDataService) {
		this.managerDataService = managerDataService;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("MyTextWebSocketHandler is channelActive");
		String sessionId = ctx.channel().id().asLongText();
		WsRemotePeer peer = new WsRemotePeer(sessionId, ctx, null);
		RemotePeerManager.put(sessionId, peer);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("MyTextWebSocketHandler is inactive");
		String sessionId = ctx.channel().id().asLongText();
		IRemotPeer peer = RemotePeerManager.getClient(sessionId);
		if (peer != null) {
			TaskQueue queue = peer.getTaskQueue();
			if (queue != null) {
				MyChannelInactiveTask task = new MyChannelInactiveTask(ctx, peer);
				queue.addTask(task);
			} else {
				RemotePeerManager.removeClient(sessionId);
				// NetworkUtil.closeGracefully(ctx);
				System.out.println("remove peer");
			}
		} else {
			// NetworkUtil.closeGracefully(ctx);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		businessHandler.onExceptionCaught(ctx, cause);

		System.out.println("MyTextWebSocketHandler is exceptionCaught");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		try {
			logger.info(msg.text());
			JSONObject json = JSON.parseObject(msg.text());
			String cmd = json.get("cmd").toString();
			ResultDataSet rds = new ResultDataSet();
			String sessionId = ctx.channel().id().asLongText();
			WsRemotePeer peer = (WsRemotePeer) RemotePeerManager.getClient(sessionId);

			switch (cmd) {
			case "initService":
				rds = initService(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "getRPCServer":
				rds = getRPCServer(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "getRPCServiceList":
				rds = getRPCServiceList(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "getChildNodes":
				rds = getChildNodes(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "closeService":
				rds = closeService(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;

			case "openService":
				rds = openService(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;

			case "updateService":
				rds = updateService(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "sayHello":
				rds = sayHello("12345645646546465465huanglinghello", 999);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;

			case "deteleRpcNode":
				rds = deteleRpcNode(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error("error", e);
		} finally {

		}
	}

	public ResultDataSet initService(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		String type = json.get("type").toString();
		ResultDataSet rds = new ResultDataSet();
		rds.setCode("success");
		managerDataService.setConnectRPC(type);
		managerDataService.setLinuxPath(type);
		managerDataService.setRpcServiceList(type);
		managerDataService.InitDataRPC();
		rpcServiceList = managerDataService.getRpcServiceList();
		linuxPath = managerDataService.getLinuxPath();
		rds.setCmd(cmd);
		return rds;
	}

	public ResultDataSet sayHello(String accId, int money) {
		ResultDataSet rds = new ResultDataSet();
		JSONObject object = new JSONObject();
		object.put("accId", accId);
		object.put("money", money);
		rds.setCode("success");
		rds.setData(object.toJSONString());
		return rds;
	}

	public ResultDataSet deteleRpcNode(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		rds.setCmd(cmd);
		try {
			if (managerDataService.deleteZookeeperNodes()) {
				rds.setCode(ResultCode.SUCCESS.getCode());
				rds.setMsg("删除rpc节点成功");
				return rds;
			}

		} catch (Exception e) {

		}
		rds.setCode(ResultCode.PARAM_INVALID.getCode());
		rds.setMsg("删除节点失败");
		return rds;

	}

	public ResultDataSet closeService(IRemotPeer peer, JSONObject json) {
		System.out.println("关闭");
		String cmd = json.get("cmd").toString();
		String name = json.get("name").toString();
		String serviceNode = json.get("service").toString();
		// System.out.println(cmd + name + serviceNode);
		ResultDataSet rds = new ResultDataSet();
		SSHUtil ssh1 = null;
		ResponseData responseData = new ResponseData();
		rds.setCmd(cmd);
		responseData.put("service", serviceNode);
		rds.setData(responseData);
		try {
			for (int i = 0; i < rpcServiceList.length; i++) {
				if (rpcServiceList[i][0].equals(name)) {
					ssh1 = new SSHUtil(rpcServiceList[i][1], rpcServiceList[i][2], rpcServiceList[i][3]);
					System.out.println(
							rpcServiceList[i][1] + " ==== " + rpcServiceList[i][2] + " ==== " + rpcServiceList[i][3]);
					String command = "cd " + linuxPath + ";./stop-" + serviceNode + ".sh";
					ssh1.execCommand(command);
					// String command = " ps -ef|grep -E
					// 'service-0.0.1-SNAPSHOT.jar' | grep -v grep|awk '{print
					// $2}'|xargs kill -9";
					while (true) {
						String line = ssh1.readLine();
						if (line == null) {
							break;
						}
						System.out.println(line);

					}
					ssh1.closeSession();
					ssh1.close();
					rds.setCode(ResultCode.SUCCESS.getCode());
					rds.setMsg("关闭服务成功");
					return rds;

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		rds.setCode(ResultCode.PARAM_INVALID.getCode());
		rds.setMsg("关闭服务失败");
		return rds;
	}

	public ResultDataSet openService(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		String name = json.get("name").toString();
		String serviceNode = json.get("service").toString();
		System.out.println(cmd + name + serviceNode);
		ResultDataSet rds = new ResultDataSet();
		SSHUtil ssh1 = null;
		ResponseData responseData = new ResponseData();
		rds.setCmd(cmd);
		responseData.put("service", serviceNode);
		rds.setData(responseData);
		try {
			for (int i = 0; i < rpcServiceList.length; i++) {
				if (rpcServiceList[i][0].equals(name)) {
					ssh1 = new SSHUtil(rpcServiceList[i][1], rpcServiceList[i][2], rpcServiceList[i][3]);
					// System.out.println(rpcServiceList[i][1] + " ==== "
					// +rpcServiceList[i][2] + " ==== " +rpcServiceList[i][3]);
					ssh1.execCommand("cd " + linuxPath + ";./start-" + serviceNode + ".sh");
					// ssh1.execCommand(" ps -ef|grep SNAPSHOT|grep -v grep");
					while (true) {
						String line = ssh1.readLine();
						if (line == null) {
							break;
						}

						System.out.println(line);

					}
					ssh1.closeSession();
					ssh1.close();
					rds.setCode(ResultCode.SUCCESS.getCode());
					rds.setMsg("开启服务成功");
					return rds;
				}
			}
			// name,user,password

		} catch (Exception e) {
			// TODO: handle exception
		}
		rds.setCode(ResultCode.PARAM_INVALID.getCode());
		rds.setMsg("关闭服务失败");
		return rds;
	}

	public ResultDataSet updateService(IRemotPeer peer, JSONObject json) {
		SSHUtil ssh1 = null;
		String cmd = json.get("cmd").toString();
		String name = json.get("name").toString();
		String serviceNode = json.get("service").toString();
		System.out.println(cmd + name + serviceNode);
		ResultDataSet rds = new ResultDataSet();
		ResponseData responseData = new ResponseData();
		rds.setCmd(cmd);
		responseData.put("service", serviceNode);
		rds.setData(responseData);
		try {
			for (int i = 0; i < rpcServiceList.length; i++) {
				System.out.println(name + "-----" + rpcServiceList[i][5]);
				if (rpcServiceList[i][0].equals(name)) {
					ssh1 = new SSHUtil(rpcServiceList[i][1], rpcServiceList[i][2], rpcServiceList[i][3]);

					// System.out.println(fileName);
					String command = "cp -f " + linuxPath + "/" + rpcServiceList[i][5] + "/" + serviceNode
							+ "-0.0.1-SNAPSHOT.jar " + linuxPath + "/";
					System.out.println(command);
					ssh1.execCommand(command);
					ssh1.closeSession();
					ssh1.close();
					rds.setCode(ResultCode.SUCCESS.getCode());
					rds.setMsg("更新源码成功");
					return rds;
				}
			}
			// name,user,password

		} catch (Exception e) {
			// TODO: handle exception
		}
		rds.setCode(ResultCode.PARAM_INVALID.getCode());
		rds.setMsg("更新源码失败");
		return rds;
	}

	public ResultDataSet getRPCServer(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		ResponseData response = new ResponseData();
		rds.setCmd(cmd);
		response.put("head", managerDataService.dubboServerRPC);
		response.put("ProviderListItems", managerDataService.getProvidersListRPC());
		response.put("ConsumerListItems", managerDataService.getConsumersListRPC());
		response.put("childNodes", managerDataService.getChildNodes());
		rds.setData(response);
		rds.setCode(ResultCode.SUCCESS.getCode());
		return rds;
	}

	public ResultDataSet getChildNodes(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		ResponseData response = new ResponseData();
		rds.setCmd(cmd);
		response.put("head", managerDataService.dubboServerRPC);
		response.put("childNodes", managerDataService.getChildNodes());
		rds.setData(response);
		rds.setCode(ResultCode.SUCCESS.getCode());
		return rds;
	}

	public ResultDataSet getRPCServiceList(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		ResponseData response = new ResponseData();
		SSHUtil ssh = null;
		for (int i = 0; i < rpcServiceList.length; i++) {
			try {
				ResponseData responseSec = new ResponseData();
				ssh = new SSHUtil(rpcServiceList[i][1], rpcServiceList[i][2], rpcServiceList[i][3]); // name,user,password
				ssh.execCommand(" ps -ef|grep SNAPSHOT|grep -v grep");
				String name = rpcServiceList[i][0];
				String[] sList = new String[20];
				int j = 0;
				while (true) {// 将目录下的所有jar包文件记录在sList中
					String line = ssh.readLine();
					if (line == null) {
						break;
					}
					String[] re = line.split("\\ ");
					for (String xString : re) {
						if (xString.indexOf("-SNAPSHOT.jar") != -1) {
							sList[j] = xString;
						}
					}
					j++;
				}
				ssh.closeSession();
				String[] resulet2 = new String[30];
				ssh.execCommand("cd " + linuxPath + "; ls -l");
				int k = 1;
				long timeLast = 0L;
				String buildPath = "";
				while (true) {// 筛选出最新的build文件名存入二维数组rpcServiceList
					String line2 = ssh.readLine();
					if (line2 == null) {
						break;
					}
					if (line2.contains("build-")) {
						resulet2[0] = line2;
						String buildPaths = line2.substring(line2.indexOf("build-"));
						line2 = line2.substring(line2.indexOf("build-") + 6);
						SimpleDateFormat simFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date dtBeg = simFormat.parse(line2);
						long timeNow = dtBeg.getTime();
						if (timeNow > timeLast) {
							timeLast = timeNow;
							buildPath = buildPaths;
						}

					}
					if (line2.contains("-0.0.1-SNAPSHOT.jar")) {
						resulet2[k] = line2;
						k++;
					}
				}
				ssh.closeSession();
				rpcServiceList[i][5] = buildPath.substring(0, 16) + "\\" + buildPath.substring(16, 25);
				ssh.close();
				responseSec.put("count", j);
				responseSec.put("name", rpcServiceList[i][0]);//服务器名称
				responseSec.put("ip", rpcServiceList[i][4]); //服务器IP
				responseSec.put("apis", sList); //进程中的服务
				responseSec.put("serviceInfo", resulet2);//根目录下的jar包 
				response.put(name, responseSec);

			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				ssh.close();
			}

		}
		/*
		 * try { ssh = new SSHUtil("116.62.35.78", "root", "Tiger2016");
		 * ssh.execCommand(" ps -ef|grep SNAPSHOT|grep -v grep"); while (true) {
		 * String line = ssh.readLine(); if (line == null ) { break; }
		 * System.out.println("line>>>:" + line); // channel.publish(client,
		 * line, null); } } catch (IOException e) { e.printStackTrace(); throw
		 * new RuntimeException(e); } finally { ssh.close(); }
		 */
		rds.setData(response);
		rds.setCode(ResultCode.SUCCESS.getCode());
		rds.setCmd(cmd);
		return rds;
	}

}