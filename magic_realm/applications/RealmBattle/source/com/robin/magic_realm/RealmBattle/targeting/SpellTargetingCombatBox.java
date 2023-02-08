package com.robin.magic_realm.RealmBattle.targeting;

import javax.swing.ListSelectionModel;

import com.robin.general.swing.ListChooser;
import com.robin.magic_realm.RealmBattle.*;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingCombatBox extends SpellTargeting {
	
	private final String BOX_0 = "Charge and Thrust";
	private final String BOX_1 = "Dodge and Swing";
	private final String BOX_2 = "Duck and Smash";
	
	public SpellTargetingCombatBox(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ListChooser chooser = new ListChooser(combatFrame, "Select targeted combat box",  new String[] { BOX_0, BOX_1, BOX_2 });
		chooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chooser.setDoubleClickEnabled(true);
		chooser.setLocationRelativeTo(null);
		chooser.setVisible(true);
		Object selection = chooser.getSelectedItem();
		if (selection == null) return false;
		String combatBox = "";
		switch(selection.toString()) {
			case BOX_0:
				combatBox = "0";
				break;
			case BOX_1:
				combatBox = "1";
				break;
			case BOX_2:
				combatBox = "2";
				break;
			default:
				combatBox = "";
		}
		spell.setExtraIdentifier(combatBox);
		return true;
	}

	@Override
	public boolean assign(HostPrefWrapper hostPrefs, CharacterWrapper activeCharacter) {
		return false;
	}

	@Override
	public boolean hasTargets() {
		return spell.getExtraIdentifier() != null && !spell.getExtraIdentifier().isEmpty();
	}
}