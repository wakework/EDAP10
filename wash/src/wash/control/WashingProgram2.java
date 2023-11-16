package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

import static wash.control.WashingMessage.Order.*;

/* Program 2 for washing machine */
public class WashingProgram2 extends ActorThread<WashingMessage> {

	private WashingIO io;
	private ActorThread<WashingMessage> temp;
	private ActorThread<WashingMessage> water;
	private ActorThread<WashingMessage> spin;

	private final int MINUTE = 60000;

	public WashingProgram2(WashingIO io, ActorThread<WashingMessage> temp, ActorThread<WashingMessage> water,
			ActorThread<WashingMessage> spin) {
		this.io = io;
		this.temp = temp;
		this.water = water;
		this.spin = spin;
	}

	@Override
	public void run() {
		System.out.println("Program 2 started");
		
		try {
			// 1. Lock the hatch
			io.lock(true);

			// 2. Let water into machine
			water.send(new WashingMessage(this, WATER_FILL));
			System.out.println("Received " + receive());

			// 3. Heat water.
			// SR1. Ensure that machine is filled with water
			temp.send(new WashingMessage(this, TEMP_SET_40));
			System.out.println("Received " + receive());

			// 4. Pre-wash for 20 minutes and keep temperature
			spin.send(new WashingMessage(this, SPIN_SLOW));
			System.out.println("Received " + receive());

			Thread.sleep(20 * MINUTE / Settings.SPEEDUP);

			spin.send(new WashingMessage(this, SPIN_OFF));
			System.out.println("Received " + receive());

			// 5. Drain machine
			temp.send(new WashingMessage(this, TEMP_IDLE));
			System.out.println("Received " + receive());

			water.send(new WashingMessage(this, WATER_DRAIN));
			System.out.println("Received " + receive());

			water.send(new WashingMessage(this, WATER_IDLE));

			// 6. Let water into machine
			water.send(new WashingMessage(this, WATER_FILL));
			System.out.println("Received " + receive());

			// 7. Heat water.
			// SR1. Ensure that machine is filled with water
			temp.send(new WashingMessage(this, TEMP_SET_60));
			System.out.println("Received " + receive());

			// 8. Wash for 30 minutes and keep temperature
			spin.send(new WashingMessage(this, SPIN_SLOW));
			System.out.println("Received " + receive());

			Thread.sleep(30 * MINUTE / Settings.SPEEDUP);

			spin.send(new WashingMessage(this, SPIN_OFF));
			System.out.println("Received " + receive());

			// 9. Drain machine
			temp.send(new WashingMessage(this, TEMP_IDLE));
			System.out.println("Received " + receive());

			water.send(new WashingMessage(this, WATER_DRAIN));
			System.out.println("Received " + receive());

			water.send(new WashingMessage(this, WATER_IDLE));

			// 10. Rinse 5 * 2mins in cold water
			System.out.println("Initiate rinising");
			for (int i = 0; i < 5; i++) {
				water.send(new WashingMessage(this, WATER_FILL));
				System.out.println("Received " + receive());

				spin.send(new WashingMessage(this, SPIN_SLOW));
				System.out.println("Received " + receive());

				Thread.sleep(2 * MINUTE / Settings.SPEEDUP);

				spin.send(new WashingMessage(this, SPIN_OFF));
				System.out.println("Received " + receive());

				// SR5. Drain water during centrifuging.
				water.send(new WashingMessage(this, WATER_DRAIN));
				System.out.println("Received " + receive());
			}

			// 11. Centrifuge
			spin.send(new WashingMessage(this, SPIN_FAST));
			System.out.println("Received " + receive());

			Thread.sleep(5 * MINUTE / Settings.SPEEDUP);

			// SR4. Barrel not spinning while opening hatch
			spin.send(new WashingMessage(this, SPIN_OFF));
			System.out.println("Received " + receive());

			// SR3. No water in machine when unlocking hatch.
			water.send(new WashingMessage(this, WATER_IDLE));

			Thread.sleep(MINUTE / Settings.SPEEDUP);

			// 12. Unlock hatch
			io.lock(false);

			System.out.println("Program 2 finished");

		} catch (InterruptedException e) {
			try {
				temp.send(new WashingMessage(this, TEMP_IDLE));
				water.send(new WashingMessage(this, WATER_IDLE));
				spin.send(new WashingMessage(this, SPIN_OFF));

			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			System.out.println("washing program 2 terminated");
		}
	}
}
