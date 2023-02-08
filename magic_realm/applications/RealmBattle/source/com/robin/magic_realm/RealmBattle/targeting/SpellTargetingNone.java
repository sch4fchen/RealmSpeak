package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.magic_realm.RealmBattle.*;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingNone extends SpellTargeting {
	
	public SpellTargetingNone(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		return true;
	}

	@Override
	public boolean assign(HostPrefWrapper hostPrefs, CharacterWrapper activeCharacter) {
		return true;
	}

	@Override
	public boolean hasTargets() {
		return true;
	}
}