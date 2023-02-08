package com.robin.general.swing;

import javax.swing.JButton;

public abstract class SingleButton extends JButton {
	
	private boolean mandatory;
	public abstract boolean needsShow();
	
	public SingleButton(String in,boolean mandatory) {
		super(in);
		this.mandatory = mandatory;
	}
	public boolean isMandatory() {
		return mandatory;
	}
}