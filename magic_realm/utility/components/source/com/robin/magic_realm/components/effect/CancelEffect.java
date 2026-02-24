package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class CancelEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.isCharacter() && !context.Target.getGameObject().hasThisAttribute(Constants.MAGIC_PROTECTION_EXTENDED)) {
			String curse = context.Spell.getExtraIdentifier();
			context.getCharacterTarget().removeCurse(curse);
		}
		else if (context.Target.isTreasureLocation()) {
			for (GameObject held : context.Target.getHold()) {
				if (held.hasThisAttribute(RealmComponent.SPELL)) {
					SpellWrapper spellWrapper = new SpellWrapper(held);
					if (spellWrapper.isAlive() && spellWrapper.getGameObject().hasThisAttribute(Constants.FREED_SPELL)) {
						spellWrapper.expireSpell();
					}
				}
			}
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
