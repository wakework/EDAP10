package factory.model;

public class WidgetMonitor {
	
	private Conveyor conveyor;
	private int queue = 0;
	
	public WidgetMonitor(Conveyor conveyor) {
		this.conveyor = conveyor;
	}
	
	public synchronized void start() throws InterruptedException {
		queue--;
		while(queue != 0) {
			wait();
		}
		
		conveyor.on();
		notifyAll();
	}
	
	public synchronized void stop() throws InterruptedException {
		queue++;
		conveyor.off();
	}

}
