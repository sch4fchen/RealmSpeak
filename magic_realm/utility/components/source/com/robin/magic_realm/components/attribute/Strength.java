package com.robin.magic_realm.components.attribute;

public class Strength {
	
	private static final int NEGLIGIBLE = 0;
	private static final int LIGHT = 1;
	private static final int MEDIUM = 2;
	private static final int HEAVY = 3;
	private static final int TREMENDOUS = 4;
	private static final int MAXIMUM = 5;
	
	private static final int RED = 100; // RED always kills
	
	private int strengthId = NEGLIGIBLE;
	
	public Strength() {
		this((String)null); // negligible!
	}
	public Strength(Strength in) {
		strengthId = in.strengthId;
	}
	public Strength(String val) {
		strengthId = readString(val);
	}
	private Strength(int strength) {
		strengthId = strength;
		if (strengthId>MAXIMUM) {
			strengthId = MAXIMUM;
		}
		if (strengthId<NEGLIGIBLE) {
			strengthId = NEGLIGIBLE;
		}
	}
	public Strength(String val,int mod) {
		strengthId = readString(val);
		modify(mod);
	}
	public int getLevels() {
		return strengthId;
	}
	public void bumpUp() {
		modify(1);
	}
	public void moveRedToMaximum() {
		if (strengthId==RED) {
			strengthId = MAXIMUM;
		}
	}
	public void modify(int val) {
		if (strengthId!=RED) { // RED is never affected
			strengthId += val;
			if (strengthId>MAXIMUM) strengthId = MAXIMUM;
			if (strengthId<NEGLIGIBLE) strengthId = NEGLIGIBLE;
		}
	}
	/**
	 * Returns new Strength with "add" added.  Won't exceed the range NEGLIGIBLE to MAXIMUM
	 */
	public Strength addStrength(int add) {
		return new Strength(strengthId+add);
	}
	public String getChitString() {
		switch(strengthId) {
			case LIGHT:				return "L";
			case MEDIUM:			return "M";
			case HEAVY:				return "H";
			case TREMENDOUS:		return "T";
			case MAXIMUM:			return "X";
			case RED:				return "!";
		}
		return "";  // NEGLIGIBLE
	}
	public String getChar() {
		switch(strengthId) {
			case LIGHT:				return "L";
			case MEDIUM:			return "M";
			case HEAVY:				return "H";
			case TREMENDOUS:		return "T";
			case MAXIMUM:			return "X";
			case RED:				return "!";
		}
		return "-";  // NEGLIGIBLE
	}
	public String toString() {
		switch(strengthId) {
			case LIGHT:				return "L";
			case MEDIUM:			return "M";
			case HEAVY:				return "H";
			case TREMENDOUS:		return "T";
			case MAXIMUM:			return "X";
			case RED:				return "RED";
		}
		return "neg";  // NEGLIGIBLE
	}
	public String fullString() {
		switch(strengthId) {
			case LIGHT:				return "Light";
			case MEDIUM:			return "Medium";
			case HEAVY:				return "Heavy";
			case TREMENDOUS:		return "Tremendous";
			case MAXIMUM:			return "Maximum";
			case RED:				return "RED";
		}
		return "Negligible";  // NEGLIGIBLE
	}
	public boolean isNegligible() {
		return strengthId == NEGLIGIBLE;
	}
	public boolean notNegligible() {
		return strengthId > NEGLIGIBLE;
	}
	public boolean equals(Object o1) {
		if (o1 instanceof Strength) {
			return equalTo((Strength)o1);
		}
		return false;
	}
	public boolean equalTo(Strength other) {
		return strengthId == other.strengthId;
	}
	public boolean strongerOrEqualTo(Strength other) {
		return strengthId >= other.strengthId;
	}
	public boolean strongerThan(Strength other) {
		return strengthId > other.strengthId;
	}
	public boolean weakerOrEqualTo(Strength other) {
		return strengthId <= other.strengthId;
	}
	public boolean weakerTo(Strength other) {
		return strengthId < other.strengthId;
	}
	public boolean isRed() {
		return strengthId==RED;
	}
	public boolean isTremendous() {
		return strengthId==TREMENDOUS;
	}
	public boolean isMaximum() {
		return strengthId==MAXIMUM;
	}
	private static int readString(String val) {
		int num = 0;
		if (val!=null) {
			val = val.toUpperCase();
			if ("L".equals(val)) {
				num = LIGHT;
			}
			else if ("M".equals(val)) {
				num = MEDIUM;
			}
			else if ("H".equals(val)) {
				num = HEAVY;
			}
			else if ("T".equals(val)) {
				num = TREMENDOUS;
			}
			else if ("X".equals(val)) {
				num = MAXIMUM;
			}
			else if ("RED".equals(val)) {
				num = RED;
			}
		}
		return num;
	}
	
	public static Strength valueOf(String val) {
		return new Strength(readString(val));
	}
}