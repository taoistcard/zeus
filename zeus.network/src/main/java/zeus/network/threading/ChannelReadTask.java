package zeus.network.threading;

import zeus.network.handler.IBusinessHandler;
import zeus.network.manager.IRemotPeer;
import zeus.network.protocol.IClientMsg;

public class ChannelReadTask extends Task {
	IBusinessHandler businessHandler;
	IClientMsg msg;
	IRemotPeer peer;

	public ChannelReadTask(IBusinessHandler businessHandler, IClientMsg msg, IRemotPeer peer) {
		this.businessHandler = businessHandler;
		this.msg = msg;
		this.peer = peer;
	}

	@Override
	protected void onRun() {
		businessHandler.onChannelRead0(peer, msg);
	}

}
