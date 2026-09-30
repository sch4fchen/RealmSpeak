package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardDread extends QuestReward {

	public static final String REMOVE = "_remove";
	
	public QuestRewardDread(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		if (remove()) {
			character.clearDread();
			return;
		}
		character.applyDread(frame);
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.Dread;
	}
	@Override
	public String getDescription() {
		if (remove()) {
			return "Remove Dread from the character.";
		}
		return "Add Dread to the character.";
	}
	private Boolean remove() {
		return getBoolean(REMOVE);
	}
}