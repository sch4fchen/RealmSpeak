package com.robin.magic_realm.components.swing;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JComponent;

import com.robin.general.swing.ComponentTools;
import com.robin.magic_realm.components.ChitComponent;
import com.robin.magic_realm.components.attribute.ColorMagic;

public class ChitBin extends JComponent {
	private static final int FIXED_SIZE = ChitComponent.S_CHIT_SIZE + (ChitBinLayout.INNER_CELL_SPACE << 1);
	
	private ChitComponent chit;
	private Rectangle rectangle;
	private Color color = null;
	
	public ChitBin() {
		ComponentTools.lockComponentSize(this,FIXED_SIZE,FIXED_SIZE);
	}
	public ChitBin(ChitComponent chit) {
		this();
		this.chit = chit;
	}
	public ChitComponent getChit() {
		return chit;
	}
	public void setChit(ChitComponent chit) {
		this.chit = chit;
	}
	public Rectangle getRectangle() {
		return rectangle;
	}
	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}
	public void setColorMagic(ColorMagic cm) {
		if (cm!=null) {
			color = cm.getColor();
		}
	}
	public Color getColor() {
		return color;
	}
}