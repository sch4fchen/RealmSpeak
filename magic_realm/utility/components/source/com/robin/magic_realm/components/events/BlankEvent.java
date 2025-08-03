package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;

public class BlankEvent implements IEvent {
	private static final String title = "Blank";
	private static final String description = "No event today.";
	public void applyBirdsong(GameData data) {
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