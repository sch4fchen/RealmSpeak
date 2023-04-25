package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardQuestPoints extends QuestReward {	
	public static final String AMOUNT = "_amount";
	public static final String SUBSTRACT = "_substract";
		
	public QuestRewardQuestPoints(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		int oldVp = getParentQuest().getInt(QuestConstants.VP_REWARD);
		int newVp;
		if (substract()) {
			newVp = oldVp-getAmount();
		}
		else {
			newVp = oldVp+getAmount();
		}	
		getParentQuest().setInt(QuestConstants.VP_REWARD,newVp);
	}
	
	public String getDescription() {
		if (substract()) {
			return "Substract "+getAmount()+" VPs of the VP reward of the quest.";
		}
		return "Add "+getAmount()+" to the VP reward of the quest.";
	}
	public RewardType getRewardType() {
		return RewardType.QuestPoints;
	}
	private int getAmount() {
		return getInt(AMOUNT);
	}
	private boolean substract() {
		return getBoolean(SUBSTRACT);
	}
}