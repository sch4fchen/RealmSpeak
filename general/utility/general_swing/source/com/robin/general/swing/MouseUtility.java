package com.robin.general.swing;

import java.awt.event.*;

public class MouseUtility {
	// This behavior is not introduced until 1.4, so I have to emulate it myself
	public static final int NOBUTTON = 0;
	public static final int BUTTON1 = 1;
	public static final int BUTTON2 = 2;
	public static final int BUTTON3 = 3;
	
	/**
	 * @deprecated		No longer needed, now that I'm all 1.5.
	 */
	public static int getMouseButton(MouseEvent ev) {
		int mod = ev.getModifiers();
		if ((mod & InputEvent.BUTTON1_MASK)>0) {
			return BUTTON1;
		}
		else if ((mod & InputEvent.BUTTON2_MASK)>0) {
			return BUTTON2;
		}
		else if ((mod & InputEvent.BUTTON3_MASK)>0) {
			return BUTTON3;
		}
		return NOBUTTON;
	}
	public static boolean isRightClick(MouseEvent ev) {
		int button = ev.getButton();
		return button!=MouseEvent.NOBUTTON && button!=MouseEvent.BUTTON1;
	}
	public static boolean isRightOrControlClick(MouseEvent ev) {
		return ev.isControlDown() || isRightClick(ev);
	}
}