package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class PhaseChitEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		GameObject spell = context.Spell.getGameObject();
		CharacterWrapper character = new CharacterWrapper(context.Target.getGameObject());
		GameObject phaseChit = spell.getGameData().createNewObject();
		
		phaseChit.setName(spell.getName()+" Phase Chit ("+character.getGameObject().getName()+")");
		phaseChit.copyAttributeBlockFrom(spell,Constants.PHASE_CHIT);
		phaseChit.renameAttributeBlock(Constants.PHASE_CHIT,"this");
		phaseChit.copyAttributeBlockFrom(spell,Constants.PHASE_CHIT_EFFECTS);
		phaseChit.renameAttributeBlock(Constants.PHASE_CHIT_EFFECTS,Constants.EFFECTS);
		phaseChit.setThisAttribute(Constants.SPELL_ID, spell.getStringId());
		spell.setThisAttribute(Constants.PHASE_CHIT_ID,phaseChit.getStringId());
		character.getGameObject().add(phaseChit);
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
