package com.robin.magic_realm.components.effect;
import javax.swing.JFrame;

import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class ViolentWindsEffect implements ISpellEffect {	
	
	@Override
	public void apply(SpellEffectContext context) {
		ClearingDetail clearing = context.getClearingTarget();
		if(!clearing.isAffectedByViolentWinds()){
			clearing.setAffectedByViolentWinds(true);
		}
		else{
			context.Spell.cancelSpell();
			RealmLogging.logMessage(context.Spell.getCaster().getGameObject().getName(),"Spell cancelled, because the targeted clearing already affected by Violent Winds.");
			return;
		}
		
		for (RealmComponent rc : clearing.getTileLocation().tile.getRealmComponentsBetweenClearing(clearing.getNum())) {
			if (!rc.isCharacter()) continue;
			CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
			if (character.getCurrentLocation().isFlying()) {
				character.moveToLocation(new JFrame(), clearing.getTileLocation());
			}
		}
		
		moveFlyingCharactersBackToClearings(clearing);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		ClearingDetail clearing = context.getClearingTarget();
		if(clearing.isAffectedByViolentWinds()){
			clearing.setAffectedByViolentWinds(false);
		}
	}

	private static void moveFlyingCharactersBackToClearings(ClearingDetail clearing) {
		for (RealmComponent rc : clearing.getTileLocation().tile.getRealmComponentsBetweenClearing(clearing.getNum())) {
			if (!rc.isCharacter()) continue;
			CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
			if (character.getRunAwayLastUsedChit().matches("FLY")) {
				character.moveToLocation(new JFrame(), clearing.getTileLocation());
			}
		}
	}	
}
