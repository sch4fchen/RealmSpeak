package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.RealmBattle.CombatSheet;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.NativeSteedChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.SteedChitComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingCreatureHorseHound extends SpellTargetingSingle {

	private boolean onlyNonFlyingTargets = false;
	private boolean onlyNonMaximumTargets = false;
	
	public SpellTargetingCreatureHorseHound(CombatFrame combatFrame,SpellWrapper spell) {
		super(combatFrame, spell);
		if (spell.getGameObject().hasThisAttribute(Constants.NON_FLYING_TARGETS)) {
			onlyNonFlyingTargets = true;
		}
		if (spell.getGameObject().hasThisAttribute(Constants.NON_MAXIMUM_TARGETS)) {
			onlyNonMaximumTargets = true;
		}
	}
	
	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> potentialTargets = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		potentialTargets = CombatSheet.filterNativeFriendly(activeParticipant, potentialTargets);
		for (RealmComponent rc : potentialTargets) {
			if ((rc.isMonster() || rc.getGameObject().hasThisAttribute(Constants.BEAST)) && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell)
					&& (!onlyNonMaximumTargets || !(((MonsterChitComponent)rc).isMaximumWeight()))
					&& (!onlyNonFlyingTargets || ((MonsterChitComponent)rc).getFlySpeed()==null)) {
				gameObjects.add(rc.getGameObject());
			}
			else if ((rc.isHorse() || rc.isNativeHorse()) && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell)) {
				if ((!onlyNonMaximumTargets || !rc.getWeight().isMaximum())
						&& (!onlyNonFlyingTargets || 
							((rc.isHorse() && ((SteedChitComponent)rc).getFlySpeed()==null)
							|| (rc.isNativeHorse() && ((NativeSteedChitComponent)rc).getFlySpeed()==null)))) {
					gameObjects.add(rc.getGameObject());
				}
			}
			else if (rc.isCharacter()) {
				CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
				for (GameObject item : character.getInventory()) {
					RealmComponent itemRc = (RealmComponent.getRealmComponent(item));
					if (itemRc.isHorse() && !itemRc.hasMagicProtection() && !itemRc.hasMagicColorImmunity(spell)) {
						if ((!onlyNonMaximumTargets || !rc.getWeight().isMaximum())
							&& (!onlyNonFlyingTargets || ((SteedChitComponent)itemRc).getFlySpeed()==null)) {
							gameObjects.add(item);
						}
					}
					else if (item.hasThisAttribute(Constants.HOUND) && !itemRc.hasMagicProtection() && !itemRc.hasMagicColorImmunity(spell)) {
						if ((!onlyNonMaximumTargets || !itemRc.getWeight().isMaximum()) && (!onlyNonFlyingTargets || ((MonsterChitComponent)itemRc).getFlySpeed()==null)) {
							gameObjects.add(item);
						}
					}
				}
				if (character.isTransformedBeast() && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell)) {
					if ((!onlyNonMaximumTargets || !rc.getWeight().isMaximum()) && (!onlyNonFlyingTargets || ((SteedChitComponent)rc).getFlySpeed()==null)) {
						gameObjects.add(character.getTransmorph());
					}
				}
			}
		}
		return true;
	}
}