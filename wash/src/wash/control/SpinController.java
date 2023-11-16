package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {

	private WashingIO io;

	private int direction;

	public SpinController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		try {

			while (true) {
				WashingMessage m = receiveWithTimeout(WashingIO.MINUTE / Settings.SPEEDUP);

				if (m == null) {

					if (direction == WashingIO.SPIN_LEFT) {
						io.setSpinMode(WashingIO.SPIN_RIGHT);
						direction = WashingIO.SPIN_RIGHT;

					} else if (direction == WashingIO.SPIN_RIGHT) {
						io.setSpinMode(WashingIO.SPIN_LEFT);
						direction = WashingIO.SPIN_LEFT;

					}

				} else {
					System.out.println("Received " + m);

					WashingMessage ack = new WashingMessage(this, Order.ACKNOWLEDGMENT);
					ActorThread<WashingMessage> sender = m.getSender();
					Order order = m.getOrder();

					switch (order) {
					case SPIN_OFF:
						io.setSpinMode(WashingIO.SPIN_IDLE);
						direction = WashingIO.SPIN_IDLE;
						
						break;

					case SPIN_SLOW:
						io.setSpinMode(WashingIO.SPIN_LEFT);
						direction = WashingIO.SPIN_LEFT;

						break;

					case SPIN_FAST:
						io.setSpinMode(WashingIO.SPIN_FAST);
						direction = WashingIO.SPIN_FAST;

						break;

					default:
						System.out.println("Invalid order");
						break;

					}
					
					sender.send(ack);
				}
			}

		} catch (InterruptedException unexpected) {
			throw new Error(unexpected);
		}
	}
}
