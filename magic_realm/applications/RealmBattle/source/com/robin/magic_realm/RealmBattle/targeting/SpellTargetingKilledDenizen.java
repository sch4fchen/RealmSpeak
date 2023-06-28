package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingKilledDenizen extends SpellTargetingSingle {

	public SpellTargetingKilledDenizen(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		for (GameObject go:battleModel.getKilledObjects()) {
			if (RealmComponent.getRealmComponent(go).isDenizen()) {
				gameObjects.add(go);
			}
		}
		return true;
	}
}