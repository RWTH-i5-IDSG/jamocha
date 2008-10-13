package org.jamocha.engine;

import java.util.GregorianCalendar;

/* TODO:
 * translate it to english. it's in german here, because its easier to use it
 * in my diploma thesis ;)
 */


public class GregorianTemporalValidity implements TemporalValidity {

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
	 */
	private static int[] expand(String cronStyle, int dBegin, int dEnd) 
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
			return result;
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
			return result;
		}
	}
	
	private int[] secs, mins, hours,days,months,years,weekdays;
	private long duration;
	
	public GregorianTemporalValidity(
			String sec, String min, String hour, String day, String month,
			String year, String weekday, long duration) {
		this.secs     = expand(sec,0,59);
		this.mins     = expand(min,0,59);
		this.hours    = expand(hour,0,23);
		this.days     = expand(day,1,31);
		this.months   = expand(month,1,12);
		this.years    = expand(year,1980,3000); // hier lauert der y3k-bug :-)
		this.weekdays = expand(weekday,0,6);
		this.duration = duration;
	}
	
	/**
	 * Ein Ereigniszeitpunkt ist ein Zeitpunkt, an dem ein Intervall beginnt
	 * oder endet. Diese Methode berechnet, beginnend bei 'from' den nächsten
	 * Ereigniszeitpunkt.
	 */
	public EventPoint getNextEvent(long from) {
		// Bestimme aus dem Timestamp die verschiedenen Zeitfelder.
		GregorianCalendar t = new GregorianCalendar();
		t.setTimeInMillis(from);
		int sec = t.get(GregorianCalendar.SECOND);
		int min = t.get(GregorianCalendar.MINUTE);
		int hour = t.get(GregorianCalendar.HOUR_OF_DAY);
		int day = t.get(GregorianCalendar.DAY_OF_MONTH);
		int month = t.get(GregorianCalendar.MONTH)+1;
		int year = t.get(GregorianCalendar.YEAR);
		int weekday;
		/* Da der Rückgabewert für Wochentage eine der Wochentags-Konstanten
		 * ist, deren Werte in der Java-API Dokumentation nicht klar definiert
		 * sind, muss man hier diese switch-Anweisung durchführen.
		 */
		switch (GregorianCalendar.DAY_OF_WEEK) {
		case GregorianCalendar.MONDAY:    weekday=0; break;
		case GregorianCalendar.TUESDAY:   weekday=1; break;
		case GregorianCalendar.WEDNESDAY: weekday=2; break;
		case GregorianCalendar.THURSDAY:  weekday=3; break;
		case GregorianCalendar.FRIDAY:    weekday=4; break;
		case GregorianCalendar.SATURDAY:  weekday=5; break;
		case GregorianCalendar.SUNDAY:    weekday=6; break;
		}
		
		
		

		return null;
	}

}
