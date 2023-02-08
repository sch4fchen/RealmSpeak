package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.general.swing.ButtonOptionDialog;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingWeather extends SpellTargetingSpecial {

	public SpellTargetingWeather(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		// use option pane here to ask "see weather" or "change weather"
		// I really shouldn't put the treasure name here, but I don't see that it will ever matter unless I add new
		// treasures that can control the weather (doubt it)
		ButtonOptionDialog dialog = new ButtonOptionDialog(combatFrame,null,"",spell.getGameObject().getName(),true);
		dialog.addSelectionObject("See the Weather Chit");
		dialog.addSelectionObject("Change the Weather Chit");
		dialog.setVisible(true);
		String val = (String)dialog.getSelectedObject();
		if (val!=null) {
			spell.addTarget(combatFrame.getHostPrefs(), spell.getGameObject());
			spell.setExtraIdentifier(val);
			return true;
		}
		return false;
	}
}