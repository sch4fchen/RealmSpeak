package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardNoCombat extends QuestReward {
		
	public QuestRewardNoCombat(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		character.getGameObject().setThisAttribute(Constants.NO_COMBAT);
	}
	
	public String getDescription() {
		return "Character does not participate in combat this round.";
	}

	public RewardType getRewardType() {
		return RewardType.NoCombat;
	}
}