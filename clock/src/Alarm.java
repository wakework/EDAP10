import clock.io.ClockOutput;

public class Alarm extends Thread {
	private DataMonitor data;
	private ClockOutput out;
	private boolean interrupt;

	public Alarm(DataMonitor data, ClockOutput out) {
		this.data = data;
		this.out = out;
		interrupt = false;
	}
	
	@Override
	public void run() {
		
		long startTime = System.currentTimeMillis();
		int counter = 0;
		
		while(!interrupt && counter < 21) {
			out.alarm();
			
			long sleep = startTime + 1000 * (counter + 1);
			long current = System.currentTimeMillis();
			
			try {
				Thread.sleep(sleep - current);
				counter++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		data.toggleActive();
	}
	
	@Override
	public void interrupt() {
		interrupt = !interrupt;
	}
}
