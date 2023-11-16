import java.util.concurrent.Semaphore;

public class DataMonitor {
	private int hour, minute, second;
	private int alarmHour, alarmMinute, alarmSecond;
	
	private boolean set = false;
	private boolean active = false;
	
	private Semaphore timeLock = new Semaphore(1);
	private Semaphore alarmTimeLock = new Semaphore(1);
	
	/*----------------------------------- TIME ------------------------------------- */
	public void setTime(int hour, int minute, int second) throws InterruptedException {
		timeLock.acquire();
		
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		
		timeLock.release();
		
		System.out.println("Time set to " + hour + ":" + minute + ":" + second);
	}
	
	public int getHour() throws InterruptedException {
		timeLock.acquire();
		int lock = hour;
		timeLock.release();
		
		return lock;
	}
	
	public int getMinute() throws InterruptedException {
		timeLock.acquire();
		int lock = minute;
		timeLock.release();
		
		return lock;
	}
	
	public int getSecond() throws InterruptedException {
		timeLock.acquire();
		int lock = second;
		timeLock.release();
		
		return lock;
	}
	
	/*--------------------------------- ALARMTIME ----------------------------------- */
	public void setAlarm(int hour, int minute, int second) throws InterruptedException {
		this.alarmHour = hour;
		this.alarmMinute = minute;
		this.alarmSecond = second;
		
		System.out.println("Alarm set to " + hour + ":" + minute + ":" + second);
	}
	
	
	public int getAlarmHour() throws InterruptedException {
		alarmTimeLock.acquire();
		int lock = alarmHour;
		alarmTimeLock.acquire();
		
		return lock;
	}
	
	public int getAlarmMinute() throws InterruptedException {
		alarmTimeLock.acquire();
		int lock = alarmMinute;
		alarmTimeLock.acquire();
		
		return lock;
	}
	
	public int getAlarmSecond() throws InterruptedException {
		alarmTimeLock.acquire();
		int lock = alarmSecond;
		alarmTimeLock.acquire();
		
		return lock;
	}
	
	/*----------------------------------- ALARM ------------------------------------- */
	public boolean toggleAlarm() {
		set = !set;
		
		if(set) {
			System.out.println("Alarm on");
		} else {
			System.out.println("Alarm off");
		}
		
		return set;
	}
	
	public boolean isSet() {
		return set;
	}
	
	public boolean toggleActive() {
		active = !active;
		
		if (active) {
			System.out.println("BEEP BEEP");
		} else {
			System.out.println("Alarm cancelled");
		}
		
		return active;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean checkAlarm() {
		return hour == alarmHour && minute == alarmMinute && second == alarmSecond;
	}
}
