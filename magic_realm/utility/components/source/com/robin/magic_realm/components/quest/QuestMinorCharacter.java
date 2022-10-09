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
import java.util.Arrays;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GameObjectBlockManager;
import com.robin.game.objects.GameObjectWrapper;
import com.robin.general.util.OrderedHashtable;
import com.robin.magic_realm.components.utility.Constants;

public class QuestMinorCharacter extends GameObjectWrapper {
	
	public static final String VIRTUAL = "_v";
	public static final String DESCRIPTION = "_d";
	
	public static final String ABILITY_BLOCK_NAME = "_abn";
	public static final String ABILITY_TYPE = "_at";
	public static final String ABILITY_DESCRIPTION = "_ad";
	public static final String BONUS_CHIT_GENERATED = "_bcg";
	
	private static final String[] IGNORE = {
		ABILITY_TYPE,
		ABILITY_DESCRIPTION,
	};
	
	public QuestMinorCharacter(GameObject obj) {
		super(obj);
	}
	public ArrayList<String> getAllAbilityBlockNames() {
		ArrayList<String> abilityBlockNames = new ArrayList<>();
		int n=0;
		String blockName;
		while(getGameObject().hasAttributeBlock(blockName=ABILITY_BLOCK_NAME+n)) {
			abilityBlockNames.add(blockName);
			n++;
		}
		return abilityBlockNames;
	}
	public String toString() {
		return getName();
	}
	public void setupAbilities() {
		ArrayList<String> ignore = new ArrayList<>(Arrays.asList(IGNORE));
		for(String abilityBlockName:getAllAbilityBlockNames()) {
			OrderedHashtable block = getGameObject().getAttributeBlock(abilityBlockName);
			for(Object o:block.keySet()) {
				String key = (String)o;
				if (ignore.contains(key)) continue;
				Object val = block.get(key);
				if (val instanceof ArrayList) {
					ArrayList<String> list = new ArrayList((ArrayList)val);
					ArrayList<String> current = getGameObject().getThisAttributeList(key);
					if (current!=null && !current.isEmpty()) {
						list.addAll(current);
					}
					getGameObject().setThisAttributeList(key,list);
				}
				else {
					getGameObject().setThisAttribute(key,(String)val);
				}
			}
			getGameObject().removeAttributeBlock(abilityBlockName);
		}
	}
	public void setupBonusChit() {
		if (getGameObject().hasThisAttribute(BONUS_CHIT_GENERATED)) return;
		getGameObject().setThisAttribute(BONUS_CHIT_GENERATED);
		GameObjectBlockManager man = new GameObjectBlockManager(getGameObject());
		GameObject bonusChit = man.extractGameObjectFromBlocks(Constants.BONUS_CHIT + "design", false);
		if (bonusChit != null) {
			bonusChit.setThisAttribute("icon_type", getGameObject().getThisAttribute("icon_type"));
			bonusChit.setThisAttribute("icon_folder", getGameObject().getThisAttribute("icon_folder"));
			bonusChit.setThisAttribute(Constants.CHIT_EARNED);
			getGameObject().add(bonusChit);
		}
	}
	public void init() {
		getGameObject().setThisAttribute(Quest.QUEST_MINOR_CHARS);
	}
	public String getBlockName() {
		return Quest.QUEST_BLOCK;
	}
	public void setDescription(String val) {
		setString(DESCRIPTION,val);
	}
	
	public String getDescription() {
		return getString(DESCRIPTION);
	}
	
	public boolean isVirtual() {
		return getBoolean(VIRTUAL);
	}
	public void setVirtual(boolean val) {
		setBoolean(VIRTUAL,val);
	}
}