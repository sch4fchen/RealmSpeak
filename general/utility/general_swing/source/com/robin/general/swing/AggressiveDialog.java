package com.robin.general.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A JDialog that remains on top of its parent, regardless of window switching
 */
public class AggressiveDialog extends JDialog {

	protected WindowAdapter windowAdapter;
	protected Window parent;
	
	public AggressiveDialog(Dialog owner) {
		this(owner,"",false);
	}
	public AggressiveDialog(Dialog owner,boolean modal) {
		this(owner,"",modal);
	}
	public AggressiveDialog(Dialog owner,String title) {
		this(owner,title,false);
	}
	public AggressiveDialog(Dialog owner,String title,boolean modal) {
		super(owner,title,modal);
		init(owner);
	}
	public AggressiveDialog(Frame owner) {
		this(owner,"",false);
	}
	public AggressiveDialog(Frame owner,boolean modal) {
		this(owner,"",modal);
	}
	public AggressiveDialog(Frame owner,String title) {
		this(owner,title,false);
	}
	public AggressiveDialog(Frame owner,String title,boolean modal) {
		super(owner,title,modal);
		init(owner);
	}
	public void init(Window owner) {
		parent = owner;
		windowAdapter = new WindowAdapter() {
			public void windowActivated(WindowEvent ev) {
				// Will guarantee that this frame is on top of the parent
				toFront();
			}
		};
	}
	public void setVisible(boolean val) {
		super.setVisible(val);
		if (val) {
			// While this dialog is visible, the window listener should be in place
			parent.removeWindowListener(windowAdapter); // just in case
			parent.addWindowListener(windowAdapter);
		}
		else {
			// When the dialog is not visible, the window listener on the parent does not need to be in place
			parent.removeWindowListener(windowAdapter);
		}
	}
}