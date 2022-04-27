package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.SpellUtility;

public class PhaseChitEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		SpellUtility.createPhaseChit(context.Target,context.Spell.getGameObject());
	}

	@Override
	public void unapply(SpellEffectContext context) {
		// A Phase spell.  Ditch the phase chit.
		if (context.Spell.getGameObject().hasThisAttribute(Constants.PHASE_CHIT_ID)) {
			GameObject phaseChit = context.Spell
					.getGameObject()
					.getGameData()
					.getGameObject(Long.valueOf(context.Spell.getGameObject().getThisAttribute(Constants.PHASE_CHIT_ID)));
			context.getCharacterTarget().getGameObject().remove(phaseChit);
			context.Spell.getGameObject().removeThisAttribute(Constants.PHASE_CHIT_ID);
		}
	}
}
