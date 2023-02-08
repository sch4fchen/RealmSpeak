package com.robin.magic_realm.components.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;

public class VictoryConditionButton extends JPanel {
	private static Font font = new Font("Dialog",Font.PLAIN,18);
	private JLabel titleLabel;
	private JTextPane descriptionField;
	private boolean selected = false;
	private ArrayList<ChangeListener> changeListeners;
	
	private MouseAdapter littleClicky = new MouseAdapter() {
		public void mousePressed(MouseEvent ev) {
			if (!isEnabled()) return;
			setSelected(!selected);
		}
	};
	
	public VictoryConditionButton(String title,String description) {
		setLayout(new BorderLayout());
		titleLabel = new JLabel(title,SwingConstants.CENTER);
		titleLabel.setFont(font);
		add(titleLabel,BorderLayout.NORTH);
		descriptionField = new JTextPane();
		descriptionField.setText(description);
		descriptionField.setEditable(false);
		descriptionField.setOpaque(false);
		for(MouseListener ml:descriptionField.getMouseListeners()) {
			descriptionField.removeMouseListener(ml);
		}
		descriptionField.addMouseListener(littleClicky);
		
		StyledDocument doc = descriptionField.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		add(descriptionField,BorderLayout.CENTER);
		
		addMouseListener(littleClicky);
		updateControls();
	}
	public void addChangeListener(ChangeListener changeListener) {
		if (changeListeners==null) {
			changeListeners = new ArrayList<>();
		}
		if (!changeListeners.contains(changeListener)) {
			changeListeners.add(changeListener);
		}
	}
	public void removeChangeListener(ChangeListener changeListener) {
		if (changeListeners.contains(changeListener)) {
			changeListeners.remove(changeListener);
		}
		if (changeListeners.isEmpty()) {
			changeListeners = null;
		}
	}
	private void fireChanged() {
		if (changeListeners==null) return;
		ChangeEvent ev = new ChangeEvent(this);
		for(ChangeListener changeListener:changeListeners) {
			changeListener.stateChanged(ev);
		}
	}
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		titleLabel.setEnabled(enabled);
		descriptionField.setEnabled(enabled);
	}
	public void setSelected(boolean val) {
		setSelected(val,true);
	}
	public void setSelected(boolean val,boolean notify) {
		if (selected==val) return;
		selected = val;
		if (notify) fireChanged();
		updateControls();
	}
	public boolean isSelected() {
		return selected;
	}
	private void updateControls() {
		setBorder(selected?BorderFactory.createLoweredBevelBorder():BorderFactory.createRaisedBevelBorder());
		setBackground(selected? UIManager.getColor("Tree.selectionBackground"): UIManager.getColor("Panel.background"));
		titleLabel.setForeground(selected? UIManager.getColor("Tree.selectionForeground"): UIManager.getColor("Label.foreground"));
		descriptionField.setForeground(selected? UIManager.getColor("Tree.selectionForeground"): UIManager.getColor("Label.foreground"));
	}
}