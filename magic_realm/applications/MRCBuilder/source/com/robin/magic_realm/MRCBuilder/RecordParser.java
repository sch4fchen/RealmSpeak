package com.robin.magic_realm.MRCBuilder;

import java.util.StringTokenizer;

public class RecordParser {

	public RecordParser(String s, String s1) {
		s = insertSpaces(s, s1);
		StringTokenizer stringtokenizer = new StringTokenizer(s.toString(), s1);
		field = new String[stringtokenizer.countTokens()];
		for (int i = 0; i < field.length; i++)
			field[i] = stringtokenizer.nextToken();

	}

	private static String insertSpaces(String s, String s1) {
		if (s.startsWith(s1))
			s = " " + s;
		if (s.endsWith(s1))
			s = s + " ";
		int i;
		while ((i = s.indexOf(s1 + s1)) >= 0)
			s = s.substring(0, i + 1) + " " + s.substring(i + 1);
		return s;
	}

	public int totalFields() {
		return field.length;
	}

	public String getField(int i) {
		if (i > 0 && i < field.length) {
			return stripEndQuotes(field[i]);
		}
		return "";
	}

	public static String stripEndQuotes(String s) {
		if (s != null && s.length() > 1 && s.startsWith("\"") && s.endsWith("\""))
			s = s.substring(1, s.length() - 1);
		return s;
	}

	public String toString() {
		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i < field.length; i++) {
			if (i > 0)
				stringbuffer.append(",");
			stringbuffer.append(field[i]);
		}

		return stringbuffer.toString();
	}

	private String field[];
}