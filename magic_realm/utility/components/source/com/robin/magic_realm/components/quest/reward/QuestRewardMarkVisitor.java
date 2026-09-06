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

public class QuestRewardMarkVisitor extends QuestReward {
	
	public static final String VISITOR_REGEX = "_regex";
	public static final String CHARACTERS_CLEARING = "_ch_cl";
	public static final String CHARACTERS_TILE = "_ch_tile";
	public static final String CHOOSE_VISITOR = "_choose_visitor";
	public static final String RANDOM_VISITOR = "_rnd_visitor";
	public static final String REMOVE = "_rmv_mrk";
	
	public QuestRewardMarkVisitor(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		ArrayList<GameObject> visitors;
		if (charactersClearingOnly()) {
			TileLocation current = character.getCurrentLocation();
			if (!current.isInClearing()) return;
			visitors = new ArrayList<>();
			for (RealmComponent rc : current.clearing.getClearingComponents()) {
				if (rc.isVisitor()) {
					visitors.add(rc.getGameObject());
				}
			}
		} else if (charactersTileOnly()) {
			TileLocation current = character.getCurrentLocation();
			visitors = new ArrayList<>();
			for (ClearingDetail cl : current.tile.getClearings()) {
				for (RealmComponent rc : cl.getClearingComponents()) {
					if (rc.isVisitor()) {
						visitors.add(rc.getGameObject());
					}
				}
			}
		} else {
			GamePool pool = new GamePool(character.getGameData().getGameObjects());
			visitors = pool.find("visitor");
		}
		String regex = getVisitorRegEx().trim();
		Pattern pattern = regex.length()==0?null:Pattern.compile(regex);
		ArrayList<GameObject> allVisitors = new ArrayList<>();
		String questId = getParentQuest().getGameObject().getStringId();
		for (GameObject go:visitors) {
			if (pattern==null || pattern.matcher(go.getName()).find()) {
				if (randomVisitor() || chooseVisitor()) {
					if (removeMark() && !Quest.GameObjectHasQuestMark(go,questId)) continue;
					if (!removeMark() && Quest.GameObjectHasQuestMark(go,questId)) continue;
					allVisitors.add(go);
				} else {
					if (removeMark()) {
						Quest.GameObjectRemoveQuestMark(go,questId);
					} else {
						Quest.GameObjectAddQuestMark(go,questId);
					}
				}
			}
		}
		if (allVisitors.size() == 0) return;
		if (chooseVisitor()) {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Choose one visitor to mark:",false);
			chooser.addGameObjects(allVisitors,false);
			chooser.setVisible(true);
			RealmComponent selectedVisitor = chooser.getFirstSelectedComponent();
			if (removeMark()) {
				Quest.GameObjectRemoveQuestMark(selectedVisitor.getGameObject(),questId);
			} else {
				Quest.GameObjectAddQuestMark(selectedVisitor.getGameObject(),questId);
			}
		}
		if (randomVisitor()) {
			if (removeMark()) {
				Quest.GameObjectRemoveQuestMark(allVisitors.get(RandomNumber.getRandom(allVisitors.size())),questId);
			} else {
				Quest.GameObjectAddQuestMark(allVisitors.get(RandomNumber.getRandom(allVisitors.size())),questId);
			}
		}
	}

	public String getDescription() {
		if (randomVisitor() || chooseVisitor()) {
			StringBuffer sb = new StringBuffer();
			if (randomVisitor()) {
				sb.append("Mark a random visitor");
			} else {
				sb.append("Mark a visitor");
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
		sb.append("Mark all visitors");
		if (charactersClearingOnly()) {
			sb.append(" in current clearing");
		}
		sb.append(" matching the name: "+getVisitorRegEx());
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.MarkVisitor;
	}
	
	public String getVisitorRegEx() {
		return getString(VISITOR_REGEX);
	}
	
	private Boolean charactersClearingOnly() {
		return getBoolean(CHARACTERS_CLEARING);
	}
	
	private Boolean charactersTileOnly() {
		return getBoolean(CHARACTERS_TILE);
	}
	
	private Boolean chooseVisitor() {
		return getBoolean(CHOOSE_VISITOR);
	}
	
	private Boolean randomVisitor() {
		return getBoolean(RANDOM_VISITOR);
	}
	
	private Boolean removeMark() {
		return getBoolean(REMOVE);
	}
}