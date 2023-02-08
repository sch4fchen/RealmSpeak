package com.robin.general.swing;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class PadlockButton extends JComponent {

	protected boolean locked;

	public PadlockButton() {
		this(false); // default is "unlocked"
	}
	public PadlockButton(boolean locked) {
		this.locked = locked;
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				toggle();
			}
		});
		setPreferredSize(new Dimension(40,20));
		setMaximumSize(new Dimension(40,20));
	}
	public void setLocked(boolean val) {
		locked = val;
		repaint();
	}
	public boolean isLocked() {
		return locked;
	}
	private void toggle() {
		locked = !locked;
		repaint();
	}
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		
		g.setStroke(new BasicStroke(2));
	
//		Dimension size = getPreferredSize();
		
		Rectangle base = new Rectangle(5,10,10,8);
		Arc2D.Float arc = new Arc2D.Float(locked?6:14,4,8,8,0,180,Arc2D.OPEN);
		arc.setAngleStart(0.0);
		arc.setAngleExtent(180.0);
		
		g.setColor(Color.black);
		g.draw(base);
		g.draw(arc);
	}
	/**
	 * For testing only
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
			final PadlockButton b = new PadlockButton();
		frame.getContentPane().add(b,"North");
		frame.setSize(400,400);
		frame.setVisible(true);
	}
}