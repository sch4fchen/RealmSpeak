package com.robin.magic_realm.components.quest;

import java.io.File;
import java.util.ArrayList;

import com.robin.game.objects.*;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class QuestLoader {
	private static int questCount = -1;

	public static boolean hasQuestsToLoad(CharacterWrapper character, HostPrefWrapper hostPrefs) {
		return getQuestCount(character, hostPrefs) > 0;
	}

	public static int getQuestCount(CharacterWrapper character, HostPrefWrapper hostPrefs) {
		if (questCount == -1) {
			questCount = findAvailableQuests(character, hostPrefs).size();
		}
		return questCount;
	}

	public static ArrayList<Quest> findAvailableQuests(CharacterWrapper character, HostPrefWrapper hostPrefs) {
		GamePool pool = new GamePool(character.getGameData().getGameObjects());
		ArrayList<String> query = new ArrayList<>();
		query.add(RealmComponent.QUEST);
		query.add("!"+Quest.STATE);
		ArrayList<GameObject> allUnassingedQuests = pool.find(query);
		ArrayList<Quest> quests = new ArrayList<>();
		for (GameObject go : allUnassingedQuests) {
			Quest quest = new Quest(go);
			if (quest.canChooseQuest(character, hostPrefs)) {
				if (hostPrefs.isUsingQuestCards() || (hostPrefs.isUsingBookOfQuests() && !quest.isEvent()) || hostPrefs.isUsingGuildQuests()) {
					quests.add(quest);
				}
			}
		}
		return quests;
	}
	
	private static String getQuestFolderPath() {
		String questFolderPath = "./quests/"; // default
		if (System.getProperty("questFolder") != null) {
			questFolderPath = System.getProperty("questFolder") + File.separator;
		}
		return questFolderPath;
	}
	
	public static Quest loadQuestByName(String name) {
		for(Quest quest:loadAllQuestsFromQuestFolder()) {
			if (quest.getName().equals(name)) return quest;
		}
		return null;
	}

	public static ArrayList<Quest> loadAllQuestsFromQuestFolder() {
		ArrayList<Quest> quests = new ArrayList<>();
		File questFolder = new File(getQuestFolderPath());
		if (questFolder.isDirectory() && questFolder.exists()) {
			File[] questFile = questFolder.listFiles();
			for (int i = 0; i < questFile.length; i++) {
				if (questFile[i].getAbsolutePath().endsWith(".rsqst")) {
					Quest quest = loadQuest(questFile[i].getAbsolutePath());
					if (quest!=null) {
						quest.autoRepair(); // Just in case
						quests.add(quest);
					}
				}
			}
		}
		return quests;
	}

	private static Quest loadQuest(String filePath) {
		GameData data = new GameData();
		data.ignoreRandomSeed = true;
		File file = new File(filePath);
		if (data.zipFromFile(file)) {
			Quest quest = new Quest(data.getGameObjects().iterator().next());
			if (quest.isValid()) {
				quest.filepath = filePath; // This is just here so that the builder can save a quest it just loaded for viewDeck() - not guaranteed!
				return quest;
			}
		}
		return null;
	}
}