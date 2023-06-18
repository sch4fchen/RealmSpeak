package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.utility.Constants;

public class MagicChitEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		GameObject spellObj = context.Spell.getGameObject();
		
		GameObject magicChit = spellObj.getGameData().createNewObject();
		magicChit.setName(spellObj.getName()+" Magic Chit ("+ context.Caster.getName()+")");
		magicChit.copyAttributeBlockFrom(spellObj,"magic_chit");
		magicChit.renameAttributeBlock("magic_chit","this");
		magicChit.setThisAttribute("spellID",spellObj.getStringId());
		magicChit.setThisAttribute("sourceSpell",spellObj.getName());
		magicChit.setThisAttribute(RealmComponent.CHARACTER_CHIT);
		magicChit.setThisAttribute(CharacterActionChitComponent.ACTION_CHIT_STATE_KEY,CharacterActionChitComponent.ACTIVE_ID);
		magicChit.setThisAttribute(Constants.CHIT_EARNED);
		magicChit.setThisAttribute("action","magic");
		spellObj.setThisAttribute("magicChitID",magicChit.getStringId());		
		context.Target.getGameObject().add(magicChit);
		
		if (!context.Target.getGameObject().equals(context.Caster)) {
			RealmComponent magicChitRc = RealmComponent.getRealmComponent(magicChit);
			magicChitRc.setOwner(RealmComponent.getRealmComponent(context.Caster));
		}
		
		if (spellObj.hasThisAttribute(Constants.RESERVE)) {
			CharacterActionChitComponent magicChitRc = (CharacterActionChitComponent)RealmComponent.getRealmComponent(magicChit);
			String colorString = context.Spell.getExtraIdentifier();
			ColorMagic colorMagic = ColorMagic.makeColorMagic(colorString,false);
			String color = ColorMagic.getColorNumber(colorMagic.getColorNumber());
			magicChit.setThisAttribute("magic",color);
			magicChitRc.enchant(colorMagic.getColorNumber());
			magicChit.setThisAttribute(Constants.BREAK_WHEN_USED);
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
		GameObject spellObj = context.Spell.getGameObject();
		
		String chitId = spellObj.getThisAttribute("magicChitID");
		GameObject magicChit =spellObj.getGameData().getGameObject(Long.valueOf(chitId));
		context.Target.getGameObject().remove(magicChit);
		spellObj.removeThisAttribute("magicChitID");
	}
}
