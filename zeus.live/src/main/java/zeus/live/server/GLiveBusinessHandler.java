package zeus.live.server;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import zeus.live.config.MyThreadPool;
import zeus.live.data.ResultDataSet;
import zeus.network.handler.ChannelReadCallback;
import zeus.network.handler.HttpAuthCallback;
import zeus.network.handler.IBusinessHandler;
import zeus.network.manager.IRemotPeer;
import zeus.network.manager.RemotePeerManager;
import zeus.network.protocol.GateCmd;
import zeus.network.protocol.HttpClientMsg;
import zeus.network.protocol.IClientMsg;
import zeus.network.util.Constants;
import zeus.network.util.HttpClientMsgUtil;
import zeus.network.util.HttpResponseUtil;

@Component
class GLiveBusinessHandler implements IBusinessHandler {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private IBusinessProcessor businessProcessor;

	/**
	 * Reference to global instance
	 */
	private LiveDataService liveDataService;

	@Override
	public boolean beforeServerStart() {
		String cId = LiveServer.getInstance().getServerInfo().getServerKey();
		liveDataService = LiveDataService.getInstance();
		return BusinessUtil.beforeServerStart() & liveDataService.beforeServerStart(cId)
				& businessProcessor.beforeServerStart();
	}

	@Override
	public void afterServerStart() {
		businessProcessor.afterServerStart();
	}

	@Override
	public void onChannelActive(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChannelInactive(ChannelHandlerContext ctx, IRemotPeer peer) {
		if (peer == null) {
			Set<IRemotPeer> peers = RemotePeerManager.getAllVirtual(ctx);
			if (peers == null || peers.isEmpty())
				return;
			// because the gateway is offline, remove all client on the gateway.
			for (IRemotPeer p : peers) {
				businessProcessor.userExit(p);
			}
		} else {
			// it is real client connected directly.
			businessProcessor.userExit(peer);
		}
	}

	@Override
	public void onExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void auth(ChannelHandlerContext ctx, IClientMsg msg, ChannelReadCallback channelReadCallback) {
		try {

			switch (msg.getGate()) {
			case GateCmd.TRANS_FIRST:
			case GateCmd.AUTH_TRANS_FIRST:
				LoginTask loginTask = new LoginTask(ctx, msg, channelReadCallback);
				MyThreadPool.getLoginTaskPool().execute(loginTask);
				return;
			default:
				IRemotPeer peer = null;
				if (RemotePeerManager.containsKey(msg.getSessionId())) {
					peer = RemotePeerManager.getClient(msg.getSessionId());
					channelReadCallback.OnAuthSuccess(peer, msg);
				} else {
					channelReadCallback.OnAuthFailed(ctx, msg);
				}
				return;
			}
		} catch (Exception e) {
			logger.error("业务逻辑处理验证：", e);
		} finally {

		}
	}

	public class LoginTask implements Runnable {
		ChannelHandlerContext ctx;
		IClientMsg msg;
		ChannelReadCallback channelReadCallback;

		public LoginTask(ChannelHandlerContext ctx, IClientMsg msg, ChannelReadCallback channelReadCallback) {
			this.ctx = ctx;
			this.msg = msg;
			this.channelReadCallback = channelReadCallback;
		}

		@Override
		public void run() {
			try {
				businessProcessor.login(ctx, msg, channelReadCallback);
			} catch (Exception e) {
				logger.error("业务逻辑处理登录任务：", e);
			} finally {

			}
		}
	}

	@Override
	public void onChannelRead0(IRemotPeer peer, IClientMsg msg) {
		try {
			if (msg.getGate() == GateCmd.DISCONNECT) {
				businessProcessor.userExit(peer);
				return;
			}
			logger.info(new String(msg.getData(), Constants.defaultCharset));
			JSONObject json = JSON.parseObject(new String(msg.getData(), Constants.defaultCharset));
			String cmd = json.get("cmd").toString();
			ResultDataSet rds = new ResultDataSet();
			rds.setCmd(cmd);
			switch (cmd) {
			case "enter":
				rds = businessProcessor.enterChannel(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "gift":
				rds = businessProcessor.giveGift(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "micCompleted":
				rds = businessProcessor.micCompleted(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "checkConMic": // 返回是否有连麦权限 ，有权限true,没有权限 false
				rds = businessProcessor.isConMic(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "audioMute": // 音频开/关禁言 ，返回解禁状态 true，禁言状态false
				rds = businessProcessor.audioMute(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "setMicSequence": // 设置麦序
				rds = businessProcessor.setMicSequence(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			case "removeMicSequence":// 移除麦序
				rds = businessProcessor.removeMicSequence(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;

			case "setMute":// 频道禁言
				rds = businessProcessor.setMute(peer, json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
				
			case "exitChannel": //退出直播间
				rds = businessProcessor.exitChannel(peer,json);
				peer.send((byte) 1, (byte) 0, GateCmd.TRANS, (short) 1, (short) 2, rds);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error("业务逻辑处理数据：", e);
		} finally {

		}
	}

	@Override
	public void onDefenseCC(ChannelHandlerContext ctx, IClientMsg msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void httpAuth(ChannelHandlerContext httpCtx, HttpClientMsg msg, HttpAuthCallback callback) {
		HttpAuthTask task = new HttpAuthTask(httpCtx, msg, callback);
		MyThreadPool.getHttpAuthTaskPool().execute(task);
	}

	public class HttpAuthTask implements Runnable {
		private ChannelHandlerContext httpCtx;
		private HttpClientMsg msg;
		private HttpAuthCallback callback;

		public HttpAuthTask(ChannelHandlerContext httpCtx, HttpClientMsg msg, HttpAuthCallback callback) {
			this.httpCtx = httpCtx;
			this.msg = msg;
			this.callback = callback;
		}

		@Override
		public void run() {
			String accId = msg.getAccId();
			String sessionId = BusinessUtil.getSessionId(accId);
			IRemotPeer peer = RemotePeerManager.getClient(sessionId);
			callback.onCallback(httpCtx, peer, msg);
		}

	}

	@Override
	public void onHttpRequest(ChannelHandlerContext httpCtx, IRemotPeer localPeer, HttpClientMsg msg) {
		System.out.println("http client msg : \r\n" + JSON.toJSONString(msg));
		ResultDataSet rds = null;
		try {
			switch (msg.getUri()) {
			case "/hello": {
				String accId = HttpClientMsgUtil.getString(msg, "accId");
				Integer money = HttpClientMsgUtil.getInt(msg, "money");
				if (accId != null && money != null) {
					rds = businessProcessor.sayHello(accId, money.intValue());
				}
			}
				break;
			case "/account/buyAccountVip":
				Integer levelVip = HttpClientMsgUtil.getInt(msg, "level");
				Integer monthVip = HttpClientMsgUtil.getInt(msg, "month");
				if (levelVip != null && monthVip != null) {
					rds = businessProcessor.buyAccountVip(localPeer, msg.getAccId(), levelVip, msg.getClientIp(),
							monthVip);
				}

				break;
			case "/account/upgradeAccountVip":
				Integer levelUVip = HttpClientMsgUtil.getInt(msg, "level");
				if (levelUVip != null) {
					rds = businessProcessor.upgradeAccountVip(localPeer, msg.getAccId(), levelUVip, msg.getClientIp());
				}
				break;
			case "/channel/openGuard":
				String cIdGuard = HttpClientMsgUtil.getString(msg, "cId");
				Integer levelGuard = HttpClientMsgUtil.getInt(msg, "level");
				Integer monthGuard = HttpClientMsgUtil.getInt(msg, "month");
				if (cIdGuard != null && levelGuard != null && monthGuard != null) {
					rds = businessProcessor.openGuard(localPeer, msg.getAccId(), cIdGuard, levelGuard, monthGuard,
							msg.getClientIp());
				}

				break;
			case "/channel/openClub":
				String cIdClub = HttpClientMsgUtil.getString(msg, "cId");
				Integer monthClub = HttpClientMsgUtil.getInt(msg, "month");
				if (cIdClub != null && monthClub != null) {
					rds = businessProcessor.openClub(localPeer, msg.getAccId(), cIdClub, monthClub, msg.getClientIp());
				}
				break;

			case "/account/buyMount":
				String mountId = HttpClientMsgUtil.getString(msg, "mountId");
				Integer monthMount = HttpClientMsgUtil.getInt(msg, "month");
				if (mountId != null && monthMount != null) {
					rds = businessProcessor.openClub(localPeer, msg.getAccId(), mountId, monthMount, msg.getClientIp());
				}
				break;

			case "/channel/buyGuardMount":
				Integer mountIdGuard = HttpClientMsgUtil.getInt(msg, "mountId");
				String cIdMount = HttpClientMsgUtil.getString(msg, "cId");
				if (mountIdGuard != null && cIdMount != null) {
					rds = businessProcessor.buyGuardMount(localPeer, msg.getAccId(), mountIdGuard, cIdMount);
				}
				break;

			default:
				rds = new ResultDataSet();
				rds.setCode("falied");
				rds.setData(msg.getUri() + " --- no uri processor");
				break;
			}

		} catch (Exception e) {

		} finally {
			HttpResponseUtil.sendResponse(httpCtx, msg.isKeepAlive(), rds);
		}
	}
}
