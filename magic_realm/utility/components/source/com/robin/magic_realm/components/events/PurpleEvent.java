package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.attribute.ColorMagic;

public class PurpleEvent implements IEvent {
	private static final String title = "Purple";
	private static final String description = "A source of purple magic is in every clearing of every hex.";
	public void applyBirdsong(GameData data) {
		RealmEvents.addInfiniteColorMagicSource(data,ColorMagic.Purple);
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		RealmEvents.removeInfiniteColorMagicSource(data,ColorMagic.Purple);
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