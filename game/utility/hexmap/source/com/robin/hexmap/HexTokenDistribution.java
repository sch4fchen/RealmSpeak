package com.robin.hexmap;

import java.awt.Rectangle;

public class HexTokenDistribution {

	private Rectangle hexRect;

	private int total = 0;
	private int current = 0;
	
	private Rectangle[] drawRect;
	
	public HexTokenDistribution(Rectangle r) {
		if (r==null) {
			throw new IllegalArgumentException("Rectangle cannot be null!");
		}
		hexRect = r;
		drawRect = null;
	}
	public void incrementTotal() {
		total++;
		updateRectangles();
	}
	public void incrementCurrent() {
		current++;
	}
	private void updateRectangles() {
		drawRect = new Rectangle[total];
		// for now, simply create a stack
		for (int i=0;i<total;i++) {
			int d = i*4;
			drawRect[i] = new Rectangle(hexRect.x-d,hexRect.y-d,hexRect.width,hexRect.height);
		}
	}
	public Rectangle getNextDrawRect() {
		if (current<drawRect.length) {
			return drawRect[current];
		}
		return null;
	}
}