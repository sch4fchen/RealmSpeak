package com.robin.magic_realm.components.events;

import java.util.ArrayList;
import java.util.Arrays;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class ThornsEvent implements IEvent {
	private static final String title = "Thorns";
	private static final String description = "At Sunset, all roadways of a random M, C, F, R hex are affected by Thorns.";
	
	public void applyBirdsong(GameData data) {
	}
	public void applySunset(GameData data) {
		ArrayList<String> tileTypes = new ArrayList<>(Arrays.asList("M","C","F","R"));
		TileComponent tile = RealmEvents.chooseRandomTile(data, tileTypes);
		if (tile!=null) {
			GameObject config = RealmEvents.findEventsConfig(data);
			tile.getGameObject().setThisAttribute(Constants.EVENT_THORNS);
			RealmEvents.addEffectForTile(config,Constants.EVENT_THORNS,tile.getGameObject().getStringId());
			RealmLogging.logMessage("Event","Thorns: All roadways in "+tile.getGameObject().getNameWithNumber()+" are blocked by Thorns.");
		}
		else {
			RealmLogging.logMessage("Event","Thorns: No valid tile found.");
		}
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_THORNS);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.EVENT_THORNS);
				RealmEvents.removeEffectForTile(config,Constants.EVENT_THORNS,id);
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