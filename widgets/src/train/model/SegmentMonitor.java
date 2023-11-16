package train.model;

import java.util.HashSet;
import java.util.Set;

public class SegmentMonitor {
	private Set<Segment> busy = new HashSet<>();
	
	public synchronized void setBusy(Segment s) throws InterruptedException {
		while (busy.contains(s)) {
			wait();
		}
		
		s.enter();
		busy.add(s);
		
	}
	
	public synchronized boolean isBusy(Segment s) throws InterruptedException {
		return busy.contains(s);
	}
	
	public synchronized void setFree(Segment s) throws InterruptedException {
		busy.remove(s);
		notifyAll();
	}
	
}
