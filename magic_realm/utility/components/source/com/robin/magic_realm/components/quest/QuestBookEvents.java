package com.robin.magic_realm.components.quest;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestBookEvents extends GameObjectWrapper {
	
	private static String QUEST_BOOK_KEY = "__qb_key_";
	private static String QUEST_EVENT_LIST = "_q_ev_lst";
	private static String QUEST_UNIQUE_ID_GENERATOR = "_uidg";
	private static String QUEST_EVENT_TEMPLATE = "_q_ev_template";
	
	public QuestBookEvents(GameObject go) {
		super(go);
	}
	public String getBlockName() {
		return "QuestBookEvents";
	}
	private int generateUniqueId() {
		int id = getInt(QUEST_UNIQUE_ID_GENERATOR);
		setInt(QUEST_UNIQUE_ID_GENERATOR,id+1);
		return id;
	}
	
	public void addEvent(Quest quest) {
		quest.setBoolean(QUEST_EVENT_TEMPLATE,true);
		quest.setInt(Quest.QUEST_UNIQUE_ID,generateUniqueId()); // All events get a unique id on entry.
		addListItem(QUEST_EVENT_LIST,quest.getGameObject().getStringId());
	}
	
	public void setupEvents(JFrame frame,CharacterWrapper character) {
		for(Quest card:getEvents()) {
			if (card.getState()!=QuestState.New && !card.isMultipleUse()) continue; // skip all play cards that are no longer new (completed or failed)
			Quest quest = card.copyQuestToGameData(getGameData());
			quest.setState(QuestState.Assigned, character.getCurrentDayKey(), character); // indicates when the quest was first assigned
			character.addQuest(frame,quest);
		}
	}
	private ArrayList<GameObject> getEventsAsObjects() {
		ArrayList<GameObject> allPlay = new ArrayList<>();
		ArrayList<String> list = getList(QUEST_EVENT_LIST);
		if (list!=null && list.size()>0) {
			for(String questId : list) {
				GameObject go = getGameData().getGameObject(Long.valueOf(questId));
				allPlay.add(go);
			}
		}
		return allPlay;
	}
	private ArrayList<Quest> getEvents() {
		ArrayList<Quest> events = new ArrayList<>();
		for(GameObject go:getEventsAsObjects()) {
			Quest quest = new Quest(go);
			events.add(quest);
		}
		return events;
	}
	
	public ArrayList<String> getAllEventNames() {
		ArrayList<String> events = new ArrayList<>();
		for(GameObject go:getEventsAsObjects()) {
			events.add(go.getName());
		}
		return events;
	}
	
	public static Long BOOK_ID = null;
	public static QuestBookEvents findBook(GameData data) {
		if (BOOK_ID==null) {
			GamePool pool = new GamePool(data.getGameObjects());
			GameObject go = pool.findFirst(QUEST_BOOK_KEY);
			if (go!=null) {
				BOOK_ID = go.getId();
				return new QuestBookEvents(go);
			}
		}
		else {
			return new QuestBookEvents(data.getGameObject(BOOK_ID));
		}
		
		// None found?  Better make one.
		GameObject go = data.createNewObject();
		go.setName("Created by QuestBookEvents");
		go.setThisAttribute(QUEST_BOOK_KEY);
		
		QuestBookEvents book = new QuestBookEvents(go);
		BOOK_ID = Long.valueOf(go.getId());
		
		return book;
	}
}