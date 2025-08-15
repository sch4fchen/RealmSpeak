package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.utility.RealmLogging;

public class BlackEvent implements IEvent {
	private static final String title = "Black";
	private static final String description = "A source of black magic is in every clearing of every hex.";
	public void applyBirdsong(GameData data) {
		RealmEvents.addInfiniteColorMagicSource(data,ColorMagic.Black);
		RealmLogging.logMessage("Event","Black: A source of black magic is in every clearing of every hex.");
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		RealmEvents.removeInfiniteColorMagicSource(data,ColorMagic.Black);
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