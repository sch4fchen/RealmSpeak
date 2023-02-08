package com.robin.magic_realm.components.quest;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellCreator {
	public static SpellWrapper CreateSpellWrapper(String spell, CharacterWrapper character) {
		GameObject spellGo = character.getGameData().getGameObjectByNameIgnoreCase(spell);
		if (spellGo == null) return null;
		SpellWrapper spellWrapper = new SpellWrapper(spellGo);
		spellWrapper.setString(SpellWrapper.CASTER_ID, String.valueOf(character.getGameObject().getId()));
		spellWrapper.setString(SpellWrapper.SPELL_ALIVE,"");
		return spellWrapper;
	}
}