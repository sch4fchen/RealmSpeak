package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMarkTraveler extends QuestReward {
	
	public static final String TRAVELER_REGEX = "_regex";
	public static final String CHARACTERS_CLEARING = "_ch_cl";
	public static final String CHARACTERS_TILE = "_ch_tile";
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
				if (rc.isTraveler()) {
					travelers.add(rc.getGameObject());
				}
			}
		} else if (charactersTileOnly()) {
			TileLocation current = character.getCurrentLocation();
			travelers = new ArrayList<>();
			for (ClearingDetail cl : current.tile.getClearings()) {
				for (RealmComponent rc : cl.getClearingComponents()) {
					if (rc.isTraveler()) {
						travelers.add(rc.getGameObject());
					}
				}
			}
		} else {
			GamePool pool = new GamePool(character.getGameData().getGameObjects());
			travelers = pool.find("traveler");
		}
		String regex = getTravelerRegEx().trim();
		Pattern pattern = regex.length()==0?null:Pattern.compile(regex);
		ArrayList<GameObject> allTravelers = new ArrayList<>();
		String questId = getParentQuest().getGameObject().getStringId();
		for (GameObject go:travelers) {
			if (pattern==null || pattern.matcher(go.getName()).find()) {
				if (randomTraveler() || chooseTraveler()) {
					if (removeMark() && !Quest.GameObjectHasQuestMark(go,questId)) continue;
					if (!removeMark() && Quest.GameObjectHasQuestMark(go,questId)) continue;
					allTravelers.add(go);
				} else {
					if (removeMark()) {
						Quest.GameObjectRemoveQuestMark(go,questId);
					} else {
						Quest.GameObjectAddQuestMark(go,questId);
					}
				}
			}
		}
		if (allTravelers.size() == 0) return;
		if (chooseTraveler()) {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Choose one traveler to mark:",false);
			chooser.addGameObjects(allTravelers,false);
			chooser.setVisible(true);
			RealmComponent selectedTraveler = chooser.getFirstSelectedComponent();
			if (removeMark()) {
				Quest.GameObjectRemoveQuestMark(selectedTraveler.getGameObject(),questId);
			} else {
				Quest.GameObjectAddQuestMark(selectedTraveler.getGameObject(),questId);
			}
		}
		if (randomTraveler()) {
			if (removeMark()) {
				Quest.GameObjectRemoveQuestMark(allTravelers.get(RandomNumber.getRandom(allTravelers.size())),questId);
			} else {
				Quest.GameObjectAddQuestMark(allTravelers.get(RandomNumber.getRandom(allTravelers.size())),questId);
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
			if (charactersTileOnly()) {
				sb.append(" in current tile");
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
	
	private Boolean charactersTileOnly() {
		return getBoolean(CHARACTERS_TILE);
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