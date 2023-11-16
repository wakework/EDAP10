import java.util.Random;

import lift.LiftView;
import lift.Passenger;

public class PassengerThread extends Thread {

	private DataMonitor data;
	private LiftView view;
	private Passenger pass;

	public PassengerThread(DataMonitor data, LiftView view) {
		this.data = data;
		this.view = view;

	}

	@Override
	public void run() {
		Random rand = new Random();

		while (true) {

			try {
				Thread.sleep(rand.nextLong(5000) + 5000);
				pass = view.createPassenger();

				pass.begin();

				data.awaitLift(pass, pass.getStartFloor());
				pass.enterLift();
				data.enter();

				data.awaitFloor(pass, pass.getDestinationFloor());
				pass.exitLift();
				data.exit();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			pass.end();
		}
	}

}
