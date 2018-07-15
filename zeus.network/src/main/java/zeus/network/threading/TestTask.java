package zeus.network.threading;

public class TestTask extends Task {

	int ThreadIndex;

	public TestTask(int index) {
		ThreadIndex = index;
	}

	@Override
	protected void onRun() {
		System.out.println("worker: " + this.getWorkerId() + "   ThreadIndex: " + ThreadIndex);
		try {
			Thread.sleep(4);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
