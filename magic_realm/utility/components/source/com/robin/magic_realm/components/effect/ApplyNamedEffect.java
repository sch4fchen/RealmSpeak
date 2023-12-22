package com.robin.magic_realm.components.effect;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.utility.SpellUtility;

public class ApplyNamedEffect implements ISpellEffect {
	String _effectName;
	
	public ApplyNamedEffect(String effectName){
		_effectName = effectName;
	}
	
	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)) {
			context.Spell.cancelSpell();
			RealmLogging.logMessage(context.Spell.getName(),"Spell canceled, item already affected by Enchant Weapon spell.");
		}
		SpellUtility.ApplyNamedSpellEffectToTarget(_effectName, context.Target.getGameObject(), context.Spell);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		if(context.Target.getGameObject().hasThisAttribute(_effectName)){
			context.Target.getGameObject().removeThisAttribute(_effectName);
		}
	}

}
