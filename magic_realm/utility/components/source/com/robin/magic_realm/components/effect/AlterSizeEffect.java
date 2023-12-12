package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.table.AlterSize;
import com.robin.magic_realm.components.utility.Constants;

public class AlterSizeEffect implements ISpellEffect {
	
	@Override
	public void apply(SpellEffectContext context) {
		int redDie = context.Spell.getRedDieLock();
		AlterSize.doNow(context.Parent,context.Spell.getCaster().getGameObject(),context.Target.getGameObject(),redDie,context.Spell);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		if(context.Target.getGameObject().hasThisAttribute(Constants.ALTER_SIZE)){
			context.Target.getGameObject().removeThisAttribute(Constants.ALTER_SIZE);
		}
	}
}
