package com.robin.magic_realm.components.attribute;

public class Speed implements Comparable {
	
	private static int DEFAULT_SPEED = 8; // Infinitely slow, for all intents and purposes
	
	private boolean infinitelySlow = false;
	private int num = DEFAULT_SPEED; // default
	
	public Speed() {
		num = DEFAULT_SPEED;
		infinitelySlow = true;
	}
	public Speed(String val) {
		this();
		if (val!=null && val!="X") {
			num = Integer.valueOf(val).intValue(); // NumberFormatException here is desired if val is not a number!
			infinitelySlow = false;
		}
	}
	public Speed(int val) {
		this();
		num = val;
		infinitelySlow = false;
	}
	public Speed(Integer val) {
		this();
		if (val!=null) {
			num = val.intValue();
			infinitelySlow = false;
		}
	}
	public Speed(Integer val,int modifier) {
		this(val);
		if (!infinitelySlow) {
			num += modifier;
		}
	}
	public Speed(String val,int modifier) {
		this(val);
		if (!infinitelySlow) {
			num += modifier;
		}
	}
	public boolean isInfinitelySlow() {
		return infinitelySlow;
	}
	public String toString() {
		if (infinitelySlow) {
			return "Not Moving";
		}
		return "Speed "+num;
	}
	public String getSpeedString() {
		if (!infinitelySlow && num>0) {
			return String.valueOf(num);
		}
		return "";
	}
	public int compareTo(Object o1) {
		int ret = 0;
		if (o1 instanceof Speed) {
			Speed s = (Speed)o1;
			ret = num - s.num;
		}
		return ret;
	}
	public int getNum() {
		return num;
	}
	public boolean equals(Object o1) {
		if (o1 instanceof Speed) {
			return equalTo((Speed)o1);
		}
		return false;
	}
	public boolean equalTo(Speed other) {
		return num == other.getNum();
	}
	public boolean fasterThan(Speed speed) {
		if (infinitelySlow) {
			return false;
		}
		if (speed.isInfinitelySlow()) {
			return true;
		}
		return num<speed.getNum();
	}
	public boolean fasterThanOrEqual(Speed speed) {
		if (infinitelySlow) {
			return false;
		}
		if (speed.isInfinitelySlow()) {
			return true;
		}
		return num<=speed.getNum();
	}
}