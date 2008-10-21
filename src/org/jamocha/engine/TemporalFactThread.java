package org.jamocha.engine;

import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.jamocha.engine.TemporalValidity.EventPoint;
import org.jamocha.engine.workingmemory.elements.Fact;

public class TemporalFactThread extends Thread {

	private static class EventPointComparator implements Comparator<EventPoint> {

		public int compare(EventPoint o1, EventPoint o2) {
			long l = o1.getTimestamp() - o2.getTimestamp();
			if (l==0) return 0;
			if (l<0) return -1;
			/*if (l>0)*/ return 1;
		}
		
	}
	
	
	private Engine engine;
	
	private Map<TemporalValidity.EventPoint, Fact> factsNextEventPoint;
	
	private Queue<TemporalValidity.EventPoint> eventPoints;
	
	public TemporalFactThread(Engine e) {
		engine = e;
		factsNextEventPoint = new HashMap<EventPoint, Fact>();
		eventPoints = new PriorityQueue<EventPoint>(100, new EventPointComparator());
		
	}
	
	private static long now() {
		GregorianCalendar now = new GregorianCalendar();
		return now.getTimeInMillis();
	}
	
	public void run() {
		while (true) {
			synchronized (this) {
				EventPoint nextEventPoint = eventPoints.poll();
				if (nextEventPoint == null) {
					/* Unsere Queue ist leer. Wir schlafen also auf  
					 * unbestimmte Zeit. Damit geben wir auch den Monitor
					 * wieder ab, damit andere Threads ggf neue Ereignis-
					 * punkte hinzufügen können.
					 */
					try {
						this.wait();
					} catch (InterruptedException e) {}
				} else {
					// Wir haben ein Element in unserer Queue.
					long to = nextEventPoint.getTimestamp();
					long from = now();
					long distance = to - from;
					
					
				}

			
			}
		}
	}
	
	
	
	
	
	
}
