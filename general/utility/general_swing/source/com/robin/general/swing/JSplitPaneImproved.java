package com.robin.general.swing;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JSplitPane;

public class JSplitPaneImproved extends JSplitPane {
	private boolean isPainted = false;
	private boolean hasProportionalLocation = false;
	private double proportionalLocation;

	public JSplitPaneImproved() {
		super();
	}

	public JSplitPaneImproved(int orientation) {
		super(orientation);
	}

	public JSplitPaneImproved(int orientation, boolean newContinuousLayout) {
		super(orientation, newContinuousLayout);
	}

	public JSplitPaneImproved(int orientation, Component leftComponent, Component rightComponent) {
		super(orientation, leftComponent, rightComponent);
	}

	public JSplitPaneImproved(int orientation, boolean newContinuousLayout, Component leftComponent, Component rightComponent) {
		super(orientation, newContinuousLayout, leftComponent, rightComponent);
	}

	public void setDividerLocation(double proportionalLocation) {
		isPainted = false;
		hasProportionalLocation = true;
		this.proportionalLocation = proportionalLocation;
	}

	public void paint(Graphics g) {
		if (!isPainted) {
			if (hasProportionalLocation)
				super.setDividerLocation(proportionalLocation);
			isPainted = true;
		}
		super.paint(g);
	}
}