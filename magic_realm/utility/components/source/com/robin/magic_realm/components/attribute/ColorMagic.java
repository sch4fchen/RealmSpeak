package com.robin.magic_realm.components.attribute;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

import com.robin.general.swing.ImageCache;

public class ColorMagic implements Comparable {
	public static final int WHITE = 1;
	public static final int GRAY = 2;
	public static final int GOLD = 3;
	public static final int PURPLE = 4;
	public static final int BLACK = 5;
	
	public static final String White = "White";
	public static final String Grey = "Grey";
	public static final String Gold = "Gold";
	public static final String Purple = "Purple";
	public static final String Black = "Black";
	
	private int color;
	private boolean infinite;
	public ColorMagic(int color,boolean infinite) {
		this.color = color;
 		this.infinite = infinite;
 		if (color<WHITE || color>BLACK) {
 			throw new IllegalArgumentException("Invalid color");
 		}
	}
	public boolean equals(Object o1) {
		if (o1 instanceof ColorMagic) {
			ColorMagic cm = (ColorMagic)o1;
			return (cm.color==color && cm.infinite==infinite);
		}
		return false;
	}
	public boolean sameColorAs(ColorMagic other) {
		return other.color==color;
	}
	public int compareTo(Object o1) {
		int ret=0;
		if (o1 instanceof ColorMagic) {
			ColorMagic cm = (ColorMagic)o1;
			ret = color - cm.color;
		}
		return ret;
	}
	public int getColorNumber() {
		return color;
	}
//	public boolean compatibleWith(MagicChit chit) {
//		return (chit.getMagicNumber()==color);
//	}
	public String getColorName() {
		switch(color) {
			case WHITE:			return White;
			case GRAY:			return Grey;
			case GOLD:			return Gold;
			case PURPLE:		return Purple;
			case BLACK:			return Black;
		}
		return null; // this shouldn't happen
	}
	public String toString() {
		return "ColorMagic:"+getColorName()+(infinite?"":"(chit)");
	}
	public void setInfinite(boolean val) {
		infinite = val;
	}
	/**
	 * @return		true if the color is infinite (like a treasure or tile)
	 */
	public boolean isInfinite() {
		return infinite;
	}
	public ImageIcon getSmallIcon() {
		Image i = getIcon().getImage();
		return new ImageIcon(i.getScaledInstance(24,24,Image.SCALE_SMOOTH));
	}
	public ImageIcon getIcon() {
		switch(color) {
			case WHITE:			return infinite?ImageCache.getIcon("colormagic/white"):ImageCache.getIcon("colormagic/whitechit");
			case GRAY:			return infinite?ImageCache.getIcon("colormagic/gray"):ImageCache.getIcon("colormagic/graychit");
			case GOLD:			return infinite?ImageCache.getIcon("colormagic/gold"):ImageCache.getIcon("colormagic/goldchit");
			case PURPLE:		return infinite?ImageCache.getIcon("colormagic/purple"):ImageCache.getIcon("colormagic/purplechit");
			case BLACK:			return infinite?ImageCache.getIcon("colormagic/black"):ImageCache.getIcon("colormagic/blackchit");
		}
		return null; // this shouldn't happen
	}
	public Color getColor() {
		switch(color) {
			case WHITE:			return Color.white;
			case GRAY:			return Color.gray;
			case GOLD:			return Color.yellow;
			case PURPLE:		return Color.magenta;
			case BLACK:			return Color.black;
		}
		return null;
	}
	public static ColorMagic makeColorMagic(String colorName,boolean infinite) {
		if (colorName!=null) {
			int color = 0;
			colorName = colorName.toLowerCase();
			if ("white".equals(colorName)) {
				color = WHITE;
			}
			else if ("gray".equals(colorName) || "grey".equals(colorName)) { // Support both spellings
				color = GRAY;
			}
			else if ("gold".equals(colorName)) {
				color = GOLD;
			}
			else if ("purple".equals(colorName)) {
				color = PURPLE;
			}
			else if ("black".equals(colorName)) {
				color = BLACK;
			}
			if (color>0) {
				return new ColorMagic(color,infinite);
			}
		}
		return null;
	}
	public static String getColorName(String colorName) {
		ColorMagic cm = ColorMagic.makeColorMagic(colorName,true);//.getColorName();
		if (cm!=null) {
			return cm.getColorName();
		}
		return colorName; // no conversion
	}
	
	public boolean isPrismColor() {
		return color == GRAY || color == GOLD || color == PURPLE;
	}
	
	public static String getColorNumber(int number) {
		switch(number) {
			case 1: return "I";
			case 2: return "II";
			case 3: return "III";
			case 4: return "IV";
			case 5: return "V";
			case 6: return "VI";
			case 7: return "VII";
			case 8: return "VIII";
			default:
				return null;
		}
	}
}