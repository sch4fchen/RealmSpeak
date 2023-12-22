package com.robin.magic_realm.RealmBattle.targeting;

import java.util.Collection;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.RealmBattle.CombatSheet;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingWeapon extends SpellTargetingSingle {

	protected SpellTargetingWeapon(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		// Targets one weapon counter, native counter, Goblin counter, Ogre counter or Giant's club
		TileLocation loc = battleModel.getBattleLocation();
		Collection<RealmComponent> realmComponents = loc.clearing.getDeepClearingComponents();
		realmComponents = CombatSheet.filterNativeFriendly(activeParticipant, realmComponents);
		for (RealmComponent rc : realmComponents) {
			if (rc.isWeapon()) {
				gameObjects.add(rc.getGameObject());
				identifiers.add(rc.getGameObject().getHeldBy().getName());
			}
			//Poison cannot be cast on Alchemist's Mixture!!
			else {
				RealmComponent owner = rc.getOwner();
				if (rc.isNative() && (owner==null || allowTargetingHirelings())) {
					gameObjects.add(rc.getGameObject());
					identifiers.add(owner==null?"denizen":owner.getGameObject().getName());
				}
				else if (rc.isMonster()) {
					if (rc.getGameObject().hasThisAttribute(Constants.WEAPON_USE)) {
						gameObjects.add(rc.getGameObject());
						identifiers.add(owner==null?"denizen":owner.getGameObject().getName());
					}
					else if (rc.getGameObject().hasThisAttribute(Constants.WEAPON_USE_CHIT) || rc.getGameObject().hasThisAttribute(Constants.MONSTER_WEAPON)) {
						MonsterChitComponent monster = (MonsterChitComponent)rc;
						gameObjects.add(monster.getWeapon().getGameObject());
						identifiers.add(owner==null?"denizen":owner.getGameObject().getName());
					}
				}
			}
		}
		return true;
	}
}