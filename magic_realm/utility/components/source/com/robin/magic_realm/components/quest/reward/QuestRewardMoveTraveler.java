package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMoveTraveler extends QuestReward {	

	public QuestRewardMoveTraveler(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
	}

	public String getDescription() {
		return "";

	}

	public RewardType getRewardType() {
		return RewardType.MoveTraveler;
	}
}