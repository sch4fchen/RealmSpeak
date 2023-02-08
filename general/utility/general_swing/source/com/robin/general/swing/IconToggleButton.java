package com.robin.general.swing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class IconToggleButton extends JComponent {
	private static final int BORDER = 2;
	private static final int EDGE = 1;
	
	private Icon icon;
	private boolean selected;
	
	private Collection actionListeners = null;

	public IconToggleButton(Icon icon) {
		this(icon,false);
	}
	public IconToggleButton(Icon icon,boolean sel) {
		this.icon = icon;
		this.selected = sel;
		Dimension buttonSize = new Dimension(icon.getIconWidth()+(BORDER<<1)+(EDGE<<1),icon.getIconHeight()+(BORDER<<1)+(EDGE<<1));
		setMinimumSize(buttonSize);
		setMaximumSize(buttonSize);
		setPreferredSize(buttonSize);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				toggleSelection();
			}
		});
	}
	public void addActionListener(ActionListener listener) {
		if (actionListeners==null) {
			actionListeners = new ArrayList();
		}
		actionListeners.add(listener);
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean val) {
		selected = val;
		repaint();
		fireActionPerformed();
	}
	private void toggleSelection() {
		selected = !selected;
		repaint();
		fireActionPerformed();
	}
	private void fireActionPerformed() {
		if (actionListeners!=null) {
			ActionEvent ev = new ActionEvent(this,0,"");
			for (Iterator i=actionListeners.iterator();i.hasNext();) {
				ActionListener listener = (ActionListener)i.next();
				listener.actionPerformed(ev);
			}
		}
	}
	public void paintComponent(Graphics g) {
		if (selected) {
			g.setColor(Color.yellow);
			g.fillRect(EDGE,EDGE,icon.getIconWidth()+(BORDER<<1),icon.getIconHeight()+(BORDER<<1));
		}
		icon.paintIcon(this,g,BORDER+EDGE,BORDER+EDGE);
	}
}