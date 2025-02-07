package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardQuestSticky extends QuestReward {
	
	public static final String UNSTICKY = "_unsticky";
	
	public QuestRewardQuestSticky(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		getParentQuest().setSticky(!unsticky());
	}
	
	public String getDescription() {
		if (unsticky()) {
			return "Quest can be exchanged for another quest again.";
		}
		return "Quest cannot be exchanged for another quest anymore.";
	}

	public RewardType getRewardType() {
		return RewardType.QuestSticky;
	}
	
	private Boolean unsticky() {
		return getBoolean(UNSTICKY);
	}
}