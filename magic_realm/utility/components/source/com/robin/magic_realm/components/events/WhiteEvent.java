package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.attribute.ColorMagic;

public class WhiteEvent implements IEvent {
	private static final String title = "White";
	private static final String description = "A source of white magic is in every clearing of every hex.";
	public void apply(GameData data) {
		RealmEvents.addInfiniteColorMagicSource(data,ColorMagic.White);
	}
	public void expire(GameData data) {
		RealmEvents.removeInfiniteColorMagicSource(data,ColorMagic.White);
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