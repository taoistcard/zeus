package zeus.network.threading;

import io.netty.channel.ChannelHandlerContext;
import zeus.network.handler.IBusinessHandler;
import zeus.network.manager.IRemotPeer;
import zeus.network.protocol.HttpClientMsg;

public class HttpReadTask extends Task {
	IBusinessHandler businessHandler;
	ChannelHandlerContext httpCtx;
	HttpClientMsg httpClientMsg;
	IRemotPeer localPeer;
	
	public HttpReadTask(IBusinessHandler businessHandler, ChannelHandlerContext httpCtx, HttpClientMsg httpClientMsg, IRemotPeer localPeer){
		this.businessHandler = businessHandler;
		this.httpCtx = httpCtx;
		this.httpClientMsg = httpClientMsg;
		this.localPeer = localPeer;
	}
	
	@Override
	protected void onRun() {
		businessHandler.onHttpRequest(httpCtx, localPeer, httpClientMsg);
	}

}
