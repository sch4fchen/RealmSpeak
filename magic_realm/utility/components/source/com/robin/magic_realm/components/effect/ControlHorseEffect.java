package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.utility.Constants;

public class ControlHorseEffect implements ISpellEffect {
	
	@Override
	public void apply(SpellEffectContext context) {
		context.Target.getGameObject().setThisAttribute(Constants.ACTIVATED);
		context.Caster.add(context.Target.getGameObject());
	}

	@Override
	public void unapply(SpellEffectContext context) {
		
	}

}
