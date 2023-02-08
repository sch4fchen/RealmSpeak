package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestDeck;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardDiscardQuest extends QuestReward {

	public QuestRewardDiscardQuest(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		Quest quest = getParentQuest();
		QuestDeck deck = QuestDeck.findDeck(character.getGameData());
		character.removeQuest(quest);
		deck.discardCard(quest);
	}
	
	public String getDescription() {
		return "Quest is discarded.";
	}

	public RewardType getRewardType() {
		return RewardType.DiscardQuest;
	}
}