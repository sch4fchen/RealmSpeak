package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.attribute.ColorMagic;

public class GreyEvent implements IEvent {
	private static final String title = "Grey";
	private static final String description = "A source of grey magic is in every clearing of every hex.";
	public void applyBirdsong(GameData data) {
		RealmEvents.addInfiniteColorMagicSource(data,ColorMagic.Grey);
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		RealmEvents.removeInfiniteColorMagicSource(data,ColorMagic.Grey);
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