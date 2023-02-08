package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardInformation extends QuestReward {

	public static final String INFORMATION_TEXT = "_it";

	public QuestRewardInformation(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		Quest.showQuestMessage(frame, getParentQuest(), getDescription(), getTitleForDialog());
	}

	public String getDescription() {
		return getString(INFORMATION_TEXT);
	}

	public RewardType getRewardType() {
		return RewardType.Information;
	}
}