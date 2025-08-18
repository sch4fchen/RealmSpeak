package com.robin.magic_realm.components.events;

import java.util.ArrayList;
import java.util.Collections;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.StateChitComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.RealmLogging;

public class ChitMigrateEvent implements IEvent {
	public ChitMigrateEvent(String type) {
		soundType=type;
	}
	private String soundType = "";
	public void applyBirdsong(GameData data) {
	}
	public void applySunset(GameData data) {
		TileComponent randomTile = RealmEvents.chooseRandomTile(data);
		if (randomTile==null) {
			RealmLogging.logMessage("Event",soundType+" Migrate: No valid tile found.");
			return;
		}
		GameObject chosenChit = null;
		for (GameObject go : randomTile.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isSound() && go.getThisAttribute("sound").matches(soundType.toLowerCase()) && ((StateChitComponent)rc).isFaceUp()) {
				if (chosenChit == null || go.getThisInt("clearing") > chosenChit.getThisInt("clearing")) {
					chosenChit = go;
				}
			}
		}
		if (chosenChit == null) {
			RealmLogging.logMessage("Event",soundType+" Migrate: No valid chit found.");
			return;
		}
		GameObject tile = chosenChit.getHeldBy();
		if (tile.hasThisAttribute(RealmComponent.TILE)) {
			ArrayList<GameObject> adjacentTiles = RealmEvents.getAllAdjacentTiles(tile,data);
			Collections.shuffle(adjacentTiles);
			Collections.shuffle(adjacentTiles);
			GameObject chosenTile = null;
			for (GameObject adjTile : adjacentTiles) {
				for (GameObject held : adjTile.getHold()) {
					RealmComponent rc = RealmComponent.getRealmComponent(held);
					if (rc.isCharacter() || rc.isHiredOrControlled()) {
						chosenTile = adjTile;
						break;
					}
				}
				if (chosenTile!=null) {
					break;
				}
			}
			if (chosenTile!=null) {
				chosenTile.add(chosenChit);
				RealmEvents.summonMonstersForSoundChit(chosenChit, chosenTile, data);
				RealmLogging.logMessage("Event",soundType+" Migrate: "+chosenChit.getNameWithNumber()+" is moved to "+chosenTile.getNameWithNumber()+" and summons denizens.");
			}
			else {
				RealmLogging.logMessage("Event",soundType+" Migrate: No valid adjacent tile found.");
				return;
			}
		}
	}
	public void expire(GameData data) {
	}
	@Override
	public String getTitle() {
		return soundType+" Migrate";
	}
	@Override
	public String getDescription(GameData data) {
		return "At sunset the highest numbered "+soundType+" chit is moved to an adjacent hex and summons denizens.";
	}
}