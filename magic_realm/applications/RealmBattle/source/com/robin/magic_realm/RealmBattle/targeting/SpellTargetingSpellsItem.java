package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingSpellsItem extends SpellTargetingSingle {

	protected SpellTargetingSpellsItem(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		GameObject treasure = spell.getGameObject().getHeldBy();
		if (treasure!=null) {
			gameObjects.add(treasure);
			return true;
		}
		return false;
	}
}