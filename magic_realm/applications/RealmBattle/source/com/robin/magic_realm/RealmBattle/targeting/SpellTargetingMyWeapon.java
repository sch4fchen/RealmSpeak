package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingMyWeapon extends SpellTargetingMyItem {
	
	public SpellTargetingMyWeapon(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell,false,true);
	}
	public boolean isAddable(RealmComponent item) {
		return item.isWeapon();
	}
}