package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.TreasureUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MagicShieldEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		CharacterWrapper character = new CharacterWrapper(context.Target.getGameObject());
		GameObject spell = context.Spell.getGameObject();
		
		GameObject magicShield = spell.getGameData().createNewObject();
		magicShield.copyAttributeBlockFrom(spell,Constants.MAGIC_SHIELD);
		magicShield.renameAttributeBlock(Constants.MAGIC_SHIELD,"this");
		String name = magicShield.getThisAttribute("name");
		if (name!=null) {
			magicShield.setName(name);
		}
		else {
			magicShield.setName("Magic Shield");
		}
		magicShield.setThisAttribute(Constants.SPELL_ID, spell.getStringId());
		magicShield.setThisAttribute("item");
		magicShield.setThisAttribute(Constants.SHIELD);
		magicShield.setThisAttribute("armor");
		magicShield.setThisAttribute("armor_choice");
		magicShield.setAttribute("intact", "chit_color", "blue");
		magicShield.setAttribute("intact", "base_price", "0");
		magicShield.setAttribute("damaged", "chit_color", "lightblue");
		magicShield.setAttribute("damaged", "base_price", "0");
		
		spell.setThisAttribute(Constants.MAGIC_SHIELD_ID,magicShield.getStringId());
		character.getGameObject().add(magicShield);
		
		TreasureUtility.doActivate(context.Parent, character, magicShield, null, false);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		if (context.Spell.getGameObject().hasThisAttribute(Constants.MAGIC_SHIELD_ID)) {
			GameObject magicShield = context.Spell
					.getGameObject()
					.getGameData()
					.getGameObject(Long.valueOf(context.Spell.getGameObject().getThisAttribute(Constants.MAGIC_SHIELD_ID)));
			magicShield.getHeldBy().remove(magicShield);
			context.Spell.getGameObject().removeThisAttribute(Constants.MAGIC_SHIELD_ID);
		}
	}

}
