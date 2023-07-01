package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;
import java.util.Collection;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.SoundChitComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.RealmObjectMaster;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingSound extends SpellTargetingSingle {

	protected SpellTargetingSound(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		secondaryTargetChoiceString = "Select a tile to move the sound to:";
		TileLocation here = battleModel.getBattleLocation();
		GameData gameData = spell.getGameObject().getGameData();
		GamePool pool = new GamePool(gameData.getGameObjects());
		
		ArrayList<GameObject> clearingTiles = getFilteredTiles();
		Collection<GameObject> c = pool.find("sound,chit");
		for (GameObject soundChitObject : c) {
			GameObject tile = soundChitObject.getHeldBy();
			if (tile!=null && !tile.hasThisAttribute("tile")) {
				tile = tile.getHeldBy(); // this jumps up one from lost castle or city
			}
			if (tile!=null) {
				SoundChitComponent soundChit = (SoundChitComponent)RealmComponent.getRealmComponent(soundChitObject);
				if (soundChit.isFaceUp()) {
					int clearing = soundChitObject.getThisInt("clearing");
					ArrayList<GameObject> tileChoices = new ArrayList<>();
					if (here.tile.getGameObject().equals(tile)) {
						gameObjects.add(soundChitObject);
						identifiers.add(tile.getName());
						for (GameObject t : clearingTiles) {
							TileComponent tc = new TileComponent(t);
							if (tc.getClearing(clearing)!=null) {
								// Moving sound from here to somewhere else
								tileChoices.addAll(clearingTiles);
							}
						}
						tileChoices.remove(here.tile.getGameObject());
					}
					else {
						if (here.tile.getClearing(clearing)!=null) {
							// Moving sound from somewhere else to here
							tileChoices.add(here.tile.getGameObject());
							gameObjects.add(soundChitObject);
							identifiers.add(tile.getName());
						}
					}
					secondaryTargets.put(tile.getName(),tileChoices);
				}
			}
		}
		return true;
	}
	public void updateSecondaryTargetsAfterSelection(TileLocation battleLocation, RealmComponent theTarget) {
		GameObject soundChitObject = theTarget.getGameObject();
		GameObject tile = soundChitObject.getHeldBy();
		if (tile!=null && !tile.hasThisAttribute("tile")) {
			tile = tile.getHeldBy(); // this jumps up one from lost castle or city
		}
		if (!battleLocation.tile.getGameObject().equals(tile)) return;
		
		secondaryTargets.clear();
		int clearing = soundChitObject.getThisInt("clearing");
		Collection<GameObject> clearingTiles = getFilteredTiles();
		ArrayList<GameObject> tileChoices = new ArrayList<>();
		for (GameObject t : clearingTiles) {
			TileComponent tc = new TileComponent(t);
			if (tc.getClearing(clearing)!=null) {
				tileChoices.add(t);
			}
		}
		tileChoices.remove(tile);
		secondaryTargets.put(tile.getName(),tileChoices);
	}
	
	private ArrayList<GameObject> getFilteredTiles() {
		GameData gameData = spell.getGameObject().getGameData();	
		Collection<GameObject> tiles = RealmObjectMaster.getRealmObjectMaster(gameData).getTileObjects();
		ArrayList<GameObject> clearingTiles = new ArrayList<>();
		for (GameObject tile : tiles) {
			TileComponent tc = (TileComponent)RealmComponent.getRealmComponent(tile);
			int clearingReq = spell.getGameObject().getThisInt("tile_req");
			if (tc.getClearingCount()>=clearingReq || clearingReq==0) {
				clearingTiles.add(tile);
			}
		}
		return clearingTiles;
	}
}