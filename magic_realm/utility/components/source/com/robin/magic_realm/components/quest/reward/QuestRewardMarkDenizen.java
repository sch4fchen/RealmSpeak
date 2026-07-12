package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMarkDenizen extends QuestReward {
	
	public static final String DENIZEN_REGEX = "_regex";
	public static final String DENIZEN_AMOUNT = "_amount";
	public static final String TILE = "_tile";
	public static final String MAP = "_map";
	public static final String REQ_MARK = "_req_mark";

	public QuestRewardMarkDenizen(GameObject go) {
		super(go);
	}
	
	public void processReward(JFrame frame,CharacterWrapper character) {
		TileLocation current = character.getCurrentLocation();
		String regex = getDenizenRegEx().trim();
		Pattern pattern = regex.length()==0?null:Pattern.compile(regex);
		int markedDenizen = 0;
		ArrayList<RealmComponent> denizens = new ArrayList<>();
		if (allOnMap()) {
			GamePool pool = new GamePool(character.getGameData().getGameObjects());
			for (GameObject go : pool.find("denizen")) {
				denizens.add(RealmComponent.getRealmComponent(go));
			}
		} else {
			if (allInTile()) {
				for (ClearingDetail cl : current.tile.getClearings()) {
					denizens.addAll(cl.getClearingComponents());
				}
			} else {
				if (!current.isInClearing()) return;
				denizens.addAll(current.clearing.getClearingComponents());
			}
		}
		
		if (getDenizenAmount()!=0) {
			Collections.shuffle(denizens);
		}
		
		String questId = getParentQuest().getGameObject().getStringId();
		for (RealmComponent rc:denizens) {
			if (requiresMark() && !Quest.GameObjectHasQuestMark(rc.getGameObject(),questId)) continue;
			if (pattern==null || pattern.matcher(rc.getGameObject().getName()).find()) {
				Quest.GameObjectAddQuestMark(rc.getGameObject(),getParentQuest().getGameObject().getStringId());
				markedDenizen++;
				if (getDenizenAmount()!=0 && markedDenizen>=getDenizenAmount()) return;
			}
		}
	}

	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		int number = getDenizenAmount();
		if (number != 0) {
			sb.append("Mark up to "+number+" denizens");
		} else {
			sb.append("Mark all denizens");
		}
		if (allOnMap()) {
			sb.append(" on the map");
		} else {
			sb.append(" in characters");
			if (allInTile()) {
				sb.append(" tile");
			} else {
				sb.append(" clearing");
			}
		}
		if (getDenizenRegEx()!=null && !getDenizenRegEx().isEmpty()) {
			sb.append(" matching the name: "+getDenizenRegEx());
		}
		sb.append(".");
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.MarkDenizen;
	}

	public String getDenizenRegEx() {
		return getString(DENIZEN_REGEX);
	}
	
	public int getDenizenAmount() {
		return getInt(DENIZEN_AMOUNT);
	}
	
	public boolean allInTile() {
		return getBoolean(TILE);
	}
	public boolean allOnMap() {
		return getBoolean(MAP);
	}
	public boolean requiresMark() {
		return getBoolean(REQ_MARK);
	}
}