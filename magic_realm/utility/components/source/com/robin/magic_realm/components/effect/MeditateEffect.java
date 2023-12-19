package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.table.Meditate;
import com.robin.magic_realm.components.utility.Constants;

public class MeditateEffect implements ISpellEffect {
	
	@Override
	public void apply(SpellEffectContext context) {
		int redDie = context.Spell.getRedDieLock();
		Meditate.doNow(context.Parent,context.Spell.getCaster().getGameObject(),context.Target.getGameObject(),redDie,context.Spell);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		if(context.Target.getGameObject().hasThisAttribute(Constants.MEDITATE)){
			context.Target.getGameObject().removeThisAttribute(Constants.MEDITATE);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.MEDITATE_EXTRA_PHASE)){
			context.Target.getGameObject().removeThisAttribute(Constants.MEDITATE_EXTRA_PHASE);
		}
	}
}
