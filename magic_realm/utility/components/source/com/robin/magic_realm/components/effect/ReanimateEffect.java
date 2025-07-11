package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
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
		monster.setAttribute("light","move_speed",monster.getAttributeInt("light","move_speed")+1);
		monster.setAttribute("dark","move_speed",monster.getAttributeInt("light","move_speed")+1);
		monster.setThisAttribute(Constants.ARMORED);
		MonsterChitComponent monsterChit = (MonsterChitComponent)RealmComponent.getRealmComponent(monster);
		if (monsterChit.getWeapon()!=null) {
			GameObject weapon = monsterChit.getWeapon().getGameObject();
			weapon.setAttribute("light","attack_speed",weapon.getAttributeInt("light","attack_speed")+1);
			weapon.setAttribute("dark","attack_speed",weapon.getAttributeInt("dark","attack_speed")+1);
		}
		if (context.Target.getGameObject().hasThisAttribute(Constants.ICON_SIZE)) {
			monster.setThisAttribute(Constants.ICON_SIZE,context.Target.getGameObject().getThisAttribute(Constants.ICON_SIZE));
		}
		if (context.Target.getGameObject().hasThisAttribute(Constants.ICON_Y_OFFSET)) {
			monster.setThisAttribute(Constants.ICON_Y_OFFSET,context.Target.getGameObject().getThisAttribute(Constants.ICON_Y_OFFSET));
		}
		if (!monster.hasThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE) && !monster.hasThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE)) {
			monster.setThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE,"skeleton");
			monster.setThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE,"wesnoth/units");
			monster.setThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE,"0.9");
		}
		monster.setThisAttribute(Constants.UNDEAD);
		monster.setThisAttribute(Constants.UNDEAD_SUMMONED);
		monster.setThisAttribute("monster");
		if (context.Target.getGameObject().hasThisAttribute("native") ) {
			monster.removeThisAttribute("native");
		}
		monster.setThisAttribute(Constants.SPOILS_NONE);
		for (GameObject held : context.Target.getGameObject().getHold()) {
			if (held.hasThisAttribute(Constants.SPELL_DENIZEN)) {
				GameObject spell = held.copy();
				monster.add(spell);
			}
		}
		context.Spell.setExtraIdentifier(monster.getStringId());
		context.getCharacterCaster().addHireling(monster);
		context.Caster.add(monster); // so that you don't have to assign as a follower right away
		CombatWrapper monsterCw = new CombatWrapper(monster);
		monsterCw.setSheetOwner(true);
		TileLocation loc = RealmComponent.getRealmComponent(context.Caster).getCurrentLocation();
		if (loc!=null && loc.clearing!=null) {
			RealmComponent.getRealmComponent(context.Caster).getCurrentLocation().clearing.add(monster,null);
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
		String id = context.Spell.getExtraIdentifier();
		if (id !=null) {
			GameObject monster = context.Spell
					.getGameObject()
					.getGameData()
					.getGameObject(Long.valueOf(id));
			RealmUtility.makeDead(RealmComponent.getRealmComponent(monster));
			context.Spell.setExtraIdentifier(null);
		}
	}

}
