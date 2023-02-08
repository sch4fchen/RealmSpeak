package com.robin.general.swing;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import com.robin.general.graphics.*;

public class PieProgressBar extends JComponent {
	private static final Font font = new Font("Dialog",Font.BOLD,10);
	private static final Color GREEN = new Color(0,220,0);
	private static final Color YELLOW = new Color(220,220,0);
	private static final Color RED = new Color(255,0,0);

	private Dimension size;
	private int currentWedge;
	private int wedges;
	private boolean showDivisions;
	
	private Hashtable<Integer, Color> colorChange;

	public PieProgressBar(int radius,int wedges,boolean showDivisions) {
		this.wedges = wedges;
		this.showDivisions = showDivisions;
		currentWedge = 0;
		int diam = (radius<<1)+1;
		size = new Dimension(diam,diam);
		Dimension compSize = new Dimension(diam+2,diam+2);
		setMinimumSize(compSize);
		setMaximumSize(compSize);
		setPreferredSize(compSize);
		assignDefaultColors();
	}
	public void assignDefaultColors() {
		int green = wedges/3;
		int yellow = (wedges*2)/3;
		
		for (int i=0;i<green;i++) {
			setColor(i,GREEN);
		}
		for (int i=green;i<yellow;i++) {
			setColor(i,GraphicsUtil.convertColor(GREEN,YELLOW,((i-green)*100)/(yellow-green)));
		}
		for (int i=yellow;i<=wedges;i++) {
			setColor(i,GraphicsUtil.convertColor(YELLOW,RED,((i-yellow)*100)/(wedges-yellow)));
		}
	}
	public void advance() {
		if (currentWedge<wedges) {
			currentWedge++;
			repaint();
		}
	}
	public void setValue(int value) {
		if (value<=wedges) {
			currentWedge = value;
			repaint();
		}
		else {
			throw new IllegalArgumentException("value cannot be more than wedges!");
		}
	}
	public void reset() {
		currentWedge = 0;
		repaint();
	}
	public void setColor(int wedge,Color c) {
		if (colorChange==null) {
			colorChange = new Hashtable<>();
		}
		colorChange.put(Integer.valueOf(wedge),c);
	}
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		
		Ellipse2D.Double oval = new Ellipse2D.Double(0.0,0.0,size.width,size.height);
		
		g.setColor(Color.black);
		g.fill(oval);
		
		double wedgeSize = 360f/(double)wedges;
		Arc2D.Double arc;
		
		double end = 90f;
		double change = ((double)currentWedge*360f)/wedges;
		double start = end - change;
		if (start<0) {
			start+=360f;
		}
		
		arc = new Arc2D.Double(0f,0f,size.width,size.height,start,change,Arc2D.PIE);
		Color wedgeColor = Color.blue;
		if (colorChange!=null) {
			Color c = colorChange.get(Integer.valueOf(currentWedge));
			if (c!=null) {
				wedgeColor = c;
			}
		}
		g.setColor(wedgeColor);
		g.fill(arc);
		
		// draw divisions
		start = 90f;
		for (int i=0;i<wedges;i++) {
			start-=wedgeSize;
			if (start<0) {
				start+=360f;
			}
			if (i>=currentWedge) {
				arc = new Arc2D.Double(0.0,0.0,size.width,size.height,start,wedgeSize,Arc2D.PIE);
				g.setColor(Color.white);
				g.fill(arc);
			}
			if (showDivisions) {
				arc = new Arc2D.Double(0.0,0.0,size.width,size.height,start,wedgeSize,Arc2D.PIE);
				g.setColor(Color.black);
				g.draw(arc);
			}
		}
		
		g.setColor(Color.black);
		g.draw(oval);
		
		g.setFont(font);
		GraphicsUtil.drawCenteredString(g,0,0,size.width,size.height,String.valueOf(currentWedge));
	}

	/**
	 * For testing only
	 */
	public static void main(String[] args) {
		int night = 14;
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
			final PieProgressBar pie = new PieProgressBar(9,night,false);
			pie.assignDefaultColors();
			pie.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent ev) {
					if (ev.getClickCount()==2) {
						pie.reset();
					}
					else {
						pie.advance();
					}
				}
			});
		frame.getContentPane().add(pie,"North");
		frame.setSize(400,400);
		frame.setVisible(true);
	}
}