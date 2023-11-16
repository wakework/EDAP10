import enhancements.DataMonitorV2;
import enhancements.LiftThreadV2;
import enhancements.PassengerThreadV2;
import lift.LiftView;

public class StopEachFloor {

	public static void main(String[] args) {

        final int NBR_FLOORS = 7, MAX_PASSENGERS = 4;
        LiftView  view = new LiftView(NBR_FLOORS, MAX_PASSENGERS);
        DataMonitor data = new DataMonitor(NBR_FLOORS, view);
        
        for (int i = 0; i < 20; i++) {
        	new PassengerThread(data, view).start();
        }
        
        Thread lift = new LiftThread(data, view);
        lift.start();
    }
	
//	public static void main(String[] args) {
//
//        final int NBR_FLOORS = 7, MAX_PASSENGERS = 4;
//        LiftView  view = new LiftView(NBR_FLOORS, MAX_PASSENGERS);
//        DataMonitorV2 data = new DataMonitorV2(NBR_FLOORS, view);
//        
//        for (int i = 0; i < 4; i++) {
//        	new PassengerThreadV2(data, view).start();
//        }
//        
//        Thread lift = new LiftThreadV2(data, view);
//        lift.start();
//    }
	
}
