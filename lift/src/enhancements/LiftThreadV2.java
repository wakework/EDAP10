package enhancements;

import lift.LiftView;

public class LiftThreadV2 extends Thread {

	private DataMonitorV2 data;
	private LiftView view;

	public LiftThreadV2(DataMonitorV2 data, LiftView view) {
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
				data.setFloor(nextFloor);

				data.checkFloor(nextFloor);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
