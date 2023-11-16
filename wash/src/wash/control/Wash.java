package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

	private static ActorThread<WashingMessage> currentProgram;

	public static void main(String[] args) throws InterruptedException {
		WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);

		WashingIO io = sim.startSimulation();

		TemperatureController temp = new TemperatureController(io);
		WaterController water = new WaterController(io);
		SpinController spin = new SpinController(io);

		temp.start();
		water.start();
		spin.start();

		while (true) {
			int n = io.awaitButton();
			System.out.println("User selected program: " + n);

			switch (n) {
			case (0):
				currentProgram.interrupt();
				break;

			case (1):
				currentProgram = new WashingProgram1(io, temp, water, spin);
				currentProgram.start();
				break;

			case (2):
				currentProgram = new WashingProgram2(io, temp, water, spin);
				currentProgram.start();
				break;

			case (3):
				currentProgram = new WashingProgram3(io, temp, water, spin);
				currentProgram.start();
				break;

			}

		}
	}
}
