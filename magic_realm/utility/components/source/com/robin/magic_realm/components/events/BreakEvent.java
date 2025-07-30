package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.utility.RealmObjectMaster;
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
		for (CharacterWrapper character:getLivingCharacters(data)) {
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
	
	private static ArrayList<CharacterWrapper> getLivingCharacters(GameData gameData) {
		GamePool pool = new GamePool(RealmObjectMaster.getRealmObjectMaster(gameData).getPlayerCharacterObjects());
		ArrayList<GameObject> list = pool.find(CharacterWrapper.NAME_KEY);
		ArrayList<CharacterWrapper> active = new ArrayList<>();
		for (GameObject characterGo : list) {
			CharacterWrapper character = new CharacterWrapper(characterGo);
			if (!character.isDead()) {
				active.add(character);
			}
		}
		return active;
	}
}