import clock.io.ClockOutput;

public class Time extends Thread {
	private DataMonitor data;
	private ClockOutput out;
	private Alarm handleAlarm;
	
	public Time (DataMonitor data, ClockOutput out) {
		this.data = data;
		this.out = out;
	}
	
	@Override
	public void run() {
		
		long startTime = System.currentTimeMillis();
		int counter = 0;
		
		try {
			
			while(true) {
				
				int hour = data.getHour();
				int minute = data.getMinute();
				int second = data.getSecond();
				
				out.displayTime(hour, minute, second);
				
				if (second == 59) {
					second = 0;
					minute++;
					
					if (minute > 59) {
						minute = 0;
						hour++;
						
						if (hour > 23) {
							hour = 0;
						}
					}
					
				} else {
					second++;
				}
				
				data.setTime(hour, minute, second);
				
				long sleep = startTime + 1000 * (counter + 1);
				long current = System.currentTimeMillis();
				
				Thread.sleep(sleep - current);
				counter++;
				
				if (data.checkAlarm() && data.isSet()) {
					handleAlarm = new Alarm(data, out);
					handleAlarm.start();
					data.toggleActive();
				}
				
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void interrupt() {
		handleAlarm.interrupt();
	}
}
