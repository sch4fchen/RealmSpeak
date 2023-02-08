package com.robin.general.io;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggingFormatter extends Formatter {

	Date dat = new Date();

	// Column separator string. Could be read from a property.
	private final static String columnSeparator = "\t";
	
	/**
	 * Format the given LogRecord.
	 * @param record the log record to be formatted.
	 * @return a formatted log record
	 */
	public synchronized String format(LogRecord record) {
		StringBuffer sb = new StringBuffer();
		sb.append(record.getLevel().getLocalizedName());
		sb.append(columnSeparator);
		if (record.getSourceClassName() != null) {	
			sb.append(truncateClassName(record.getSourceClassName()));
		} else {
			sb.append(record.getLoggerName());
		}
		if (record.getSourceMethodName() != null) {
			sb.append(".");
			sb.append(record.getSourceMethodName());
			sb.append("(*)");
		}
		sb.append(columnSeparator);
		String message = formatMessage(record);
		sb.append(message);
		if (record.getThrown() != null) {
			sb.append(columnSeparator);
			sb.append(record.getThrown().getClass().getName());
			sb.append(columnSeparator);
			sb.append(record.getThrown().getMessage());
			StackTraceElement[] traceFrames = record.getThrown().getStackTrace();
			for (int i=0; i<traceFrames.length; i++){
				StackTraceElement element=traceFrames[i];
				sb.append(columnSeparator);
				sb.append(element.toString());
			}
		}
		sb.append(System.lineSeparator());
		return sb.toString();
	}
	private String truncateClassName(String className) {
		int lastDot = className.lastIndexOf(".");
		if (lastDot>=0 && (lastDot+1)<className.length()) {
			className = className.substring(lastDot+1);
		}
		return className;
	}
}