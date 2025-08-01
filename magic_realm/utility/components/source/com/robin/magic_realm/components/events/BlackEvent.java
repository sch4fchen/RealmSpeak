package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.attribute.ColorMagic;

public class BlackEvent implements IEvent {
	private static final String title = "Black";
	private static final String description = "A source of black magic is in every clearing of every hex.";
	public void applyBirdsong(GameData data) {
		RealmEvents.addInfiniteColorMagicSource(data,ColorMagic.Black);
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
	public String getDescription() {
		return description;
	}
}