package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class SleepEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		CombatWrapper tile = context.getCombatTarget();
		String clearing = context.Spell.getExtraIdentifier();
		tile.addSleepClearing(Integer.parseInt(clearing));
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
