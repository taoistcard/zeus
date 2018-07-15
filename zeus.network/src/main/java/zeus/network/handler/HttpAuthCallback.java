package zeus.network.handler;

import io.netty.channel.ChannelHandlerContext;
import zeus.network.manager.IRemotPeer;
import zeus.network.protocol.HttpClientMsg;

public interface HttpAuthCallback {
	public void onCallback(ChannelHandlerContext httpCtx, IRemotPeer localPeer, HttpClientMsg msg);
}
