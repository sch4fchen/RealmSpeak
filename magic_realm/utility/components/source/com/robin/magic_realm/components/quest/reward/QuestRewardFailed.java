package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestState;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardFailed extends QuestReward {
	public QuestRewardFailed(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		getParentQuest().setState(QuestState.Failed,character.getCurrentDayKey(), character);
	}

	public String getDescription() {
		return "Quest Failed";
	}

	public RewardType getRewardType() {
		return RewardType.QuestFailed;
	}
}