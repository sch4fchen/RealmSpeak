package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestState;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardDeactivateQuest extends QuestReward {
	
	public QuestRewardDeactivateQuest(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		Quest quest = getParentQuest();
		quest.setState(QuestState.Assigned,character.getCurrentDayKey(), character);
	}
	
	public String getDescription() {
		return "Quest is deactivated.";
	}

	public RewardType getRewardType() {
		return RewardType.DeactivateQuest;
	}
}