package com.robin.magic_realm.components.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.robin.general.swing.AggressiveDialog;
import com.robin.general.swing.IntegerField;
import com.robin.magic_realm.components.wrapper.DayKey;

public class MonthDaySelectionDialog extends AggressiveDialog {
	private IntegerField month;
	private IntegerField day;
	private boolean canceled = false;

	public MonthDaySelectionDialog(JFrame parent) {
		super(parent,"Enter month and day",true);
		setLocationRelativeTo(null);
		setSize(250,100);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		setLayout(new BorderLayout());
		
		JPanel inputs = new JPanel(new GridLayout(2,3));
		JPanel monthInput = new JPanel(new GridLayout(1,2));
		JLabel monthText = new JLabel("Month:");
		month = new IntegerField(1);
		monthInput.add(monthText);
		monthInput.add(month);
		JPanel dayInput = new JPanel(new GridLayout(1,2));
		JLabel dayText = new JLabel("Day:");
		day = new IntegerField(1);
		monthInput.add(dayText);
		monthInput.add(day);		
		inputs.add(monthInput);
		inputs.add(dayInput);
		add(inputs,BorderLayout.CENTER);
		
		JPanel buttons = new JPanel(new GridLayout(1,2));
		JButton buttonOk = new JButton("Ok");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
			}
		});
		JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				canceled = true;
				setVisible(false);
				dispose();
			}
		});
		buttons.add(buttonOk);
		buttons.add(buttonCancel);
		add(buttons,BorderLayout.SOUTH);
	}
		
	public DayKey getSelectedDayKey() {
		if (canceled) return null;
		return new DayKey(this.month.getInt(),this.day.getInt());
	}
}