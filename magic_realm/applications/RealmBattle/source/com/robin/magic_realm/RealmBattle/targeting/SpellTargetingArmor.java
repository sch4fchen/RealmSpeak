package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingArmor extends SpellTargetingItem {
	
	public SpellTargetingArmor(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell,true,true);
	}
	public boolean isAddable(RealmComponent item) {
		return item.isArmor();
	}
}