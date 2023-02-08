package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingMyItem extends SpellTargetingSingle {
	
	private boolean active;
	private boolean inactive;
	
	public SpellTargetingMyItem(CombatFrame combatFrame, SpellWrapper spell,boolean active,boolean inactive) {
		super(combatFrame, spell);
		this.active = active;
		this.inactive = inactive;
	}
	
	public boolean isAddable(RealmComponent item) {
		return item.isWeapon() || item.isArmor() || item.isTreasure();
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		CharacterWrapper caster = spell.getCaster();
		ArrayList<GameObject> inv = new ArrayList<>();
		if (active) {
			inv.addAll(caster.getActiveInventory());
		}
		if (inactive) {
			inv.addAll(caster.getInactiveInventory());
		}
		for (GameObject go:inv) {
			RealmComponent itemRc = RealmComponent.getRealmComponent(go);
			if (isAddable(itemRc)) {
				gameObjects.add(go);
			}
		}
		return true;
	}
}