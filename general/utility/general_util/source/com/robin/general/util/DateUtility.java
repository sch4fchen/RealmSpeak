package com.robin.general.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtility {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
	
	/**
	 * Returns the time as is currently set on your computer.
	 */
	public static Date getNow() {
		GregorianCalendar cal = (GregorianCalendar)GregorianCalendar.getInstance();
		Date date = cal.getTime();
		return date;
	}
	/**
	 * Turn a date into a string that can be saved and later parsed by convertString2Date().
	 */
	public static String convertDate2String(Date date) {
		return dateFormat.format(date);
	}
	/**
	 * Read a string created by the convertDate2String() method, and return a date.
	 */
	public static Date convertString2Date(String val) {
		try {
			return dateFormat.parse(val);
		}
		catch(ParseException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}