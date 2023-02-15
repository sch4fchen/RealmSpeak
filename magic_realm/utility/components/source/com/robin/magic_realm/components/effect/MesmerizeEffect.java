package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.table.Mesmerize;

public class MesmerizeEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		int d = context.Spell.getRedDieLock();
		Mesmerize.doNow(context.Parent,context.Spell.getCaster().getGameObject(),context.Target.getGameObject(),true,d);
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
