package org.jamocha.engine.configurations;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class TemporalValidityConfiguration extends AbstractConfiguration {

	private Parameter year,month,day,weekday,hour,minute,second,duration;
	
	public boolean isFactBinding() {
		return false;
	}

	public String getExpressionString() {
		return null;
	}

	public JamochaValue getValue(Engine engine) throws EvaluationException {
		return null;
	}

	public String format(Formatter visitor) {
		return null;
	}

	public Parameter getYear() {
		return year;
	}

	public void setYear(Parameter year) {
		this.year = year;
	}

	public Parameter getMonth() {
		return month;
	}

	public void setMonth(Parameter month) {
		this.month = month;
	}

	public Parameter getDay() {
		return day;
	}

	public void setDay(Parameter day) {
		this.day = day;
	}

	public Parameter getWeekday() {
		return weekday;
	}

	public void setWeekday(Parameter weekday) {
		this.weekday = weekday;
	}

	public Parameter getHour() {
		return hour;
	}

	public void setHour(Parameter hour) {
		this.hour = hour;
	}

	public Parameter getMinute() {
		return minute;
	}

	public void setMinute(Parameter minute) {
		this.minute = minute;
	}

	public Parameter getSecond() {
		return second;
	}

	public void setSecond(Parameter second) {
		this.second = second;
	}

	public Parameter getDuration() {
		return duration;
	}

	public void setDuration(Parameter duration) {
		this.duration = duration;
	}

}
