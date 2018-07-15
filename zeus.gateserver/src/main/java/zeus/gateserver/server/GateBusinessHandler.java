package zeus.gateserver.server;

import org.apache.log4j.Logger;

import com.i5i58.util.Constant;
import com.i5i58.util.StringUtils;

import io.netty.channel.ChannelHandlerContext;
import zeus.gateserver.config.MyThreadPool;
import zeus.network.connector.ConnectorManager;
import zeus.network.connector.IConnector;
import zeus.network.handler.ChannelReadCallback;
import zeus.network.handler.HttpAuthCallback;
import zeus.network.handler.IBusinessHandler;
import zeus.network.manager.IRemotPeer;
import zeus.network.manager.RemotePeerManager;
import zeus.network.protocol.GateCmd;
import zeus.network.protocol.HttpClientMsg;
import zeus.network.protocol.IClientMsg;
import zeus.network.protocol.IntranetMsg;
import zeus.network.protocol.MessageFactory;
import zeus.network.util.Constants;

public class GateBusinessHandler implements IBusinessHandler {

	Logger logger = Logger.getLogger(getClass());

	@Override
	public void onChannelActive(ChannelHandlerContext ctx) {
	}

	@Override
	public void onChannelInactive(ChannelHandlerContext ctx, IRemotPeer peer) {
		do {
			if (peer == null)
				break;

			String accId = (String) peer.getUserData().get("accId");
			if (accId == null || accId.isEmpty())
				break;

			MyThreadPool.getUpdateOnlineThread().execute(()->{
				GateServer.getInstance().addOnlineCount(-1);
			});
			
			String serverKey = getServerLoggedIn(accId);
			if (serverKey == null)
				break;

			delServerLoggedIn(accId);

			IConnector connector = ConnectorManager.get(serverKey);
			if (connector == null)
				break;

			String sessionId = ctx.channel().id().asLongText();
			byte[] sessionIdBytes = sessionId.getBytes(Constants.defaultCharset);
			IntranetMsg DISCONN = MessageFactory.createIntranetMsg(sessionIdBytes, Constants.PROTOCOL_VERSION,
					(byte) 0x00, GateCmd.DISCONNECT, (short) 0, (short) 0, null);
			connector.send(DISCONN);

			// @SuppressWarnings("unchecked")
			// Map<String, IConnector> personalConnectors = (Map<String,
			// IConnector>) peer.getUserData()
			// .get(Constants.CONNECTORS);
			// if (personalConnectors == null)
			// break;
			//
			// String sessionId = ctx.channel().id().asLongText();
			// byte[] sessionIdBytes = sessionId.getBytes();
			// for (IConnector connector : personalConnectors.values()) {
			// IntranetMsg DISCONN =
			// MessageFactory.createIntranetMsg(sessionIdBytes,
			// Constants.PROTOCOL_VERSION,
			// (byte) 0x00, GateCmd.DISCONNECT, (short) 0, (short) 0, null);
			// connector.send(DISCONN);
			// }
			
		} while (false);
	}

	@Override
	public void onExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void auth(ChannelHandlerContext ctx, IClientMsg msg, ChannelReadCallback channelReadCallback) {
		try {
			switch (msg.getGate()) {
			case GateCmd.AUTH_TRANS_FIRST:
				AuthTask task = new AuthTask(ctx, msg, channelReadCallback);
				MyThreadPool.getAuthTaskPool().execute(task);
				break;
			default:
				String sessionId = ctx.channel().id().asLongText();
				IRemotPeer peer = null;
				if (RemotePeerManager.containsKey(sessionId)) {
					peer = RemotePeerManager.getClient(sessionId);
					channelReadCallback.OnAuthSuccess(peer, msg);
				} else {
					logger.error("auth.OnAuthFailed");
					channelReadCallback.OnAuthFailed(ctx, msg);
				}
				return;
			}
		} catch (Exception e) {
			logger.error("网关业务逻辑处理验证：", e);
		} finally {

		}
	}

	public class AuthTask implements Runnable {
		ChannelHandlerContext ctx;
		IClientMsg msg;
		ChannelReadCallback channelReadCallback;

		public AuthTask(ChannelHandlerContext ctx, IClientMsg msg, ChannelReadCallback channelReadCallback) {
			this.ctx = ctx;
			this.msg = msg;
			this.channelReadCallback = channelReadCallback;
		}

		@Override
		public void run() {
			try {
				byte accIdLength = msg.getData()[0];
				String accId = new String(msg.getData(), 1, accIdLength, Constants.defaultCharset);
				byte tokenLength = msg.getData()[accIdLength + 1];
				String token = new String(msg.getData(), accIdLength + 2, tokenLength, Constants.defaultCharset);
				if (this.doAuth(accId, token)) {
					int length = accIdLength + tokenLength + 2;
					if (msg.getData().length > length) {
						byte[] data = new byte[msg.getData().length - length];
						System.arraycopy(msg.getData(), length, data, 0, data.length);
						msg.setData(data);
						msg.setLength(data.length);
					} else {
						msg.setData(null);
						msg.setLength(0);
					}
					MyThreadPool.getUpdateOnlineThread().execute(()->{
						GateServer.getInstance().addOnlineCount(1);
					});
					
					channelReadCallback.OnDoAuthSuccess(ctx, msg, "accId", accId);
				} else {
					channelReadCallback.OnAuthFailed(ctx, msg);
				}
			} catch (Exception e) {
				logger.error("网关业务逻辑处理验证任务：", e);
			} finally {

			}
		}

		private boolean doAuth(String accId, String token) {
			if (!StringUtils.StringIsEmptyOrNull(accId) && !StringUtils.StringIsEmptyOrNull(token)) {
				// 验证token
				// String key = Constant.HOT_ACCOUNT_TOKEN_SET_KEY + accId;
				String key = Constant.HOT_ACCOUNT_TOKEN_SET_KEY + accId;
				System.out.println(accId + "=====" + token + "======" + key);
				if (GateServer.getJedisUtils().exist(key) && GateServer.getJedisUtils().get(key).equals(token)) {
					// 如果token验证成功，将token对应的用户id存在request中，便于之后注入
					System.out.println("yes token");
					return true;
				} else {
					System.out.println("token failed");
					return false;
				}
			} else {
				System.out.println("token failed:params null");
				return false;
			}
		}
	}

	public String getServerLoggedIn(String accId) {
		return GateServer.getJedisUtils().hget(Constant.HOT_USERDATA + accId, Constant.SUB_USERDATA_SERVER_LOGGED_IN);
	}

	public void setServerLoggedIn(String serverKey, String accId) {
		GateServer.getJedisUtils().hset(Constant.HOT_USERDATA + accId, Constant.SUB_USERDATA_SERVER_LOGGED_IN,
				serverKey);
	}

	public void delServerLoggedIn(String accId) {
		GateServer.getJedisUtils().hdel(Constant.HOT_USERDATA + accId, Constant.SUB_USERDATA_SERVER_LOGGED_IN);
	}

	// public void addPersonalConnector(IRemotPeer peer, IClientMsg msg){
	// String serverKey = msg.getServerKey();
	// IConnector connector = null;
	// switch (msg.getGate()) {
	// case GateCmd.AUTH_TRANS_FIRST:
	// if (serverKey != null && !serverKey.isEmpty()) {
	// connector = ConnectorManager.get(serverKey);
	// }
	// break;
	//
	// case GateCmd.SWITCH:
	// break;
	// case GateCmd.SWITCH_TRANS:
	// break;
	// default:
	// break;
	// }
	// // add new connector
	// if (connector != null) {
	// String accId = (String) peer.getUserData().get("accId");
	// setCurrentServer(serverKey, accId);
	//// @SuppressWarnings("unchecked")
	//// Map<String, IConnector> personalConnectors = (Map<String, IConnector>)
	// peer.getUserData()
	//// .get(Constants.CONNECTORS);
	//// if (personalConnectors == null) {
	//// personalConnectors = new HashMap<String, IConnector>();
	//// peer.getUserData().put(Constants.CONNECTORS, personalConnectors);
	//// }
	//// if (!personalConnectors.containsKey(serverKey)) {
	//// personalConnectors.put(serverKey, connector);
	//// System.out.println("----addPersonalConnector------ " + serverKey);
	//// }
	// }else{
	// logger.error("cannot find server connector.");
	// }
	// }

	// public void resetPersonalConnector(IRemotPeer peer, IClientMsg msg){
	// String accId = (String) peer.getUserData().get("accId");
	// delPersonalConnector(peer, msg);
	// setCurrentServer(msg.getServerKey(), accId);
	// System.out.println("----resetPersonalConnector------ " +
	// msg.getServerKey());
	// }
	//
	// public void delPersonalConnector(IRemotPeer peer, IClientMsg msg){
	//// @SuppressWarnings("unchecked")
	//// Map<String, IConnector> personalConnectors = (Map<String, IConnector>)
	// peer.getUserData().get(Constants.CONNECTORS);
	//
	//// String accId = (String) peer.getUserData().get("accId");
	//
	//// String preServerKey = getCurrentServer(accId);
	//
	//// if (preServerKey != null && !preServerKey.isEmpty()){
	//// IConnector preConnector = ConnectorManager.get(preServerKey);
	//// if (personalConnectors != null){
	//// personalConnectors.remove(preServerKey);
	//// System.out.println("----delPersonalConnector------ " + preServerKey);
	//// }
	//// if (preConnector != null){
	//// preConnector.send(GateCmd.createVirtualCloseMsg(msg.getSessionId()));
	//// }
	//// }
	//// delCurrentServer(accId);
	// }

	@Override
	public void onChannelRead0(IRemotPeer peer, IClientMsg msg) {
		try {
			// System.out.println(String.format(
			// "auth close: channelId:%s, meg: [version:%s, crc:%s, length:%s,
			// gate:%s, main:%s, sub:%s]",
			// ctx.channel().id().asLongText(), msg.getVersion(), msg.getCrc(),
			// msg.getLength(), msg.getGate(),
			// msg.getMain(), msg.getSub()));

			String accId = (String) peer.getUserData().get("accId");
			String serverKey = msg.getServerKey();
			IConnector targetConnector = null;
			if (serverKey != null && !serverKey.isEmpty()) {
				targetConnector = ConnectorManager.get(serverKey);
			}

			switch (msg.getGate()) {
			// case GateCmd.AUTH:
			// break;
			case GateCmd.TRANS_FIRST:
				if (targetConnector != null) {
					if (peer != null && peer.getUserData().containsKey("accId")) {
						IntranetMsg TRANS_FIRST = new IntranetMsg(msg.getSessionId(), msg);
						targetConnector.sendWithAccId(TRANS_FIRST, peer.getUserData().get("accId").toString());// "d9e40973589a42289d54bdb8d25f73fa");
					} else {
						logger.error("TRANS_FIRST user auth failed");
					}
				} else {
					logger.error("TRANS_FIRST cannot find target the server.");
				}
				break;
			case GateCmd.AUTH_TRANS_FIRST:
				if (targetConnector != null) {
					setServerLoggedIn(serverKey, accId);
					IntranetMsg AUTH_TRANS_FIRST = new IntranetMsg(msg.getSessionId(), msg);
					targetConnector.sendWithAccId(AUTH_TRANS_FIRST, peer.getUserData().get("accId").toString());// "d9e40973589a42289d54bdb8d25f73fa");
				} else {
					logger.error("AUTH_TRANS_FIRST cannot find target the server.");
				}
				break;
			case GateCmd.TRANS:
				if (targetConnector != null) {
					IntranetMsg TRANS = new IntranetMsg(msg.getSessionId(), msg);
					targetConnector.send(TRANS);
				} else {
					logger.error("TRANS cannot find target the server.");
				}
				break;
			case GateCmd.DISCONNECT:
				if (targetConnector != null) {
					IntranetMsg virtualClose = GateCmd.createVirtualCloseMsg(msg.getSessionId());
					targetConnector.send(virtualClose);
				} else {
					logger.error("DISCONNECT cannot find target the server.");
				}
				break;
			case GateCmd.SWITCH:
			case GateCmd.SWITCH_TRANS: {
				IConnector preConn = null;
				String preServerKey = getServerLoggedIn(accId);
				if (preServerKey != null) {
					preConn = ConnectorManager.get(preServerKey);
				}
				if (preConn == targetConnector) {
					break;
				}
				delServerLoggedIn(accId);
				setServerLoggedIn(serverKey, accId);
				if (msg.getGate() == GateCmd.SWITCH_TRANS) {
					IntranetMsg SWITCH_TRANS = new IntranetMsg(msg.getSessionId(), msg);
					preConn.send(SWITCH_TRANS);
					targetConnector.send(SWITCH_TRANS);
				}
			}
				break;
			default:
				onDefenseCC(peer.getContext(), msg);
				break;
			}
		} catch (Exception e) {
			logger.error("网关业务逻辑处理接收消息：", e);
		} finally {

		}
	}

	@Override
	public void onDefenseCC(ChannelHandlerContext ctx, IClientMsg msg) {

		logger.error(String.format(
				"onDefenseCC: [channelId:%s, ip:%s], meg: [version:%s, crc:%s, length:%s, gate:%s, main:%s, sub:%s]",
				ctx.channel().id().asLongText(), ctx.channel().remoteAddress(), msg.getVersion(), msg.getCrc(),
				msg.getLength(), msg.getGate(), msg.getMain(), msg.getSub()));
	}

	@Override
	public boolean beforeServerStart() {
		return true;

	}

	@Override
	public void afterServerStart() {

	}

	@Override
	public void httpAuth(ChannelHandlerContext httpCtx, HttpClientMsg msg, HttpAuthCallback callback) {
		assert false : "http auth is not supported on gate";
	}

	@Override
	public void onHttpRequest(ChannelHandlerContext httpCtx, IRemotPeer localPeer, HttpClientMsg msg) {
		assert false : "http request is not supported on gate";
	}
}
