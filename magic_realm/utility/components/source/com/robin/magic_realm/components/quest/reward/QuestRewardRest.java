package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.swing.ChitRestManager;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardRest extends QuestReward {
	
	public static final String AMOUNT = "_amount";
	
	public QuestRewardRest(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		ChitRestManager rester = new ChitRestManager(frame,character,getAmount());
		rester.setVisible(true);
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.Rest;
	}
	@Override
	public String getDescription() {
		return "Character can rest " +getAmount() +" asterisks.";
	}
	private int getAmount() {
		return getInt(AMOUNT);
	}	
}