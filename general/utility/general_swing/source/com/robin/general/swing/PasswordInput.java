package com.robin.general.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PasswordInput extends JDialog {

	protected JPasswordField field;
	protected JButton cancel;
	protected JButton okay;
	protected String password;

	private PasswordInput(Component parent) {
		super();
		password = null;
		initComponents(parent);
	}
	private void initComponents(Component parent) {
		setSize(200,100);
		setLocation(50,50);
		setTitle("Enter password:");
		setModal(true);
		setResizable(false);
		
		Box vbox = Box.createVerticalBox();
		vbox.add(Box.createVerticalGlue());
			Box hbox = Box.createHorizontalBox();
				field = new JPasswordField();
				ComponentTools.lockComponentSize(field,120,25);
			hbox.add(field);
		vbox.add(hbox);
		vbox.add(Box.createVerticalGlue());
			hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalGlue());
				cancel = new JButton("Cancel");
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						password = null;
						close();
					}
				});
			hbox.add(cancel);
				okay = new JButton("Okay");
				okay.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						password = new String(field.getPassword());
						if (password==null || password.length()==0) {
							password = null;
						}
						close();
					}
				});
			hbox.add(okay);
			hbox.add(Box.createHorizontalGlue());
		vbox.add(hbox);
		
		getRootPane().setDefaultButton(okay);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(vbox,"Center");
		
		field.grabFocus();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				close();
			}
		});
	}
	public void close() {
		setVisible(false);
	}
	public String getPassword() {
		return password;
	}
	
	public static String showPasswordDialog(Component parent) {
		PasswordInput input = new PasswordInput(parent);
		input.setVisible(true);
		return input.getPassword();
	}
	
	/**
	 * For testing only
	 */
	public static void main(String[]args) {
		System.out.println("the password you entered is:  "+PasswordInput.showPasswordDialog(null));
		System.exit(0);
	}
}