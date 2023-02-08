package com.robin.magic_realm.RealmCharacterBuilder.EditPanel;

import javax.swing.JLabel;

import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class BlankEditPanel extends AdvantageEditPanel {

	public BlankEditPanel(CharacterWrapper pChar, String levelKey) {
		super(pChar, levelKey);
		
		JLabel label = new JLabel("Select an advantage type on the left, or keep this one if you are just making a note.");
		add(label);
	}

	protected void applyAdvantage() {
		// does nothing
	}
	public String getSuggestedDescription() {
		return null;
	}

	public boolean isCurrent() {
		return false;
	}

	public String toString() {
		return "Blank";
	}
}