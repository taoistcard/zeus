package zeus.live.config;

import zeus.live.server.LiveServer;

public class IdleThead implements Runnable {
	private volatile boolean exit = false;
	LiveServer liveServer;
	public IdleThead(LiveServer liveServer){
		this.liveServer = liveServer;
	}
	
	public boolean isExit() {
		return exit;
	}
	public void setExit(boolean exit) {
		this.exit = exit;
	}
	@Override
	public void run() {
		while (!exit) {
			try {
				System.out.println("idle thread running..... sleep 30 second");
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (liveServer != null){
			liveServer.idleTaskFinished();
		}
	}

}
