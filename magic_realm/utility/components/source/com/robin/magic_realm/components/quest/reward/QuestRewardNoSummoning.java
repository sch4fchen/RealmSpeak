package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardNoSummoning extends QuestReward {
		
	public QuestRewardNoSummoning(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		character.getGameObject().setThisAttribute(Constants.NO_SUMMONING);
	}
	
	public String getDescription() {
		return "Character does not summon monsters this round.";
	}

	public RewardType getRewardType() {
		return RewardType.NoSummoning;
	}
}