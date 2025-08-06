package com.robin.magic_realm.components.events;

import java.util.ArrayList;
import java.util.Arrays;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.RealmLogging;

public class MigrateEvent implements IEvent {
	private static final String title = "Migrate";
	private static final String description = "At Sunset, swap the warning chits of a random M, C, F, R, W or S tiles and another tily of the same type.";
	
	public void applyBirdsong(GameData data) {
	}
	public void applySunset(GameData data) {
		ArrayList<String> tileTypes = new ArrayList<>(Arrays.asList("M","XM","C","XC","F","R","W","S"));
		TileComponent tile = RealmEvents.chooseRandomTile(data, tileTypes);
		if (tile!=null) {
			String type = tile.getTileType();
			GamePool pool = new GamePool(data.getGameObjects());
			ArrayList<GameObject> tiles = pool.find("tile,tile_type="+type);
			GameObject tile2 = tiles.get(RandomNumber.getRandom(tiles.size()));
			GameObject chit1 = null;
			ArrayList<GameObject> possibleChit1 = new ArrayList<>();
			for (GameObject go : tile.getHold()) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isWarning()) {
					possibleChit1.add(go);
				}
			}
			if (possibleChit1!=null && !possibleChit1.isEmpty()) {
				chit1 = possibleChit1.get(RandomNumber.getRandom(possibleChit1.size()));
			}
			GameObject chit2 = null;
			ArrayList<GameObject> possibleChit2 = new ArrayList<>();
			for (GameObject go : tile2.getHold()) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isWarning()) {
					possibleChit2.add(go);
				}
			}
			if (possibleChit2!=null && !possibleChit2.isEmpty()) {
				chit2 = possibleChit2.get(RandomNumber.getRandom(possibleChit2.size()));
			}
			if (chit1!=null && chit2!=null) {
				tile.getGameObject().add(chit2);
				tile2.add(chit1);
				RealmLogging.logMessage("Event","Migrate: Warning chits of "+tile.getGameObject().getNameWithNumber()+" and "+tile2.getNameWithNumber()+" were swapped.");
			}
			else {
				RealmLogging.logMessage("Event","Migrate: Warning chits couldn't be swapped.");
			}
		}
		else {
			RealmLogging.logMessage("Event","Migrate: No valid tile found.");
		}
	}
	public void expire(GameData data) {
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