package com.robin.magic_realm.RealmQuestBuilder;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.components.quest.Quest;

public class QuestBuilderModel {
	
	private Quest quest;
	
	public QuestBuilderModel() {
		initNewQuest();
	}
	public Quest getQuest() {
		return quest;
	}
	public void initNewQuest() {
		GameData gameData = new GameData("QuestBuilder");
		quest = new Quest(gameData.createNewObject());
		quest.init();
		quest.setName("Untitled Quest");
		quest.setDescription("This is a quest.");
		quest.createQuestStep(false);
	}
	public void loadQuest() {
	}
	public void saveQuest() {
	}
}