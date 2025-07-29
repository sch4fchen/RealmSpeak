package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingOtherArtifact extends SpellTargetingSingle {

	protected SpellTargetingOtherArtifact(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {		
		for (RealmComponent participant : combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true)) {
			if (!participant.isCharacter()) continue;
			CharacterWrapper character = new CharacterWrapper(participant.getGameObject());
			for (GameObject item : character.getInventory()) {
				if (item.hasThisAttribute("artifact") || item.hasThisAttribute("book")) {
					RealmComponent rc = RealmComponent.getRealmComponent(item);
					if (!rc.isEnchanted()) {
						gameObjects.add(item);
					}
				}
			}
		}
		return true;
	}
}