package com.robin.magic_realm.components.effect;

import java.util.ArrayList;

import com.robin.magic_realm.components.RealmComponent;

public class RunAwayEffect implements ISpellEffect {
	
	@Override
	public void apply(SpellEffectContext context) {
		ArrayList<RealmComponent> targets = context.Spell.getTargets();

	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
