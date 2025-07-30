package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellMasterWrapper;

public class BreakEvent implements IEvent {
	private static final String title = "Break";
	private static final String description = "At midnight, all spells in effect anywhere are broken. This includes Curses and Mesmerizes.";
	public void apply(GameData data) {
	}
	public void expire(GameData data) {
		SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(data);
		sm.expireAllSpells();
		for (CharacterWrapper character:RealmEvents.getLivingCharacters(data)) {
			character.removeAllCurses();
		}
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public String getDescription() {
		return description;
	}
}