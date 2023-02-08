package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.RealmCalendar;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementWeather extends QuestRequirement {
	
	public static final String WEATHER = "_weather";
	public static final String WEATHER_ENABLED = "_weather_enabled";
	
	public QuestRequirementWeather(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		RealmCalendar realmCalender = RealmCalendar.getCalendar(character.getGameData());
		if (weatherEnabled()) {
			if (!realmCalender.isUsingWeather()) {
				return false;
			}
		}
		
		if (getWeather().isEmpty()) {
			return true;
		}
		
		String currentWeather = realmCalender.getWeatherTypeName(character.getCurrentMonth());
		return currentWeather.toLowerCase().matches(getWeather());
	}

	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		if (weatherEnabled()) {
			sb.append("Weather must be enabled. ");
		}
		if (!getWeather().isEmpty()) {
			sb.append("Weather must be '"+getWeather()+"'.");
		}
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Weather;
	}
	
	private String getWeather() {
		return getString(WEATHER).toLowerCase();
	}
	
	private boolean weatherEnabled() {
		return getBoolean(WEATHER_ENABLED);
	}
}