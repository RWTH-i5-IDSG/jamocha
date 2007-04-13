package org.jamocha.parser;

import java.util.GregorianCalendar;
import java.util.TimeZone;

public class JamochaValueUtils {

    public static JamochaValue convertToDateTime(String n) {
	int day = 0, month = 0, year = 0, hours = 0, minutes = 0, seconds = 0;
	String gmtoffset = "+0";

	n = "\"" + n + "\""; // dirty hack; later, we should fix the indices
	// in the substring calls

	day = Integer.parseInt(n.substring(9, 11));
	month = Integer.parseInt(n.substring(6, 8));
	year = Integer.parseInt(n.substring(1, 5));

	if (n.length() > 12) {
	    hours = Integer.parseInt(n.substring(12, 14));
	    minutes = Integer.parseInt(n.substring(15, 17));
	    if (n.length() > 18) {
		if (n.charAt(17) == ':') {
		    seconds = Integer.parseInt(n.substring(18, 20));
		    if (n.length() > 21) {
			gmtoffset = (n.substring(20, n.length() - 1));
		    }
		} else /* if (n.charAt(17)=='+' || n.charAt(17)=='-') */{
		    gmtoffset = (n.substring(17, n.length() - 1));
		}
	    }
	}

	GregorianCalendar cal = new GregorianCalendar(year, month - 1, day, hours, minutes, seconds);
	cal.setTimeZone(TimeZone.getTimeZone("GMT" + gmtoffset + ":00"));
	return JamochaValue.newDate(cal);
    }
    
    public static JamochaValue convertToDouble(String n){
	return JamochaValue.newDouble(Double.parseDouble(n));
    }
   
    public static JamochaValue convertToLong(String n){
	return JamochaValue.newLong(Integer.parseInt(n));
    }    
    
}
