package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class HorseWhisperEvent implements IEvent {
	private static final String title = "Horse Whisper";
	private static final String description = "At Sunset, all horses in a random hex cannot gallop.";
	public void applyBirdsong(GameData data) {
	}
	public void applySunset(GameData data) {
		TileComponent tile = RealmEvents.chooseRandomTile(data);
		if (tile!=null) {
			GameObject config = RealmEvents.findEventsConfig(data);
			tile.getGameObject().setThisAttribute(Constants.EVENT_HORSE_WHISPER);
			RealmEvents.addEffectForTile(config,Constants.EVENT_HORSE_WHISPER,tile.getGameObject().getStringId());
			RealmLogging.logMessage("Event","Horse Whisper: All horses in "+tile.getGameObject().getNameWithNumber()+" cannot gallop.");
		}
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_HORSE_WHISPER);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.EVENT_HORSE_WHISPER);
				RealmEvents.removeEffectForTile(config,Constants.EVENT_HORSE_WHISPER,id);
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
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_HORSE_WHISPER);
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
		text = "In "+text.substring(0,text.length()-2) + " all horses cannot gallop.";
		return text;
	}
}