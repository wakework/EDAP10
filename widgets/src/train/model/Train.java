package train.model;

import java.util.Stack;

public class Train extends Thread {
	
	private Stack<Segment> queue;
//	private SegmentMonitor data;
	private TestMonitor data;
	private Route route;
	
	public Train(SegmentMonitor data, Route route) {
//		this.data = data;
		this.route = route;
		queue = new Stack<>();
	}
	
	public Train(TestMonitor data, Route route) {
		this.data = data;
		this.route = route;
		queue = new Stack<>();
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 3; i++) {
        	Segment temp = route.next();
        	
        	try {
        		while (data.isBusy(temp)) {
        			temp = route.next();
        		}
        		
        		queue.add(temp);
				data.setBusy(temp);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
        }
		
		while(true) {
			        	
        	try {
        		Segment temp = route.next();
        		Segment tail = queue.get(0); 
				
        		data.setBusy(temp);
				queue.push(temp); 
				
				queue.remove(tail);
				tail.exit();
				data.setFree(tail);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
        }
		
	}
	
}
