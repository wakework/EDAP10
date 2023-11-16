import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {
	private static DataMonitor data = new DataMonitor();
	private static Time handleTime;

	/* Main thread. Creates Time-thread and handles choices. */
    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();

        out.displayTime(0, 0, 0);
        handleTime = new Time(data, out);
        
        handleTime.start();
        
        while(true) {
        	
			in.getSemaphore().acquire();
    		UserInput userInput = in.getUserInput();
            
            int choice = userInput.getChoice();
            int h = userInput.getHours();
            int m = userInput.getMinutes();
            int s = userInput.getSeconds();
            
            handleChoice(out, choice, h, m, s);
            
    	}
    }
	
	private static void handleChoice(ClockOutput out, int choice, int h, int m, int s) throws InterruptedException {
		System.out.println("Choice: " + choice);
		
		/* New time set. */
		if (choice == 1) { 
        	data.setTime(h, m, s);
        
        /* New alarm set. */
        } else if (choice == 2) {
        	data.setAlarm(h, m, s);
        
        /* Toggle alarm. */
        } else if (choice == 3) {
        	out.setAlarmIndicator(data.toggleAlarm());
        	
        	if (data.isActive()) {
        		handleTime.interrupt();
        		data.toggleActive();
        	}
        	
        }
		
	}
	
}
