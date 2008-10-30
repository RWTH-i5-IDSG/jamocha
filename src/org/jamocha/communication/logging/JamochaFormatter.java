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
		String logger = record.getLoggerName();
		logger = logger.substring(logger.lastIndexOf(".")+1);
		sb.append(" ").append(logger).append(": ");
		sb.append(record.getMessage()).append("\n");
		return sb.toString();
	}

}
