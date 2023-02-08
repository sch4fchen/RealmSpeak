package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMakeWhole extends QuestReward {
	
	public QuestRewardMakeWhole(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		SpellUtility.heal(character);
		SpellUtility.repair(character);
	}
	
	public String getDescription() {
		return "Heals all fatigue and wounds, cancels wither curse and repairs items.";
	}
	public RewardType getRewardType() {
		return RewardType.MakeWhole;
	}
}