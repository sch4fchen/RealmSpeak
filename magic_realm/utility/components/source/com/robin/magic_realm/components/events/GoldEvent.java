package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.utility.RealmLogging;

public class GoldEvent implements IEvent {
	private static final String title = "Gold";
	private static final String description = "A source of gold magic is in every clearing of every hex.";
	public void applyBirdsong(GameData data) {
		RealmEvents.addInfiniteColorMagicSource(data,ColorMagic.Gold);
		RealmLogging.logMessage("Event","Gold: A source of gold magic is in every clearing of every hex.");
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		RealmEvents.removeInfiniteColorMagicSource(data,ColorMagic.Gold);
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