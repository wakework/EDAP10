package enhancements;

import lift.LiftView;
import lift.Passenger;

public class DataMonitorV2 {

	private LiftView view;
	private int[] toEnter, toExit;
	private boolean doors = false;
	private int currentFloor = 0, nbrOfPass = 0, direction = 1, entering = 0, exiting = 0, floors;

	public DataMonitorV2(int floors, LiftView view) {
		this.view = view;
		this.floors = floors;
		toEnter = new int[floors];
		toExit = new int[floors];

		for (int i = 0; i < floors; i++) {
			toEnter[i] = 0;
			toExit[i] = 0;
		}
	}

	/* ----------------------------- LIFT ----------------------------- */

	public synchronized int direction() {
		boolean check = false;

		if (direction == 1) {
			for (int i = currentFloor; i < floors; i++) {
				if (toEnter[i] > 0 || toExit[i] > 0 || currentFloor == 0) {
					check = true;
				} 
			}
			
			if (check) {
				direction = 1;
			} else {
				direction = -1;
			}

		} else {
			for (int i = 0; i < currentFloor; i++) {
				if (toEnter[i] > 0 || toExit[i] > 0 || currentFloor == floors - 1) {
					check = true;
				}
			}
			
			if(check) {
				direction = -1;
			} else {
				direction = 1;
			}
		}

		if (currentFloor == floors - 1) {
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
	
	public synchronized void setFloor(int floor) {
		currentFloor = floor;
	}

	public synchronized void awaitLift(Passenger pass, int floor) throws InterruptedException {
		toEnter[pass.getStartFloor()]++;
		notifyAll();

		while ((currentFloor != floor || nbrOfPass > 3 || !doors)
				|| (pass.getDestinationFloor() > currentFloor && direction() == -1)
				|| (pass.getDestinationFloor() < currentFloor) && direction() == 1) {
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
