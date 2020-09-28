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

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.utility.TemplateLibrary;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardSummonMonster extends QuestReward {
	
	public static final String MONSTER_NAME = "_mn";
	
	public QuestRewardSummonMonster(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		GameObject template = TemplateLibrary.getSingleton().getCompanionTemplate(getMonsterKeyName(),getMonsterQuery());
		GameObject monster = TemplateLibrary.getSingleton().createCompanionFromTemplate(getGameData(),template);			
		character.getCurrentLocation().clearing.add(monster,null);	
	}
	
	public ImageIcon getIcon() {
		GameObject template = TemplateLibrary.getSingleton().getCompanionTemplate(getMonsterKeyName(),getMonsterQuery());
		RealmComponent rc = RealmComponent.getRealmComponent(template);
		return rc.getIcon();
	}
	
	public String getDescription() {
		return getMonsterKeyName()+" is placed in the characters clearing.";
	}

	public RewardType getRewardType() {
		return RewardType.SummonMonster;
	}
	
	public String getMonsterKeyName() {
		return getString(QuestConstants.KEY_PREFIX+MONSTER_NAME);
	}
	
	public String getMonsterQuery() {
		return getString(QuestConstants.VALUE_PREFIX+MONSTER_NAME);
	}
}