package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.table.SummonDemon;

public class SummonDemonEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		int d = context.Spell.getRedDieLock();
		SummonDemon.doNow(context.Parent,context.Spell.getCaster().getGameObject(),context.Target.getGameObject(),true,d,context.Spell.getAttackSpeed());
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
