package zeus.manager.config;

import zeus.manager.server.ManagerServer;

public class IdleThead implements Runnable {
	private volatile boolean exit = false;
	ManagerServer managerServer;
	public IdleThead(ManagerServer managerServer){
		this.managerServer = managerServer;
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
				System.out.println("manager thread running..... sleep 30 second");
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (managerServer != null){
			managerServer.idleTaskFinished();
		}
	}

}
