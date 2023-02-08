package com.robin.magic_realm.RealmSpeak;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public abstract class RealmSpeakInternalFrame extends JInternalFrame {
	
	protected static final int BOTTOM_HEIGHT = 0;
	
	protected Integer forceWidth = null;
	
	public RealmSpeakInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
		super(title,resizable,closable,maximizable,iconifiable);
	}
	public void clearForceWidth() {
		forceWidth = null;
	}
	public void setForceWidth(Integer val) {
		forceWidth = val;
	}
	/**
	 * Resize according to a set strategy
	 */
	public abstract void organize(JDesktopPane desktop);
	public abstract boolean onlyOneInstancePerGame();
	public abstract String getFrameTypeName();
}