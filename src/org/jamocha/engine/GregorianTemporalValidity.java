package org.jamocha.engine;

import java.util.Arrays;
import java.util.GregorianCalendar;

/* TODO:
 * translate it to english. it's in german here, because its easier to use it
 * in my diploma thesis ;)
 */


public class GregorianTemporalValidity implements TemporalValidity {

	// einen Monat maximales Vorwärtsfenster
	public final long MAXIMUM_FORWARD_WINDOW = 60 * 60 * 24 * 30;

	class IntervalLayer {
		
		private int field;
		
		private int[] beginPoints;
		
		/**
		 * erstellt eine Intervallebene. Mit 'field' wird eine Konstante aus
		 * GregorianCalendar übergeben, die den Typ der Ebene angibt (Monat,
		 * Jahr, usw). 'beginPoints' sind die Startpunkte dieser Intervallebene.
		 * Es wird davon ausgegangen, dass diese Startpunkte sortiert sind.
		 */
		public IntervalLayer(int field, int[] beginPoints) {
			this.field=field;
			this.beginPoints=beginPoints;
		}

		/**
		 * gibt true zurück, gdw 'timestamp' in dieser Intervallebene aktiviert
		 * ist
		 */
		public boolean isActivated(long timestamp) {
			GregorianCalendar b = new GregorianCalendar();
			b.setTimeInMillis(timestamp);
			int f = b.get(field);
			return ( Arrays.binarySearch(beginPoints, f) >= 0 );
		}
		
		public long nextActivatedFrom(long timestamp) {
			GregorianCalendar b = new GregorianCalendar();
			b.setTimeInMillis(timestamp);
			int f = b.get(field);
			// bestimme nächstmöglichen Eintrag
			int idx = 0;
			while (beginPoints[idx] < f) {
				idx++;
				if (idx == beginPoints.length) {
					idx = 0;
					break;
				}
			}
			int newFieldval = beginPoints[idx];
			while (f != newFieldval) {
				b.add(field, 1);
				f = b.get(field);
			}
			return b.getTimeInMillis();
		}
	}
	
	
	/**
	 * expandiert eine Eingabe im "crontab"-Format, beispielsweise '* /5'
	 * zu einer Liste von Ganzzahlen.
	 * 
	 * Wenn ungültige Werte übergeben werden, wird NumberFormatException
	 * geworfen.
	 * 
	 * @param cronStyle 	Der Eingabestring
	 * @param dBegin 		Der Beginn des Wertebereichs
	 * @param dEnd			Das Ende des Wertebereichs (inklusiv)
	 * @param type			Die GregorianCalendar-Konstante für das Feld
	 */
	private IntervalLayer expand(String cronStyle, int dBegin, int dEnd, int type) 
			throws NumberFormatException{
		if (cronStyle.startsWith("*")) {
			int stepSize;
			if (cronStyle.equals("*")) {
				// FALL "*"
				stepSize = 1;
			} else {
				// FALL "*/n"
				stepSize = Integer.parseInt(cronStyle.substring(2));
			}
			
			/* hier wird berechnet, wie viele Elemente entstehen werden.
			 * Es sind "/ (dEnd-dBegin+1)/stepSize \" viele, wobei / und \
			 * für die obere Gaussklammer stehen. Da Fließkommaoperationen
			 * hierfür zu ungenau sind, muss hier anders gerechnet werden:
			 */
			int cnt = (dEnd-dBegin+1) / stepSize; // Integer-Division
			if ( ( (dEnd-dBegin+1) % stepSize != 0 ) )  cnt++; // "Gaussklammer"
			
			int[] result = new int[cnt];
			int j=0;
			for (int i=dBegin; i<=dEnd; i+=stepSize) result[j++]=i;
			return new IntervalLayer(type,result);
		} else {
			// FALL "n,m,o,p,q,..."
			String[] values = cronStyle.split(",");
			int[] result = new int[values.length];
			for (int i=0; i< values.length; i++) {
				result[i] = Integer.parseInt(values[i]);
				// prüfe auf gültigkeit
				if (result[i] < dBegin || result[i] > dEnd)
					throw new NumberFormatException();
			}
			return new IntervalLayer(type,result);
		}
	}
	
	private IntervalLayer[] intervalLayers;
	
	private long duration;
	
	public GregorianTemporalValidity(
			String sec, String min, String hour, String day, String month,
			String year, String weekday, long duration) {
		
		IntervalLayer secs, mins, hours,days,months,years,weekdays;
		
		secs     = expand(sec,0,59,GregorianCalendar.SECOND);
		mins     = expand(min,0,59,GregorianCalendar.MINUTE);
		hours    = expand(hour,0,23,GregorianCalendar.HOUR_OF_DAY);
		days     = expand(day,1,31,GregorianCalendar.DAY_OF_MONTH);
		months   = expand(month,1,12,GregorianCalendar.MONTH);
		years    = expand(year,1980,3000,GregorianCalendar.YEAR); // hier lauert der y3k-bug :-)
		weekdays = expand(weekday,0,6,GregorianCalendar.DAY_OF_WEEK);
		
		intervalLayers = new IntervalLayer[7];
		intervalLayers[0] = years;
		intervalLayers[1] = months;
		intervalLayers[2] = days;
		intervalLayers[3] = weekdays;
		intervalLayers[4] = hours;
		intervalLayers[5] = mins;
		intervalLayers[6] = secs;
		
		this.duration = duration;
	}
	
	private long getNextEventHelper(long from, long forward_distance) {

		/* Gebe MAXIMUM_FORWARD_DISTANCE+1 zurück, wenn wir über das maximale
		 * Vorwärtsfenster hinaus geraten sind. Es dient hier als
		 * Ersatz für "unendlich".
		 * Man kann hier nicht Long.MAX_VALUE benutzen, da sonst die weitere
		 * Arithmetik in getNextEvent(long) nicht funktionieren würde.
		 */
		if (forward_distance> MAXIMUM_FORWARD_WINDOW) return MAXIMUM_FORWARD_WINDOW;
		
		long t = from;
		
		/*
		 * "Durchhangeln" über die Intervallebenen
		 */
		for(IntervalLayer layer : intervalLayers) {
			if (!layer.isActivated(t)) {
				t = layer.nextActivatedFrom(t);
			}
		}
		
		/* Überprüfe, ob der Wert immernoch in allen Intervallebenen aktivierend
		 * ist */
		boolean isIn=true;
		for(IntervalLayer layer : intervalLayers) {
			isIn = isIn && layer.isActivated(t);
		}
		
		if (isIn) {
			return t;
		} else {
			return getNextEventHelper(t, t-from);
		}

	}
	
	
	/**
	 * Ein Ereigniszeitpunkt ist ein Zeitpunkt, an dem ein Intervall beginnt
	 * oder endet. Diese Methode berechnet, beginnend bei 'from' den nächsten
	 * Ereigniszeitpunkt.
	 */
	public EventPoint getNextEvent(long from) {
	
		long r1 = getNextEventHelper(from, 0);
		long r2 = getNextEventHelper(from-duration, 0) + duration;
		
		EventPoint result = (r1 < r2) ? 
						new EventPoint(EventPoint.Type.START, r1) :
						new EventPoint(EventPoint.Type.STOP,  r2) ;
		
		return result;
	}

}
