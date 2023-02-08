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