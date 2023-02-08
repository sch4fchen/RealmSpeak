package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMarkDenizen extends QuestReward {
	
	public static final String DENIZEN_REGEX = "_regex";
	public static final String DENIZEN_AMOUNT = "_amount";

	public QuestRewardMarkDenizen(GameObject go) {
		super(go);
	}
	
	public void processReward(JFrame frame,CharacterWrapper character) {
		TileLocation current = character.getCurrentLocation();
		if (!current.isInClearing()) return;
		String regex = getDenizenRegEx().trim();
		Pattern pattern = regex.length()==0?null:Pattern.compile(regex);
		int markedDenizen = 0;
		ArrayList<RealmComponent> denizens = current.clearing.getClearingComponents();
		if (getDenizenAmount()!=0) {
			Collections.shuffle(denizens);
		}
		for(RealmComponent rc:denizens) {
			if (pattern==null || pattern.matcher(rc.getGameObject().getName()).find()) {
				rc.getGameObject().setThisAttribute(QuestConstants.QUEST_MARK,getParentQuest().getGameObject().getStringId());
				markedDenizen++;
				if (getDenizenAmount()!=0 && markedDenizen>=getDenizenAmount()) return;
			}
		}
	}

	public String getDescription() {
		int number = getDenizenAmount();
		if (number != 0) {
			return "Mark up to "+number+" denizens in current clearing matching name: "+getDenizenRegEx();
		}
		return "Mark all denizens in current clearing matching name: "+getDenizenRegEx();
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
}