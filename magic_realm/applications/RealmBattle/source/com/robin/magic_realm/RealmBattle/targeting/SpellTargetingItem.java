package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.StateChitComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingItem extends SpellTargetingSingle {
	
	private boolean active;
	private boolean inactive;
	
	public SpellTargetingItem(CombatFrame combatFrame, SpellWrapper spell,boolean active,boolean inactive) {
		super(combatFrame, spell);
		this.active = active;
		this.inactive = inactive;
	}
	
	public boolean isAddable(RealmComponent item) {
		return (item.isWeapon() || item.isArmor() || item.isTreasure() || item.getGameObject().hasThisAttribute(Constants.BROOMSTICK)) && !item.getGameObject().hasThisAttribute(Constants.HOUND);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		for (RealmComponent participant : combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true)) {
			ArrayList<GameObject> items = new ArrayList<>();
			if (participant.isCharacter()) {
				CharacterWrapper character = new CharacterWrapper(participant.getGameObject());
				if (character.isMistLike()) continue;
				if (active) {
					items.addAll(character.getActiveInventory());
				}
				if (inactive) {
					items.addAll(character.getInactiveInventory());
				}
				for (GameObject go:items) {
					RealmComponent itemRc = RealmComponent.getRealmComponent(go);
					if (isAddable(itemRc)) {
						gameObjects.add(go);
					}
				}
				for (StateChitComponent chit : character.getFlyChits()) {
					if (chit.getGameObject().hasThisAttribute(Constants.BROOMSTICK)) gameObjects.add(chit.getGameObject());
				}
			} else if (participant.isMonster() || participant.isNative()) {
				for (GameObject held : participant.getHold()) {
					if (held.hasThisAttribute(Constants.MONSTER_WEAPON)
							|| held.hasThisAttribute(Constants.SHIELD)
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