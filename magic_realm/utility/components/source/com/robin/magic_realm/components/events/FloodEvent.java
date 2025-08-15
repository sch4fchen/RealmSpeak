package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class FloodEvent implements IEvent {
	private static final String title = "Flood";
	private static final String description = "In all non-mountain clearings of a random River and all adjacent tiles Flood is cast in the first round of combat";
	
	public void applyBirdsong(GameData data) {
	}
	public void applySunset(GameData data) {
		ArrayList<TileComponent> waterTiles = RealmEvents.chooseRandomWaterAndAdjacentTiles(data);
		if (!waterTiles.isEmpty()) {
			for (TileComponent tile : waterTiles) {
				GameObject config = RealmEvents.findEventsConfig(data);
				for (ClearingDetail clearing : tile.getClearings()) {
					if (!clearing.isMountain()) {
						tile.getGameObject().addThisAttributeListItem(Constants.EVENT_FLOOD,clearing.getNumString());
					}
				}
				RealmEvents.addEffectForTile(config,Constants.EVENT_FLOOD,tile.getGameObject().getStringId());
				RealmLogging.logMessage("Event","Flood: Cast Flood in all non-mountain clearings in "+tile.getGameObject().getNameWithNumber()+".");
			}
		}
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_FLOOD);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.EVENT_FLOOD);
				RealmEvents.removeEffectForTile(config,Constants.EVENT_FLOOD,id);
			}
		}
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public String getDescription(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		String text = "";
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_FLOOD);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				text = text + tile.getNameWithNumber() + ", ";
			}
		}
		else {
			return description;
		}
		if (text.isEmpty()) return description;
		text = "In " + text.substring(0,text.length()-2) + " Flood is cast in the first round of combat.";
		return text;
	}
}