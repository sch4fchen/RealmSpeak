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
package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestCounter;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardCounter extends QuestReward {
	public static final String COUNTER = "_c";
	public static final String SET_COUNT = "_sv";
	public static final String INCREASE_COUNT = "_iv";
	public static final String DECREASE_COUNT = "_dv";
	
	public QuestRewardCounter(GameObject go) {
		super(go);
	}
	
	public void processReward(JFrame frame,CharacterWrapper character) {
		QuestCounter counter = getQuestCounter();
		if (needToIncreaseQuestCount()) {
			counter.increaseCountByValue(getValueToIncrease());
		}
		if (needToDecreaseQuestCount()) {
			counter.decreaseCountByValue(getValueToDecrease());
		}
		if (needToSetQuestCount()) {
			counter.setCount(getValueToSet());
		}
	}
	
	public String getDescription() {
		QuestCounter questCounter = getQuestCounter();
		if (questCounter==null) return "ERROR - No counter found!";
		
		StringBuffer sb = new StringBuffer();
		sb.append(getQuestCounter().getName());
		sb.append(" is changed by " +totalChange());
		if (needToSetQuestCount()) {
			sb.append(" and is set to " +getValueToSet());
		}
		sb.append(".");
		return sb.toString();
	}
	public RewardType getRewardType() {
		return RewardType.Counter;
	}
	public QuestCounter getQuestCounter() {
		String id = getString(COUNTER);
		if (id!=null) {
			GameObject go = getGameData().getGameObject(Long.valueOf(id));
			if (go!=null) {
				return new QuestCounter(go);
			}
		}
		return null;
	}
	public int getValueToSet() {
		return getInt(SET_COUNT);
	}
	public int getValueToIncrease() {
		if (needToIncreaseQuestCount()) {
			return getInt(INCREASE_COUNT);
		}
		else {
			return 0;
		}
	}
	public int getValueToDecrease() {
		if (needToDecreaseQuestCount()) {
			return getInt(DECREASE_COUNT);
		}
		else {
			return 0;
		}
	}
	private int totalChange() {
		return getValueToIncrease()-getValueToDecrease();
	}
	private boolean needToSetQuestCount() {
		return getValueToSet() != QuestConstants.ALL_VALUE;
	}
	private boolean needToIncreaseQuestCount() {
		return getValueToIncrease() != QuestConstants.ALL_VALUE;
	}
	private boolean needToDecreaseQuestCount() {
		return getValueToDecrease() != QuestConstants.ALL_VALUE;
	}
}