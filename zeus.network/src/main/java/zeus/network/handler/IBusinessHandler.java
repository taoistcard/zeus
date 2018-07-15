package zeus.network.handler;

import io.netty.channel.ChannelHandlerContext;
import zeus.network.manager.IRemotPeer;
import zeus.network.protocol.HttpClientMsg;
import zeus.network.protocol.IClientMsg;

/**
 * ҵ���߼��ӿ�
 * @author frank
 *
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
public interface IBusinessHandler {
	
	/**
	 * ��������ǰ
	 * @return �Ƿ��������
	 */
	public boolean beforeServerStart();
	
	/**
	 * ����������
	 */
	public void afterServerStart();

	/**
	 * ͨ������
	 * @param ctx ͨ�����������
	 */
	public void onChannelActive(ChannelHandlerContext ctx);

	/**
	 * ͨ���ر�
	 * @param ctx ͨ�����������
	 */
	public void onChannelInactive(ChannelHandlerContext ctx, IRemotPeer peer);

	/**
	 * �����쳣
	 * @param ctx ͨ�����������
	 * @param cause �쳣ԭ��
	 */
	public void onExceptionCaught(ChannelHandlerContext ctx, Throwable cause);

	/**
	 * ��ȡ����
	 * @param ctx ͨ�����������
	 * @param msg ��Ϣ
	 * @param peer �û�Զ�̴���
	 */
	public void onChannelRead0(IRemotPeer peer, IClientMsg msg);

	/**
	 * CC��������¼���ψ��Ԅ�̎��Ƿ���Ϣip����Ϣͷ����Ϣ
	 * 
	 * @param ctx ͨ�����������
	 * @param msg ��Ϣ
	 */
	public void onDefenseCC(ChannelHandlerContext ctx, IClientMsg msg);

//	public IRemotPeer virtualAuth(ChannelHandlerContext ctx, IIntranetMsg msg);
//
//	public IRemotPeer auth(ChannelHandlerContext ctx, IClientMsg msg);
	
	/**
	 * �û������֤
	 * @param ctx ͨ�����������
	 * @param msg ��Ϣ
	 * @param channelReadCallback �û���֤��ݺ�ص��ӿ�
	 */
	public void auth(ChannelHandlerContext ctx, IClientMsg msg, ChannelReadCallback channelReadCallback);
	
	
	/**
	 * check whether the user who sent the http request is logged in
	 * @author songfl
	 * @param msg
//	 * @param cId
//	 * @param accId
	 * */
	public void httpAuth(ChannelHandlerContext httpCtx, HttpClientMsg msg, HttpAuthCallback callback);
	
	/**
	 * handle the http request
	 * @author songfl
	 * @param httpCtx	the http channel context connected with a http-client
	 * @param localPeer	found by sessionId return by httAuth(accId), localPeer must be non-null
	 * @param msg
//	 * @param uri		Distinguish between different requests
//	 * @param header	http header
//	 * @param params	http params
	 * */
	public void onHttpRequest(ChannelHandlerContext httpCtx, IRemotPeer localPeer, HttpClientMsg msg);
	
}
