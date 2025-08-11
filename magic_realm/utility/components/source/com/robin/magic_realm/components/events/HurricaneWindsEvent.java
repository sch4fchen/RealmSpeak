package com.robin.magic_realm.components.events;

import java.util.ArrayList;
import java.util.Arrays;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class HurricaneWindsEvent implements IEvent {
	private static final String title = "Hurricane Winds";
	private static final String description = "In a random mountain clearing Hurricane Winds is cast in the first round of combat.";
	
	public void applyBirdsong(GameData data) {
	}
	public void applySunset(GameData data) {
		ArrayList<String> clearingTypes = new ArrayList<>(Arrays.asList("mountain"));
		ClearingDetail clearing = RealmEvents.chooseRandomClearing(data,clearingTypes);
		if (clearing!=null) {
			TileComponent tile = clearing.getTileLocation().tile;
			GameObject config = RealmEvents.findEventsConfig(data);
			tile.getGameObject().addThisAttributeListItem(Constants.EVENT_HURRICANE_WINDS,clearing.getNumString());
			RealmEvents.addEffectForTile(config,Constants.EVENT_HURRICANE_WINDS,tile.getGameObject().getStringId());
			RealmLogging.logMessage("Event","Hurricane Winds: Cast Hurricane Winds in the first round of combat in "+clearing.getNumString()+" of "+tile.getGameObject().getNameWithNumber()+".");
		}
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_HURRICANE_WINDS);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.EVENT_HURRICANE_WINDS);
				RealmEvents.removeEffectForTile(config,Constants.EVENT_HURRICANE_WINDS,id);
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
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_HURRICANE_WINDS);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				for (String cl : tile.getThisAttributeList(Constants.EVENT_HURRICANE_WINDS)) {;
					text = text + tile.getNameWithNumber() +" ("+cl+")"+ ", ";
				}
			}
		}
		else {
			return description;
		}
		if (text.isEmpty()) return description;
		text = "In " + text.substring(0,text.length()-2) + " Hurricane Winds is cast in the first round of combat.";
		return text;
	}
}