package com.robin.general.swing;

import java.awt.*;
import javax.swing.*;

public class IconCounter extends JComponent {

	private Icon icon;
	private int value;
	private int iconSpacer;
	
	public IconCounter(Icon icon) {
		this.icon = icon;
		this.value = 0;
		this.iconSpacer = 1;
		updateSize();
	}
	private void updateSize() {
		Dimension s = new Dimension((icon.getIconWidth()+iconSpacer)*value,icon.getIconHeight());
		setMinimumSize(s);
		setMaximumSize(s);
		setPreferredSize(s);
		revalidate();
		repaint();
	}
	public void paintComponent(Graphics g) {
		Dimension d = getSize();
		int dx = icon.getIconWidth()+iconSpacer;
		int y=0;
		for (int x=0;x<d.width;x+=dx) {
			icon.paintIcon(this,g,x,y);
		}
	}
	public void setValue(int val) {
		value = val;
		updateSize();
	}
	public void addValue(int val) {
		value += val;
		updateSize();
	}
	public int getValue() {
		return value;
	}
}