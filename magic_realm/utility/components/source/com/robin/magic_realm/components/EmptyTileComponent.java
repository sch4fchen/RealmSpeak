package com.robin.magic_realm.components;

import java.awt.*;

import javax.swing.UIManager;

public class EmptyTileComponent extends TileComponent {
	private static final Stroke POSITION_HIGHLIGHT_STROKE
		= new BasicStroke(10,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	
	private static final Color INVISIBLE_COLOR = UIManager.getColor("ScrollPane.background");
	
	private boolean valid = false;
	public EmptyTileComponent() {
		super();
		lightColor = Color.darkGray;
		darkColor = Color.darkGray;
	}
	public String toString() {
		return "EmptyTileComponent:"+valid;
	}
	public void setValidPosition(boolean val) {
		valid = val;
	}
	public boolean isValidPosition() {
		return valid;
	}
	public void paintTo(Graphics g,int x,int y,int w,int h) {
		paint(g.create(x,y,w,h));
	}
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		if (valid) {
			Shape shape = getShape(10,10,getChitSize()-20);
			g.setStroke(POSITION_HIGHLIGHT_STROKE);
			g.setColor(Color.yellow);
			g.draw(shape);
		}
		else {
			// for debugging
			Shape shape = getShape(10,10,getChitSize()-20);
			g.setStroke(POSITION_HIGHLIGHT_STROKE);
			g.setColor(INVISIBLE_COLOR);
			g.draw(shape);
		}
	}
	public boolean isLightSideUp() {
		return true;
	}
	public boolean isDarkSideUp() {
		return false;
	}
	public ClearingDetail findClearing(Point relativePoint) {
		return null;
	}
}