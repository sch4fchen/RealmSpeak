package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;

public class FigthChitEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		GameObject spellObj = context.Spell.getGameObject();
		
		GameObject fightChit = spellObj.getGameData().createNewObject();
		fightChit.setName(spellObj.getName()+" Fight Chit ("+ context.Caster.getName()+")");
		fightChit.copyAttributeBlockFrom(spellObj,"fight_chit");
		fightChit.renameAttributeBlock("fight_chit","this");
		fightChit.setThisAttribute("spellID",spellObj.getStringId());
		fightChit.setThisAttribute("sourceSpell",spellObj.getName());
		fightChit.setThisAttribute(RealmComponent.CHARACTER_CHIT);
		fightChit.setThisAttribute(CharacterActionChitComponent.ACTION_CHIT_STATE_KEY,CharacterActionChitComponent.ACTIVE_ID);
		fightChit.setThisAttribute(Constants.CHIT_EARNED);
		fightChit.setThisAttribute("action","FIGHT");
		spellObj.setThisAttribute("fightChitID",fightChit.getStringId());		
		context.Target.getGameObject().add(fightChit);
		
		if (!context.Target.getGameObject().equals(context.Caster)) {
			RealmComponent fightChitRc = RealmComponent.getRealmComponent(fightChit);
			fightChitRc.setOwner(RealmComponent.getRealmComponent(context.Caster));
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
		GameObject spellObj = context.Spell.getGameObject();
		
		String chitId = spellObj.getThisAttribute("fightChitID");
		GameObject fightChit =spellObj.getGameData().getGameObject(Long.valueOf(chitId));
		context.Target.getGameObject().remove(fightChit);
		spellObj.removeThisAttribute("fightChitID");
	}
}
