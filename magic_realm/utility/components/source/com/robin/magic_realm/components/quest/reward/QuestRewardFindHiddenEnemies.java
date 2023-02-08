package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardFindHiddenEnemies extends QuestReward {
	
	public QuestRewardFindHiddenEnemies(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		character.setFoundHiddenEnemies(true);
	}
	
	public String getDescription() {
		return "Character finds hidden enemies.";
	}

	public RewardType getRewardType() {
		return RewardType.FindHiddenEnemies;
	}
}