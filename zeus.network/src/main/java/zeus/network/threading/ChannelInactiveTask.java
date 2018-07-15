package zeus.network.threading;

import io.netty.channel.ChannelHandlerContext;
import zeus.network.handler.IBusinessHandler;
import zeus.network.manager.IRemotPeer;
import zeus.network.manager.RemotePeerManager;
import zeus.network.util.NetworkUtil;

public class ChannelInactiveTask extends Task {
	IBusinessHandler businessHandler;
	ChannelHandlerContext ctx;
	IRemotPeer peer;
	
	public ChannelInactiveTask(IBusinessHandler businessHandler, ChannelHandlerContext ctx, IRemotPeer peer){
		this.ctx = ctx;
		this.peer = peer;
		this.businessHandler = businessHandler;
	}
	
	@Override
	protected void onRun() {
		businessHandler.onChannelInactive(ctx, peer);
		RemotePeerManager.removeClient(peer.getSessionId());
		NetworkUtil.closeGracefully(ctx);
		TaskQueue queue = peer.getTaskQueue();
		if (queue != null){
			queue.decrementPeerCount();
		}
		peer.setTaskQueue(null);
	}

}
