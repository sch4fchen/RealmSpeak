package com.robin.magic_realm.MRCBuilder;

public class OutlineEntry {

	public OutlineEntry() {
		this(null, null);
	}

	public OutlineEntry(String s, String s1) {
		header = stripCR(s);
		content = stripCR(s1);
	}

	public String getHeader() {
		return header;
	}

	public String getContent() {
		return content;
	}

	public String toString() {
		return header + ":  " + content;
	}

	public static String stripCR(String s) {
		if (s != null) {
			int i = 0;
			int k = s.indexOf("\r", i);
			for (int l = s.indexOf("\n", i); k >= 0 || l >= 0;) {
				int j;
				if (l == -1 || k != -1 && k < l)
					j = k;
				else
					j = l;
				k = s.indexOf("\r", j);
				l = s.indexOf("\n", j);
				s = s.substring(0, j) + " " + s.substring(j + 1);
			}

			return s;
		}
		else {
			return "";
		}
	}

	String header;
	String content;
}