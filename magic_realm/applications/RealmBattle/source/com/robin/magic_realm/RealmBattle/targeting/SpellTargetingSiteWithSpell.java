package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingSiteWithSpell extends SpellTargetingSingle {
	
	public SpellTargetingSiteWithSpell(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		TileLocation loc = battleModel.getBattleLocation();
		CharacterWrapper caster = spell.getCaster();
		
		for (RealmComponent rc : loc.clearing.getClearingComponents()) {
			if (rc.isTreasureLocation() && caster.hasTreasureLocationDiscovery(rc.toString())) {
				for (GameObject held : rc.getHold()) {
					if (held.hasThisAttribute(RealmComponent.SPELL)) {
						gameObjects.add(rc.getGameObject());
						break;
					}
				}
			}
		}
		return true;
	}
}