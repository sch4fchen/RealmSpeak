package com.robin.magic_realm.components.effect;

import com.robin.general.util.OrderedHashtable;
import com.robin.magic_realm.components.quest.QuestMinorCharacter;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.utility.SpellUtility;

public class EnchantWeaponEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)) {
			context.Spell.cancelSpell();
			RealmLogging.logMessage(context.Spell.getName(),"Spell canceled, item already affected by Enchant Weapon spell.");
		}
		if (SpellUtility.ApplyNamedSpellEffectToTargetAndReturn(Constants.ENCHANTED_WEAPON, context.Target.getGameObject(), context.Spell)) {
			SpellUtility.ApplyNamedSpellEffectToTarget(Constants.ENCHANTED_ALERTED_WEAPON, context.Target.getGameObject(), context.Spell);
			OrderedHashtable<String, Object> stats = context.Spell.getGameObject().getAttributeBlock(Constants.ENCAHNTED_WEAPON_STATS);
			if (stats.get("length")!=null) {
				SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ENCHANTED_WEAPON_LENGTH, context.Target.getGameObject(), context.Spell, stats.get("length").toString());
			}
			if (stats.get("strength")!=null) {
				SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ENCHANTED_WEAPON_STRENGTH, context.Target.getGameObject(), context.Spell, stats.get("strength").toString());
			}
			if (stats.get("sharpness")!=null) {
				SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ENCHANTED_WEAPON_SHARPNESS, context.Target.getGameObject(), context.Spell, stats.get("sharpness").toString());
			}
			if (stats.get("speed")!=null) {
				SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ENCHANTED_WEAPON_SPEED, context.Target.getGameObject(), context.Spell, stats.get("speed").toString());
			}
		}
	}
	

	@Override
	public void unapply(SpellEffectContext context) {
		if(context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)){
			context.Target.getGameObject().removeThisAttribute(Constants.ENCHANTED_WEAPON);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_ALERTED_WEAPON)){
			context.Target.getGameObject().removeThisAttribute(Constants.ENCHANTED_ALERTED_WEAPON);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON_LENGTH)){
			context.Target.getGameObject().removeThisAttribute(Constants.ENCHANTED_WEAPON_LENGTH);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON_STRENGTH)){
			context.Target.getGameObject().removeThisAttribute(Constants.ENCHANTED_WEAPON_STRENGTH);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON_SHARPNESS)){
			context.Target.getGameObject().removeThisAttribute(Constants.ENCHANTED_WEAPON_SHARPNESS);
		}
		if(context.Target.getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON_SPEED)){
			context.Target.getGameObject().removeThisAttribute(Constants.ENCHANTED_WEAPON_SPEED);
		}
	}

}
