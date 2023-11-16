import lift.LiftView;

public class LiftThread extends Thread {

	private DataMonitor data;
	private LiftView view;

	public LiftThread(DataMonitor data, LiftView view) {
		this.data = data;
		this.view = view;
	}

	@Override
	public void run() {

		while (true) {

			int nextFloor = data.getFloor() + data.direction();

			try {
				data.checkHalt();
				
				view.moveLift(data.getFloor(), nextFloor);

				data.checkFloor(nextFloor);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
}
