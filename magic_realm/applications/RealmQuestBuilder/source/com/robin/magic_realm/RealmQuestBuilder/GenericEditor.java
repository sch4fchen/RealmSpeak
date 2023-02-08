package com.robin.magic_realm.RealmQuestBuilder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.robin.game.objects.GameData;
import com.robin.general.swing.AggressiveDialog;
import com.robin.general.swing.ComponentTools;

public abstract class GenericEditor extends AggressiveDialog {
	
	protected GameData realmSpeakData;
	protected boolean canceledEdit = true;
	
	protected JButton okButton;
	protected JButton cancelButton;
	
	protected abstract void save();
	protected abstract boolean isValidForm();
	
	public GenericEditor(JFrame parent,GameData realmSpeakData) {
		super(parent,true);
		this.realmSpeakData = realmSpeakData;
	}
	public boolean getCanceledEdit() {
		return canceledEdit;
	}
	protected Box buildOkCancelLine() {
		Box bottom = Box.createHorizontalBox();
		bottom.add(Box.createHorizontalGlue());
		okButton = new JButton("Ok");
		ComponentTools.lockDialogButtonSize(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (!isValidForm()) return;
				canceledEdit = false;
				save();
				setVisible(false);
			}
		});
		getRootPane().setDefaultButton(okButton);
		bottom.add(okButton);
		bottom.add(Box.createHorizontalStrut(20));
		cancelButton = new JButton("Cancel");
		ComponentTools.lockDialogButtonSize(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				canceledEdit = true;
				setVisible(false);
			}
		});
		bottom.add(cancelButton);
		return bottom;
	}
}