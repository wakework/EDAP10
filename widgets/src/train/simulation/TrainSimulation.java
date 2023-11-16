package train.simulation;

//import train.model.SegmentMonitor;
import train.model.TestMonitor;
import train.model.Train;
import train.view.TrainView;

public class TrainSimulation {
	
//	private static SegmentMonitor data = new SegmentMonitor();
	private static TestMonitor data = new TestMonitor();

    public static void main(String[] args) {

        TrainView view = new TrainView();
        
        for (int i = 0; i < 20; i++) {
        	Thread train = new Train(data, view.loadRoute());
        	train.start();
        }
        
    }

}
