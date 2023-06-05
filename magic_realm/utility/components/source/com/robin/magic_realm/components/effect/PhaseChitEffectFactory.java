package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.utility.Constants;

public class PhaseChitEffectFactory {	
	public static ISpellEffect[] create(String effect){
		switch(effect.toLowerCase()){
			case "nullify":return new ISpellEffect[]{new NullifyEffect()};
			case "magic_protection": return new ISpellEffect[]{new ApplyNamedEffect(Constants.MAGIC_PROTECTION)};
			case "blinding_light": return new ISpellEffect[]{new ApplyNamedEffect(Constants.BLINDING_LIGHT)};
			case "holy_shield": return new ISpellEffect[]{new ApplyNamedEffect(Constants.HOLY_SHIELD)};
			
			default: return null;
		}
	}
}