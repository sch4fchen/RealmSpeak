package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.RealmBattle.CombatSheet;
import com.robin.magic_realm.components.BattleChit;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingCreatureHorseHound extends SpellTargetingSingle {

	private boolean onlyNonFlyingTargets = false;
	
	public SpellTargetingCreatureHorseHound(CombatFrame combatFrame,SpellWrapper spell) {
		super(combatFrame, spell);
		if (spell.getGameObject().hasThisAttribute(Constants.NON_FLYING_TARGETS)) {
			onlyNonFlyingTargets = true;
		}
	}
	
	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> potentialTargets = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		potentialTargets = CombatSheet.filterNativeFriendly(activeParticipant, potentialTargets);
		for (RealmComponent rc : potentialTargets) {
			if (onlyNonFlyingTargets && ((BattleChit)rc).getFlySpeed()!=null) continue;
			
			if (rc.isMonster() && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell)) {
				gameObjects.add(rc.getGameObject());
			}
			else if (rc.getGameObject().hasThisAttribute(Constants.BEAST) && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell)) {
				gameObjects.add(rc.getGameObject());
			}
			else if ((rc.isHorse() || rc.isNativeHorse()) && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell)) {
				gameObjects.add(rc.getGameObject());
			}
			else if (rc.isCharacter()) {
				CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
				for (GameObject item : character.getInventory()) {
					RealmComponent itemRc = (RealmComponent.getRealmComponent(item));
					if (itemRc.isHorse() && !itemRc.hasMagicProtection() && !itemRc.hasMagicColorImmunity(spell)) {
						gameObjects.add(item);
					}
					else if (item.hasThisAttribute(Constants.HOUND) && !itemRc.hasMagicProtection() && !itemRc.hasMagicColorImmunity(spell)) {
						gameObjects.add(item);
					}
				}
				if (character.isTransformedBeast() && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell)) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		return true;
	}
}