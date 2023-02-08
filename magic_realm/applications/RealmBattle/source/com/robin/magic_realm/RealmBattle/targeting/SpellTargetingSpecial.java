package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public abstract class SpellTargetingSpecial extends SpellTargeting {

	public SpellTargetingSpecial(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean assign(HostPrefWrapper hostPrefs,CharacterWrapper activeCharacter) {
		// Does nothing here, because all assignment is done during populate
		return true;
	}
	public boolean hasTargets() {
		return true; // always true
	}
}