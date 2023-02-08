package com.robin.general.util;

import java.util.ArrayList;
import java.util.Collection;

public class StringBufferedList {
	private String comma;
	private String and;
	private ArrayList<String> list;
	public StringBufferedList() {
		this(", ","and ");
	}
	public StringBufferedList(String comma,String and) {
		this.comma = comma;
		this.and = and;
		list = new ArrayList<>();
	}
	public int size() {
		return list.size();
	}
	public void append(String val) {
		list.add(val);
	}
	public void appendAll(Collection<String> list) {
		for (String val:list) {
			append(val);
		}
	}
	public void countIdenticalItems() {
		HashLists hash = new HashLists();
		ArrayList<String> keys = new ArrayList<>();
		int n=0;
		for (String string:list) {
			hash.put(string,"n"+(n++));
			if (!keys.contains(string)) {
				keys.add(string);
			}
		}
		list.clear();
		for (String string:keys) {
			int count = hash.getList(string).size();
			if (count==1) {
				list.add(string);
			}
			else {
				list.add(count+" "+string+(count==1?"":"s"));
			}
		}
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<list.size();i++) {
			String val = list.get(i);
			if (sb.length()>0) {
				sb.append(comma);
				if (i==(list.size()-1)) {
					sb.append(and);
				}
			}
			sb.append(val);
		}
		return sb.toString();
	}
}