package org.jamocha.engine;

import java.beans.ExceptionListener;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.PriorityQueue;
import java.util.Queue;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.TemporalValidity.EventPoint;

public abstract class TemporalThread extends Thread {

	protected static class EventPointComparator implements Comparator<EventPoint> {

		public int compare(EventPoint o1, EventPoint o2) {
			long l = o1.getTimestamp() - o2.getTimestamp();
			if (l==0) return 0;
			if (l<0) return -1;
			/*if (l>0)*/ return 1;
		}
	}
	
	protected Engine engine;
	
	protected Queue<TemporalValidity.EventPoint> eventPoints;
	
	protected ExceptionListener exceptionListener;
	
	public TemporalThread(Engine e) {
		engine = e;
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
	
	protected static long now() {
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
					
					long d = to-now();
					if (d <= 0) {
						/* Wir müssen jetzt handeln. Hier ist jetzt potentiell
						 * ein Ereignispunkt erreicht.
						 * 
						 * Es kann aber sein, dass das korrespondierende Fakt
						 * inzwischen gelöscht wurde. Deshalb holen wir den
						 * Kopf der Queue nochmal
						 */
						Logging.logger(this.getClass()).info("Lag: "+(-d)+"ms");
						nextEventPoint = eventPoints.peek();						
						
						while (nextEventPoint.getTimestamp() == to) {
							eventPoints.remove();
							handle(nextEventPoint);
							skipToNextEventPoint(nextEventPoint);
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

	protected abstract void skipToNextEventPoint(EventPoint nextEventPoint);

	protected abstract void handle(EventPoint nextEventPoint);
	
	protected void notifyForException(Exception e) {
		if (exceptionListener != null)
			exceptionListener.exceptionThrown(e);
	}

}
