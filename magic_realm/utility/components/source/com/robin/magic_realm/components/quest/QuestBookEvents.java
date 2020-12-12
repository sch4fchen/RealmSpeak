/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.components.quest;

import java.util.ArrayList;
import java.util.Iterator;

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
			if (card.getState()!=QuestState.New) continue; // skip all play cards that are no longer new (completed or failed)
			Quest quest = card.copyQuestToGameData(getGameData());
			quest.setState(QuestState.Assigned, character.getCurrentDayKey(), character); // indicates when the quest was first assigned
			character.addQuest(frame,quest);
		}
	}
	private ArrayList<GameObject> getEventsAsObjects() {
		ArrayList<GameObject> allPlay = new ArrayList<GameObject>();
		ArrayList list = getList(QUEST_EVENT_LIST);
		if (list!=null && list.size()>0) {
			for(Iterator i=list.iterator();i.hasNext();) {
				String questId = (String)i.next();
				GameObject go = getGameData().getGameObject(Long.valueOf(questId));
				allPlay.add(go);
			}
		}
		return allPlay;
	}
	private ArrayList<Quest> getEvents() {
		ArrayList<Quest> events = new ArrayList<Quest>();
		for(GameObject go:getEventsAsObjects()) {
			Quest quest = new Quest(go);
			events.add(quest);
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
		BOOK_ID = new Long(go.getId());
		
		return book;
	}
}