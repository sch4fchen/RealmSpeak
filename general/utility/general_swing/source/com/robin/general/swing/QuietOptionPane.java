package com.robin.general.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;

import javax.swing.*;

/**
 * A utility class to create JOptionPane dialogs that can be silenced
 */
public class QuietOptionPane {
	
	private static final Font FONT = UIManager.getFont("Label.font");
	
	private Object message;
	private String silencingString;
	private JCheckBox silencingOption;
	
	private QuietOptionPane(Object message,String silencingString) {
		this.message = message;
		this.silencingString = silencingString;
	}
	private boolean isSilenced() {
		return silencingOption.isSelected();
	}
	private JPanel getPanel(boolean defaultOn) {
		JPanel panel = new JPanel(new BorderLayout(10,10));
		if (message instanceof Component) {
			panel.add((Component)message,"Center");
		}
		else {
			JTextArea area = new JTextArea(message.toString());
			area.setEditable(false);
			area.setOpaque(false);
			area.setFont(FONT);
			panel.add(area,"Center");
		}
		silencingOption = new JCheckBox(silencingString,defaultOn);
		panel.add(silencingOption,"South");
		return panel;
	}
	
	private static boolean lastWasSilenced;
	
	public static boolean isLastWasSilenced() {
		return lastWasSilenced;
	}
	
	public static int showConfirmDialog(Component parentComponent, Object message, String silencingString, boolean defaultOn) throws HeadlessException {
		return showConfirmDialog(parentComponent,message,"Select an Option",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,silencingString,defaultOn);
	}
	public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, String silencingString, boolean defaultOn) throws HeadlessException {
		return showConfirmDialog(parentComponent,message,title,optionType,JOptionPane.QUESTION_MESSAGE,null,silencingString,defaultOn);
	}
	public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, String silencingString, boolean defaultOn) throws HeadlessException {
		return showConfirmDialog(parentComponent,message,title,optionType,messageType,null,silencingString,defaultOn);
	}
	public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, String silencingString, boolean defaultOn) throws HeadlessException {
		QuietOptionPane qop = new QuietOptionPane(message,silencingString);
		int ret = JOptionPane.showConfirmDialog(parentComponent,qop.getPanel(defaultOn),title,optionType,messageType,icon);
		lastWasSilenced = qop.isSilenced();
		return ret;
	}
	public static void showMessageDialog(Component parentComponent, Object message, String silencingString, boolean defaultOn) throws HeadlessException {
		showMessageDialog(parentComponent,message,"",JOptionPane.INFORMATION_MESSAGE,null,silencingString,defaultOn);
	}
	public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType, Icon icon, String silencingString, boolean defaultOn) throws HeadlessException {
		QuietOptionPane qop = new QuietOptionPane(message,silencingString);
		JOptionPane.showMessageDialog(parentComponent,qop.getPanel(defaultOn),title,messageType,icon);
		lastWasSilenced = qop.isSilenced();
	}
	
	public static void main(String[] args) {
//		JOptionPane.showConfirmDialog(null,"This is\na test");
		
		do {
			QuietOptionPane.showMessageDialog(null,"This is\na test","Don't ask me again!",false);
//			QuietOptionPane.showConfirmDialog(null,"This is\na test","Don't ask me again!",false);
		} while (!QuietOptionPane.isLastWasSilenced());
		
		System.exit(0);
	}
}