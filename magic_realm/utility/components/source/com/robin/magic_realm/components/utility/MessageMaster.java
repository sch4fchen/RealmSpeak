package com.robin.magic_realm.components.utility;

import javax.swing.*;

import com.robin.general.swing.FrameManager;

public class MessageMaster implements Runnable {
	private JFrame parent;
	private String message;
	private String title;
	private int type;
	private Icon icon;
	
	private MessageMaster(JFrame parent,String message,String title,int type,Icon icon) {
		this.parent = parent;
		this.message = message;
		this.title = title;
		this.type = type;
		this.icon = icon;
	}
	public void run() {
		if (icon==null) {
			switch(type) {
				case JOptionPane.INFORMATION_MESSAGE:
					icon = UIManager.getIcon("OptionPane.informationIcon");
					break;
				case JOptionPane.WARNING_MESSAGE:
					icon = UIManager.getIcon("OptionPane.warningIcon");
					break;
				case JOptionPane.ERROR_MESSAGE:
					icon = UIManager.getIcon("OptionPane.errorIcon");
					break;
				case JOptionPane.QUESTION_MESSAGE:
					icon = UIManager.getIcon("OptionPane.questionIcon");
					break;
			}
		}
		
		FrameManager.showDefaultManagedFrame(parent,message,title,icon,true);
	}
	
	public static void showMessage(JFrame parent,String message,String title,int type) {
		showMessage(parent,message,title,type,null);
	}
	public static void showMessage(JFrame parent,String message,String title,int type,Icon icon) {
		MessageMaster mm = new MessageMaster(parent,message,title,type,icon);
		SwingUtilities.invokeLater(mm);
	}
}