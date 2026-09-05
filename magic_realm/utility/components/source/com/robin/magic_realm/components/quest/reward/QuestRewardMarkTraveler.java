package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMarkTraveler extends QuestReward {
	
	public static final String TRAVELER_REGEX = "_regex";
	public static final String CHARACTERS_CLEARING = "_ch_cl";
	public static final String CHOOSE_TRAVELER = "_choose_tr";
	public static final String RANDOM_TRAVELER = "_rnd_tr";
	public static final String REMOVE = "_rmv_mrk";
	
	public QuestRewardMarkTraveler(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		ArrayList<GameObject> travelers;
		if (charactersClearingOnly()) {
			TileLocation current = character.getCurrentLocation();
			if (!current.isInClearing()) return;
			travelers = new ArrayList<>();
			for (RealmComponent rc : current.clearing.getClearingComponents()) {
				travelers.add(rc.getGameObject());
			}
		} else {
			GamePool pool = new GamePool(character.getGameData().getGameObjects());
			travelers = pool.find("traveler");
		}
		String regex = getTravelerRegEx().trim();
		Pattern pattern = regex.length()==0?null:Pattern.compile(regex);
		ArrayList<GameObject> allTravelers = new ArrayList<>();
		for (GameObject go:travelers) {
			if (pattern==null || pattern.matcher(go.getName()).find()) {
				if (randomTraveler() || chooseTraveler()) {
					allTravelers.add(go);
				} else {
					if (removeMark()) {
						Quest.GameObjectRemoveQuestMark(go,getParentQuest().getGameObject().getStringId());
					} else {
						Quest.GameObjectAddQuestMark(go,getParentQuest().getGameObject().getStringId());
					}
				}
			}
		}
		if (chooseTraveler()) {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Choose one traveler to mark:",false);
			chooser.addGameObjects(allTravelers,false);
			chooser.setVisible(true);
			RealmComponent selectedTraveler = chooser.getFirstSelectedComponent();
			if (removeMark()) {
				Quest.GameObjectRemoveQuestMark(selectedTraveler.getGameObject(),getParentQuest().getGameObject().getStringId());
			} else {
				Quest.GameObjectAddQuestMark(selectedTraveler.getGameObject(),getParentQuest().getGameObject().getStringId());
			}
		}
		if (randomTraveler()) {
			if (removeMark()) {
				Quest.GameObjectRemoveQuestMark(allTravelers.get(RandomNumber.getRandom(allTravelers.size())),getParentQuest().getGameObject().getStringId());
			} else {
				Quest.GameObjectAddQuestMark(allTravelers.get(RandomNumber.getRandom(allTravelers.size())),getParentQuest().getGameObject().getStringId());
			}
		}
	}

	public String getDescription() {
		if (randomTraveler() || chooseTraveler()) {
			StringBuffer sb = new StringBuffer();
			if (randomTraveler()) {
				sb.append("Mark a random traveler");
			} else {
				sb.append("Mark a traveler");
			}
			if (charactersClearingOnly()) {
				sb.append(" in current clearing");
			}
			sb.append(".");
			return sb.toString();
		}
		StringBuffer sb = new StringBuffer();
		sb.append("Mark all travelers");
		if (charactersClearingOnly()) {
			sb.append(" in current clearing");
		}
		sb.append(" matching the name: "+getTravelerRegEx());
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.MarkTraveler;
	}
	
	public String getTravelerRegEx() {
		return getString(TRAVELER_REGEX);
	}
	
	private Boolean charactersClearingOnly() {
		return getBoolean(CHARACTERS_CLEARING);
	}
	
	private Boolean chooseTraveler() {
		return getBoolean(CHOOSE_TRAVELER);
	}
	
	private Boolean randomTraveler() {
		return getBoolean(RANDOM_TRAVELER);
	}
	
	private Boolean removeMark() {
		return getBoolean(REMOVE);
	}
}