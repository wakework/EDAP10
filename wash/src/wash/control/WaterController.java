package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

	private WashingIO io;

	private ActorThread<WashingMessage> sender;

	private boolean isFilling = false;
	private boolean isDraining = false;

	private final int PERIOD = 1; // Didn't work with 5

	public WaterController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		try {
			while (true) {

				WashingMessage m = receiveWithTimeout(PERIOD * 1000 / Settings.SPEEDUP);

				if (m == null) {

					if (isFilling) {
						if (io.getWaterLevel() > WashingIO.MAX_WATER_LEVEL / 2) {
							io.fill(false);
							isFilling = false;
							
							sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
						} 
						
					} else if (isDraining) {
						if (io.getWaterLevel() == 0) {
							sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
						} 
						
					}

				} else {
					System.out.println("Received " + m);

					sender = m.getSender();
					Order order = m.getOrder();

					switch (order) {
					case WATER_IDLE:

						if (isFilling) {
							io.fill(false);
							isFilling = false;
						}

						if (isDraining) {
							io.drain(false);
							isDraining = false;
						}

						break;

					case WATER_FILL:
						io.drain(false);
						isDraining = false;
						
						io.fill(true);
						isFilling = true;
						break;

					case WATER_DRAIN:
						// SR2. Close valve before draining.
						io.fill(false);
						isFilling = false;
						
						io.drain(true);
						isDraining = true;
						break;

					default:
						System.out.println("Invalid order");
						break;
						
					}
				}
			}

		} catch (InterruptedException unexpected) {
			throw new Error(unexpected);
		}
	}
}
