package com.robin.magic_realm.components.events;

import java.util.ArrayList;
import java.util.Arrays;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.RealmLogging;

public class EnchantEvent implements IEvent {
	private static final String title = "Enchant";
	private static final String description = "A random hex, which isn't a 'W' or 'H' hex, flips.";
	public void applyBirdsong(GameData data) {
		ArrayList<String> tileTypes = new ArrayList<>(Arrays.asList("V","M","C","XM","XC","S","F","ST","R"));
		TileComponent tile = RealmEvents.chooseRandomTile(data, tileTypes);
		if (tile!=null) {
			tile.flip();
			RealmLogging.logMessage("Event","Enchant: "+tile.getGameObject().getNameWithNumber()+" is flipped.");
		}
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public String getDescription(GameData data) {
		return description;
	}
}