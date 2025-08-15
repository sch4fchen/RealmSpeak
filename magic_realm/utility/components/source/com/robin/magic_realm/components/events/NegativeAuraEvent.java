package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class NegativeAuraEvent implements IEvent {
	private static final String title = "Negative Aura";
	private static final String description = "A random hex with unhired denizens is affected by Negative Aura.";
	public void applyBirdsong(GameData data) {
		GameObject tile = RealmEvents.chooseRandomTileWithUnhiredNatives(data);
		if (tile!=null) {
			GameObject config = RealmEvents.findEventsConfig(data);
			tile.setThisAttribute(Constants.EVENT_NEGATIVE_AURA);
			RealmEvents.addEffectForTile(config,Constants.EVENT_NEGATIVE_AURA,tile.getStringId());
			RealmLogging.logMessage("Event","Negative Aura: "+tile.getNameWithNumber()+" is affected by Negative Aura.");
		}
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_NEGATIVE_AURA);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.EVENT_NEGATIVE_AURA);
				RealmEvents.removeEffectForTile(config,Constants.EVENT_NEGATIVE_AURA,id);
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
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_NEGATIVE_AURA);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				text = text + tile.getNameWithNumber() + ", ";
			}
		}
		text = text.substring(0,text.length()-2) + " is affected by Negative Aura.";
		return text;
	}
}