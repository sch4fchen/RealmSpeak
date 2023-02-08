package com.robin.general.swing;

import java.util.ArrayList;

public class SingleButtonManager {
	
	private boolean oneShowing;
	private boolean mandatoryShowing;
	private ArrayList<SingleButton> buttons;
	
	public SingleButtonManager() {
		buttons = new ArrayList<SingleButton>();
	}
	public void addButton(SingleButton button) {
		buttons.add(button);
	}
	public void updateButtonVisibility() {
		oneShowing = false;
		mandatoryShowing = false;
		for (SingleButton button:buttons) {
			if (!oneShowing && button.needsShow()) {
				button.setVisible(true);
				oneShowing = true;
				mandatoryShowing = button.isMandatory();
			}
			else {
				button.setVisible(false);
			}
		}
	}
//	public boolean hasOneShowing() {
//		return oneShowing;
//	}
	public boolean hasMandatoryShowing() {
		return mandatoryShowing;
		
	}
}