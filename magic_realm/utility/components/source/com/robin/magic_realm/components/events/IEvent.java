package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;

public interface IEvent {
	String getTitle();
	String getDescription(GameData data);
	void applyBirdsong(GameData data);
	void applySunset(GameData data);
	void expire(GameData data);
}