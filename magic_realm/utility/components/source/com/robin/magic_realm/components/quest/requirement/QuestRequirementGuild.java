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
package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementGuild extends QuestRequirement {

	public static final String GUILD = "_gld";
	public static final String GUILD_LEVEL = "_gld_lvl";
	public static final String EXCEED_LEVEL = "_excd_lvl";
	public static final String SUBCEED_LEVEL = "_sucd_lvl";
	
	public QuestRequirementGuild(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (character.getCurrentGuild() != null && (getGuildName().isEmpty() || character.getCurrentGuild().matches(getGuildName()))) {
			if (exceedAllowed()) {
				return character.getCurrentGuildLevel() >= getGuildLevel();
			}
			if (subceedAllowed()) {
				return character.getCurrentGuildLevel() <= getGuildLevel();
			}
			return character.getCurrentGuildLevel() == getGuildLevel();
			
		}
		return false;
	}

	protected String buildDescription() {
		if (getGuildName().isEmpty()) {
			return "Character must be level "+getGuildLevel()+" member in any guild.";
		}
		return "Character must be level "+getGuildLevel()+" member in the "+getGuildName()+".";
	}
	public RequirementType getRequirementType() {
		return RequirementType.Guild;
	}
	private int getGuildLevel() {
		return getInt(GUILD_LEVEL);
	}
	private String getGuildName() {
		return getString(GUILD);
	}
	private boolean exceedAllowed() {
		return getBoolean(EXCEED_LEVEL);
	}
	private boolean subceedAllowed() {
		return getBoolean(SUBCEED_LEVEL);
	}
}