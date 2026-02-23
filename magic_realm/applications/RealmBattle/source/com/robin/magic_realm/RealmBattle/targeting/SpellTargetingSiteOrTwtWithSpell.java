package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingSiteOrTwtWithSpell extends SpellTargetingSingle {
	
	public SpellTargetingSiteOrTwtWithSpell(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		TileLocation loc = battleModel.getBattleLocation();
		CharacterWrapper caster = spell.getCaster();
		
		/*for (GameObject item : caster.getInventory()) {
			if (item.hasThisAttribute(RealmComponent.TREASURE_WITHIN_TREASURE)) {
				for (GameObject held : item.getHold()) {
					if (held.hasThisAttribute(RealmComponent.SPELL)) {
						gameObjects.add(item);
						break;
					}
				}
			}
		}*/
		
		for (RealmComponent rc : loc.clearing.getClearingComponents()) {
			if ((rc.isTreasureLocation() && caster.hasTreasureLocationDiscovery(rc.toString()))
					|| (rc.getGameObject().hasThisAttribute(RealmComponent.TREASURE_WITHIN_TREASURE) && !rc.isTreasureLocation())) {
				for (GameObject held : rc.getHold()) {
					if (held.hasThisAttribute(RealmComponent.SPELL)) {
						gameObjects.add(rc.getGameObject());
						break;
					}
				}
			}
			if (rc.isCharacter()) {
				for (GameObject item : rc.getHold()) {
					if (item.hasThisAttribute(RealmComponent.TREASURE_WITHIN_TREASURE)) {
						for (GameObject held : item.getHold()) {
							if (held.hasThisAttribute(RealmComponent.SPELL)) {
								gameObjects.add(item);
								break;
							}
						}
					}
				}
			}
		}
		return true;
	}
}