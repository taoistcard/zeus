package zeus.manager.handler;

import io.netty.channel.ChannelHandlerContext;
import zeus.network.handler.IBusinessHandler;
import zeus.network.manager.IRemotPeer;
import zeus.network.manager.RemotePeerManager;
import zeus.network.threading.Task;
import zeus.network.threading.TaskQueue;
import zeus.network.util.NetworkUtil;

public class MyChannelInactiveTask extends Task {
	IBusinessHandler businessHandler;
	ChannelHandlerContext ctx;
	IRemotPeer peer;
	
	public MyChannelInactiveTask(ChannelHandlerContext ctx, IRemotPeer peer){
		this.ctx = ctx;
		this.peer = peer;
	}
	
	@Override
	protected void onRun() {
		RemotePeerManager.removeClient(peer.getSessionId());
		System.out.println("remove peer");
		NetworkUtil.closeGracefully(ctx);
		TaskQueue queue = peer.getTaskQueue();
		if (queue != null){
			queue.decrementPeerCount();
		}
		peer.setTaskQueue(null);
	}

}
