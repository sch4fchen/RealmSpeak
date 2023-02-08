package com.robin.general.swing;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class PointBar extends JComponent {
	
	private int value;
	private int goal;
	private int max;
	private int div;
	
	public PointBar(int div,int max) {
		this.value = 0;
		this.max = max;
		this.div = div;
	}
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		Dimension s = getSize();
		int divn = max/div;
		double divw = (double)s.width/(double)divn;
		double x = 0.0;
		
		double divv = (double)s.width/(double)max;
		
		// Goal
		int dw;
		if (value<goal) {
			g.setColor(Color.red);
			dw = (int)(goal*divv);
			g.fillRoundRect(0,0,dw,s.height-1,5,5);
			g.fillRect(5,0,dw-5,s.height-1);
		}
		
		// Value
		g.setColor(Color.green);
		dw = (int)(value*divv);
		g.fillRoundRect(0,0,dw,s.height-1,5,5);
		g.fillRect(5,0,dw-5,s.height-1);
		
		g.setColor(Color.black);
		g.drawRoundRect(0,0,s.width-1,s.height-1,5,5);
		for (int i=0;i<divn-1;i++) {
			x += divw;
			int dx = (int)x;
			g.drawLine(dx,0,dx,s.height);
		}
	}
	public void setValue(int val) {
		this.value = val;
		repaint();
	}
	public void setGoal(int goal) {
		this.goal = goal;
		repaint();
	}
	public static void main(String[] args) {
		PointBar bar = new PointBar(3,12);
		bar.setValue(4);
		bar.setPreferredSize(new Dimension(200,10));
		JOptionPane.showMessageDialog(null,bar);
	}
}