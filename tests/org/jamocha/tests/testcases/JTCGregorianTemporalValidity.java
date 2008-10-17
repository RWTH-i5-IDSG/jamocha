package org.jamocha.tests.testcases;

import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.jamocha.engine.GregorianTemporalValidity;
import org.jamocha.engine.TemporalValidity;

public class JTCGregorianTemporalValidity extends TestCase {

	public void test() {
		GregorianTemporalValidity gtv = new GregorianTemporalValidity();
		
		gtv.setSeconds("0");
		gtv.setHours("15");
		gtv.setYears("");
		gtv.setMonths("10");
		gtv.setMinutes("30");
		gtv.setDays("*");
		gtv.setWeekdays("3");
		gtv.setDuration(10);
		
		
		GregorianCalendar t = new GregorianCalendar();
		System.out.println(t.getTime());
		long tinm = t.getTimeInMillis();
		
		for (int i=0; i< 10; i++) {
			TemporalValidity.EventPoint next = gtv.getNextEvent(tinm+1000);
			tinm=next.getTimestamp();
			
			t.setTimeInMillis(tinm);
			
			System.out.println(t.getTime()+" "+next.getType());
			
		}
		
	}
	
	
}
