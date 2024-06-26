package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.RealmBattle.CombatSheet;
import com.robin.magic_realm.components.BattleHorse;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingActiveHorse extends SpellTargetingSingle {

	private boolean filterControlledHorses = false;
	protected SpellTargetingActiveHorse(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
		this.filterControlledHorses = spell.getGameObject().hasThisAttribute(Constants.CONTROL_HORSE);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> potentialTargets = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		potentialTargets = CombatSheet.filterNativeFriendly(activeParticipant, potentialTargets);
		for (RealmComponent rc : potentialTargets) {
			if (rc.hasMagicProtection() || rc.hasMagicColorImmunity(spell)) continue;
			if (rc.isNativeHorse()) {
				if (filterControlledHorses && rc.getGameObject().hasThisAttribute(Constants.CONTROLLED_HORSE)) continue;
				gameObjects.add(rc.getGameObject());
				continue;
			}
			if (rc.isNative() || rc.isMonster()) {
				for (GameObject item : rc.getHold()) {
					RealmComponent itemRc = (RealmComponent.getRealmComponent(item));
					if (itemRc.isNativeHorse() && !((BattleHorse)itemRc).isDead() && !itemRc.hasMagicProtection() && !itemRc.hasMagicColorImmunity(spell)) {
						if (filterControlledHorses && rc.getGameObject().hasThisAttribute(Constants.CONTROLLED_HORSE)) continue;
						gameObjects.add(item);
					}
				}
				continue;
			}
			if (rc.isCharacter() && !rc.getGameObject().equals(spell.getCaster().getGameObject())) {
				CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
				for (GameObject item : character.getActiveInventory()) {
					RealmComponent itemRc = (RealmComponent.getRealmComponent(item));
					if (itemRc.isHorse() && !itemRc.hasMagicProtection() && !itemRc.hasMagicColorImmunity(spell)) {
						if (filterControlledHorses && rc.getGameObject().hasThisAttribute(Constants.CONTROLLED_HORSE)) continue;
						gameObjects.add(item);
					}
				}
			}
		}
		return true;
	}
}