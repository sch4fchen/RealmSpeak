package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.attribute.ColorMagic;

public class PrismEvent implements IEvent {
	private static final String title = "Prism";
	private static final String description = "A source of grey, gold and purple magic is in every clearing of every hex.";
	public void applyBirdsong(GameData data) {
		RealmEvents.addInfiniteColorMagicSource(data,ColorMagic.Grey);
		RealmEvents.addInfiniteColorMagicSource(data,ColorMagic.Gold);
		RealmEvents.addInfiniteColorMagicSource(data,ColorMagic.Purple);
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		RealmEvents.removeInfiniteColorMagicSource(data,ColorMagic.Grey);
		RealmEvents.removeInfiniteColorMagicSource(data,ColorMagic.Gold);
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