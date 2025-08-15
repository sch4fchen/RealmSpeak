package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class PeacefulDayEvent implements IEvent {
	private static final String title = "Peaceful Day";
	private static final String description = "A random hex won't summon monsters this day.";
	public void applyBirdsong(GameData data) {
		TileComponent tile = RealmEvents.chooseRandomTile(data);
		if (tile!=null) {
			GameObject config = RealmEvents.findEventsConfig(data);
			tile.getGameObject().setThisAttribute(Constants.EVENT_PEACEFUL_DAY);
			RealmEvents.addEffectForTile(config,Constants.EVENT_PEACEFUL_DAY,tile.getGameObject().getStringId());
			RealmLogging.logMessage("Event","Peaceful Day: "+tile.getGameObject().getNameWithNumber()+" won't summon monsters.");
		}
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_PEACEFUL_DAY);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.EVENT_PEACEFUL_DAY);
				RealmEvents.removeEffectForTile(config,Constants.EVENT_PEACEFUL_DAY,id);
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