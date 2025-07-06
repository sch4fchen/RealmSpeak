package com.robin.magic_realm.components.wrapper;

import com.robin.general.util.StringUtilities;

public class DayKey implements Comparable<DayKey> {
	private int month;
	private int day;
	public DayKey(int month,int day) {
		this.month = month;
		this.day = day;
	}
	public DayKey(String dayKeyString) {
		this.month = getMonth(dayKeyString);
		this.day = getDay(dayKeyString);
	}
	public boolean after(DayKey dayKey) {
		return compareTo(dayKey)>0;
	}
	public boolean before(DayKey dayKey) {
		return compareTo(dayKey)<0;
	}
	public boolean equals(DayKey dayKey) {
		return compareTo(dayKey)==0;
	}
	public boolean equals(Object obj) {
		if (obj instanceof DayKey) {
			return equals((DayKey)obj);
		}
		return false;
	}
	public int compareTo(DayKey dayKey) {
		int val = day - dayKey.day;
		val += (month - dayKey.month) * 28;
		return val;
	}
	public String getReadable() {
		return month+"-"+StringUtilities.zeroPaddedInt(day,2);
	}
	public String toString() {
		return getString(month,day);
	}
	public static String getString(int month,int day) {
		return "month_"+month+"_day_"+day;
	}
	public static int getMonth(String dayKey) {
		int dayIndex = dayKey.indexOf("_day_");
		return Integer.parseInt(dayKey.substring(6,dayIndex));
	}
	public static int getDay(String dayKey) {
		int dayIndex = dayKey.indexOf("_day_");
		return Integer.parseInt(dayKey.substring(dayIndex+5));
	}
	public DayKey addDays(int numberOfDays) {
		int month = this.month + ((this.day+numberOfDays)/28);
		int day = (this.day+numberOfDays)%28;
		return new DayKey(month, day);
	}
}