package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class ControlHorseEffect implements ISpellEffect {
	
	@Override
	public void apply(SpellEffectContext context) {
		context.Spell.setExtraIdentifier(context.Target.getHeldBy().getGameObject().getStringId());
		context.Target.getGameObject().setThisAttribute(Constants.ACTIVATED);
		context.Target.getGameObject().setThisAttribute(Constants.BREAK_CONTROL_WHEN_INACTIVE);
		context.Target.getGameObject().setThisAttribute(Constants.NOT_DEACTIVATABLE);
		context.Target.getGameObject().setThisAttribute(Constants.NOT_TRADABLE);
		CombatWrapper combat = new CombatWrapper(context.Target.getGameObject());
		combat.setHorseCannotManeuver(true);
		context.Caster.add(context.Target.getGameObject());
	}

	@Override
	public void unapply(SpellEffectContext context) {
		context.Target.getGameObject().removeThisAttribute(Constants.ACTIVATED);
		context.Target.getGameObject().removeThisAttribute(Constants.BREAK_CONTROL_WHEN_INACTIVE);
		context.Target.getGameObject().removeThisAttribute(Constants.NOT_DEACTIVATABLE);
		context.Target.getGameObject().removeThisAttribute(Constants.NOT_TRADABLE);
		GameObject formerOwner = context.getGameData().getGameObject(context.Spell.getExtraIdentifier());
		formerOwner.add(context.Target.getGameObject());
	}

}
