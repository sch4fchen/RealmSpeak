package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.utility.Constants;

public class PhaseChitEffectFactory {	
	public static ISpellEffect[] create(String effect){
		switch(effect.toLowerCase()){
			case "nullify":return new ISpellEffect[]{new NullifyEffect()};
			case "magic_protection": return new ISpellEffect[]{new ApplyNamedEffect(Constants.MAGIC_PROTECTION)};
			case "magic_protection_extended": return new ISpellEffect[]{new ApplyNamedEffect(Constants.MAGIC_PROTECTION_EXTENDED)};
			case "blinding_light": return new ISpellEffect[]{new ApplyNamedEffect(Constants.BLINDING_LIGHT),new RemovePotentialBlocksEffect()};
			case "holy_shield": return new ISpellEffect[]{new ApplyNamedEffect(Constants.HOLY_SHIELD)};
			case "reserve": return new ISpellEffect[]{new MagicChitEffect()};
			case "dark_favor": return new ISpellEffect[]{new DarkFavorEffect(), new ApplyNamedEffect(Constants.DARK_FAVOR)};
			case "dazzle": return new ISpellEffect[]{new ApplyNamedEffect(Constants.DAZZLE)};
			case "luck": return new ISpellEffect[]{new ApplyNamedEffect(Constants.LUCK)};
			
			default: return null;
		}
	}
}