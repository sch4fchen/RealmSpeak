package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class FogEvent implements IEvent {
	private static final String title = "Fog";
	private static final String description = "A random tile and all adjacent tiles are affected by Fog.";
	public void applyBirdsong(GameData data) {
		ArrayList<TileComponent> tiles = RealmEvents.chooseRandomAndAdjacentTiles(data);
		if (tiles!=null && !tiles.isEmpty()) {
			GameObject config = RealmEvents.findEventsConfig(data);
			for (TileComponent tile : tiles) {
				tile.getGameObject().setThisAttribute(Constants.EVENT_FOG);
				RealmEvents.addEffectForTile(config,Constants.EVENT_FOG,tile.getGameObject().getStringId());
				RealmLogging.logMessage("Event","Fog: Cannot peer in "+tile.getGameObject().getName());
			}
		}
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_FOG);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.EVENT_FOG);
				RealmEvents.removeEffectForTile(config,Constants.EVENT_FOG,id);
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
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_FOG);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				text = text + tile.getNameWithNumber() + ", ";
			}
		}
		text = text.substring(0,text.length()-2) + " are affected by Fog.";
		return text;
	}
}