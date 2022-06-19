package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.ClearingDetail;

public class LightEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		ClearingDetail clearing = context.getClearingTarget();
		clearing.setLighted(true);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		ClearingDetail clearing = context.getClearingTarget();
		clearing.setLighted(false);
	}
	
}
