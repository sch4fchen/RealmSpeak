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
	private int startCount = 0;
	private int count = 0;
	
	public QuestCounter(GameObject go) {
		super(go);
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
	}
	public String getBlockName() {
		return Quest.QUEST_BLOCK;
	}
	public String getTagName() {
		return TAG_FRONT + getName() + TAG_END;
	}
	public int getStartCount() {
		return startCount;
	}
	public void setStartCount(int initialValue) {
		startCount = initialValue;
	}
	public void increaseCounter() {
		increaseCounterByValue(1);
	}
	public void increaseCounterByValue(int value) {
		setCount(getCount() + value);
	}
	public void decreaseCounter() {
		decreaseCounterByValue(1);
	}
	public void decreaseCounterByValue(int value) {
		setCount(getCount() - value);
	}
	public void setCounter(int value) {
		setCount(value);
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}