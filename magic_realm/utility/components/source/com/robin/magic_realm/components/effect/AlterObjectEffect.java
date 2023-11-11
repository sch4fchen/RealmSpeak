package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.table.AlterObject;
import com.robin.magic_realm.components.utility.Constants;

public class AlterObjectEffect implements ISpellEffect {
	
	@Override
	public void apply(SpellEffectContext context) {
		int redDie = context.Spell.getRedDieLock();
		AlterObject.doNow(context.Parent,context.Spell.getCaster().getGameObject(),context.Target.getGameObject(),redDie,context.Spell);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		if(context.Target.getGameObject().hasThisAttribute(Constants.ALTER_WEIGHT)){
			context.Target.getGameObject().removeThisAttribute(Constants.ALTER_WEIGHT);
		}
	}
}
