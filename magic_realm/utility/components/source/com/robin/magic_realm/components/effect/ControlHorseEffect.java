package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class ControlHorseEffect implements ISpellEffect {
	
	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.getGameObject().hasThisAttribute(Constants.CONTROLLED_HORSE)) {
			context.Spell.cancelSpell();
			RealmLogging.logMessage(context.Spell.getCaster().getGameObject().getName(),"Spell cancelled, because the targeted horse is already controlled.");
			return;
		}
		context.Spell.setExtraIdentifier(context.Target.getHeldBy().getGameObject().getStringId());
		context.Target.getGameObject().setThisAttribute(Constants.ACTIVATED);
		context.Target.getGameObject().setThisAttribute(Constants.BREAK_CONTROL_WHEN_INACTIVE);
		context.Target.getGameObject().setThisAttribute(Constants.CONTROLLED_HORSE);
		CombatWrapper combat = new CombatWrapper(context.Target.getGameObject());
		combat.setHorseCannotManeuver(true);
		context.Caster.add(context.Target.getGameObject());
	}

	@Override
	public void unapply(SpellEffectContext context) {
		context.Target.getGameObject().removeThisAttribute(Constants.ACTIVATED);
		context.Target.getGameObject().removeThisAttribute(Constants.BREAK_CONTROL_WHEN_INACTIVE);
		context.Target.getGameObject().removeThisAttribute(Constants.CONTROLLED_HORSE);
		GameObject formerOwner = context.getGameData().getGameObject(context.Spell.getExtraIdentifier());
		RealmComponent formerOwnerRc = RealmComponent.getRealmComponent(formerOwner);
		RealmComponent horse = context.Target;
		if (context.Caster.hasThisAttribute(Constants.DEAD)) {
			if (horse.isHorse()) {
				CombatWrapper combat = new CombatWrapper(context.Caster);
				GameObject killedBy = combat.getKilledBy();
				RealmComponent killer = RealmComponent.getRealmComponent(killedBy);
				if (killer.isCharacter()) {
					killedBy.add(horse.getGameObject());
				}
				else {
					if (killer.isHiredOrControlled() && killer.getOwner()!=null && killer.getOwner().isCharacter()) {
						killer.getOwner().add(horse);
					} else {
						SetupCardUtility.resetDenizen(horse.getGameObject());
					}
				}
			}
			else if (horse.isNativeHorse()) {
				if (formerOwnerRc.getCurrentLocation().equals(horse.getCurrentLocation())) {
					formerOwner.add(horse.getGameObject());
				}
				else {
					if (!horse.getGameObject().hasThisAttribute(RealmComponent.MONSTER_STEED) && formerOwnerRc.isNative() && horse.getCurrentLocation()!=null && horse.getCurrentLocation().clearing!=null) {
						horse.getCurrentLocation().clearing.add(horse.getGameObject(),null);
					}
					else if (horse.getGameObject().hasThisAttribute(RealmComponent.MONSTER_STEED) ) {
						formerOwnerRc.add(horse);
					}
					else {
						SetupCardUtility.resetDenizen(horse.getGameObject());
					}
				}
			}
			else {
				if (horse.getCurrentLocation()!=null && horse.getCurrentLocation().clearing!=null) {
					horse.getCurrentLocation().clearing.add(horse.getGameObject(),null);
				} else {
					SetupCardUtility.resetDenizen(horse.getGameObject());
				}
			}
		} else {
			if (horse.getCurrentLocation()!=null && horse.getCurrentLocation().clearing!=null) {
				horse.getCurrentLocation().clearing.add(horse.getGameObject(),null);
			}
			else if (horse.getGameObject().hasThisAttribute(RealmComponent.MONSTER_STEED) ) {
				formerOwnerRc.add(horse);
			}
			else {
				SetupCardUtility.resetDenizen(horse.getGameObject());
			}
		}
	}

}
