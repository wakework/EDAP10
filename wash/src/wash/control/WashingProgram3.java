package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

import static wash.control.WashingMessage.Order.*;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram3 extends ActorThread<WashingMessage> {

	private WashingIO io;
	private ActorThread<WashingMessage> temp;
	private ActorThread<WashingMessage> water;
	private ActorThread<WashingMessage> spin;

	public WashingProgram3(WashingIO io, ActorThread<WashingMessage> temp, ActorThread<WashingMessage> water,
			ActorThread<WashingMessage> spin) {
		this.io = io;
		this.temp = temp;
		this.water = water;
		this.spin = spin;
	}

	@Override
	public void run() {
		try {
			System.out.println("Program 3 started");

			// 1. Switch off heating
			temp.send(new WashingMessage(this, TEMP_IDLE));

			// 2. Wait for temperature controller to acknowledge
			System.out.println("Received " + receive());

			// 3. Drain water from machine
			// SR5. Drain water during centrifuging.
			water.send(new WashingMessage(this, WATER_DRAIN));
			System.out.println("Received " + receive());

			// 4. Stop water regulation
			// SR3. No water in machine when unlocking hatch.
			water.send(new WashingMessage(this, WATER_IDLE));

			// 5. Turn off spin
			// SR4. Barrel not spinning while opening hatch
			spin.send(new WashingMessage(this, SPIN_OFF));
			System.out.println("Received " + receive());

			// 6. Unlock hatch
			io.lock(false);

			System.out.println("Program 3 finished");

		} catch (InterruptedException e) {
			try {
				temp.send(new WashingMessage(this, TEMP_IDLE));
				water.send(new WashingMessage(this, WATER_IDLE));
				spin.send(new WashingMessage(this, SPIN_OFF));

			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			System.out.println("washing program 3 terminated");
		}
	}
}
