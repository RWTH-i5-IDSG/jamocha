package org.jamocha.engine.configurations;

import org.jamocha.engine.Engine;
import org.jamocha.engine.GregorianTemporalValidity;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.TemporalValidity;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

public class TemporalValidityConfiguration extends AbstractConfiguration {

	private Parameter year,month,day,weekday,hour,minute,second,duration;
	
	private static Parameter star = JamochaValue.newString("*");
	
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
		return (year==null) ? star : year;
	}

	public void setYear(Parameter year) {
		this.year = year;
	}

	public Parameter getMonth() {
		return (month==null) ? star : month;
	}

	public void setMonth(Parameter month) {
		this.month = month;
	}

	public Parameter getDay() {
		return (day==null) ? star : day;
	}

	public void setDay(Parameter day) {
		this.day = day;
	}

	public Parameter getWeekday() {
		return (weekday==null) ? star : weekday;
	}

	public void setWeekday(Parameter weekday) {
		this.weekday = weekday;
	}

	public Parameter getHour() {
		return (hour==null) ? star : hour;
	}

	public void setHour(Parameter hour) {
		this.hour = hour;
	}

	public Parameter getMinute() {
		return (minute==null) ? star : minute;
	}

	public void setMinute(Parameter minute) {
		this.minute = minute;
	}

	public Parameter getSecond() {
		return (second==null) ? star : second;
	}

	public void setSecond(Parameter second) {
		this.second = second;
	}

	public Parameter getDuration() {
		return (duration==null) ? star : duration;
	}

	public void setDuration(Parameter duration) {
		this.duration = duration;
	}

	public TemporalValidity getTemporalValidity(Engine engine) throws EvaluationException {
		TemporalValidityConfiguration tvc = this;
		GregorianTemporalValidity temporalValidity = new GregorianTemporalValidity();
		temporalValidity.setDays(tvc.getDay().getValue(engine).implicitCast(JamochaType.STRING).getStringValue());
		temporalValidity.setHours(tvc.getHour().getValue(engine).implicitCast(JamochaType.STRING).getStringValue());
		temporalValidity.setMinutes(tvc.getMinute().getValue(engine).implicitCast(JamochaType.STRING).getStringValue());
		temporalValidity.setSeconds(tvc.getSecond().getValue(engine).implicitCast(JamochaType.STRING).getStringValue());
		temporalValidity.setWeekdays(tvc.getWeekday().getValue(engine).implicitCast(JamochaType.STRING).getStringValue());
		temporalValidity.setMonths(tvc.getMonth().getValue(engine).implicitCast(JamochaType.STRING).getStringValue());
		temporalValidity.setYears(tvc.getYear().getValue(engine).implicitCast(JamochaType.STRING).getStringValue());
		temporalValidity.setDuration((int)tvc.getDuration().getValue(engine).implicitCast(JamochaType.LONG).getLongValue());
		return temporalValidity;
	}
	
}
