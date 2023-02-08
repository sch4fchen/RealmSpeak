package com.robin.magic_realm.components.utility;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.magic_realm.components.RealmComponent;

public class RealmComponentError {
	private RealmComponent rc;
	private String title;
	private String error;
	private boolean optional = false;
	
	public RealmComponentError(RealmComponent rc,String title,String error) {
		this(rc,title,error,false);
	}
	public RealmComponentError(RealmComponent rc,String title,String error,boolean optional) {
		this.rc = rc;
		this.title = title;
		this.error = error;
		this.optional = optional;
	}

	public String getError() {
		return error;
	}

	public RealmComponent getRc() {
		return rc;
	}

	public String getTitle() {
		return title;
	}
	
	/**
	 * @return		true if the character wants to ignore the dialog (assuming that's an option) and continue
	 */
	public boolean showDialog(JFrame frame) {
		if (optional) {
			int ret = JOptionPane.showConfirmDialog(frame,error,title,JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
			return ret==JOptionPane.YES_OPTION;
		}
		else {
			JOptionPane.showMessageDialog(frame,error,title,JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}