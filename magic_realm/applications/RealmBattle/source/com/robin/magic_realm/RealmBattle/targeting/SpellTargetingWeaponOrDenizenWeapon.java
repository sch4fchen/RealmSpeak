package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.*;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingWeaponOrDenizenWeapon extends SpellTargetingSingle {

	protected SpellTargetingWeaponOrDenizenWeapon(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		for (RealmComponent participant : combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true)) {
			if (participant.isCharacter()) {
				CharacterWrapper character = new CharacterWrapper(participant.getGameObject());
				if (character.isMistLike()) continue;
				for (GameObject go:character.getActiveInventory()) {
					RealmComponent itemRc = RealmComponent.getRealmComponent(go);
					if (itemRc.isWeapon()) {
						gameObjects.add(go);
					}
				}
			} else if (participant.isMonster() || participant.isNative()) {
				if (participant.getGameObject().hasThisAttribute(Constants.WEAPON_USE) && !participant.getGameObject().hasThisAttribute(Constants.NO_WEAPON_USAGE)) {
					gameObjects.add(participant.getGameObject());
				}
				for (GameObject held : participant.getHold()) {
					if (held.hasThisAttribute(Constants.MONSTER_WEAPON)
							|| held.hasThisAttribute(Constants.GIANT_CLUB)
							|| held.hasThisAttribute(Constants.GIANT_AXE)) {
						gameObjects.add(held);
					}
				}
			}
		}
		return true;
	}
}