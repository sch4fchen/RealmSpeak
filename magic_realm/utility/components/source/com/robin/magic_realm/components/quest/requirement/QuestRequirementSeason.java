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
import com.robin.magic_realm.components.utility.RealmCalendar;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementSeason extends QuestRequirement {
	
	public static final String SEASON = "_season";

	public QuestRequirementSeason(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		RealmCalendar realmCalender = RealmCalendar.getCalendar(character.getGameData());
		GameObject currentSeason = realmCalender.getCurrentSeason(character.getCurrentMonth());
		
		return currentSeason.getName().matches(getSeason());
	}

	protected String buildDescription() {
		return "Season must be '"+getSeason()+"'.";
	}

	public RequirementType getRequirementType() {
		return RequirementType.Season;
	}
	
	private String getSeason() {
		return getString(SEASON);
	}
}