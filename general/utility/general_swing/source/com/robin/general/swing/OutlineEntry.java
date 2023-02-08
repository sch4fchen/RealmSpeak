package com.robin.general.swing;

public class OutlineEntry {
	String header;
	String content;
	public OutlineEntry() {
		this(null,null);
	}
	public OutlineEntry(String h,String c) {
		header = stripCR(h);
		content = stripCR(c);
	}
	public String getHeader() {
		return header;
	}
	public String getContent() {
		return content;
	}
	public String toString() {
		return header+":  "+content;
	}
    public static String stripCR(String in) {
    	if (in!=null) {
		    int ix=0;
		    int i2=in.indexOf("\r",ix);
		    int i3=in.indexOf("\n",ix);
		    while ((i2>=0) || (i3>=0)) {
				if (i3==-1 || (i2!=-1 && i2<i3)) {
					ix=i2;
				}
				else {
					ix=i3;
				}
				i2=in.indexOf("\r",ix);
				i3=in.indexOf("\n",ix);
				in=in.substring(0,ix)+" "+in.substring(ix+1);
		    }
		    return in;
		}
		return "";
    }
}