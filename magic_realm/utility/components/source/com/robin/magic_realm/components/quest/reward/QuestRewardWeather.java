package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.RealmCalendar;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardWeather extends QuestReward {
	
	public static final String WEATHER = "_Weahter";
	
	public QuestRewardWeather(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		RealmCalendar realmCalender = RealmCalendar.getCalendar(character.getGameData());
		realmCalender.setWeatherResult(RealmCalendar.getWeatherInt(getWeather()));
		boolean freezing = realmCalender.isFreezingWeather(character.getCurrentMonth());
		RealmUtility.updateWaterClearings(character.getGameData(),freezing);
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.Weather;
	}
	@Override
	public String getDescription() {
		return "Sets the weather to '"+getWeather()+"'.";
	}
	private String getWeather() {
		return getString(WEATHER);
	}
}