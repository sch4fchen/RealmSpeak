package com.robin.magic_realm.MRCBuilder;

import java.awt.*;

public class Chit {

	public Chit() {
		this("");
	}

	public Chit(RecordParser recordparser) {
		this(recordparser.getField(1) + " " + recordparser.getField(2) + recordparser.getField(3) + recordparser.getField(4) + recordparser.getField(5));
	}

	public Chit(String s) {
		setTextLines(s);
	}

	public String getTextLines() {
		return textLine1 + " " + textLine2;
	}

	public String getTextLine(int i) {
		switch (i) {
			case 0: // '\0'
				return textLine1;

			case 1: // '\001'
				return textLine2;
		}
		return null;
	}

	public void setTextLines(String s) {
		String as[] = parseText(s);
		if (as != null) {
			textLine1 = as[0];
			textLine2 = as[1];
		}
		else {
			textLine1 = "";
			textLine2 = "";
		}
	}

	public static String[] parseText(String s) {
		s = s.trim().toUpperCase();
		int i = s.indexOf(' ');
		if (i >= 0) {
			String as[] = new String[2];
			as[0] = s.substring(0, i);
			as[1] = s.substring(i + 1);
			return as;
		}
		return null;
	}

	public static void draw(Graphics g, Chit chit) {
		draw(g, 0, 0, chit.textLine1, chit.textLine2);
	}

	public static void draw(Graphics g, int i, int j, String s, String s1) {
		g.setClip(i, j, 45, 45);
		g.setFont(font);
		g.setColor(darkGreen);
		g.drawString(s, i + (45 - g.getFontMetrics().stringWidth(s) >> 1), ((j + 22 + (g.getFontMetrics().getAscent() >> 1)) - g.getFontMetrics().getAscent()) + 4);
		g.drawString(s1, i + (45 - g.getFontMetrics().stringWidth(s1) >> 1), j + 22 + (g.getFontMetrics().getAscent() >> 1) + 4);
		g.setClip(null);
	}

	public static final int SIZE = 45;
	public static final Font font = new Font("Dialog", 1, 12);
	public static final Color darkGreen = new Color(0, 130, 0);
	String textLine1;
	String textLine2;

}