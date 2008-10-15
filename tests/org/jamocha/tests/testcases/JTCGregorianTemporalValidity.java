package org.jamocha.tests.testcases;

import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.jamocha.engine.GregorianTemporalValidity;
import org.jamocha.engine.TemporalValidity;

public class JTCGregorianTemporalValidity extends TestCase {

	public void test() {
		String sec = "0";
		String hour = "15";
		String year = "";
		String month = "10";
		long duration = 60;
		String min = "30";
		String day = "";
		String weekday = "3";
		GregorianTemporalValidity gtv = new GregorianTemporalValidity(
				sec,min,hour,day,month,year,weekday,duration);
		
		GregorianCalendar t = new GregorianCalendar();
		System.out.println(t.getTime());
		long tinm = t.getTimeInMillis() / 1000;
		
		for (int i=0; i< 10; i++) {
			TemporalValidity.EventPoint next = gtv.getNextEvent(tinm);
			tinm=next.getTimestamp();
			
			t.setTimeInMillis(tinm*1000);
			
			System.out.println(t.getTime()+" "+next.getType());
			
		}
		
	}
	
	
}
