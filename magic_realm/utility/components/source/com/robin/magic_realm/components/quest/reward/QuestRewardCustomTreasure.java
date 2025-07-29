package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.table.Loot;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardCustomTreasure extends QuestReward {
	private static Logger logger = Logger.getLogger(QuestStep.class.getName());
	public static String TREASURE_REGEX = "_regex";
	public static String TREASURE_NAME = "_name";
	public static String TREASURE_PRICE = "_price";
	public static String TREASURE_FAME = "_fame";
	public static String TREASURE_NOTORIETY = "_notoriety";
	public static String TREASURE_WEIGHT = "_weight";
	public static String TREASURE_SIZE = "_size";
	public static String TREASURE_GREAT = "_great";
	public static String LOCATION_ONLY = "_loc_only";
	public static String LOCATION = "_loc";
	
	public static String NO_CHANGE = "unmodified";
	public static String SMALL = "small";
	public static String LARGE = "large";
	public static String NOT_GREAT = "not_great";
	public static String GREAT = "great";
	
	public QuestRewardCustomTreasure(GameObject go) {
		super(go);
	}
	
	public void processReward(JFrame frame,CharacterWrapper character) {
		if (getTreasureRegex().isEmpty()) return;
		GameObject treasure = character.getGameData().getGameObjectByNameIgnoreCase(getTreasureRegex());
		if (treasure == null) return;
		GameObject customTreasure = treasure.copy();
		customTreasure.setName(getTreasureName());
		if (getTreasurePrice()!=QuestConstants.ALL_VALUE) {
			customTreasure.setThisAttribute("base_price", getTreasurePrice());
		}
		if (getTreasureFame()!=QuestConstants.ALL_VALUE) {
			customTreasure.setThisAttribute("fame", getTreasureFame());
		}
		if (getTreasureNotoriety()!=QuestConstants.ALL_VALUE) {
			customTreasure.setThisAttribute("notoriety", getTreasureNotoriety());
		}
		if (getTreasureWeight()!=NO_CHANGE) {
			customTreasure.setThisAttribute(Constants.WEIGHT, getTreasureWeight());
			if (customTreasure.getThisAttribute(Constants.WEIGHT) == "N") {
				customTreasure.removeThisAttribute(Constants.WEIGHT);
			}
		}
		if (getTreasureSize()!=NO_CHANGE) {
			customTreasure.setThisAttribute("treasure", getTreasureSize());
		}
		if (getTreasureIsGreat()!=NO_CHANGE) {
			if (getTreasureIsGreat()==NOT_GREAT) {
				customTreasure.removeThisAttribute("great");
			}
			if (getTreasureIsGreat()==GREAT) {
				customTreasure.setThisAttribute("great", "");
			}
		}
		if (locationOnly()) {
			QuestLocation loc = getQuestLocation();
			if (loc == null) return;
			ArrayList<TileLocation> validLocations = new ArrayList<>();
			validLocations = loc.fetchAllLocations(frame, character, getGameData());
			if(validLocations.isEmpty()) {
				logger.fine("QuestLocation "+loc.getName()+" doesn't have any valid locations!");
				return;
			}
			int random = RandomNumber.getRandom(validLocations.size());
			TileLocation tileLocation = validLocations.get(random);
			tileLocation.clearing.add(customTreasure,null);
		}
		else {
			Loot.addItemToCharacter(frame,null,character,customTreasure);
		}
	}

	private String getTreasureRegex() {
		return getString(TREASURE_REGEX);
	}
	private String getTreasureName() {
		return getString(TREASURE_NAME);
	}
	private int getTreasurePrice() {
		return getInt(TREASURE_PRICE);
	}
	private int getTreasureFame() {
		return getInt(TREASURE_FAME);
	}
	private int getTreasureNotoriety() {
		return getInt(TREASURE_NOTORIETY);
	}
	private String getTreasureWeight() {
		return getString(TREASURE_WEIGHT);
	}
	private String getTreasureSize() {
		return getString(TREASURE_SIZE);
	}
	private String getTreasureIsGreat() {
		return getString(TREASURE_GREAT);
	}
	
	public String getDescription() {
		if (locationOnly() && getQuestLocation() != null) {
			return "Custom treasure "+getTreasureName()+" is placed in "+getQuestLocation().getName();
		}
		return "Custom treasure "+getTreasureName()+" is given to the character.";
	}
	public RewardType getRewardType() {
		return RewardType.CustomTreasure;
	}
	private boolean locationOnly() {
		return getBoolean(LOCATION_ONLY);
	}
	public boolean usesLocationTag(String tag) {
		QuestLocation loc = getQuestLocation();
		return loc!=null && tag.equals(loc.getName());
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
	public void setQuestLocation(QuestLocation location) {
		setString(LOCATION,location.getGameObject().getStringId());
	}
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup,LOCATION);
	}
}