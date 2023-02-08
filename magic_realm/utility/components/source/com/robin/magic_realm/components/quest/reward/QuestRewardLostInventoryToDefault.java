package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardLostInventoryToDefault extends QuestReward {
	public static final String LOCATION = "_l";

	public QuestRewardLostInventoryToDefault(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		getParentQuest().setLostInventoryRule(RewardType.LostInventoryToDefault);
	}
	
	public String getDescription() {
		return "All lost inventory will be sent to default setup locations.";
	}

	public RewardType getRewardType() {
		return RewardType.LostInventoryToDefault;
	}
}