package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestState;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardActivateQuest extends QuestReward {
	
	public QuestRewardActivateQuest(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		Quest quest = getParentQuest();
		if (quest.getState()==QuestState.Assigned) {
			quest.setState(QuestState.Active,character.getCurrentDayKey(), character);
		}
	}
	
	public String getDescription() {
		return "Quest is activated.";
	}

	public RewardType getRewardType() {
		return RewardType.ActivateQuest;
	}
}