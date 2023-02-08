package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMagicColor extends QuestReward {
	
	public static final String COLOR = "_color";
	public static final String REMOVE = "_remove";
	public static final String LOCATION = "_loc";
	public static final String AFFECT = "_affeact";
	
	public static final String CHARACTERS_CLEARING = "Characters clearing";
	public static final String CHARACTERS_TILE = "Characters tile";
	public static final String LOC_RANDOM_CLEARING = "Random clearing of the location";
	public static final String LOC_RANDOM_TILE = "Random tile of the location";
	public static final String LOC_ALL_TILES = "All tiles of the location";
	public static final String ALL = "All clearings in the realm";
	
	public QuestRewardMagicColor(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<ClearingDetail> clearingsToAffect = new ArrayList<ClearingDetail>();
		switch (getTarget()) {
			default:
			case CHARACTERS_CLEARING:
				clearingsToAffect.add(character.getCurrentClearing());
				break;
			case CHARACTERS_TILE:
				clearingsToAffect.addAll(character.getCurrentLocation().tile.getClearings());
				break;
			case LOC_RANDOM_CLEARING:
				QuestLocation loc1 = getQuestLocation();
				ArrayList<TileLocation> loc1Tiles = loc1.fetchAllLocations(frame, character, character.getGameData());
				int random1 = RandomNumber.getRandom(loc1Tiles.size());
				clearingsToAffect.add(loc1Tiles.get(random1).clearing);
				break;
			case LOC_RANDOM_TILE:
				QuestLocation loc2 = getQuestLocation();
				ArrayList<TileLocation> loc2Tiles = loc2.fetchAllLocations(frame, character, character.getGameData());
				int random2 = RandomNumber.getRandom(loc2Tiles.size());
				clearingsToAffect.addAll(loc2Tiles.get(random2).tile.getClearings());
				break;
			case LOC_ALL_TILES:
				QuestLocation loc3 = getQuestLocation();
				ArrayList<TileLocation> loc3Tiles = loc3.fetchAllLocations(frame, character, character.getGameData());
				for (TileLocation tileLoc : loc3Tiles) {
					clearingsToAffect.addAll(tileLoc.tile.getClearings());
				}
				break;
			case ALL:
				GamePool pool = new GamePool(character.getGameData().getGameObjects());
				for (GameObject go : pool.find("tile")) {
					RealmComponent rc = RealmComponent.getRealmComponent(go);
					if (rc != null && rc.isTile()) {
						TileComponent tc = (TileComponent) rc;
						clearingsToAffect.addAll(tc.getClearings());
					}
				}
				break;
		}
		ColorMagic color = ColorMagic.makeColorMagic(getColor(), false);
		for (ClearingDetail clearing : clearingsToAffect) {
			clearing.setMagic(color.getColorNumber()-1, !remove());
		}
	}
	
	private String getColor() {
		return getString(COLOR);
	}
	
	private boolean remove() {
		return getBoolean(REMOVE);
	}
	
	private String getTarget() {
		return getString(AFFECT);
	}
	
	@Override
	public String getDescription() {
		if (remove()) {
			return getColor()+" Magic is removed from "+getTarget()+".";
		}
		return getTarget()+" is/are provided with "+getColor()+" Magic.";
	}

	public RewardType getRewardType() {
		return RewardType.MagicColor;
	}

	
	public void setQuestLocation(QuestLocation location) {
		setString(LOCATION,location.getGameObject().getStringId());
	}
	public QuestLocation getQuestLocation() {
		String id = getString(LOCATION);
		if (id!=null) {
			GameObject go = getGameData().getGameObject(Long.valueOf(id));
			if (go!=null) {
				return new QuestLocation(go);
			}
		}
		return null;
	}
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup,LOCATION);
	}
}