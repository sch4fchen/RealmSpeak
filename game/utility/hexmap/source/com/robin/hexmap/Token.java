package com.robin.hexmap;

import java.awt.Graphics;

public interface Token {
	public int sortOrder();
	public HexMapPoint getPosition();
	public void drawToken(Graphics g,int x,int y,int w,int h);
	public boolean blocksMove();
}