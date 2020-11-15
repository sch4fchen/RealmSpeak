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

public class QuestRequirementWeather extends QuestRequirement {
	
	public static final String WEATHER = "_weather";
	
	public QuestRequirementWeather(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		RealmCalendar realmCalender = RealmCalendar.getCalendar(character.getGameData());
		String currentWeather = realmCalender.getWeatherTypeName(character.getCurrentMonth());
		
		return currentWeather.toLowerCase().matches(getWeather());
	}

	protected String buildDescription() {
		return "Weather must be '"+getWeather()+"'.";
	}

	public RequirementType getRequirementType() {
		return RequirementType.Weather;
	}
	
	private String getWeather() {
		return getString(WEATHER).toLowerCase();
	}
}