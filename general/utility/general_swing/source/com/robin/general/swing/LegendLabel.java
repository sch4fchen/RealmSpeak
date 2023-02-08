package com.robin.general.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * A class that will show a color box and a label - useful for color legends
 */
public class LegendLabel extends JLabel {
	public LegendLabel(Color color,String text) {
		super(text);
		setIcon(new LegendIcon(color));
	}
	
	private class LegendIcon implements Icon {
		private Color color;
		public LegendIcon(Color color) {
			this.color = color;
		}
		public int getIconHeight() {
			return 15;
		}
		public int getIconWidth() {
			return 15;
		}
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(color);
			g.fillRect(x,y,getIconWidth(),getIconHeight());
			g.setColor(Color.black);
			g.drawRect(x,y,getIconWidth()-1,getIconHeight()-1);
		}
	}
}