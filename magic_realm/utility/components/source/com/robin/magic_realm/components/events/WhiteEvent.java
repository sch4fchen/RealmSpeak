package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.utility.RealmLogging;

public class WhiteEvent implements IEvent {
	private static final String title = "White";
	private static final String description = "A source of white magic is in every clearing of every hex.";
	public void applyBirdsong(GameData data) {
		RealmEvents.addInfiniteColorMagicSource(data,ColorMagic.White);
		RealmLogging.logMessage("Event","White: A source of white magic is in every clearing of every hex.");
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		RealmEvents.removeInfiniteColorMagicSource(data,ColorMagic.White);
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