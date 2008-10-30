package org.jamocha.communication.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class JamochaFormatter extends SimpleFormatter {

	private DateFormat formatter;
	
	public JamochaFormatter() {
		formatter = new SimpleDateFormat("HH:mm:ss");
	}

	@Override
	public synchronized String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append(formatter.format(new Date()));
		int lvl = record.getLevel().intValue();
		sb.append(" ").append(lvl);
		String logger = record.getLoggerName();
		logger = logger.substring(logger.lastIndexOf(".")+1);
		sb.append(" ").append(logger).append(": ");
		String msg = (record.getMessage() !=null) ? record.getMessage() : "EXCEPTION: "+ record.getThrown().getClass().getSimpleName() +" "+record.getThrown().getMessage(); 
		sb.append(msg).append("\n");
		return sb.toString();
	}

}
