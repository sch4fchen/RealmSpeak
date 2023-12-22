package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.ArmorChitComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class RepairEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)) {
			RealmLogging.logMessage(context.Spell.getName(),"Spell canceled, item already affected by Enchant Weapon spell.");
			return;
		}
		ArmorChitComponent armor = (ArmorChitComponent)context.Target;
		armor.setIntact(true);
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
