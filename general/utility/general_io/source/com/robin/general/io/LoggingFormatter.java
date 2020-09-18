/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
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