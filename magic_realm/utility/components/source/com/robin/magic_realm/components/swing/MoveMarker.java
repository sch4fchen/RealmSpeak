package com.robin.magic_realm.components.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.robin.general.graphics.GraphicsUtil;
import com.robin.general.swing.ComponentTools;
import com.robin.general.swing.ImageCache;
import com.robin.general.util.StringUtilities;

public class MoveMarker extends JComponent {
	private static final Font font = new Font("Dialog",Font.BOLD,12);
	
	private ImageIcon backing;
	private String tip;
	private int cost;
	
	public MoveMarker(String name,int val) {
		tip = StringUtilities.capitalize(name)+" movement cost = ";
		setCost(val);
		backing = ImageCache.getIcon("phases/"+name);
		ComponentTools.lockComponentSize(this,backing.getIconWidth(),backing.getIconHeight());
		setBorder(BorderFactory.createEtchedBorder());
	}
	public void setCost(int val) {
		cost = val;
		setToolTipText(tip+cost);
		repaint();
	}
	public void paintComponent(Graphics g) {
		g.drawImage(backing.getImage(),0,0,null);
		g.setFont(font);
		g.setColor(Color.white);
		GraphicsUtil.drawCenteredString(g,16,16,12,12,String.valueOf(cost));
	}
}