package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;

public class MoveChitEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		GameObject spellObj = context.Spell.getGameObject();
		
		GameObject moveChit = spellObj.getGameData().createNewObject();
		moveChit.setName(spellObj.getName()+" Move Chit ("+ context.Caster.getName()+")");
		moveChit.copyAttributeBlockFrom(spellObj,"move_chit");
		moveChit.renameAttributeBlock("move_chit","this");
		moveChit.setThisAttribute(Constants.SPELL_ID,spellObj.getStringId());
		moveChit.setThisAttribute("sourceSpell",spellObj.getName());
		moveChit.setThisAttribute(RealmComponent.CHARACTER_CHIT);
		moveChit.setThisAttribute(CharacterActionChitComponent.ACTION_CHIT_STATE_KEY,CharacterActionChitComponent.ACTIVE_ID);
		moveChit.setThisAttribute(Constants.CHIT_EARNED);
		moveChit.setThisAttribute("action","MOVE");
		spellObj.setThisAttribute("moveChitID",moveChit.getStringId());
		context.Target.getGameObject().add(moveChit);
		
		if (!context.Target.getGameObject().equals(context.Caster)) {
			RealmComponent moveChitRc = RealmComponent.getRealmComponent(moveChit);
			moveChitRc.setOwner(RealmComponent.getRealmComponent(context.Caster));
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
		GameObject spellObj = context.Spell.getGameObject();
		
		String chitId = spellObj.getThisAttribute("moveChitID");
		GameObject moveChit =spellObj.getGameData().getGameObject(Long.valueOf(chitId));
		context.Target.getGameObject().remove(moveChit);
		spellObj.removeThisAttribute("moveChitID");
	}
}
