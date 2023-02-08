package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardTalkToWiseBird extends QuestReward {
	
	public QuestRewardTalkToWiseBird(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		character.setDoInstantPeer(true);
	}
	
	public String getDescription() {
		return "Character does a free peer action.";
	}
	public RewardType getRewardType() {
		return RewardType.TalkToWiseBird;
	}
}