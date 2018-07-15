package zeus.network.handler;

import io.netty.channel.ChannelHandlerContext;
import zeus.network.manager.IRemotPeer;
import zeus.network.protocol.IClientMsg;

/**
 * ͨ����ȡ���ݻص��ӿڣ����ڽ�������ʱ��֤�û�����Ƿ�ͨ����ص�������ȡ����
 * @author Administrator
 *
 */
public interface ChannelReadCallback {

	/**
	 * 需要验证并通过
	 * @param doAuthSuccessCtx
	 * @param doAuthSuccessMsg
	 * @param key
	 * @param userData
	 */
	public void OnDoAuthSuccess(final ChannelHandlerContext ctx, final IClientMsg msg, String key, Object userData);

	/**
	 * 需要验证并失败
	 * @param doAuthFailedCtx
	 * @param doAuthFailedMsg
	 */
	public void OnDoAuthFailed(final ChannelHandlerContext ctx, final IClientMsg msg);
	
	/**
	 * 已验证并通过
	 * @param authPeer
	 * @param authSuccessMsg
	 */
	public void OnAuthSuccess(final IRemotPeer peer, final IClientMsg msg);
	
	/**
	 * 已验证并失败
	 * @param authFailedCtx
	 * @param authFailed
	 */
	public void OnAuthFailed(final ChannelHandlerContext ctx, final IClientMsg msg);
}
