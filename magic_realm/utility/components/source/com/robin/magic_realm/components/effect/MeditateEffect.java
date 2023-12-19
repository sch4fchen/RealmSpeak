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
		if(context.Target.getGameObject().hasThisAttribute(Constants.MEDITATE_DISCOVER_SITES)){
			context.Target.getGameObject().removeThisAttribute(Constants.MEDITATE_DISCOVER_SITES);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.MEDITATE_NO_BLOCKING)){
			context.Target.getGameObject().removeThisAttribute(Constants.MEDITATE_NO_BLOCKING);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.MEDITATE_USE_AND_DISCOVER_PATHS_AND_PASSAGES)){
			context.Target.getGameObject().removeThisAttribute(Constants.MEDITATE_USE_AND_DISCOVER_PATHS_AND_PASSAGES);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.MEDITATE_EXTRA_PHASE)){
			context.Target.getGameObject().removeThisAttribute(Constants.MEDITATE_EXTRA_PHASE);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.MEDITATE_IMPROVED_ENCHANTING)){
			context.Target.getGameObject().removeThisAttribute(Constants.MEDITATE_IMPROVED_ENCHANTING);
		}
	}
}
