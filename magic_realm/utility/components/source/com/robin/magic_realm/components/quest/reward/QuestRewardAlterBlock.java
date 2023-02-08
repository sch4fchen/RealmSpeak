package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.GainType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardAlterBlock extends QuestReward {
	
	public static final String GAIN_TYPE = "_gt";

	public QuestRewardAlterBlock(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		boolean blocked = getGainType()==GainType.Gain;
		character.setBlocked(blocked);
	}
	
	public String getDescription() {
		return "Character becomes "+(getGainType()==GainType.Gain?"BLOCKED":"UNBLOCKED");
	}

	public RewardType getRewardType() {
		return RewardType.AlterBlock;
	}
	
	public GainType getGainType() {
		return GainType.valueOf(getString(GAIN_TYPE));
	}
}