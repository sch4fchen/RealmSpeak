package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class ReanimateEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		GameObject monster = context.getGameData().createNewObject();
		SpellWrapper.copyMonsterAttributesToObject(context.Target.getGameObject(), "this", monster);
		monster.setName("Undead");
		monster.removeAttributeBlock("light");
		monster.removeAttributeBlock("dark");
		monster.copyAttributeBlockFrom(context.Target.getGameObject(), "light");
		monster.copyAttributeBlockFrom(context.Target.getGameObject(), "dark");
		monster.setAttribute("light","attack_speed",monster.getAttributeInt("light","attack_speed")+1);
		monster.setAttribute("dark","attack_speed",monster.getAttributeInt("light","attack_speed")+1);
		monster.setAttribute("light","move_speed",monster.getAttributeInt("light","attack_speed")+1);
		monster.setAttribute("dark","move_speed",monster.getAttributeInt("light","attack_speed")+1);
		monster.setThisAttribute(Constants.ARMORED);
		monster.setThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE,"skeleton");
		monster.setThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE,"wesnoth/units");
		monster.setThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE,"0.9");
		monster.setThisAttribute(Constants.UNDEAD);
		monster.setThisAttribute(Constants.SPOILS_NONE);
		for (GameObject held : context.Target.getGameObject().getHold()) {
			if (held.hasThisAttribute(Constants.SPELL_DENIZEN)) {
				GameObject spell = held.copy();
				monster.add(spell);
			}
		}
		context.Spell.setExtraIdentifier(monster.getStringId());
		context.getCharacterCaster().addHireling(monster);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		String id = context.Spell.getExtraIdentifier();
		if (id !=null) {
			GameObject monster = context.Spell
					.getGameObject()
					.getGameData()
					.getGameObject(Long.valueOf(context.Spell.getGameObject().getThisAttribute(Constants.PHASE_CHIT_ID)));
			RealmUtility.makeDead(RealmComponent.getRealmComponent(monster));
		}
	}

}