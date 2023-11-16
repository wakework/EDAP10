
import lift.LiftView;
import lift.Passenger;

public class DataMonitor {

	private LiftView view;
	private int[] toEnter, toExit;
	private boolean doors = false;
	private int currentFloor = 0, nbrOfPass = 0, direction = 1, entering = 0, exiting = 0;

	public DataMonitor(int floors, LiftView view) {
		this.view = view;
		toEnter = new int[floors];
		toExit = new int[floors];

		for (int i = 0; i < floors; i++) {
			toEnter[i] = 0;
			toExit[i] = 0;
		}
	}

	/* ----------------------------- LIFT ----------------------------- */

	public synchronized int direction() {

		if (currentFloor == 6) {
			direction = -1;

		} else if (currentFloor == 0) {
			direction = 1;

		}

		return direction;
	}

	public synchronized void checkHalt() throws InterruptedException {
		while (nbrOfPass == 0 && !enteringPassengers()) {
			wait();
		}
	}

	private synchronized boolean enteringPassengers() {
		boolean waitingPassengers = false;

		for (int i = 0; i < toEnter.length; i++) {
			if (toEnter[i] != 0) {
				waitingPassengers = true;
			}
		}

		return waitingPassengers;
	}

	public synchronized void checkFloor(int floor) throws InterruptedException {
		currentFloor = floor;

		if ((toEnter[floor] > 0 && nbrOfPass < 4) || toExit[floor] > 0) {
			view.openDoors(floor);
			doors = true;
			notifyAll();

			while ((nbrOfPass < 4 && toEnter[floor] > 0) || toExit[floor] > 0 || entering != 0 || exiting != 0) {
				wait();
			}

			doors = false;
			view.closeDoors();
		}

	}

	
	/* -------------------------- PASSENGERS -------------------------- */

	public synchronized int getFloor() {
		return currentFloor;
	}

	public synchronized void awaitLift(Passenger pass, int floor) throws InterruptedException {
		toEnter[pass.getStartFloor()]++;
		notifyAll();

		while (currentFloor != floor || nbrOfPass > 3 || !doors) {
			wait();
		}

		entering++;
		nbrOfPass++;
		toEnter[floor]--;

	}

	public synchronized void enter() {
		entering--;
		if (entering == 0 && exiting == 0) {
			notifyAll();
		}
	}

	public synchronized void awaitFloor(Passenger pass, int floor) throws InterruptedException {
		toExit[pass.getDestinationFloor()]++;

		while (currentFloor != floor || !doors) {
			wait();
		}

		exiting++;
		nbrOfPass--;
		toExit[floor]--;

	}

	public synchronized void exit() {
		exiting--;
		if (entering == 0 && exiting == 0) {
			notifyAll();
		}
	}
}
