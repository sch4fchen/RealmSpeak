package com.robin.magic_realm.components.events;

import java.awt.Point;
import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmCalendar;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.map.Tile;

public class FrozenRiverEvent implements IEvent {
	private static final String title = "Frozen River";
	private static final String description = "A random River tile and all adjacent tiles have their water clearings frozen until the end of the week.";
	public void applyBirdsong(GameData data) {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> allTiles = pool.find("tile");
		ArrayList<TileComponent> waterTiles = new ArrayList<>();
		for (GameObject tile : allTiles) {
			TileComponent tileComponent = new TileComponent(tile);
			for (ClearingDetail cl : tileComponent.getClearings()) {
				if (cl.isWater() || cl.isFrozenWater()) {
					waterTiles.add(tileComponent);
					break;
				}
			}
		}
		
		if (!waterTiles.isEmpty()) {
			TileComponent chosenTile = waterTiles.remove(RandomNumber.getRandom(waterTiles.size()));
			ArrayList<TileComponent> freezingTiles = new ArrayList<>();
			freezingTiles.add(chosenTile);
			Point basePosition = Tile.getPositionFromGameObject(chosenTile.getGameObject());
			for (TileComponent tile : waterTiles) {
				Point position = Tile.getPositionFromGameObject(tile.getGameObject());
				if ((position.x==basePosition.x || position.x==basePosition.x-1 || position.x==basePosition.x+1)
						&& (position.y==basePosition.y || position.y==basePosition.y-1 || position.y==basePosition.y+1)) {
					freezingTiles.add(tile);
				}
			}
			for (TileComponent tile : freezingTiles) {
				GameObject config = RealmEvents.findEventsConfig(data);
				tile.getGameObject().setThisAttribute(Constants.EVENT_FROZEN_WATER);
				RealmEvents.addEffectForTile(config,Constants.EVENT_FROZEN_WATER,tile.getGameObject().getStringId());
				RealmLogging.logMessage("Event","Frozen Water: All water clreaings in "+tile.getGameObject().getNameWithNumber()+" are frozen until the end of the week.");
			}
		}
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		GameWrapper game = GameWrapper.findGame(data);
		if (RealmCalendar.isSeventhDay(game.getDay())) {
			GameObject config = RealmEvents.findEventsConfig(data);
			ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_FROZEN_WATER);
			if (ids!=null && !ids.isEmpty()) {
				for (String id : ids) {
					GameObject tile = data.getGameObject(Long.valueOf(id));
					tile.removeThisAttribute(Constants.EVENT_FROZEN_WATER);
					RealmEvents.removeEffectForTile(config,Constants.EVENT_FROZEN_WATER,id);
				}
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
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_FROZEN_WATER);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				text = text + tile.getNameWithNumber() + ", ";
			}
		}
		text = "All water clearings in "+text.substring(0,text.length()-2) + " are frozen until the end of the week.";
		return text;
	}
}