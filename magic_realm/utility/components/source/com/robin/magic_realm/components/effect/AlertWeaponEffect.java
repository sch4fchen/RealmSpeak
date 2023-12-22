package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.WeaponChitComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class AlertWeaponEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)) {
			context.Spell.cancelSpell();
			RealmLogging.logMessage(context.Spell.getName(),"Spell canceled, item already affected by Enchant Weapon spell.");
		}
		WeaponChitComponent weapon = (WeaponChitComponent)context.Target;
		if (!weapon.isAlerted()) {
			weapon.setAlerted(true);
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
