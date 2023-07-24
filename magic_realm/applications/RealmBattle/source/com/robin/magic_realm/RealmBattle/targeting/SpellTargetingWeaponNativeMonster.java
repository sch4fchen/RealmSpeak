package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.*;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingWeaponNativeMonster extends SpellTargetingSingle {

	protected SpellTargetingWeaponNativeMonster(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> potentialTargets = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		potentialTargets = CombatSheet.filterNativeFriendly(activeParticipant, potentialTargets);
		for (RealmComponent rc:potentialTargets) {
			if ((rc.isNative() || rc.isMonster())
			 && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell)
			 && !rc.getGameObject().hasThisAttribute(Constants.ANOMALY) && !rc.getGameObject().hasThisAttribute(Constants.TITAN)) {
				gameObjects.add(rc.getGameObject());
			}
			if (rc.isCharacter()) {
				for (GameObject go : rc.getHold()) {
					if (RealmComponent.getRealmComponent(go).isWeapon()) {
						gameObjects.add(go);
					}
				}
			}
		}
		return true;
	}
}