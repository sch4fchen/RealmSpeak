package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class AddSharpnessEffect implements ISpellEffect {
	
	private int magnatude;

	public AddSharpnessEffect(int mag){
		magnatude = mag;
	}

	
	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)) {
			context.Spell.cancelSpell();
			RealmLogging.logMessage(context.Spell.getName(),"Spell canceled, item already affected by Enchant Weapon spell.");
		}
		int val = context.Target.getGameObject().getThisInt(Constants.ADD_SHARPNESS) + magnatude;
		context.Target.getGameObject().setThisAttribute(Constants.ADD_SHARPNESS,val);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		// Decrement sharpness by one
		int val = context.Target.getGameObject().getThisInt(Constants.ADD_SHARPNESS) - magnatude;
		
		if (val==0) {
			context.Target.getGameObject().removeThisAttribute(Constants.ADD_SHARPNESS);
		}
		else {
			context.Target.getGameObject().setThisAttribute(Constants.ADD_SHARPNESS,val);
		}
	}

}
