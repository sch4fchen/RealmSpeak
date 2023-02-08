package com.robin.general.io;

import java.util.*;

public class RecordParser {

	private String[] field;
	
	public RecordParser(String line,String delim) {
		line = insertSpaces(line,delim);
		StringTokenizer tokens = new StringTokenizer(line.toString(),delim);
		field = new String[tokens.countTokens()];
		for (int i=0;i<field.length;i++) {
			field[i] = tokens.nextToken();
		}
	}
	
	private static String insertSpaces(String val,String delim) {
		if (val.startsWith(delim)) {
			val = " "+val;
		}
		if (val.endsWith(delim)) {
			val = val+" ";
		}
		int index;
		while((index = val.indexOf(delim+delim))>=0) {
			val = val.substring(0,index+1)+" "+val.substring(index+1);
		}
		return val;
	}
	
	public int totalFields() {
		return field.length;
	}
	
	public String getField(int index) {
		if (index>0 && index<field.length) {
			return stripEndQuotes(field[index]);
		}
		return "";
	}
	public static String stripEndQuotes(String val) {
		if (val!=null && val.length()>1) {
			if (val.startsWith("\"") && val.endsWith("\"")) {
				val = val.substring(1,val.length()-1);
			}
		}
		return val;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<field.length;i++) {
			if (i>0) {
				sb.append(",");
			}
			sb.append(field[i]);
		}
		return sb.toString();
	}
}