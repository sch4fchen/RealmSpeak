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

import com.robin.game.objects.*;

public class QuestCounter extends GameObjectWrapper {
	private static String TAG_FRONT = "<";
	private static String TAG_END = ">";
	private final String COUNT = "count";
	
	public QuestCounter(GameObject go) {
		super(go);
		go.setThisAttribute(COUNT, "0");
	}
	public QuestCounter(GameObject go, int count) {
		super(go);
		go.setThisAttribute(COUNT, count);
	}
	public Quest getParentQuest() {
		GameObject quest = getGameObject().getHeldBy();
		return new Quest(quest);
	}
	public String toString() {
		return getName();
	}
	public void init() {
		getGameObject().setThisAttribute(Quest.QUEST_COUNTER);
		getGameObject().setThisAttribute(COUNT, 0);
	}
	public String getBlockName() {
		return Quest.QUEST_BLOCK;
	}
	public String getTagName() {
		return TAG_FRONT + getName() + TAG_END;
	}
	public void increaseCount() {
		increaseCountByValue(1);
	}
	public void increaseCountByValue(int value) {
		setCount(getCount() + value);
	}
	public void decreaseCount() {
		decreaseCountByValue(1);
	}
	public void decreaseCountByValue(int value) {
		setCount(getCount() - value);
	}
	public int getCount() {
		return getGameObject().getThisInt(COUNT);
	}
	public void setCount(int count) {
		getGameObject().setThisAttribute(COUNT, count);
	}
}