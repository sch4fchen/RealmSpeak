package com.robin.magic_realm.components.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Small floating control shown over a {@link CenteredMapView}: "Default View" recalls a saved
 * pan/zoom, "Set Default" saves the current one. Dragged via a dedicated grip handle so the
 * buttons themselves only ever fire their click action - never a drag.
 * <p>
 * By default this control anchors to the lower-left corner of its container, tracking resizes,
 * until the user drags it somewhere else (after which it stays put, only being clamped back into
 * view if the container shrinks too far).
 */
public class MapDefaultViewControl extends JPanel {

	public static final int MARGIN = 10;
	private static final int GRIP_WIDTH = 14;

	private boolean userPositioned = false;
	private Point dragStartInPanel = null;

	public MapDefaultViewControl(CenteredMapView map) {
		super(new BorderLayout(2,0));
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.darkGray),
				BorderFactory.createEmptyBorder(2,2,2,2)));
		
		JComponent grip = createGripHandle();
		add(grip,BorderLayout.WEST);

		JPanel buttons = new JPanel(new GridLayout(1,2,4,0));
		JButton defaultViewButton = new JButton("Default View");
		defaultViewButton.setToolTipText("Recall the saved default view");
		defaultViewButton.addActionListener(ev -> map.restoreDefaultView());
		JButton setDefaultButton = new JButton("Set Default");
		setDefaultButton.setToolTipText("Save the current pan/zoom as the default view");
		setDefaultButton.addActionListener(ev -> map.setAsDefaultView());
		buttons.add(defaultViewButton);
		buttons.add(setDefaultButton);
		add(buttons,BorderLayout.CENTER);
	}

	private JComponent createGripHandle() {
		JComponent grip = new JComponent() {
			protected void paintComponent(Graphics g) {
				g.setColor(Color.darkGray);
				for (int row=0;row<3;row++) {
					for (int col=0;col<2;col++) {
						g.fillRect(3+col*5,4+row*5,2,2);
					}
				}
			}
		};
		grip.setPreferredSize(new Dimension(GRIP_WIDTH,10));
		grip.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		grip.setToolTipText("Drag to move");

		MouseAdapter dragHandler = new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				dragStartInPanel = SwingUtilities.convertPoint(grip,ev.getPoint(),MapDefaultViewControl.this);
			}
			public void mouseDragged(MouseEvent ev) {
				userPositioned = true;
				Point nowInPanel = SwingUtilities.convertPoint(grip,ev.getPoint(),MapDefaultViewControl.this);
				Point loc = getLocation();
				moveTo(loc.x+(nowInPanel.x-dragStartInPanel.x), loc.y+(nowInPanel.y-dragStartInPanel.y));
			}
		};
		grip.addMouseListener(dragHandler);
		grip.addMouseMotionListener(dragHandler);
		return grip;
	}

	/**
	 * Moves to the given location, clamped within the parent's current bounds so the control
	 * can never be dragged (or resized) entirely out of view.
	 */
	private void moveTo(int x,int y) {
		Container parent = getParent();
		if (parent!=null) {
			x = Math.max(0,Math.min(x,parent.getWidth()-getWidth()));
			y = Math.max(0,Math.min(y,parent.getHeight()-getHeight()));
		}
		setLocation(x,y);
	}

	/**
	 * Called whenever the map container resizes. Re-anchors to the lower-left corner with margin
	 * if the user has never dragged this control; otherwise just clamps the existing position so
	 * a shrinking window can't strand it off-screen.
	 */
	public void reanchor(int containerWidth,int containerHeight) {
		setSize(getPreferredSize());
		if (userPositioned) {
			moveTo(getX(),getY());
		}
		else {
			setLocation(MARGIN,MARGIN);
		}
	}
}
