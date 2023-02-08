package com.robin.hexmap;

import java.awt.Color;

public class HexTag {
	private String tagString;
	private Color color;
	
	public HexTag(String s,Color c) {
		this.tagString = s;
		this.color = c;
	}
	public String getTagString() {
		return tagString;
	}
	public Color getColor() {
		return color;
	}
	
	public void merge(HexTag tag) {
		tagString = tagString+","+tag.getTagString();
	}
}