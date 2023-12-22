package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MakeWholeEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)) {
			RealmLogging.logMessage(context.Spell.getName(),"Spell canceled, item already affected by Enchant Weapon spell.");
			return;
		}
		CharacterWrapper character = context.getCharacterTarget();
		SpellUtility.heal(character);
		SpellUtility.repair(character);
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
