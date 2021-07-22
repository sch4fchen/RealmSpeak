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

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.ChitItemType;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMarkItem extends QuestReward {	
	public static final String ITEM_CHITTYPES = "_ict";
	public static final String ITEM_REGEX = "_irx";
	public static final String ITEM_INVENTORY = "_iinv";
	public static final String SINGLE_ITEM = "_single_i";

	public QuestRewardMarkItem(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		ArrayList<GameObject> objects;
		if (mustBeInInventory()) {
			objects = QuestRewardItem.getObjectList(character.getInventory(),getChitTypes(),getItemRegex());
		}
		else {
			objects = QuestRewardItem.getObjectList(character.getGameData().getGameObjects(),getChitTypes(),getItemRegex());
		}
		if(onlySingleItem()){
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Choose one item to mark:",false);
			chooser.addGameObjects(objects,false);
			chooser.setVisible(true);
			RealmComponent item = chooser.getFirstSelectedComponent();
			item.getGameObject().setThisAttribute(QuestConstants.QUEST_MARK,getParentQuest().getGameObject().getStringId());
			return;
		}
		for (GameObject item : objects) {
			item.setThisAttribute(QuestConstants.QUEST_MARK,getParentQuest().getGameObject().getStringId());
		}
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		if(onlySingleItem()) {
			sb.append("Marks single item");
		}
		else {
			sb.append("Marks all items");
		}
		String regex = getItemRegex();
		if (regex!=null && regex.trim().length()>0) {
			sb.append(", matching /");
			sb.append(regex);
			sb.append("/");
		}
		if(mustBeInInventory()) {
			sb.append(" in character's inventory");
		}
		sb.append(".");
		return sb.toString();

	}

	public RewardType getRewardType() {
		return RewardType.MarkItem;
	}
	public ArrayList<ChitItemType> getChitTypes() {
		return ChitItemType.listToTypes(getList(ITEM_CHITTYPES));
	}
	public String getItemRegex() {
		return getString(ITEM_REGEX);
	}
	public boolean mustBeInInventory() {
		return getBoolean(ITEM_INVENTORY);
	}
	public boolean onlySingleItem() {
		return getBoolean(SINGLE_ITEM);
	}
}