package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardSummonTraveler extends QuestReward {
	private static Logger logger = Logger.getLogger(QuestStep.class.getName());
	public static final String TRAVELER_NAME = "_tr";
	public static final String RANDOM_TRAVELER = "_rnd_tr";
	public static final String RANDOM_CLEARING = "_rc";
	public static final String SUMMON_TO_LOCATION = "_summon_loc";
	public static final String LOCATION = "_loc";
	public static final String MARK = "_mark";
	
	public QuestRewardSummonTraveler(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> travelersToSummone = new ArrayList<>();
		ArrayList<GameObject> travelerTemplatesToSummon = new ArrayList<>();
		GamePool pool = new GamePool(character.getGameData().getGameObjects());
		ArrayList<GameObject> allTravelerTemplates = pool.find(Constants.TRAVELER_TEMPLATE+",!"+Constants.USED);
		if (allTravelerTemplates==null || allTravelerTemplates.isEmpty()) {
			logger.fine("No traveler template found.");
			return;
		}
		if (randomTraveler()) {
			travelerTemplatesToSummon.add(allTravelerTemplates.get(RandomNumber.getRandom(allTravelerTemplates.size())));
		} else {
			String pattern = travelerName();
			for (GameObject go : allTravelerTemplates) {
				if (pattern!=null && pattern.matches(go.getName())) {
					travelerTemplatesToSummon.add(go);
				}
			}
		}
			
		for (GameObject template : travelerTemplatesToSummon) {
			template.setName(template.getName());
			template.copyAttributeBlockFrom(template,"this");
			template.removeThisAttribute(Constants.TRAVELER_TEMPLATE);
			template.setThisAttribute(Constants.TEMPLATE_ASSIGNED);
			template.addAll(template.getHold());
			template.setThisAttribute(Constants.USED);
			travelersToSummone.add(template);
		}

		for (GameObject traveler : travelersToSummone) {
			if (markTravelers()) {
				traveler.setThisAttribute(QuestConstants.QUEST_MARK,getParentQuest().getGameObject().getStringId());
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
				tileLocation.clearing.add(traveler,null);
			} else 	if (randomClearing()) {
				ArrayList<ClearingDetail> clearings = character.getCurrentLocation().tile.getClearings();
				int random = RandomNumber.getRandom(clearings.size());
				clearings.get(random).add(traveler,null);
			} else {
				character.getCurrentLocation().clearing.add(traveler,null);
			}
		}
	}
	
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Summons ");
		if (randomTraveler()) {
			sb.append(" a random traveler");
		} else {
			sb.append("a single or multiple travelers with the name "+travelerName());
		}
		if (locationOnly()) {
			sb.append(" to the location");
			if (getQuestLocation()!=null) {
				sb.append(" "+getQuestLocation());
			}
		} else if (randomClearing()) {
			sb.append("to a random clearing of characters tile");
		}
		sb.append(".");
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.SummonTraveler;
	}
	
	private String travelerName() {
		return getString(TRAVELER_NAME);
	}
	
	private boolean randomTraveler() {
		return getBoolean(RANDOM_TRAVELER);
	}
	
	private boolean randomClearing() {
		return getBoolean(RANDOM_CLEARING);
	}
	
	private boolean locationOnly() {
		return getBoolean(SUMMON_TO_LOCATION);
	}
	
	private boolean markTravelers() {
		return getBoolean(MARK);
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
}