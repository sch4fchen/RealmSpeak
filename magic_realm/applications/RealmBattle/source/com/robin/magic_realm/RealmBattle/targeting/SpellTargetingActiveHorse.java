package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.RealmBattle.CombatSheet;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingActiveHorse extends SpellTargetingSingle {

	protected SpellTargetingActiveHorse(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> potentialTargets = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		potentialTargets = CombatSheet.filterNativeFriendly(activeParticipant, potentialTargets);
		for (RealmComponent rc : potentialTargets) {
			if (rc.hasMagicProtection() && rc.hasMagicColorImmunity(spell.getRequiredColorMagic())) continue;
			if (rc.isNativeHorse()) {
				gameObjects.add(rc.getGameObject());
				continue;
			}
			if (rc.isCharacter()) {
				CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
				for (GameObject item : character.getActiveInventory()) {
					if ((RealmComponent.getRealmComponent(item)).isHorse()) {
						gameObjects.add(item);
					}
				}
			}
		}
		return true;
	}
}