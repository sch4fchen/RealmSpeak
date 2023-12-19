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
		if(context.Target.getGameObject().hasThisAttribute(Constants.ALTER_SIZE)){
			context.Target.getGameObject().removeThisAttribute(Constants.ALTER_SIZE);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_VULNERABILITY)){
			context.Target.getGameObject().removeThisAttribute(Constants.ALTER_SIZE_INCREASED_VULNERABILITY);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_VULNERABILITY)){
			context.Target.getGameObject().removeThisAttribute(Constants.ALTER_SIZE_DECREASED_VULNERABILITY);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_WEIGHT)){
			context.Target.getGameObject().removeThisAttribute(Constants.ALTER_SIZE_INCREASED_WEIGHT);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_WEIGHT)){
			context.Target.getGameObject().removeThisAttribute(Constants.ALTER_SIZE_DECREASED_WEIGHT);
		}
	}
}
