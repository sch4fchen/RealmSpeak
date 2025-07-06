package com.robin.magic_realm.components.attribute;

public class Harm {
	private Strength strength;
	private int sharpness;
	private boolean ignoresArmor;
	private boolean wound = false;
	private boolean adjustable = true; // default
	public Harm(Strength strength,int sharpness) {
		this(strength,sharpness,false);
	}
	public Harm(Strength strength,int sharpness,boolean ignoresArmor) {
		this.strength = strength;
		this.sharpness = sharpness;
		this.ignoresArmor = ignoresArmor;
	}
	public Harm(Harm harm) {
		this.strength = new Strength(harm.getStrength());
		this.sharpness = harm.sharpness;
		this.ignoresArmor = harm.ignoresArmor;
	}
	public boolean isNegligible() {
		return strength.isNegligible() && sharpness==0;
	}
	public String toString() {
		return strength.toString()+(sharpness>0?("+"+sharpness):"")+(ignoresArmor?" (ignores armor)":"");
	}
	public String toKey() {
		StringBuffer sb = new StringBuffer();
		sb.append(strength.toString());
		sb.append("+");
		sb.append(sharpness);
		return sb.toString();
	}
	public static Harm getHarmFromKey(String val) {
		int plus = val.indexOf('+');
		String st = val.substring(0,plus);
		String sh = val.substring(plus+1);
		return new Harm(new Strength(st),Integer.parseInt(sh));
	}
	public int getSharpness() {
		return sharpness;
	}
	public Strength getStrength() {
		return strength;
	}
	public void setIgnoresArmor(boolean val) {
		ignoresArmor = val;
	}
	public boolean getIgnoresArmor() {
		return ignoresArmor;
	}
	public void dampenSharpness() {
		if (sharpness>0) {
			sharpness--;
		}
	}
	public void setWound(boolean val) {
		wound = val;
		if (wound) {
			// Wounding means no other harm
			strength = new Strength();
			sharpness = 0;
			ignoresArmor = true;
		}
	}
	public boolean isWound() {
		return wound;
	}
	/**
	 * Use this method to increase/decrease the overall harm
	 */
	public void changeLevels(int change) {
		if (!adjustable) {
			throw new IllegalStateException("Cannot adjust harm!");
		}
		int sharpnessChange = strength.getLevels()+change;
		strength.modify(change);
		if (sharpnessChange<0) { // only decrease sharpness - never increase
			sharpness += sharpnessChange;
			if (sharpness<0) {
				sharpness = 0;
			}
		}
	}
	public void dropOneLevel() {
		if (strength.getLevels()>0) {
			strength.modify(-1);
		}
		else {
			dampenSharpness();
		}
	}
	/**
	 * @return		The total strength, accounting for sharpness
	 */
	public Strength getAppliedStrength() {
		return strength.addStrength(sharpness);
	}
	public boolean isAdjustable() {
		return adjustable;
	}
	public void setAdjustable(boolean adjustable) {
		this.adjustable = adjustable;
	}
}