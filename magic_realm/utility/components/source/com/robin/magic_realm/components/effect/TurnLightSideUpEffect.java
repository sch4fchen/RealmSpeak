package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.ChitComponent;

public class TurnLightSideUpEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target instanceof ChitComponent) {
			((ChitComponent)context.Target).setLightSideUp();
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
