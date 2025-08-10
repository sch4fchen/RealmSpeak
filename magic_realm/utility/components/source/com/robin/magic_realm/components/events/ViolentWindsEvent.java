package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;

public class ViolentWindsEvent implements IEvent {
	private static final String title = "Violent Winds";
	private static final String description = "No one is able to Fly today (but first Fly activity for landing). Flyers +1 to maneuver.";
	public void applyBirdsong(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		config.setThisAttribute(Constants.EVENT_VIOLENT_WINDS);
		for (GameObject tile : RealmEvents.chooseAllTiles(data)) {
			tile.setThisAttribute(Constants.EVENT_VIOLENT_WINDS);
			RealmEvents.addEffectForTile(config,Constants.EVENT_VIOLENT_WINDS,tile.getStringId());
		}
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		config.removeThisAttribute(Constants.EVENT_VIOLENT_WINDS);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_VIOLENT_WINDS);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.EVENT_VIOLENT_WINDS);
				RealmEvents.removeEffectForTile(config,Constants.EVENT_VIOLENT_WINDS,id);
			}
		}
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