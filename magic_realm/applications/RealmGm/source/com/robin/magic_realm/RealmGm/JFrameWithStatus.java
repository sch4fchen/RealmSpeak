package com.robin.magic_realm.RealmGm;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class JFrameWithStatus extends JFrame {
	
	protected JLabel status;
	
	public JFrameWithStatus() {
		status = new JLabel(" ");
	}
	
	public void showStatus(String val) {
		status.setText(val);
		status.paint(status.getGraphics());
	}
	public void resetStatus() {
		showStatus(" ");
	}
}