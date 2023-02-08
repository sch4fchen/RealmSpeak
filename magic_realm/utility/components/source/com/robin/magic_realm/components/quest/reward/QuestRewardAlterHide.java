package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.GainType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardAlterHide extends QuestReward {
	
	public static final String GAIN_TYPE = "_gt";

	public QuestRewardAlterHide(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		boolean hidden = getGainType()==GainType.Gain;
		character.setHidden(hidden);
	}
	
	public String getDescription() {
		return "Character becomes "+(getGainType()==GainType.Gain?"HIDDEN":"UNHIDDEN");
	}

	public RewardType getRewardType() {
		return RewardType.AlterHide;
	}
	
	public GainType getGainType() {
		return GainType.valueOf(getString(GAIN_TYPE));
	}
}