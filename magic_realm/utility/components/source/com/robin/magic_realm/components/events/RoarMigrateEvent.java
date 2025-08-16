package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.StateChitComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.RealmLogging;

public class RoarMigrateEvent implements IEvent {
	private static final String title = "Roar Migrate";
	private static final String description = "At sunset the highest numbered Patter chit is moved to an adjacent hex and summons denizens.";
	public void applyBirdsong(GameData data) {
	}
	public void applySunset(GameData data) {
		TileComponent randomTile = RealmEvents.chooseRandomTile(data);
		if (randomTile==null) {
			RealmLogging.logMessage("Event","Roar Migrate: No valid tile found.");
			return;
		}
		GameObject chosenChit = null;
		for (GameObject go : randomTile.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isSound() && go.getThisAttribute("sound").matches("roar") && ((StateChitComponent)rc).isFaceUp()) {
				if (chosenChit == null || go.getThisInt("clearing") > chosenChit.getThisInt("clearing")) {
					chosenChit = go;
				}
			}
			if (chosenChit == null) {
				RealmLogging.logMessage("Event","Roar Migrate: No valid chit found.");
				return;
			}
			GameObject tile = chosenChit.getHeldBy();
			if (tile.hasThisAttribute(RealmComponent.TILE)) {
				ArrayList<GameObject> adjacentTiles = RealmEvents.getAllAdjacentTiles(tile,data);
				GameObject chosenTile = adjacentTiles.get(RandomNumber.getRandom(adjacentTiles.size()));
				chosenTile.add(chosenChit);
				RealmEvents.summonMonstersForSoundChit(chosenChit, chosenTile, data);
				RealmLogging.logMessage("Event","Roar Migrate: "+chosenChit.getNameWithNumber()+" is moved to "+chosenTile.getNameWithNumber()+" and summons denizens.");
			}
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