package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingCaster extends SpellTargetingSingle {

	protected SpellTargetingCaster(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		if (!combatFrame.getActiveCharacter().hasMagicProtection()) {
			gameObjects.add(combatFrame.getActiveCharacter().getGameObject());
		}
		return true;
	}
}