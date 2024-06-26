package com.robin.magic_realm.components;

import java.awt.Color;

public class MagicRealmColor {
	public static final Color BLACK			= new Color(  0,  0,  0);
	public static final Color BLUE			= new Color(119,199,234);
	public static final Color LIGHTBLUE		= new Color(222,239,247);
	public static final Color GREEN			= new Color( 99,180, 98);
	public static final Color LIGHTGREEN	= new Color(186,231,164);
	public static final Color LIMEGREEN		= new Color(159,214, 98);
	public static final Color FORESTGREEN	= new Color( 90,180, 75);
	public static final Color RED			= new Color(214, 93, 70);
	public static final Color PINK			= new Color(239,190,221);
	public static final Color ORANGE		= new Color(214,148, 72);
	public static final Color LIGHTORANGE	= new Color(239,193,142);
	public static final Color PURPLE		= new Color(180,145,180);
	public static final Color YELLOW		= new Color(239,235,123);
	public static final Color PALEYELLOW	= new Color(255,255,200);
	public static final Color PEACH			= new Color(229,186,136);
	public static final Color TAN			= new Color(198,167, 85);
	public static final Color GOLD			= new Color(239,204,110);
	public static final Color BROWN			= new Color(194,128, 80);
	public static final Color GRAY			= new Color(214,214,214);
	public static final Color DARKGRAY		= new Color(170,170,170);
	public static final Color WHITE			= new Color(255,255,255);
	
	public static final Color DARKBLUE		= new Color( 24, 66,132);
	public static final Color DARKGREEN		= new Color(114,112, 22);
	public static final Color DARKRED		= new Color(125,  6, 39);
	public static final Color DARKORANGE	= new Color(199,108,  0);
	public static final Color DARKPURPLE	= new Color( 93,  0,126);
	
	public static final Color DISCOVERY_HIGHLIGHT_COLOR = new Color(102,255,153);
	
	public static final Color CHIT_COMMITTED = Color.blue;
	public static final Color CHIT_ALERTED = Color.green;
	public static final Color CHIT_FATIGUED = Color.orange;
	public static final Color CHIT_WOUNDED = Color.red;
	public static final Color CHIT_BERSERK = Color.darkGray;
//	public static final Color CHIT_COMMITTED = new Color(0,0,255,100); //Color.blue;
//	public static final Color CHIT_ALERTED = new Color(0,255,0,100); //Color.green;
//	public static final Color CHIT_FATIGUED = new Color(255,255,0,100); //Color.orange;
//	public static final Color CHIT_WOUNDED = new Color(255,0,0,100); //Color.red;
	
	public static Color getColor(String string) {
		string = string.trim().toUpperCase();
		if (string.equals("BLACK")) {
			return BLACK;
		}
		else if (string.equals("BLUE")) {
			return BLUE;
		}
		else if (string.equals("LIGHTBLUE")) {
			return LIGHTBLUE;
		}
		else if (string.equals("GREEN")) {
			return GREEN;
		}
		else if (string.equals("LIGHTGREEN")) {
			return LIGHTGREEN;
		}
		else if (string.equals("LIMEGREEN")) {
			return LIMEGREEN;
		}
		else if (string.equals("FORESTGREEN")) {
			return FORESTGREEN;
		}
		else if (string.equals("RED")) {
			return RED;
		}
		else if (string.equals("PINK")) {
			return PINK;
		}
		else if (string.equals("ORANGE")) {
			return ORANGE;
		}
		else if (string.equals("LIGHTORANGE")) {
			return LIGHTORANGE;
		}
		else if (string.equals("PURPLE")) {
			return PURPLE;
		}
		else if (string.equals("YELLOW")) {
			return YELLOW;
		}
		else if (string.equals("PEACH")) {
			return PEACH;
		}
		else if (string.equals("TAN")) {
			return TAN;
		}
		else if (string.equals("GOLD")) {
			return GOLD;
		}
		else if (string.equals("BROWN")) {
			return BROWN;
		}
		else if (string.equals("GRAY")) {
			return GRAY;
		}
		else if (string.equals("DARKGRAY")) {
			return DARKGRAY;
		}
		else if (string.equals("WHITE")) {
			return WHITE;
		}
		else if (string.equals("PALEYELLOW")) {
			return PALEYELLOW;
		}
		else if (string.equals("DARKBLUE")) {
			return DARKBLUE;
		}
		else if (string.equals("DARKGREEN")) {
			return DARKGREEN;
		}
		else if (string.equals("DARKRED")) {
			return DARKRED;
		}
		else if (string.equals("DARKORANGE")) {
			return DARKORANGE;
		}
		else if (string.equals("DARKPURPLE")) {
			return DARKPURPLE;
		}
		throw new IllegalArgumentException("Invalid color: "+string);
	}
	
	public static Color getClanColor(String clan) {
		switch(clan) {
		case "1":
			return BLUE;
		case "2":
			return GREEN; 
		case "3":
			return RED;
		case "4":
			return PURPLE;
		case "5":
			return ORANGE;
		case "0":
		default:
			return YELLOW;
		}
	}
}