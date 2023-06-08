package com.robin.magic_realm.RealmBattle.targeting;

import javax.swing.JOptionPane;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingTile extends SpellTargetingSpecial {

	public SpellTargetingTile(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}
	
	public boolean populate(BattleModel battleModel, RealmComponent activeParticipant) {
		// Target the spellcaster's clearing
		TileLocation loc = battleModel.getBattleLocation();
		boolean validTarget = true;
		if (spell.getGameObject().hasThisAttribute(Constants.TARGET_CLEARINGS)) {
			validTarget = false;
			String clearings = spell.getGameObject().getThisAttribute(Constants.TARGET_CLEARINGS);
			if (clearings.matches("river") || clearings.matches("water")) {
				for (ClearingDetail cl : loc.tile.getClearings()) {
					if (cl.isWater() || cl.isFrozenWater()) {
						validTarget = true;
						break;
					}
				}
			}
		}
		if (validTarget) {
			gameObjects.add(loc.tile.getGameObject());
			spell.addTarget(combatFrame.getHostPrefs(),loc.tile.getGameObject(),true);
			CombatFrame.broadcastMessage(activeParticipant.getGameObject().getName(),"Targets the "+loc.tile.getGameObject().getName());
			JOptionPane.showMessageDialog(combatFrame,"The current tile was selected as the target.");
		}
		return true;
	}
	
	public boolean hasTargets() {
		return !gameObjects.isEmpty();
	}
}