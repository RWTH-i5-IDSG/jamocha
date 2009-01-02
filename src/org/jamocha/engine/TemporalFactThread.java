package org.jamocha.engine;

import java.util.HashMap;
import java.util.Map;

import org.jamocha.engine.TemporalValidity.EventPoint;
import org.jamocha.engine.TemporalValidity.EventPoint.Type;
import org.jamocha.engine.workingmemory.elements.Fact;

public class TemporalFactThread extends TemporalThread {

	protected Map<TemporalValidity.EventPoint, Fact> eventPoint2Fact;
	
	protected Map<Fact, TemporalValidity.EventPoint> fact2nextEventPoint;
	
	public TemporalFactThread(Engine e) {
		super(e, "Temporal Fact Thread");
		eventPoint2Fact = new HashMap<EventPoint, Fact>();
		fact2nextEventPoint = new HashMap<Fact, EventPoint>();
	}
	
	
	protected void handle(EventPoint nextEventPoint) {
		Fact f = eventPoint2Fact.get(nextEventPoint);
		if (nextEventPoint.getType() == Type.START) {
			try {
				engine.hardAssertFact(f);
			} catch (AssertException e) {
				notifyForException(e);
			}
		} else {
			try {
				engine.hardRetractFact(f);
			} catch (RetractException e) {
				notifyForException(e);
			}
		}
	}
	
	public synchronized void insertFact(Fact f) {
		EventPoint e = f.getTemporalValidity().getNextEvent(now());
		eventPoint2Fact.put(e, f);
		fact2nextEventPoint.put(f, e);
		eventPoints.add(e);
		this.notify();
	}
	
	public synchronized void removeFact(Fact f) {
		eventPoint2Fact.values().remove(f);
		EventPoint ep = fact2nextEventPoint.get(f);
		eventPoints.remove(ep);		
		fact2nextEventPoint.remove(f);
		this.notify();
	}
	
	protected void skipToNextEventPoint(EventPoint actEventPoint) {
		Fact f = eventPoint2Fact.get(actEventPoint);
		EventPoint newEP = f.getTemporalValidity().getNextEvent(actEventPoint.getTimestamp()+1l);
		eventPoints.add(newEP);
		eventPoint2Fact.put(newEP, f);
		fact2nextEventPoint.put(f, newEP);
		eventPoint2Fact.remove(actEventPoint);
	}
	
	
}
