package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.utility.SpellUtility;

public class EnchantWeaponEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)) {
			context.Spell.cancelSpell();
			RealmLogging.logMessage(context.Spell.getName(),"Spell canceled, item already affected by Enchant Weapon spell.");
		}
		SpellUtility.ApplyNamedSpellEffectToTarget(Constants.ENCHANTED_WEAPON, context.Target.getGameObject(), context.Spell);
	}
	

	@Override
	public void unapply(SpellEffectContext context) {
		if(context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)){
			context.Target.getGameObject().removeThisAttribute(Constants.ENCHANTED_WEAPON);
		}
	}

}
