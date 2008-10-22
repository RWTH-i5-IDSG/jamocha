package org.jamocha.engine;

import java.beans.ExceptionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.jamocha.engine.TemporalValidity.EventPoint;
import org.jamocha.engine.TemporalValidity.EventPoint.Type;
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
	
	private Map<TemporalValidity.EventPoint, Fact> eventPoint2Fact;
	
	private Map<Fact, TemporalValidity.EventPoint> fact2nextEventPoint;
	
	private Queue<TemporalValidity.EventPoint> eventPoints;
	
	private ExceptionListener exceptionListener;
	
	public TemporalFactThread(Engine e) {
		engine = e;
		eventPoint2Fact = new HashMap<EventPoint, Fact>();
		fact2nextEventPoint = new HashMap<Fact, EventPoint>();
		eventPoints = new PriorityQueue<EventPoint>(100, new EventPointComparator());
		exceptionListener = null;
	}
	
	/**
	 * Registriere das Objekt, dass benachrichtigt werden soll,
	 * sobald hier eine Exception auftritt. Es kann nur ein
	 * solches Objekt geben, da eine mehrfache Behandlung unsinnig
	 * ist.
	 */
	public void registerExceptionListener(ExceptionListener l) {
		exceptionListener = l;
	}
	
	private static long now() {
		GregorianCalendar now = new GregorianCalendar();
		return now.getTimeInMillis();
	}
	
	public void run() {
		while (true) {
			synchronized (this) {
				EventPoint nextEventPoint = eventPoints.peek();//nicht entfernen
				if (nextEventPoint == null) {
					/* Unsere Queue ist leer. Wir schlafen also auf  
					 * unbestimmte Zeit. Damit geben wir auch den Monitor
					 * wieder ab, damit andere Threads ggf neue Ereignis-
					 * punkte hinzufügen können.
					 */
					try {
						this.wait(); //until notified
					} catch (InterruptedException e) {}
				} else {
					// Wir haben ein Element in unserer Queue.
					long to = nextEventPoint.getTimestamp();
					long from = now();
					long distance = to - from;
					try {
						if (distance>0) this.wait(distance); //until notified
					} catch (InterruptedException e) {}
					
					if (to - now() <= 0) {
						// Wir müssen jetzt handeln
						while (nextEventPoint.getTimestamp() == to) {
							eventPoints.remove();
							handle(nextEventPoint);
							eventPoint2Fact.remove(nextEventPoint);
							nextEventPoint = eventPoints.peek();
							if (nextEventPoint == null) break;
						}
					}
					/*
					 * else {
					 * 	ENTWEDER 'SPURIOUS WAKEUP' ODER
					 * 	NOTIFY()-CALL WEGEN NEUEM ELEMENT.
					 * 	WIR BEGINNEN DIE SCHLEIFE EINFACH
					 *  NOCHMAL
					 * }
					 */
				}
			}
		}
	}

	private void handle(EventPoint nextEventPoint) {
		Fact f = eventPoint2Fact.get(nextEventPoint);
		if (nextEventPoint.getType() == Type.START) {
			try {
				engine.hardAssertFact(f, this);
			} catch (AssertException e) {
				notifyForException(e);
			}
		} else {
			try {
				engine.hardRetractFact(f, this);
			} catch (RetractException e) {
				notifyForException(e);
			}
		}
		
		EventPoint newEP = f.getTemporalValidity().getNextEvent(nextEventPoint.getTimestamp()+1000l);
		eventPoints.add(newEP);
		eventPoint2Fact.put(newEP, f);
		fact2nextEventPoint.put(f, newEP);
	}
	
	private void notifyForException(Exception e) {
		if (exceptionListener != null)
			exceptionListener.exceptionThrown(e);
	}

	/**
	 * Fügt Fakt in Thread-interne Liste ein, wenn es temporal ist.
	 * Es gibt dann true zurück. Ansonsten gibt es false zurück und
	 * signalisiert damit der Engine, dass das Fakt nichttemporal ist
	 * und sie sich deshalb selbst um das Fakt kümmern muss.
	 */
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
	
	
	
	
}
