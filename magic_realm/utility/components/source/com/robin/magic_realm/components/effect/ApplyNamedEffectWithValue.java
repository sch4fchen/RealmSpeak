package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.utility.SpellUtility;

public class ApplyNamedEffectWithValue implements ISpellEffect {
	String _effectName;
	
	public ApplyNamedEffectWithValue(String effectName){
		_effectName = effectName;
	}
	
	@Override
	public void apply(SpellEffectContext context) {
		SpellUtility.ApplyNamedSpellEffectWithValueToTarget(_effectName, context.Target.getGameObject(), context.Spell, context.Spell.getGameObject().getThisAttribute(_effectName));
	}

	@Override
	public void unapply(SpellEffectContext context) {
		if(context.Target.getGameObject().hasThisAttribute(_effectName)){
			context.Target.getGameObject().removeThisAttribute(_effectName);
		}
	}

}
