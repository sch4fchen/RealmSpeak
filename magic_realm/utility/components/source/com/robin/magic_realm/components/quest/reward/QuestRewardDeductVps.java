package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardDeductVps extends QuestReward {	
	public static final String AMOUNT = "_amount";
	public static final String ADD_VPS = "_add";
		
	public QuestRewardDeductVps(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		if (addVps()) {
			character.addDeductVPs(-getAmount());
			return;
		}
		character.addDeductVPs(getAmount());
	}
	
	public String getDescription() {
		return "Character must deduct "+getAmount()+" VPs.";
	}
	public RewardType getRewardType() {
		return RewardType.DeductVps;
	}
	private int getAmount() {
		return getInt(AMOUNT);
	}
	private boolean addVps() {
		return getBoolean(ADD_VPS);
	}
}