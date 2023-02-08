package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestDeck;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardResetQuestToDeck extends QuestReward {

	public QuestRewardResetQuestToDeck(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		Quest quest = getParentQuest();
		QuestDeck deck = QuestDeck.findDeck(character.getGameData());
		character.removeQuest(quest);
		quest.reset();

		if (quest.isAllPlay()) {
			deck.addAllPlayCard(quest);
		}
		else {
			deck.addCards(quest, 1);
		}
		deck.shuffle();
	}
	
	public String getDescription() {
		return "Quest is reset and shuffled back into quest deck.";
	}

	public RewardType getRewardType() {
		return RewardType.ResetQuestToDeck;
	}
}