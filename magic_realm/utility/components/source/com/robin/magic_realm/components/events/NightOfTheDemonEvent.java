package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class NightOfTheDemonEvent implements IEvent {
	private static final String title = "Night of the Demon";
	private static final String description = "Roll on Summon Demon effect for each combat in a random tile.";
	public void applyBirdsong(GameData data) {
		TileComponent tile = RealmEvents.chooseRandomTile(data);
		if (tile!=null) {
			GameObject config = RealmEvents.findEventsConfig(data);
			tile.getGameObject().setThisAttribute(Constants.EVENT_NIGHT_OF_THE_DEMON);
			RealmEvents.addEffectForTile(config,Constants.EVENT_NIGHT_OF_THE_DEMON,tile.getGameObject().getStringId());
			RealmLogging.logMessage("Event","Night of the Demon: Summon Demon for each combat in "+tile.getGameObject().getName());
		}
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_NIGHT_OF_THE_DEMON);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.EVENT_NIGHT_OF_THE_DEMON);
				RealmEvents.removeEffectForTile(config,Constants.EVENT_NIGHT_OF_THE_DEMON,id);
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
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_NIGHT_OF_THE_DEMON);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				text = text + tile.getNameWithNumber() + ", ";
			}
		}
		text = text.substring(0,text.length()-2) + " is affected by the Night of the Demon.";
		return text;
	}
}