package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.table.AlterObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class AlterObjectEffect implements ISpellEffect {
	
	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)) {
			context.Spell.cancelSpell();
			RealmLogging.logMessage(context.Spell.getName(),"Spell canceled, item already affected by Enchant Weapon spell.");
			return;
		}
		int redDie = context.Spell.getRedDieLock();
		boolean success = AlterObject.doNow(context.Parent,context.Spell.getCaster().getGameObject(),context.Target.getGameObject(),redDie,context.Spell);
		if (!success) {
			context.Spell.cancelSpell();
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
		if(context.Target.getGameObject().hasThisAttribute(Constants.ALTER_WEIGHT)){
			context.Target.getGameObject().removeThisAttribute(Constants.ALTER_WEIGHT);
		}
	}
}
