package com.robin.general.swing;

import javax.swing.JButton;
import javax.swing.JComponent;

public class ComponentButton extends JButton {
	private JComponent component;

	public ComponentButton(String text, JComponent component) {
		super(text);
		this.component = component;
	}

	public JComponent getMyComponent() {
		return component;
	}
}