package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

	private WashingIO io;

	private ActorThread<WashingMessage> sender;

	private int target;
	private boolean waitForResponse = false;

	private final int DT = 10, MARGIN = 2;
	private final double MU = 0.5 + 0.2, ML = 0.001 + 0.2;

	public TemperatureController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		try {
			while (true) {

				WashingMessage m = receiveWithTimeout(DT * 1000 / Settings.SPEEDUP);

				if (m == null) {

					

					regulate();

				} else {
					System.out.println("TemperatureController got " + m);

					sender = m.getSender();
					Order order = m.getOrder();

					if (order == Order.TEMP_IDLE) {
						io.heat(false);

						target = 0;
						waitForResponse = false;

						sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
					} else {
						if (order == Order.TEMP_SET_40) {
							target = 40;
						} else if (order == Order.TEMP_SET_60) {
							target = 60;
						}

						io.heat(true);
						regulate();

						waitForResponse = true;

					}
				}
			}

		} catch (InterruptedException unexpected) {
			throw new Error(unexpected);
		}
	}

	private void regulate() throws InterruptedException {
		
		if (target == 0) {
			return;
		}

		if (io.getTemperature() >= (target - MU)) {
			io.heat(false);

		} else if (io.getTemperature() <= (target - MARGIN) + ML) {
			io.heat(true);
		}

		if (waitForResponse && io.getTemperature() >= (target - MARGIN)) {
			sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
			waitForResponse = false;
		}

	}
}
