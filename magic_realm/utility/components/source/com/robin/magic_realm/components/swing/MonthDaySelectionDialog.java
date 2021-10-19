/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
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