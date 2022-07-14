package com.robin.magic_realm.components.effect;

public class FinalChitHarmEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		context.Spell.getCaster().updateChitEffects();
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
