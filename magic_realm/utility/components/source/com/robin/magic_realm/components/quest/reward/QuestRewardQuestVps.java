package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardQuestVps extends QuestReward {	
	public static final String AMOUNT = "_amount";
	public static final String SUBSTRACT = "_substract";
	public static final String BONUS_VP = "_bonus_vp";
		
	public QuestRewardQuestVps(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		if (bonus()) {
			int vps = substract()?-getAmount():getAmount();
			character.addQuestBonusVps(vps);
			return;
		}
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
		StringBuilder sb = new StringBuilder();
		if (substract()) {
			sb.append("Substract "+"VPs of ");
		}
		else {
			sb.append("Add "+getAmount()+" to the ");
		}
		if (bonus()) {
			sb.append("bonus quest VP of the character.");
		} else {
			sb.append("quest VP reward of the quest.");
		}
		return sb.toString();
	}
	public RewardType getRewardType() {
		return RewardType.QuestVps;
	}
	private int getAmount() {
		return getInt(AMOUNT);
	}
	private boolean substract() {
		return getBoolean(SUBSTRACT);
	}
	private boolean bonus() {
		return getBoolean(BONUS_VP);
	}
}