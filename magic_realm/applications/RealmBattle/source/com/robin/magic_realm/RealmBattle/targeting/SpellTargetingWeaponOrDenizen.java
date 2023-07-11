package com.robin.magic_realm.RealmBattle.targeting;

import java.util.Collection;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.RealmBattle.CombatSheet;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingWeaponOrDenizen extends SpellTargetingSingle {

	protected SpellTargetingWeaponOrDenizen(CombatFrame combatFrame, SpellWrapper spell) {
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
			else {
				RealmComponent owner = rc.getOwner();
				if (rc.isNative() ||  rc.isMonster()) {
					gameObjects.add(rc.getGameObject());
					identifiers.add(owner==null?"denizen":owner.getGameObject().getName());
				}
			}
		}
		return true;
	}
}