package com.robin.general.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

public class ControlNotifier implements ActionListener,CaretListener,ChangeListener {
	
	private ArrayList<ActionListener> actionListeners;
	
	public ControlNotifier() {
	}
	public void addActionListener(ActionListener actionListener) {
		if (actionListeners==null) {
			actionListeners = new ArrayList<>();
		}
		if (!actionListeners.contains(actionListener)) {
			actionListeners.add(actionListener);
		}
	}
	public void removeActionListener(ActionListener actionListener) {
		if (actionListeners!=null) {
			actionListeners.remove(actionListener);
			if (actionListeners.isEmpty()) {
				actionListeners = null;
			}
		}
	}
	public void actionPerformed(ActionEvent ev) {
		fireActionPerformed();
	}
	public void caretUpdate(CaretEvent e) {
		fireActionPerformed();
	}
	public void stateChanged(ChangeEvent e) {
		fireActionPerformed();
	}
	private void fireActionPerformed() {
		if (actionListeners==null) return;
		ActionEvent ev = new ActionEvent(this,0,"");
		for (ActionListener actionListener:actionListeners) {
			actionListener.actionPerformed(ev);
		}
	}
	public JTextField getTextField() {
		JTextField field = new JTextField();
		field.addCaretListener(this);
		field.addActionListener(this);
		return field;
	}
	public IntegerField getIntegerField() {
		IntegerField field = new IntegerField();
		field.addCaretListener(this);
		field.addActionListener(this);
		return field;
	}
	public JButton getButton(String name) {
		JButton button = new JButton(name);
		button.addActionListener(this);
		return button;
	}
	public JCheckBox getCheckBox(String name) {
		JCheckBox button = new JCheckBox(name);
		button.addActionListener(this);
		return button;
	}
	public JRadioButton getRadioButton(String name) {
		return getRadioButton(name,false);
	}
	public JRadioButton getRadioButton(String name,boolean checked) {
		JRadioButton button = new JRadioButton(name,checked);
		button.addActionListener(this);
		return button;
	}
	public JSlider getSlider(int min,int max,int value) {
		JSlider slider = new JSlider(min,max,value);
		slider.addChangeListener(this);
		return slider;
	}
	public JComboBox getComboBox(Object[] array) {
		JComboBox cb = new JComboBox(array);
		cb.addActionListener(this);
		return cb;
	}
}