package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.TreasureUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MagicWeaponEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		CharacterWrapper character = new CharacterWrapper(context.Target.getGameObject());
		GameObject spell = context.Spell.getGameObject();
		
		GameObject magicWeapon = spell.getGameData().createNewObject();
		magicWeapon.setName("Magic Shield");
		magicWeapon.copyAttributeBlockFrom(spell,Constants.MAGIC_WEAPON);
		magicWeapon.renameAttributeBlock(Constants.MAGIC_WEAPON,"this");
		String name = magicWeapon.getThisAttribute("name");
		if (name!=null) {
			magicWeapon.setName(name);
		}
		else {
			magicWeapon.setName("Magic Weapon");
		}
		magicWeapon.setThisAttribute(Constants.SPELL_ID, spell.getStringId());
		magicWeapon.setThisAttribute("item");
		magicWeapon.setThisAttribute("weapon");
		magicWeapon.setAttribute("unalerted", "chit_color", "lightblue");
		magicWeapon.setAttribute("unalerted", "base_price", "0");
		magicWeapon.setAttribute("alerted", "chit_color", "blue");
		magicWeapon.setAttribute("alerted", "base_price", "0");
		
		String attackSpeed = magicWeapon.getThisAttribute("attack_speed");
		if (attackSpeed != null) {
			magicWeapon.setAttribute("unalerted", "attack_speed", attackSpeed);
			magicWeapon.setAttribute("alerted", "attack_speed", attackSpeed);
		}
		String strength = magicWeapon.getThisAttribute("strength");
		String sharpness = magicWeapon.getThisAttribute("sharpness");
		magicWeapon.setAttribute("unalerted", "strength", strength);
		magicWeapon.setAttribute("unalerted", "sharpness", sharpness);
		magicWeapon.setAttribute("alerted", "strength", strength);
		magicWeapon.setAttribute("alerted", "sharpness", sharpness);
		
		spell.setThisAttribute(Constants.MAGIC_WEAPON_ID,magicWeapon.getStringId());
		character.getGameObject().add(magicWeapon);
		
		TreasureUtility.doActivate(context.Parent, character, magicWeapon, null, false);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		if (context.Spell.getGameObject().hasThisAttribute(Constants.MAGIC_WEAPON_ID)) {
			GameObject magicWeapon = context.Spell
					.getGameObject()
					.getGameData()
					.getGameObject(Long.valueOf(context.Spell.getGameObject().getThisAttribute(Constants.MAGIC_WEAPON_ID)));
			magicWeapon.getHeldBy().remove(magicWeapon);
			context.Spell.getGameObject().removeThisAttribute(Constants.MAGIC_WEAPON_ID);
		}
	}

}
