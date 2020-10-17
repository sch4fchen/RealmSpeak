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
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardGuild extends QuestReward {
	public static final String GUILD = "_guild";
	public static final String GUILD_LEVEL = "_guild_lvl";
	
	public QuestRewardGuild(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		if (getGuildName().matches(QuestConstants.REMOVE)) {
			character.clearGuild();
			return;
		}
		if (!getGuildName().matches(QuestConstants.CURRENT)) {
			character.setCurrentGuild(getGuildName());
		}
		character.setCurrentGuildLevel(getGuildLevel());
	}
	
		
	public String getDescription() {
		if (getGuildName().matches(QuestConstants.REMOVE)) {
			return "Removes the characters guild membership.";
		}
		if (getGuildName().matches(QuestConstants.CURRENT)) {
			return "Set characters current guild level to "+getGuildLevel()+".";
		}
		return "Set characters guild level of "+getGuildName()+" to "+getGuildLevel()+".";
	}
	public RewardType getRewardType() {
		return RewardType.Guild;
	}
	private int getGuildLevel() {
		return getInt(GUILD_LEVEL);
	}
	private String getGuildName() {
		return getString(GUILD);
	}
}