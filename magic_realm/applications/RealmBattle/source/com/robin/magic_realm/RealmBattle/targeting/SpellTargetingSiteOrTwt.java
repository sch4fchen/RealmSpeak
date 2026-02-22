package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingSiteOrTwt extends SpellTargetingSingle {
	
	public SpellTargetingSiteOrTwt(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		CharacterWrapper caster = spell.getCaster();
		TileLocation loc = caster.getCurrentLocation();
		if (loc==null || !loc.hasClearing()) {
			return false;
		}
		
		for (GameObject item : caster.getInventory()) {
			if (item.hasThisAttribute(RealmComponent.TREASURE_WITHIN_TREASURE)) {
				gameObjects.add(item);
			}
		}
		for (RealmComponent rc : loc.clearing.getClearingComponents()) {
			if (rc.isTreasureLocation() && caster.hasTreasureLocationDiscovery(rc.toString())) {
				gameObjects.add(rc.getGameObject());
			}
		}
		return true;
	}
}