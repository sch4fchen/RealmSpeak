package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingStaff extends SpellTargetingItem {
	
	public SpellTargetingStaff(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell,true,false);
	}
	public boolean isAddable(RealmComponent item) {
		return item.isWeapon() && item.getGameObject().getName().startsWith("Staff");
	}
}