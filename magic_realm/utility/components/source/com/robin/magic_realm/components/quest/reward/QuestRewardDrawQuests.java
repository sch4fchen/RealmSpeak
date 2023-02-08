package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestDeck;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardDrawQuests extends QuestReward {

	public QuestRewardDrawQuests(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		QuestDeck deck = QuestDeck.findDeck(character.getGameData());
		deck.drawCards(frame, character);
	}
	
	public String getDescription() {
		return "Character draws quest card(s).";
	}

	public RewardType getRewardType() {
		return RewardType.DrawQuests;
	}
}