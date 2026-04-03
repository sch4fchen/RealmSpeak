package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingActiveWeaponType extends SpellTargetingMyItem {
	private String keyword;
	public SpellTargetingActiveWeaponType(CombatFrame combatFrame, SpellWrapper spell,String keyword,boolean includeInactive) {
		super(combatFrame, spell,true,includeInactive);
		this.keyword = keyword;
	}
	public boolean isAddable(RealmComponent item) {
		return (item.isWeapon() || (item.isTreasure() && item.getGameObject().hasThisAttribute(RealmComponent.WEAPON) && !spell.getGameObject().hasThisAttribute(TARGETS_NO_TREASURE_WEAPONS))) && item.getGameObject().getName().toLowerCase().indexOf(keyword)>=0;
	}
}