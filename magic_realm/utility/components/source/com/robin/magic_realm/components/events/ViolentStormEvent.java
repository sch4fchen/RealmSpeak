package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;

public class ViolentStormEvent implements IEvent {
	private static final String title = "Violent Storm";
	private static final String description = "A random tile and all adjacent tiles are affected by the Violent Storm.";
	public void applyBirdsong(GameData data) {
		ArrayList<TileComponent> tiles = RealmEvents.chooseRandomAndAdjacentTiles(data);
		if (tiles!=null && !tiles.isEmpty()) {
			GameObject config = RealmEvents.findEventsConfig(data);
			DieRoller dieRoller = new DieRoller();
			dieRoller.rollDice();
			int result = dieRoller.getHighDieResult();
			int phasesLost;
			if (result<=1) {
				phasesLost = 4;
			}
			else if (result<=3) {
				phasesLost = 3;
			}
			else if (result<=5) {
				phasesLost = 2;
			}
			else {
				phasesLost = 1;
			}
			for (TileComponent tile : tiles) {
				tile.getGameObject().setThisAttribute(Constants.SP_STORMY_BY_EVENT,phasesLost);
				RealmEvents.addEffectForTile(config,Constants.SP_STORMY_BY_EVENT,tile.getGameObject().getStringId());
				RealmLogging.logMessage("Event","Violent Storm: "+phasesLost+" phase"+(phasesLost==1?"":"s")+" lost on entry on tile "+tile.getGameObject().getName());
			}
		}
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.SP_STORMY_BY_EVENT);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				tile.removeThisAttribute(Constants.SP_STORMY_BY_EVENT);
				RealmEvents.removeEffectForTile(config,Constants.SP_STORMY_BY_EVENT,id);
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
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.SP_STORMY_BY_EVENT);
		int phasesLost = 0;
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				phasesLost = tile.getThisInt(Constants.SP_STORMY_BY_EVENT);
				text = text + tile.getNameWithNumber() + ", ";
			}
		}
		text = text.substring(0,text.length()-2) + " are affected by the Violent Storm ("+phasesLost+" phase"+(phasesLost==1?"":"s")+").";
		return text;
	}
}