package com.robin.hexmap;

import java.awt.Color;

public class HexGuide {
	private String name;
	private Token fromToken;
	private Token toToken;
	private Color color;
	private boolean showName;
	private boolean showDistance;
	
	public HexGuide(String name,Token from,Token to,Color c,boolean showName,boolean showDistance) {
		this.name = name;
		this.fromToken = from;
		this.toToken = to;
		this.color = c;
		this.showName = showName;
		this.showDistance = showDistance;
	}
	public HexMapPoint getFrom() {
		return fromToken.getPosition();
	}
	public HexMapPoint getTo() {
		return toToken.getPosition();
	}
	public String getName() {
		return name;
	}
	public Color getColor() {
		return color;
	}
	public boolean getShowName() {
		return showName;
	}
	public boolean getShowDistance() {
		return showDistance;
	}
}