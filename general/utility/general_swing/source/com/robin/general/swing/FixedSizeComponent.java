package com.robin.general.swing;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public abstract class FixedSizeComponent extends JComponent {

	private static final Insets NO_INSETS = new Insets(0,0,0,0);

	private ArrayList<ChangeListener> changeListeners = null;
	
	public abstract int getComponentWidth();
	public abstract int getComponentHeight();

	public FixedSizeComponent() {
	}
	protected Insets getBorderInsets() {
		Border border = getBorder();
		if (border!=null) {
			return border.getBorderInsets(this);
		}
		return NO_INSETS;
	}
	public void setBorder(Border b) {
		super.setBorder(b);
		updateComponentSize();
	}
	protected void updateComponentSize() {
		Insets in = getBorderInsets();
		
		int cw = getComponentWidth()+in.left+in.right;
		int ch = getComponentWidth()+in.top+in.bottom;
		
		Dimension d = new Dimension(cw,ch);
		setMaximumSize(d);
		setMinimumSize(d);
		setPreferredSize(d);
	}
	public int getWidth() {
		Insets in = getBorderInsets();
		return getComponentWidth()+in.left+in.right;
	}
	public int getHeight() {
		Insets in = getBorderInsets();
		return getComponentHeight()+in.top+in.bottom;
	}
	public void addChangeListener(ChangeListener listener) {
		if (changeListeners==null) {
			changeListeners = new ArrayList<>();
		}
		if (!changeListeners.contains(listener)) {
			changeListeners.add(listener);
		}
	}
	public void removeChangeListener(ChangeListener listener) {
		if (changeListeners!=null) {
			changeListeners.remove(listener);
			if (changeListeners.size()==0) {
				changeListeners = null;
			}
		}
	}
	protected void fireStateChanged() {
		if (changeListeners!=null && changeListeners.size()>0) {
			ChangeEvent ev = new ChangeEvent(this);
			for (ChangeListener listener : changeListeners) {
				listener.stateChanged(ev);
			}
		}
	}
}