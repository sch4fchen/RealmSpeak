package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class CancelEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.isCharacter() && !context.Target.getGameObject().hasThisAttribute(Constants.MAGIC_PROTECTION_EXTENDED)) {
			String curse = context.Spell.getExtraIdentifier();
			context.getCharacterTarget().removeCurse(curse);
		}
		else {
			// Target is a spell
			SpellWrapper spell = new SpellWrapper(context.Target.getGameObject());
			if (spell.getAffectedTarget()==null || !spell.getAffectedTarget().getGameObject().hasThisAttribute(Constants.MAGIC_PROTECTION_EXTENDED)) {
				spell.expireSpell();
			}
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
