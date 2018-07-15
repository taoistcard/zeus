package zeus.live.server;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.http.protocol.ResponseDate;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.i5i58.clubTask.ClubTaskUtils;
import com.i5i58.data.account.Account;
import com.i5i58.data.account.AccountProperty;
import com.i5i58.data.account.AccountVipConfig;
import com.i5i58.data.account.HotAccount1;
import com.i5i58.data.account.HotAccountVipConfig;
import com.i5i58.data.account.MountStore;
import com.i5i58.data.account.Wallet;
import com.i5i58.data.channel.ChGoodsType;
import com.i5i58.data.channel.Channel;
import com.i5i58.data.channel.ChannelAdminor;
import com.i5i58.data.channel.ChannelAuth;
import com.i5i58.data.channel.ChannelFansClub;
import com.i5i58.data.channel.ChannelGuard;
import com.i5i58.data.channel.ChannelGuardConfig;
import com.i5i58.data.channel.ChannelMount;
import com.i5i58.data.channel.ChannelRecord;
import com.i5i58.data.channel.ConnectMicInfo;
import com.i5i58.data.channel.HotChannel;
import com.i5i58.data.channel.HotChannelGift;
import com.i5i58.data.channel.HotChannelGuardConfig;
import com.i5i58.data.channel.HotChannelMic;
import com.i5i58.data.channel.HotChannelMount;
import com.i5i58.data.channel.HotChannelViewer;
import com.i5i58.data.channel.HotFansClubConfig;
import com.i5i58.data.record.GoodsType;
import com.i5i58.data.record.RecordConsumption;
import com.i5i58.userTask.TaskUtil;
import com.i5i58.util.AuthVerify;
import com.i5i58.util.ChannelUtils;
import com.i5i58.util.DataSaveThread;
import com.i5i58.util.DateUtils;
import com.i5i58.util.JedisUtils;
import com.i5i58.util.JsonUtils;
import com.i5i58.util.MountPresentUtil;
import com.i5i58.util.ServerCode;
import com.i5i58.util.StringUtils;
import com.i5i58.yunxin.YunxinIM;
import com.i5i58.yunxin.Utils.CodeToString;
import com.i5i58.yunxin.Utils.YXResultSet;

import io.netty.channel.ChannelHandlerContext;
import zeus.live.config.MyThreadPool;
import zeus.live.data.DataMap;
import zeus.live.data.HotDaoDelegate;
import zeus.live.data.MutedUser;
import zeus.live.data.MutedUserManager;
import zeus.live.data.PriDaoDelegate;
import zeus.live.data.ResultCode;
import zeus.live.data.ResultDataSet;
import zeus.live.data.SecDaoDelegate;
import zeus.live.data.UserData;
import zeus.live.yunxin.TaskDataBaseGiveGift;
import zeus.live.yunxin.TaskYxChatEnter;
import zeus.live.yunxin.TaskYxChatExit;
import zeus.live.yunxin.TaskYxChatGift;
import zeus.live.yunxin.TaskYxChatMicSeqChanged;
import zeus.live.yunxin.TaskYxChatOpenClub;
import zeus.live.yunxin.TaskYxChatOpenGuard;
import zeus.live.yunxin.TaskYxChatSetMute;
import zeus.live.yunxin.TaskYxLinkMic;
import zeus.live.yunxin.TaskYxNoticeAudioStatus;
import zeus.network.handler.ChannelReadCallback;
import zeus.network.manager.IRemotPeer;
import zeus.network.protocol.IClientMsg;
import zeus.network.util.Constants;
import zeus.network.util.LiveUserDataKey;

@Component
public class BusinessProcessorImp implements IBusinessProcessor {
	private Logger logger = Logger.getLogger(getClass());

	LiveDataService liveDataService;
	private PriDaoDelegate priDaoDelegate;
	private SecDaoDelegate secDaoDelegate;
	private HotDaoDelegate hotDaoDelegate;
	private EntityManager entityManager;
	private JedisUtils jedisUtils;
	private AuthVerify<ChannelAuth> channelAdminAuthVerify;
	private JsonUtils jsonUtil;
	private MountPresentUtil mountPresentUtil;
	private ChannelUtils channelUtils;
	private TaskUtil taskUtil;
	private ClubTaskUtils clubTaskUtils;

	@Override
	public ResultDataSet sayHello(String accId, int money) {
		ResultDataSet rds = new ResultDataSet();
		JSONObject object = new JSONObject();
		object.put("accId", accId);
		object.put("money", money);
		rds.setCode("success");
		rds.setData(object.toJSONString());
		return rds;
	}

	@Override
	public boolean beforeServerStart() {
		liveDataService = LiveDataService.getInstance();
		priDaoDelegate = liveDataService.getPriDaoDelegate();
		secDaoDelegate = liveDataService.getSecDaoDelegate();
		hotDaoDelegate = liveDataService.getHotDaoDelegate();
		entityManager = liveDataService.getEntityManager();
		jedisUtils = liveDataService.getJedisUtils();
		channelAdminAuthVerify = liveDataService.getChannelAdminAuthVerify();
		jsonUtil = liveDataService.getJsonUtil();
		mountPresentUtil = liveDataService.getMountPresentUtil();
		channelUtils = liveDataService.getChannelUtils();
		taskUtil = liveDataService.getTaskUtil();
		clubTaskUtils = liveDataService.getClubTaskUtils();

		return true;
	}

	@Override
	public void afterServerStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void login(ChannelHandlerContext ctx, IClientMsg msg, ChannelReadCallback channelReadCallback) {
		byte accIdLength = msg.getData()[0];
		String accId = new String(msg.getData(), 1, accIdLength, Constants.defaultCharset);
		String cId = liveDataService.getChannel().getId();
		HotAccount1 hotAcc;
		try {
			Query htAccQuery = entityManager.createQuery(
					"SELECT new com.i5i58.data.account.HotAccount1(c.accId, c.openId, c.phoneNo,c.nickName, c.stageName, c.anchor, c.gender, c.birthDate, c.faceSmallUrl, c.faceOrgUrl , c.version ,  g.vip , g.vipDeadline, g.richScore, g.score, g.mountsId, g.mountsName,g.clubCid, g.clubName,g.fansCount, g.focusCount, g.essayCount ,  g.medals,  c.location, c.signature, c.personalBrief) FROM Account c ,AccountProperty g WHERE c.accId= g.accId AND c.accId='"
							+ accId + "'");
			hotAcc = (HotAccount1) htAccQuery.getSingleResult();
		} catch (Exception e) {
			hotAcc = null;
		}
		if (hotAcc == null) {
			System.out.println("seal start 1");
			return;
		}
		Wallet walletViewer = priDaoDelegate.getWalletPriDao().findByAccId(accId);
		HotChannelViewer hotViewer = new HotChannelViewer();
		hotViewer.setId(cId + "_" + accId);
		hotViewer.setcId(cId);
		hotViewer.setAccId(accId);
		hotViewer.setName(hotAcc.getNickName());
		hotViewer.setFaceSmallUrl(hotAcc.getFaceSmallUrl());
		hotViewer.setVip(hotAcc.getVip());
		hotViewer.setVipDeadLine(hotAcc.getVipDeadLine());
		hotViewer.setRichScore(hotAcc.getRichScore());
		hotViewer.setScore(hotAcc.getScore());
		hotViewer.setAndroid(false);
		ChannelAdminor channelAdmin = priDaoDelegate.getChannelAdminorPriDao().findByCIdAndAccId(cId, accId);
		if (channelAdmin != null) {
			hotViewer.setAdminRight(channelAdmin.getAdminRight());
		} else {
			hotViewer.setAdminRight(0);
		}
		if (liveDataService.getChannel().getOwnerId().equals(hotAcc.getId())) {
			hotViewer.setAdminRight(65535);
		}
		ChannelGuard channelGuard = priDaoDelegate.getChannelGuardPriDao().findByAccIdAndCId(accId, cId); // 记录观众骑士信息
		if (channelGuard != null) {
			hotViewer.setMountsId(channelGuard.getMountsId());
			hotViewer.setMountsName(channelGuard.getMountsName());
			hotViewer.setGuardLevel(channelGuard.getGuardLevel());
			hotViewer.setGuardDeadLine(channelGuard.getDeadLine());
		} else {
			hotViewer.setMountsId(0);
			hotViewer.setMountsName("");
			hotViewer.setGuardLevel(0);
			hotViewer.setGuardDeadLine(0);
		}
		if (!StringUtils.StringIsEmptyOrNull(hotAcc.getClubCId())) {
			ChannelFansClub channelFansClub = priDaoDelegate.getChannelFansClubPriDao()
					.findByCIdAndAccId(hotAcc.getClubCId(), accId);
			if (channelFansClub != null && channelFansClub.getEndDate() >= DateUtils.getNowTime()) {
				hotViewer.setFansClub(1);
				hotViewer.setClubName(liveDataService.getChannel().getClubName());
				hotViewer.setClubLevel(liveDataService.getChannel().getClubLevel());
				hotViewer.setClubDeadLine(channelFansClub.getEndDate());
				hotViewer.setFansClubScore(channelFansClub.getPersonalScore());
			} else {
				hotViewer.setFansClub(0);
				hotViewer.setClubName("");
				hotViewer.setClubLevel(0);
				hotViewer.setClubDeadLine(0);
				hotViewer.setFansClubScore(0);
			}
		}
		hotDaoDelegate.getHotChannelViewerDao().save(hotViewer);

		// 设置主播麦序在0位========================================
		if (hotViewer.getAccId().equals(liveDataService.getChannel().getOwnerId())) {
			HotChannelMic findMic = hotDaoDelegate.getHotChannelMicDao().findOne(cId + "_" + accId);
			long time = DateUtils.getNowTime();
			if (findMic != null) {
				findMic.setFaceSmallUrl(hotViewer.getFaceSmallUrl());
				findMic.setGuardLevel(hotViewer.getGuardLevel());
				findMic.setGuardDeadLine(hotViewer.getGuardDeadLine());
				findMic.setIndexId(0);
				findMic.setSitTime(time);
				findMic.setName(hotViewer.getName());
				findMic.setVip(hotViewer.getVip());
				findMic.setVipDeadLine(hotViewer.getVipDeadLine());
				findMic.setRichScore(hotViewer.getRichScore());
				hotDaoDelegate.getHotChannelMicDao().save(findMic);
				liveDataService.setMic(0, findMic);
			} else {
				HotChannelMic newMic = new HotChannelMic();
				newMic.setId(cId + "_" + accId);
				newMic.setAccId(accId);
				newMic.setcId(cId);
				newMic.setFaceSmallUrl(hotViewer.getFaceSmallUrl());
				newMic.setGuardLevel(hotViewer.getGuardLevel());
				newMic.setGuardDeadLine(hotViewer.getGuardDeadLine());
				newMic.setIndexId(0);
				newMic.setSitTime(time);
				newMic.setName(hotViewer.getName());
				newMic.setVip(hotViewer.getVip());
				newMic.setVipDeadLine(hotViewer.getVipDeadLine());
				newMic.setRichScore(hotViewer.getRichScore());
				newMic.setAudioRight(true);
				hotDaoDelegate.getHotChannelMicDao().save(newMic);
				liveDataService.setMic(0, newMic);
			}
		}

		liveDataService.getChannel().setPlayerCount(liveDataService.getChannel().getPlayerCount() + 1);
		liveDataService.getChannel().setPlayerTimes(liveDataService.getChannel().getPlayerTimes() + 1);
		hotDaoDelegate.getHotChannelDao().save(liveDataService.getChannel());

		BusinessUtil.addSessionId(accId, msg.getSessionId());
		byte[] datares = new byte[msg.getData().length - accIdLength - 1];
		System.arraycopy(msg.getData(), accIdLength + 1, datares, 0, datares.length);
		msg.setLength(datares.length);
		msg.setData(datares);
		UserData userData = new UserData();
		userData.setViewer(hotViewer);
		userData.setWallet(walletViewer);
		if (hotViewer.getAccId().equals(liveDataService.getHotOwner().getId())) {
			liveDataService.setAnchorWallet(walletViewer);
		}
		channelReadCallback.OnDoAuthSuccess(ctx, msg, LiveUserDataKey.UserData, userData);
	}

	@Override
	public void userExit(IRemotPeer peer) {
		logger.info(String.format("a %s user exit now. sessionid %s", peer.isVirtual() ? "virtual" : "direct",
				peer.getSessionId()));
		UserData userData = (UserData) peer.getUserData().get(LiveUserDataKey.UserData);
		if (userData == null)
			return;
		HotChannelViewer hotChannelViewer = userData.getViewer();
		if (hotChannelViewer == null)
			return;

		BusinessUtil.delSessionId(hotChannelViewer.getAccId());
	}

	@Override
	public ResultDataSet exitChannel(IRemotPeer peer,JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		rds.setCmd(cmd);
		HotChannel hotChannel = liveDataService.getChannel();
		String cId = hotChannel.getId();
		String accId = ((UserData) peer.getUserData().get(LiveUserDataKey.UserData)).getViewer().getAccId();
		if (StringUtils.StringIsEmptyOrNull(cId) || StringUtils.StringIsEmptyOrNull(accId)) {
			rds.setCode(ResultCode.SUCCESS.getCode());
			return rds;
		} 
		channelUtils.removeViewer(cId, accId);// 删除观众
		channelUtils.removeRicher(cId, accId);// 删除贵宾
		
		try {
			channelUtils.channelWatchingFinish(cId, accId);
			clubTaskUtils.performDailyClockTask(accId, cId);
			taskUtil.performTaskOnExitChannel(accId, cId);
		} catch (ParseException e) {
			logger.error("", e);
		}
		HotChannelViewer hotViewer = hotDaoDelegate.getHotChannelViewerDao().findOne(cId + "_" + accId);

		if (hotViewer != null && hotChannel != null) {
			if (hotChannel.getPlayerCount() > 0) {
				hotChannel.setPlayerCount(hotChannel.getPlayerCount() - 1);
				hotDaoDelegate.getHotChannelDao().save(hotChannel);
			} else {
				hotChannel.setPlayerCount(0);
				hotDaoDelegate.getHotChannelDao().save(hotChannel);
				logger.error("退出房间时频道人数小于1");
			}
			
			TaskYxChatExit taskYxChatExit = new TaskYxChatExit(hotChannel.getYunXinRId(), accId, hotViewer.getName(),
					hotViewer.getFaceSmallUrl(), hotViewer.getRichScore(), hotViewer.getScore(), hotViewer.getVip(),
					hotViewer.getVipDeadLine(), hotViewer.getGuardLevel(), hotViewer.getGuardDeadLine(),
					hotViewer.getFansClub(), hotViewer.getClubName(), hotViewer.getClubLevel(),
					hotViewer.getClubDeadLine(), jsonUtil);
			MyThreadPool.getYunxinPool().execute(taskYxChatExit);
			//从内存观众列表中移除
			liveDataService.removeRichman(hotViewer);
			liveDataService.removeViewer(hotViewer);
			
			BusinessUtil.delSessionId(hotViewer.getAccId());
			hotDaoDelegate.getHotChannelViewerDao().delete(hotViewer);
		}
		if (hotChannel != null && !hotChannel.getOwnerId().equals(accId)) {
			HotChannelMic hcm = hotDaoDelegate.getHotChannelMicDao().findOne(cId + "_" + accId);
			if (hcm != null) {
				hotDaoDelegate.getHotChannelMicDao().delete(hcm);
			}
		}
		rds.setCode(ResultCode.SUCCESS.getCode());
		return rds;
	}

	@Override
	public ResultDataSet setMute(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		rds.setCmd(cmd);
		HotChannelViewer hotViewer = ((UserData) peer.getUserData().get(LiveUserDataKey.UserData)).getViewer();
		String admin = hotViewer.getAccId();
		String optValue = json.get("optValue").toString();
		String accId = json.get("accId").toString();
		Account account = priDaoDelegate.getAccountPriDao().findOne(accId);
		if (liveDataService.getChannel() == null) {
			rds.setMsg(ServerCode.NO_CHANNEL.getCode());
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			return rds;
		}
		if (account == null) {
			rds.setMsg(ServerCode.NO_ACCOUNT.getCode());
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			return rds;
		}
		if (!channelUtils.verifyHotChannelAuth(liveDataService.getChannel(), admin, ChannelAuth.PROHIBIT_SPEAK)) {
			rds.setMsg("您没有该权限");
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			return rds;
		}
		try {
			YXResultSet yxResultSet = YunxinIM.setChatRoomMemberRole(liveDataService.getChannel().getYunXinRId(),
					priDaoDelegate.getChannelPriDao().findOne(liveDataService.getChannel().getId()).getCreatorId(),
					accId, "-2", optValue, "");
			if (!yxResultSet.getCode().equals("200")) {
				rds.setCode(ResultCode.SERVICE_ERROR.getCode());
				rds.setMsg(CodeToString.getString(yxResultSet.getCode()));
				return rds;
			}
			rds.setCode(ResultCode.SUCCESS.getCode());
			// 写入内存禁言列表
			MutedUserManager mutedUsers = liveDataService.getMutedUers();
			if (optValue.equals("false") && mutedUsers.containsKey(accId)) {
				mutedUsers.remove(accId);
			} else if (optValue.equals("true") && !mutedUsers.containsKey(accId)) {
				mutedUsers.put(accId, new MutedUser());
				TaskYxChatSetMute taskYxChatSetMute = new TaskYxChatSetMute(liveDataService.getChannel().getYunXinRId(),
						admin, account.getNickName(), optValue, jsonUtil);
				MyThreadPool.getYunxinPool().execute(taskYxChatSetMute);
			}

			return rds;

		} catch (IOException e) {
			logger.error("", e);
			rds.setMsg("设置发生异常");
			rds.setCode(ResultCode.SERVICE_ERROR.getCode());
			return rds;
		}

	}

	@Override
	public ResultDataSet enterChannel(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		rds.setCmd(cmd);
		HotChannelViewer hotViewer = ((UserData) peer.getUserData().get(LiveUserDataKey.UserData)).getViewer();
		if (hotViewer == null) {
			rds.setCode("token_invalid");
			rds.setMsg("该账号不存在");
			return rds;
		}
		String accId = hotViewer.getAccId();
		String cId = liveDataService.getChannel().getId();
		int indexByViewer = liveDataService.addViewer(hotViewer);
		int indexByRicher = liveDataService.addRichman(hotViewer);
		try {
			BusinessUtil.channelWatchingStart(cId, accId);
			taskUtil.performTaskOnEnterChannel(cId, accId);
			taskUtil.performTaskOnWatchChannel(accId);

		} catch (ParseException e) {
			logger.error("", e);
		}
		HashMap<String, Object> response = new HashMap<String, Object>();
		response.put("channel", liveDataService.getChannel());
		response.put("owner", liveDataService.getHotOwner());
		if (liveDataService.getChannel().getOwnerId().equals(accId)) {
			response.put("isAnchor", true);
		}
		response.put("adminRight", hotViewer.getAdminRight());
		if (!StringUtils.StringIsEmptyOrNull(cId)) {

			ConnectMicInfo connMicInfo = new ConnectMicInfo();
			connMicInfo.setcId(cId);
			connMicInfo.setHlsPullUrl(liveDataService.getChannel().getHlsPullUrl());
			connMicInfo.setHttpPullUrl(liveDataService.getChannel().getHttpPullUrl());
			connMicInfo.setRtmpPullUrl(liveDataService.getChannel().getRtmpPullUrl());
			connMicInfo.setFaceUrl(liveDataService.getHotOwner().getFaceSmallUrl());
			connMicInfo.setOwnerId(liveDataService.getHotOwner().getId());
			connMicInfo.setStageName(liveDataService.getHotOwner().getStageName());
			response.put("connMicInfo", connMicInfo);
		}
		TaskYxChatEnter taskYxChatEnter = new TaskYxChatEnter(liveDataService.getChannel().getYunXinRId(), accId,
				hotViewer.getName(), hotViewer.getFaceSmallUrl(), hotViewer.getRichScore(), hotViewer.getScore(),
				hotViewer.getGuardLevel(), hotViewer.getGuardDeadLine(), hotViewer.getVip(), hotViewer.getVipDeadLine(),
				hotViewer.getFansClub(), hotViewer.getClubName(), hotViewer.getClubLevel(), hotViewer.getClubDeadLine(),
				hotViewer.getMountsId(), hotViewer.getMountsId(), indexByViewer, indexByRicher, jsonUtil, channelUtils,
				hotViewer, cId);
		MyThreadPool.getYunxinPool().execute(taskYxChatEnter);
		rds.setCode("success");
		rds.setData(response);
		return rds;
	}

	@Override
	public ResultDataSet setMicSequence(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		rds.setCmd(cmd);
		String accId = ((UserData) peer.getUserData().get(LiveUserDataKey.UserData)).getViewer().getAccId();
		String micAccId = json.get("micAccId").toString();
		int index = json.getInteger("index");
		String cId = liveDataService.getChannel().getId();
		if (index < 1) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("index_error");
			return rds;
		}
		if (liveDataService.getChannel() == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.NO_CHANNEL.getCode());
			return rds;
		}
		HotChannelViewer hotViewer = liveDataService.getHotViewerByAccId(accId);
		if (hotViewer == null) {
			rds.setCode(ResultCode.SERVICE_ERROR.getCode());
			rds.setMsg("no_viewer_0");
			return rds;
		}
		HotChannelViewer micViewer = liveDataService.getHotViewerByAccId(micAccId);
		if (micViewer == null) {
			rds.setCode(ResultCode.SERVICE_ERROR.getCode());
			rds.setMsg("no_viewer_1");
			return rds;
		}
		if (!channelAdminAuthVerify.Verify(ChannelAuth.OPERATE_USER_MIC_SEQUENCE_AUTHORITY,
				hotViewer.getAdminRight())) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("没有管理员权限");
			return rds;
		}
		HotChannelMic findMic = liveDataService.getHotChannelMicByAccId(micAccId);
		long time = DateUtils.getNowTime();
		if (findMic != null) {
			findMic.setIndexId(index);
			findMic.setSitTime(time);
			liveDataService.updateMic(index, findMic);
			hotDaoDelegate.getHotChannelMicDao().save(findMic);
		} else {
			HotChannelMic newMic = new HotChannelMic();
			newMic.setId(cId + "_" + micAccId);
			newMic.setAccId(micAccId);
			newMic.setcId(cId);
			newMic.setFaceSmallUrl(micViewer.getFaceSmallUrl());
			newMic.setGuardLevel(micViewer.getGuardLevel());
			newMic.setIndexId(index);
			newMic.setSitTime(time);
			newMic.setName(micViewer.getName());
			newMic.setVip(micViewer.getVip());
			newMic.setRichScore(micViewer.getRichScore());
			hotDaoDelegate.getHotChannelMicDao().save(newMic);
			liveDataService.addMic(index, newMic);
		}
		TaskYxChatMicSeqChanged TaskMicSeqChanged = new TaskYxChatMicSeqChanged(
				liveDataService.getChannel().getYunXinRId(), micAccId, index, time, jsonUtil);
		MyThreadPool.getYunxinPool().execute(TaskMicSeqChanged);
		rds.setCode(ResultCode.SUCCESS.getCode());
		return rds;
	}

	@Override
	public ResultDataSet removeMicSequence(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		rds.setCmd(cmd);
		String accId = ((UserData) peer.getUserData().get(LiveUserDataKey.UserData)).getViewer().getAccId();
		String micAccId = json.get("micAccId").toString();
		if (liveDataService.getChannel() == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.NO_CHANNEL.getCode());
			return rds;
		}
		if (liveDataService.getChannel().getOwnerId().equals(micAccId)) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("不可移除主播");
			return rds;
		}
		HotChannelViewer hotViewer = liveDataService.getHotViewerByAccId(accId);
		if (hotViewer == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("no_viewer_0");
			return rds;
		}
		HotChannelViewer micViewer = liveDataService.getHotViewerByAccId(micAccId);
		if (micViewer == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("no_viewer_1");
			return rds;
		}
		if (!channelAdminAuthVerify.Verify(ChannelAuth.OPERATE_USER_MIC_SEQUENCE_AUTHORITY,
				hotViewer.getAdminRight())) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("没有管理员权限");
			return rds;
		}
		HotChannelMic findMic = liveDataService.getHotChannelMicByAccId(micAccId);
		long time = DateUtils.getNowTime();
		if (findMic == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("该用户不在麦序中");
			return rds;
		}
		hotDaoDelegate.getHotChannelMicDao().delete(findMic);
		liveDataService.deleteMic(findMic);
		TaskYxChatMicSeqChanged TaskMicSeqChanged = new TaskYxChatMicSeqChanged(
				liveDataService.getChannel().getYunXinRId(), micAccId, -1, // -1表示移除麦序
				time, jsonUtil);
		MyThreadPool.getYunxinPool().execute(TaskMicSeqChanged);
		rds.setCode(ResultCode.SUCCESS.getCode());
		return rds;
	}

	@Override
	public ResultDataSet micCompleted(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		rds.setCmd(cmd);
		HotChannelViewer user = ((UserData) peer.getUserData().get(LiveUserDataKey.UserData)).getViewer();
		String toAccId = json.get("toAccId").toString();
		// HotChannelViewer micViewer =
		// liveDataService.getHotDaoDelegate().getHotChannelViewerDao().findOne(LiveDataService.getChannel().getId()
		// + '_' + accId);
		if (!channelAdminAuthVerify.Verify(ChannelAuth.OPERATE_USER_MIC_SEQUENCE_AUTHORITY, user.getAdminRight())) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("没有管理员权限");
			return rds;
		}
		HotChannelViewer micViewer = liveDataService.getHotViewerByAccId(toAccId);
		if (micViewer == null) {
			rds.setCode("failed");
			rds.setMsg("该用户已不在直播间");
			return rds;
		}
		TaskYxLinkMic yxChatLinkMicThread = new TaskYxLinkMic(liveDataService.getChannel().getYunXinRId(),
				micViewer.getAccId(), micViewer.getName(), micViewer.getFaceSmallUrl(), micViewer.getVip(),
				micViewer.getVipDeadLine(), micViewer.getGuardLevel(), micViewer.getGuardDeadLine(),
				micViewer.getRichScore(), micViewer.getScore(), micViewer.getFansClub(), micViewer.getClubName(),
				micViewer.getClubLevel(), micViewer.getClubDeadLine(), jsonUtil);
		MyThreadPool.getYunxinPool().execute(yxChatLinkMicThread);
		rds.setCode(ResultCode.SUCCESS.getCode());
		return rds;
	}

	@Override
	public ResultDataSet audioMute(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		rds.setCmd(cmd);
		HotChannelViewer user = ((UserData) peer.getUserData().get(LiveUserDataKey.UserData)).getViewer();
		String accId = user.getAccId();
		String toAccId = json.get("toAccId").toString();
		Boolean audioFlag = json.getBoolean("audioFlag");
		if (!channelAdminAuthVerify.Verify(ChannelAuth.OPERATE_USER_MIC_SEQUENCE_AUTHORITY, user.getAdminRight())) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("没有管理员权限");
			return rds;
		}
		HotChannelMic hotChannelMic = liveDataService.getHotChannelMicByAccId(toAccId);
		if (hotChannelMic == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("该用户不在麦序列表上");
			return rds;
		}

		if (!audioFlag.equals(hotChannelMic.isAudioRight())) {
			hotChannelMic.setAudioRight(audioFlag);
			TaskYxNoticeAudioStatus yxNoticeAudioStatusThread = new TaskYxNoticeAudioStatus(accId, toAccId, audioFlag,
					jsonUtil);
			MyThreadPool.getYunxinPool().execute(yxNoticeAudioStatusThread);
			rds.setData(audioFlag);
			rds.setCode(ResultCode.SUCCESS.getCode());
			return rds;
		}
		rds.setCode(ResultCode.PARAM_INVALID.getCode());
		rds.setMsg("该用户音频已是" + audioFlag + "状态");
		return rds;
	}

	@Override
	public ResultDataSet isConMic(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		String accId = json.get("toAccId").toString();
		rds.setCmd(cmd);
		HotChannelMic hotChannelMic = liveDataService.getHotChannelMicByAccId(accId);
		if (hotChannelMic == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("该用户不在麦序上");
			return rds;
		}
		if (!priDaoDelegate.getAccountPriDao().findOne(accId).isAnchor()) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("该用户不是主播");
			return rds;
		}
		rds.setCode(ResultCode.SUCCESS.getCode());
		return rds;
	}

	@Override
	public ResultDataSet giveGift(IRemotPeer peer, JSONObject json) {
		String cmd = json.get("cmd").toString();
		ResultDataSet rds = new ResultDataSet();
		rds.setCmd(cmd);
		UserData userData = (UserData) peer.getUserData().get(LiveUserDataKey.UserData);
		HotChannelViewer giveGiftViewer = userData.getViewer();
		if (giveGiftViewer == null) {
			rds.setCode(ResultCode.TOKEN_INVALID.getCode());
			rds.setMsg("该账号不存在");
			return rds;
		}

		MutedUserManager mutedUsers = liveDataService.getMutedUers();
		if (mutedUsers.containsKey(giveGiftViewer.getAccId())) {
			rds.setCode(ResultCode.AUTH.getCode());
			rds.setMsg("已被禁言");
			return rds;
		}

		String accId = giveGiftViewer.getAccId();
		String cId = liveDataService.getChannel().getId();
		int giftId = json.getIntValue("giftId");
		int giftCount = json.getIntValue("giftCount");
		int continuous = json.getIntValue("continuous");
		HotChannelGift hotGift = liveDataService.getGift(giftId);
		if (hotGift == null) {
			rds.setMsg("该礼物不存在");
			rds.setCode("param_invalid");
			return rds;
		}
		Long time = DateUtils.getNowTime();
		if (hotGift.isForVip() && (giveGiftViewer.getVip() == 0 || giveGiftViewer.getVipDeadLine() < time)) {
			rds.setCode("param_invalid");
			rds.setMsg("只有Vip才能赠送该礼物！");
			return rds;
		}
		if (hotGift.isForGuard()) {
			if (giveGiftViewer.getGuardLevel() == 0 || giveGiftViewer.getGuardDeadLine() < time) {
				rds.setCode("param_invalid");
				rds.setMsg("只有守护才能赠送该礼物！");
				return rds;
			}
		}
		long amount = hotGift.getPrice() * giftCount;
		long anchorAmount = hotGift.getAnchorPrice() * giftCount;
		Wallet walletViewer = userData.getWallet();
		if (amount > walletViewer.getGiftTicket() && amount > walletViewer.getDiamond()
				&& amount > walletViewer.getiGold()) {
			rds.setMsg("您的虎币不足");
			rds.setCode("igold_not_enough");
			return rds;
		}
		long cost = 0;
		String unit = "";
		long commission = 0;
		if (walletViewer.getGiftTicket() >= amount) {
			unit = "giftTicket";
			cost = amount;
			walletViewer.setGiftTicket(walletViewer.getGiftTicket() - amount);
		} else if (walletViewer.getDiamond() >= amount) {
			unit = "diamond";
			cost = amount;
			commission = anchorAmount;
			walletViewer.setDiamond(walletViewer.getDiamond() - amount);
		} else if (walletViewer.getiGold() >= amount) {
			unit = "iGold";
			cost = amount;
			commission = anchorAmount;
			walletViewer.setiGold(walletViewer.getiGold() - amount);
		}
		System.out.println("user cost " + cost);
		System.out.println("anchor commission " + anchorAmount);
		// ensure it is needed to replace in-memory data in callback.
		userData.increaseWalletVersion();
		MyThreadPool.getDatabaseThread().execute(new ConsumeTask(unit, cost, commission, userData.getWalletVersion(),
				priDaoDelegate.getWalletPriDao(), peer, liveDataService.getHotOwner().getId()));

		liveDataService.getChannel().setWeekOffer(liveDataService.getChannel().getWeekOffer() + amount);
		AccountProperty accountProperty = priDaoDelegate.getAccountPropertyPriDao().findByAccId(accId);
		int preRichScoreLevel = BusinessUtil.getRichScoreLevel(giveGiftViewer.getRichScore());
		accountProperty.setRichScore(accountProperty.getRichScore() + amount);
		giveGiftViewer.setRichScore(giveGiftViewer.getRichScore() + amount);
		int curRichScoreLevel = BusinessUtil.getRichScoreLevel(giveGiftViewer.getRichScore());
		priDaoDelegate.getAccountPropertyPriDao().save(accountProperty);
		// accountPropertyPriDao.save(accountProperty);
		// 缓存周榜数据，非该业务关键数据，及时性要求较高，采用高优先级线程池处理
		long offer = BusinessUtil.addWeekOffer(cId, accId, amount, giveGiftViewer.getName(),
				giveGiftViewer.getFaceSmallUrl(), giveGiftViewer.getVip(), giveGiftViewer.getGuardLevel(),
				giveGiftViewer.getRichScore());
		int indexByViewer = liveDataService.updateViewer(giveGiftViewer);
		int indexByRicher = liveDataService.updateRicher(giveGiftViewer);
		boolean condition = BusinessUtil.giftAnmiCondition(giftCount, continuous, hotGift.getCondition());
		// 云信发送聊天室刷礼物消息，用户需要尽快看见刷出的礼物，非该业务关键数据，及时性要求较高，采用云信专属线程池处理
		TaskYxChatGift yxChatGiftThread = new TaskYxChatGift(liveDataService.getChannel().getYunXinRId(),
				giveGiftViewer.getAccId(), giveGiftViewer.getName(), giveGiftViewer.getFaceSmallUrl(),
				giveGiftViewer.getVip(), giveGiftViewer.getVipDeadLine(), giveGiftViewer.getGuardLevel(),
				giveGiftViewer.getGuardDeadLine(), giveGiftViewer.getRichScore(), giveGiftViewer.getScore(),
				giveGiftViewer.getFansClub(), giveGiftViewer.getClubName(), giveGiftViewer.getClubLevel(),
				giveGiftViewer.getClubDeadLine(), giftId, giftCount, continuous, condition,
				liveDataService.getChannel().getWeekOffer(), offer, indexByViewer, indexByRicher,
				curRichScoreLevel > preRichScoreLevel, hotDaoDelegate.getHotChannelDao(), hotGift.isBroadcast(),
				jsonUtil, channelUtils, giveGiftViewer, cId);
		MyThreadPool.getYunxinPool().execute(yxChatGiftThread);

		// 刷礼物mysql记录，非该业务关键数据，及时性要求较低，采用低优先级线程池处理
		ChannelRecord cgr = new ChannelRecord(cId, accId, giftId, giftCount, ChGoodsType.CHANNEL_GIFT.getValue(),
				amount, DateUtils.getNowTime(), hotGift.getName());
		DataSaveThread<ChannelRecord, Long> dataSaveThread = new DataSaveThread<ChannelRecord, Long>(cgr,
				priDaoDelegate.getChannelRecordPriDao());
		TaskDataBaseGiveGift dataBaseGiveGiftThread = new TaskDataBaseGiveGift(accId, cId,
				priDaoDelegate.getWalletPriDao(), amount, anchorAmount, priDaoDelegate.getAccountPropertyPriDao(),
				dataSaveThread);
		MyThreadPool.getLowPrioritytPool().execute(dataBaseGiveGiftThread);

		clubTaskUtils.performGiftGivenTask(accId, cId);
		rds.setData(walletViewer);
		rds.setCode("success");
		return rds;
	}

	@Override
	@Transactional
	public ResultDataSet openClub(IRemotPeer peer, String accId, String cId, int month, String clientIP) {
		ResultDataSet rds = new ResultDataSet();
		if (liveDataService.getChannel() == null) {
			rds.setMsg(ServerCode.NO_CHANNEL.getCode());
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			return rds;
		}

		Channel ch = priDaoDelegate.getChannelPriDao().findByCId(cId);
		if (ch == null) {
			rds.setMsg(ServerCode.NO_CHANNEL.getCode());
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			return rds;
		}
		Account account = priDaoDelegate.getAccountPriDao().findOne(accId);
		if (account == null) {
			rds.setMsg(ServerCode.NO_ACCOUNT.getCode());
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
		}

		Wallet walletClub = priDaoDelegate.getWalletPriDao().findByAccId(accId);
		if (walletClub == null) {
			rds.setMsg(ServerCode.NO_WALLET.getCode());
			rds.setCode(ResultCode.SERVICE_ERROR.getCode());
			return rds;
		}
		HotFansClubConfig hotFansClubConfig = hotDaoDelegate.getHotFansClubConfigDao().findOne(month);
		if (hotFansClubConfig == null) {
			rds.setMsg("miss_fans_club_config");
			rds.setCode(ResultCode.SERVICE_ERROR.getCode());
			return rds;
		}
		if (walletClub.getiGold() < hotFansClubConfig.getDiscount() * month) {
			rds.setMsg(ServerCode.IGOLD_NOT_ENOUGH.getCode());
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			return rds;
		}
		walletClub.setiGold(walletClub.getiGold() - (long) (hotFansClubConfig.getDiscount() * month)); // 是否可以这样转
		priDaoDelegate.getWalletPriDao().save(walletClub);

		ChannelFansClub fansClub = priDaoDelegate.getChannelFansClubPriDao().findByCIdAndAccId(cId, accId);
		long now = DateUtils.getNowTime();
		try {
			if (fansClub != null) {
				if (fansClub.getEndDate() > now) {
					fansClub.setEndDate(DateUtils.AddMonth(fansClub.getEndDate(), month));
					priDaoDelegate.getChannelFansClubPriDao().save(fansClub);
				} else {
					fansClub.setEndDate(DateUtils.AddMonth(now, month));
					priDaoDelegate.getChannelFansClubPriDao().save(fansClub);
				}
			} else {
				fansClub = new ChannelFansClub();
				fansClub.setcId(cId);
				fansClub.setAccId(accId);
				// fansClub.setClubName(hotChannel.getClubName());
				fansClub.setEndDate(DateUtils.AddMonth(now, month));
				priDaoDelegate.getChannelFansClubPriDao().save(fansClub);
			}
		} catch (ParseException e) {
			logger.error("", e);
			rds.setMsg("parse_exception");
			rds.setCode(ResultCode.SERVICE_ERROR.getCode());
			return rds;
		}

		ch.setClubMemberCount(ch.getClubMemberCount() + 1);
		priDaoDelegate.getChannelPriDao().save(ch);
		liveDataService.getChannel().setClubMemberCount(ch.getClubMemberCount());
		hotDaoDelegate.getHotChannelDao().save(liveDataService.getChannel());

		RecordConsumption recordClub = new RecordConsumption();
		recordClub.setId(StringUtils.createUUID());
		recordClub.setAccId(accId);
		recordClub.setChannelId(liveDataService.getChannel().getChannelId());
		recordClub.setAmount(hotFansClubConfig.getDiscount() * month);
		recordClub.setClientIp(clientIP);
		recordClub.setGoodsNumber(month);
		recordClub.setDate(DateUtils.getNowTime());
		recordClub.setDeadline(fansClub.getEndDate());
		recordClub.setDescribe("");
		recordClub.setGoodsId("");
		recordClub.setGoodsType(GoodsType.BUY_FANSCLUBS.getValue());
		priDaoDelegate.getRecordConsumptionPriDao().save(recordClub);

		HotChannelViewer hotViewer = hotDaoDelegate.getHotChannelViewerDao().findOne(cId + "_" + accId);
		if (hotViewer != null) {
			hotViewer.setClubDeadLine(fansClub.getEndDate());
			hotViewer.setClubName(liveDataService.getChannel().getClubName());
			hotViewer.setClubLevel(liveDataService.getChannel().getClubLevel());
			hotViewer.setFansClub(1);
			hotDaoDelegate.getHotChannelViewerDao().save(hotViewer);
			TaskYxChatOpenClub yxChatOpenGuardThread = new TaskYxChatOpenClub(
					liveDataService.getChannel().getYunXinRId(), hotViewer.getAccId(), hotViewer.getName(),
					hotViewer.getFaceSmallUrl(), hotViewer.getVip(), hotViewer.getVipDeadLine(),
					hotViewer.getGuardLevel(), hotViewer.getGuardDeadLine(), hotViewer.getRichScore(),
					hotViewer.getScore(), hotViewer.getFansClub(), hotViewer.getClubName(), hotViewer.getClubLevel(),
					hotViewer.getClubDeadLine(), jsonUtil);
			MyThreadPool.getYunxinPool().execute(yxChatOpenGuardThread);
		}

		rds.setCode(ResultCode.SUCCESS.getCode());
		return rds;
	}

	@Override
	@Transactional
	public ResultDataSet openGuard(IRemotPeer peer, String accId, String cId, int level, int month, String clientIP) {
		ResultDataSet rds = new ResultDataSet();
		if (liveDataService.getChannel() == null) {
			rds.setMsg(ServerCode.NO_CHANNEL.getCode());
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			return rds;
		}
		// HotChannelViewer hotViewer = hotChannelViewerDao.findOne(cId
		// + "_" +
		// accId);
		// if (hotViewer == null) {
		// rds.setMsg(ServerCode.NO_VIEWER.getCode());
		// rds.setCode(ResultCode.PARAM_INVALID.getCode());
		// return rds;
		// }
		Wallet wallet = priDaoDelegate.getWalletPriDao().findByAccId(accId);
		if (wallet == null) {
			rds.setMsg(ServerCode.NO_WALLET.getCode());
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			return rds;
		}
		HotChannelGuardConfig hotChannelGuardConfig = hotDaoDelegate.getHotChannelGuardConfigDao()
				.findByLevelAndMonth(level, month);
		if (hotChannelGuardConfig == null) {
			Iterable<ChannelGuardConfig> channelGuardConfig = priDaoDelegate.getChannelGuardConfigPriDao().findAll();
			for (ChannelGuardConfig hgc : channelGuardConfig) {
				HotChannelGuardConfig hcgc = new HotChannelGuardConfig();
				hcgc.setId(hgc.getId());
				hcgc.setLevel(hgc.getLevel());
				hcgc.setMonth(hgc.getMonth());
				hcgc.setPrice(hgc.getPrice());
				hotDaoDelegate.getHotChannelGuardConfigDao().save(hcgc);
			}
			hotChannelGuardConfig = hotDaoDelegate.getHotChannelGuardConfigDao().findByLevelAndMonth(level, month);
		}
		if (hotChannelGuardConfig == null) {
			rds.setMsg("miss_guard_config");
			rds.setCode(ResultCode.SERVICE_ERROR.getCode());
			return rds;
		}
		long amount = hotChannelGuardConfig.getPrice();
		if (wallet.getiGold() < amount) {
			rds.setMsg(ServerCode.IGOLD_NOT_ENOUGH.getCode());
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			return rds;
		}
		ChannelGuard channelGuard = priDaoDelegate.getChannelGuardPriDao().findByAccIdAndCId(accId, cId);
		List<MountStore> mountStoreList = priDaoDelegate.getMountStorePriDao().findByAccIdAndCId(accId, cId);
		long today;
		try {
			today = DateUtils.getDate(new Date());
			long todayLong = new Date().getTime();
			if (channelGuard != null && channelGuard.getGuardLevel() > level) {
				if (channelGuard.getDeadLine() > today) {
					rds.setMsg("您已有更高级别的守护，无法开通该等级");
					rds.setCode(ResultCode.PARAM_INVALID.getCode());
					return rds;
				}
			}
			wallet.setiGold(wallet.getiGold() - amount);
			priDaoDelegate.getWalletPriDao().save(wallet);
			Long deadLine;
			if (channelGuard != null) {
				if (channelGuard.getDeadLine() > today && level == channelGuard.getGuardLevel()) {
					deadLine = DateUtils.AddMonth(channelGuard.getDeadLine(), month);
				} else {
					deadLine = DateUtils.AddMonth(today, month);
					channelGuard.setStartLine(todayLong);
				}
				channelGuard.setDeadLine(deadLine);
				channelGuard.setGuardLevel(level);
				priDaoDelegate.getChannelGuardPriDao().save(channelGuard);
				for (MountStore g : mountStoreList) {
					ChannelMount mount = priDaoDelegate.getChannelMountPriDao().findOne(g.getMountsId());
					if (mount.isForGuard()) {
						if (level < mount.getLevel()) {
							break;
						}
						g.setEndTime(deadLine);
						priDaoDelegate.getMountStorePriDao().save(g);
					}
				}
			} else {
				channelGuard = new ChannelGuard();
				channelGuard.setcId(cId);
				channelGuard.setAccId(accId);
				channelGuard.setGuardLevel(level);
				channelGuard.setDeadLine(DateUtils.AddMonth(today, month));
				channelGuard.setStartLine(todayLong);
				priDaoDelegate.getChannelGuardPriDao().save(channelGuard);
			}

			mountPresentUtil.presentMountForOpenGuard(accId, level, cId, channelGuard.getDeadLine());

			RecordConsumption record = new RecordConsumption();
			record.setId(StringUtils.createUUID());
			record.setAccId(accId);
			record.setChannelId(liveDataService.getChannel().getChannelId());
			record.setAmount(amount);
			record.setClientIp(clientIP);
			record.setGoodsNumber(month);
			record.setDate(DateUtils.getNowTime());
			record.setDeadline(channelGuard.getDeadLine());
			record.setDescribe("");
			record.setGoodsId(String.valueOf(level));
			record.setGoodsType(GoodsType.BUY_GUARD.getValue());
			priDaoDelegate.getRecordConsumptionPriDao().save(record);

			HotChannelViewer hotViewer = hotDaoDelegate.getHotChannelViewerDao().findOne(cId + "_" + accId);
			if (hotViewer != null) {
				hotViewer.setGuardLevel(channelGuard.getGuardLevel());
				hotViewer.setGuardDeadLine(channelGuard.getDeadLine());
				hotDaoDelegate.getHotChannelViewerDao().save(hotViewer);

				TaskYxChatOpenGuard yxChatOpenGuardThread = new TaskYxChatOpenGuard(
						liveDataService.getChannel().getYunXinRId(), hotViewer.getAccId(), hotViewer.getName(),
						hotViewer.getFaceSmallUrl(), hotViewer.getVip(), hotViewer.getVipDeadLine(),
						hotViewer.getGuardLevel(), hotViewer.getGuardDeadLine(), hotViewer.getRichScore(),
						hotViewer.getScore(), hotViewer.getFansClub(), hotViewer.getClubName(),
						hotViewer.getClubLevel(), hotViewer.getClubDeadLine(), jsonUtil);
				MyThreadPool.getYunxinPool().execute(yxChatOpenGuardThread);
			}
			JSONObject object = new JSONObject();
			object.put("cId", channelGuard.getcId());
			object.put("guardLevel", channelGuard.getGuardLevel());
			object.put("guardDeadLine", channelGuard.getDeadLine());
			rds.setData(object.toJSONString());
			rds.setCode(ResultCode.SUCCESS.getCode());
		} catch (ParseException e) {
			logger.error("", e);
			rds.setCode(ResultCode.SERVICE_ERROR.getCode());
			rds.setMsg("time_parse_error");
		}
		return rds;
	}

	@Override
	@Transactional
	public ResultDataSet buyGuardMount(IRemotPeer peer, String accId, int mountId, String cId) {
		ResultDataSet rds = new ResultDataSet();

		Wallet wallet = priDaoDelegate.getWalletPriDao().findByAccId(accId);
		if (wallet == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.NO_WALLET.getCode());
			return rds;
		}
		// Channel channel = channelPriDao.findOne(cId);
		if (liveDataService.getChannel() == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.NO_CHANNEL.getCode());
			return rds;
		}
		HotChannelMount mount = liveDataService.getMount(mountId);
		if (mount == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.NO_MOUNT.getCode());
			return rds;
		}
		if (mount.getPrice() > wallet.getiGold()) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.IGOLD_NOT_ENOUGH.getCode());
			return rds;
		}
		if (!mount.isForGuard()) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("该坐骑不是守护坐骑");
			return rds;
		}
		if (StringUtils.StringIsEmptyOrNull(cId)) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("没有指定频道");
			return rds;
		}
		ChannelGuard guard = priDaoDelegate.getChannelGuardPriDao().findByAccIdAndCId(accId, cId);
		if (guard == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("该坐骑需要在频道中开通守护");
			return rds;
		}

		long date = 0;
		try {
			date = DateUtils.getNowDate();
			if (guard.getDeadLine() < date) {
				rds.setCode(ResultCode.PARAM_INVALID.getCode());
				rds.setMsg("该频道守护已过期");
				return rds;
			}
		} catch (ParseException e) {
			logger.error("", e);
			rds.setCode(ResultCode.SERVICE_ERROR.getCode());
			rds.setMsg("date_error");
			return rds;
		}

		wallet.setiGold(wallet.getiGold() - mount.getPrice());
		priDaoDelegate.getWalletPriDao().save(wallet);

		MountStore newMountstore = priDaoDelegate.getMountStorePriDao().findByAccIdAndMountsIdAndCId(accId, mountId,
				cId);
		if (newMountstore == null) {
			newMountstore = new MountStore();
			newMountstore.setId(StringUtils.createUUID());
			newMountstore.setAccId(accId);
			newMountstore.setMountsId(mountId);
			newMountstore.setcId(cId);
			newMountstore.setStartTime(date);
			newMountstore.setEndTime(guard.getDeadLine());
		}
		newMountstore.setEndTime(guard.getDeadLine());
		priDaoDelegate.getMountStorePriDao().save(newMountstore);

		RecordConsumption record = new RecordConsumption();
		record.setId(StringUtils.createUUID());
		record.setAccId(accId);
		record.setChannelId(liveDataService.getChannel().getChannelId());
		record.setAmount(mount.getPrice());
		record.setClientIp("");
		record.setDate(DateUtils.getNowTime());
		record.setDeadline(guard.getDeadLine());
		record.setDescribe("");
		record.setGoodsId(String.valueOf(mountId));
		record.setGoodsType(GoodsType.BUY_MOUNT.getValue());
		record.setGoodsNumber(1);
		priDaoDelegate.getRecordConsumptionPriDao().save(record);

		rds.setCode(ResultCode.SUCCESS.getCode());
		return rds;
	}

	@Override
	@Transactional
	public ResultDataSet buyMount(IRemotPeer peer, String accId, int mountId, int month, String clientIP) {
		ResultDataSet rds = new ResultDataSet();

		Wallet wallet = priDaoDelegate.getWalletPriDao().findByAccId(accId);
		if (wallet == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.NO_WALLET.getCode());
			return rds;
		}
		HotChannelMount mount = liveDataService.getMount(mountId);
		if (mount == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.NO_MOUNT.getCode());
			return rds;
		}

		// 验证坐骑是否是vip专属
		long vipDeadLine = -1;
		if (mount.isForVip()) {
			try {
				long nowDate = DateUtils.getNowDate();
				AccountProperty accountProperty = priDaoDelegate.getAccountPropertyPriDao().findOne(accId);
				if (accountProperty == null) {
					rds.setMsg(ServerCode.NO_ACCOUNT.getCode());
					rds.setCode(ResultCode.PARAM_INVALID.getCode());
					return rds;
				}
				if (accountProperty.getVip() == 0) {
					rds.setCode(ResultCode.PARAM_INVALID.getCode());
					rds.setMsg("非vip用户不能购买该坐骑");
					return rds;
				}
				if (accountProperty.getVipDeadline() < nowDate) {
					rds.setCode(ResultCode.PARAM_INVALID.getCode());
					rds.setMsg("vip已到期");
					return rds;
				}
				vipDeadLine = accountProperty.getVipDeadline();
			} catch (ParseException e) {
				logger.error("", e);
				rds.setCode(ResultCode.SERVICE_ERROR.getCode());
				rds.setMsg("date_error");
				return rds;
			}
		}
		// 当前接口不能用来购买守护坐骑
		if (mount.isForGuard()) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("该坐骑需要开通频道守护");
			return rds;
		}
		if (mount.getPrice() > wallet.getiGold()) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.IGOLD_NOT_ENOUGH.getCode());
			return rds;
		}
		wallet.setiGold(wallet.getiGold() - mount.getPrice());
		priDaoDelegate.getWalletPriDao().save(wallet);

		double price = month * mount.getPrice();
		// int addDate = mount.getValidity() * month;
		long date = 0;
		long deadline = 0;

		MountStore mountStore = priDaoDelegate.getMountStorePriDao().findByAccIdAndMountsId(accId, mountId);

		try {
			date = DateUtils.getDate(new Date());

			if (mountStore != null) {
				if (vipDeadLine > 0) {
					deadline = vipDeadLine;
				} else {
					if (mountStore.getEndTime() >= date) {
						deadline = DateUtils.AddMonth(mountStore.getEndTime(), month);
					} else {
						deadline = DateUtils.AddMonth(date, month);
					}
				}
				mountStore.setEndTime(deadline);
				priDaoDelegate.getMountStorePriDao().save(mountStore);
			} else {
				if (vipDeadLine > 0) {
					deadline = vipDeadLine;
				} else {
					deadline = DateUtils.AddMonth(date, month);
				}
				MountStore newMountstore = new MountStore();
				newMountstore.setId(StringUtils.createUUID());
				newMountstore.setAccId(accId);
				newMountstore.setMountsId(mountId);
				newMountstore.setStartTime(date);
				newMountstore.setEndTime(deadline);
				priDaoDelegate.getMountStorePriDao().save(newMountstore);
			}
		} catch (ParseException e) {
			logger.error("", e);
		}
		RecordConsumption record = new RecordConsumption();
		record.setId(StringUtils.createUUID());
		record.setAccId(accId);
		record.setChannelId("");
		record.setAmount(price);
		record.setClientIp(clientIP);
		record.setDate(DateUtils.getNowTime());
		record.setDeadline(deadline);
		record.setDescribe("");
		record.setGoodsId(String.valueOf(mountId));
		record.setGoodsType(GoodsType.BUY_MOUNT.getValue());
		record.setGoodsNumber(1);

		priDaoDelegate.getRecordConsumptionPriDao().save(record);
		rds.setCode(ResultCode.SUCCESS.getCode());
		return rds;
	}

	@Override
	@Transactional
	public ResultDataSet buyAccountVip(IRemotPeer peer, String accId, int level, String clientIP, int month) {
		long date = 0;
		long deadline = 0;
		ResultDataSet rds = new ResultDataSet();

		try {
			date = DateUtils.getDate(new Date());
			Wallet wallet = priDaoDelegate.getWalletPriDao().findByAccId(accId);
			if (wallet == null) {
				rds.setCode(ResultCode.PARAM_INVALID.getCode());
				rds.setMsg(ServerCode.NO_WALLET.getCode());
				return rds;
			}
			HotAccountVipConfig config = hotDaoDelegate.getHotAccountVipConfigDao().findByLevelAndMonth(level, month);
			if (config == null) {
				Iterable<AccountVipConfig> accountVipConfig = priDaoDelegate.getAccountVipConfigPriDao().findAll();
				for (AccountVipConfig hgc : accountVipConfig) {
					HotAccountVipConfig hcgc = new HotAccountVipConfig();
					hcgc.setId(hgc.getId());
					hcgc.setLevel(hgc.getLevel());
					hcgc.setMonth(hgc.getMonth());
					hcgc.setPrice(hgc.getPrice());
					hotDaoDelegate.getHotAccountVipConfigDao().save(hcgc);
				}
				config = hotDaoDelegate.getHotAccountVipConfigDao().findByLevelAndMonth(level, month);
			}
			if (config == null) {
				rds.setCode(ResultCode.PARAM_INVALID.getCode());
				rds.setMsg(ServerCode.NO_VIP.getCode());
				return rds;
			}
			if (config.getPrice() > wallet.getiGold()) {
				rds.setCode(ResultCode.PARAM_INVALID.getCode());
				rds.setMsg(ServerCode.IGOLD_NOT_ENOUGH.getCode());
				return rds;
			}
			AccountProperty accountProperty = priDaoDelegate.getAccountPropertyPriDao().findOne(accId);
			if (accountProperty == null) {
				rds.setCode(ResultCode.SERVICE_ERROR.getCode());
				rds.setMsg("用戶屬性不存在");
				return rds;
			}
			if (accountProperty != null && level < accountProperty.getVip()) {
				if (accountProperty.getVipDeadline() > date) {
					rds.setCode(ResultCode.PARAM_INVALID.getCode());
					rds.setMsg("您已经购买了更高等级的VIP，无法开通该等级VIP");
					return rds;
				}
			}

			wallet.setiGold(wallet.getiGold());
			priDaoDelegate.getWalletPriDao().save(wallet);

			// double price = month * config.getPrice();
			// int addDate = config.getMonth() * month;

			// MountStore mountStore =
			// mountStoreDao.findByAccIdAndMountsId(accId,
			// mountId);

			if (accountProperty.getVipDeadline() > date && accountProperty.getVip() == level) {
				deadline = DateUtils.AddMonth(accountProperty.getVipDeadline(), month);
			} else {
				deadline = DateUtils.AddMonth(date, month);
			}
			accountProperty.setVip(level);
			accountProperty.setVipDeadline(deadline);
			priDaoDelegate.getAccountPropertyPriDao().save(accountProperty);

			List<MountStore> mountStoreList = priDaoDelegate.getMountStorePriDao().findByAccId(accId);
			for (MountStore m : mountStoreList) {
				ChannelMount mount = priDaoDelegate.getChannelMountPriDao().findOne(m.getMountsId());
				if (mount.isForVip()) {
					if (level < mount.getLevel()) {
						break;
					}
					m.setEndTime(deadline);
					priDaoDelegate.getMountStorePriDao().save(m);
				}
			}
			/*
			 * HotAccount hotAccounts = hotAccountDao.findOne(accId);
			 * hotAccounts.setVip(accountProperty.getVip());
			 * hotAccounts.setVipDeadLine(accountProperty.getVipDeadline ());
			 * hotAccountDao.save(hotAccounts);
			 */

			mountPresentUtil.presentMountForBuyVip(accId, level, deadline);

			RecordConsumption record = new RecordConsumption();
			record.setId(StringUtils.createUUID());
			record.setAccId(accId);
			record.setChannelId("");
			record.setAmount(config.getPrice());
			record.setClientIp(clientIP);
			record.setGoodsNumber(month);
			record.setDate(DateUtils.getNowTime());
			record.setDeadline(deadline);
			record.setDescribe("");
			record.setGoodsId(String.valueOf(level));
			record.setGoodsType(GoodsType.BUY_VIP.getValue());
			priDaoDelegate.getRecordConsumptionPriDao().save(record);
			rds.setCode(ResultCode.SUCCESS.getCode());
			return rds;
		} catch (ParseException e) {
			logger.error("", e);
			rds.setCode(ResultCode.SERVICE_ERROR.getCode());
			rds.setMsg("server_ex");
			return rds;
		}
	}

	@Override
	@Transactional
	public ResultDataSet upgradeAccountVip(IRemotPeer peer, String accId, int level, String clientIP) {
		int month = 0;
		int oldLevel = 0;
		long price = 0;
		long oldPrice = 0;
		long newPrice = 0;
		ResultDataSet rds = new ResultDataSet();
		Wallet walletUpdateVip = priDaoDelegate.getWalletPriDao().findByAccId(accId);
		if (walletUpdateVip == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.NO_WALLET.getCode());
			return rds;
		}

		AccountProperty accountProperty = priDaoDelegate.getAccountPropertyPriDao().findOne(accId);
		if (accountProperty != null && accountProperty.getVip() == 0) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("" + accountProperty.getVip());
			return rds;
		}
		if (accountProperty != null && level <= accountProperty.getVip()) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("您已经购买了更高等级的VIP，无法开通该等级VIP");
			return rds;
		}
		oldLevel = accountProperty.getVip();
		Sort sort = new Sort(Direction.fromString("desc"), "deadline");
		Pageable pageable = new PageRequest(0, 1, sort);

		Page<RecordConsumption> recordConsumption = priDaoDelegate.getRecordConsumptionPriDao()
				.findByAccIdAndGoodsType(accId, GoodsType.BUY_VIP.getValue(), pageable);
		if (recordConsumption != null && recordConsumption.getSize() != 0) {
			month = recordConsumption.getContent().get(0).getGoodsNumber();
		} else {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg("您没有购买过VIP");
			return rds;
		}
		HotAccountVipConfig config = hotDaoDelegate.getHotAccountVipConfigDao().findByLevelAndMonth(level, month);
		HotAccountVipConfig oldConfig = hotDaoDelegate.getHotAccountVipConfigDao().findByLevelAndMonth(oldLevel, month);
		if (config == null || oldConfig == null) {
			Iterable<AccountVipConfig> accountVipConfig = priDaoDelegate.getAccountVipConfigPriDao().findAll();
			for (AccountVipConfig hgc : accountVipConfig) {
				HotAccountVipConfig hcgc = new HotAccountVipConfig();
				hcgc.setId(hgc.getId());
				hcgc.setLevel(hgc.getLevel());
				hcgc.setMonth(hgc.getMonth());
				hcgc.setPrice(hgc.getPrice());
				hotDaoDelegate.getHotAccountVipConfigDao().save(hcgc);
			}
			config = hotDaoDelegate.getHotAccountVipConfigDao().findByLevelAndMonth(level, month);
			oldConfig = hotDaoDelegate.getHotAccountVipConfigDao().findByLevelAndMonth(oldLevel, month);
		}
		if (config == null || oldConfig == null) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.NO_VIP.getCode());
			return rds;
		}
		newPrice = config.getPrice();
		oldPrice = oldConfig.getPrice();
		price = newPrice - oldPrice;
		if (price > walletUpdateVip.getiGold()) {
			rds.setCode(ResultCode.PARAM_INVALID.getCode());
			rds.setMsg(ServerCode.IGOLD_NOT_ENOUGH.getCode());
			return rds;
		}

		walletUpdateVip.setiGold(walletUpdateVip.getiGold() - price);
		priDaoDelegate.getWalletPriDao().save(walletUpdateVip);

		accountProperty.setVip(level);
		priDaoDelegate.getAccountPropertyPriDao().save(accountProperty);

		RecordConsumption record = new RecordConsumption();
		record.setId(StringUtils.createUUID());
		record.setAccId(accId);
		record.setChannelId("");
		record.setAmount(config.getPrice());
		record.setClientIp(clientIP);
		record.setDate(DateUtils.getNowTime());
		record.setDeadline(accountProperty.getVipDeadline());
		record.setDescribe("");
		record.setGoodsId(String.valueOf(level));
		record.setGoodsType(GoodsType.BUY_VIP.getValue());
		record.setGoodsNumber(month);
		priDaoDelegate.getRecordConsumptionPriDao().save(record);

		rds.setCode(ResultCode.SUCCESS.getCode());
		rds.setMsg("Server Suc");
		return rds;
	}
}
