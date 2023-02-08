package com.robin.hexmap;

public class Placement {
	private String name;
	private int offsetX;
	private int offsetY;
	private boolean borderHex;
	public Placement(String name,int offsetX,int offsetY,boolean borderHex) {
		this.name = name;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.borderHex = borderHex;
	}
	public String getName() {
		return name;
	}
	public int getOffsetX() {
		return offsetX;
	}
	public int getOffsetY() {
		return offsetY;
	}
	public boolean isBorderHex() {
		return borderHex;
	}
	public String toString() {
		return "name: "+name+"  ("+offsetX+","+offsetY+")";
	}
}