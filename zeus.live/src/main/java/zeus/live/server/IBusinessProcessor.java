package zeus.live.server;

import com.alibaba.fastjson.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import zeus.live.data.ResultDataSet;
import zeus.network.handler.ChannelReadCallback;
import zeus.network.manager.IRemotPeer;
import zeus.network.protocol.IClientMsg;

public interface IBusinessProcessor {
	
	public boolean beforeServerStart();
	
	public void afterServerStart();
	
	public ResultDataSet sayHello(String accId, int money);
	
	/**
	 * 用户登录
	 * */
	public void login(ChannelHandlerContext ctx, IClientMsg msg, ChannelReadCallback channelReadCallback);
	/**
	 * 用户退出
	 * */
	public void userExit(IRemotPeer peer);

	/**
	 * 退出直播间
	 * */
	public ResultDataSet exitChannel(IRemotPeer peer,JSONObject json);
	
	/**
	 * 进入直播间
	 * */
	public ResultDataSet enterChannel(IRemotPeer peer, JSONObject json);
	
	/**
	 * 设置直播间禁言（不允许发言）
	 * */
	public ResultDataSet setMute(IRemotPeer peer, JSONObject json);
	
	/**
	 * 设置麦序
	 * */
	public ResultDataSet setMicSequence(IRemotPeer peer, JSONObject json);
	
	/**
	 * 移除麦序
	 * */
	public ResultDataSet removeMicSequence(IRemotPeer peer, JSONObject json);
	
	/**
	 * 连麦成功后广播
	 * */
	public ResultDataSet micCompleted(IRemotPeer peer, JSONObject json);
	
	/**
	 * 设置音频禁言
	 * */
	public ResultDataSet audioMute(IRemotPeer peer, JSONObject json);
	
	/**
	 * 是否有权限连麦 1.在麦序上 2.是主播
	 * */
	public ResultDataSet isConMic(IRemotPeer peer, JSONObject json);
	
	/**
	 * 赠送礼物
	 * */
	public ResultDataSet giveGift(IRemotPeer peer, JSONObject json);
	
	/**
	 * 开通粉丝团
	 * */
	public ResultDataSet openClub(IRemotPeer peer, String accId, String cId, int month, String clientIP);

	/**
	 * 开通守护
	 * */
	public ResultDataSet openGuard(IRemotPeer peer, String accId, String cId, int level, int month, String clientIP);

	/**
	 * 购买VIP
	 * */
	public ResultDataSet buyAccountVip(IRemotPeer peer, String accId, int level, String clientIP, int month);

	/**
	 * 升级VIP
	 * */
	public ResultDataSet upgradeAccountVip(IRemotPeer peer, String accId, int level, String clientIP);

	/**
	 * 购买守护坐骑
	 * */
	public ResultDataSet buyGuardMount(IRemotPeer peer, String accId, int mountId, String cId);

	/**
	 * 购买坐骑
	 * */
	public ResultDataSet buyMount(IRemotPeer peer, String accId, int mountId, int month, String clientIP);

}
