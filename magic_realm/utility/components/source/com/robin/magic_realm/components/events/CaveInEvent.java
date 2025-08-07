package com.robin.magic_realm.components.events;

import java.util.ArrayList;
import java.util.Arrays;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class CaveInEvent implements IEvent {
	private static final String title = "Cave In";
	private static final String description = "In a random caves cleariong Roof Collapses is cast in the first round of combat.";
	
	public void applyBirdsong(GameData data) {
	}
	public void applySunset(GameData data) {
		ArrayList<String> clearingTypes = new ArrayList<>(Arrays.asList("caves"));
		ClearingDetail clearing = RealmEvents.chooseRandomClearing(data,clearingTypes);
		if (clearing!=null) {
			TileComponent tile = clearing.getTileLocation().tile;
			GameObject config = RealmEvents.findEventsConfig(data);
			tile.getGameObject().addThisAttributeListItem(Constants.EVENT_CAVE_IN,clearing.getNumString());
			RealmEvents.addEffectForTile(config,Constants.EVENT_CAVE_IN,tile.getGameObject().getStringId());
			RealmLogging.logMessage("Event","Cave In: Cast Roof Collapses in the first round of combat in "+clearing.getNumString()+" of "+tile.getGameObject().getNameWithNumber()+".");
		}
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_CAVE_IN);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.EVENT_CAVE_IN);
				RealmEvents.removeEffectForTile(config,Constants.EVENT_CAVE_IN,id);
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