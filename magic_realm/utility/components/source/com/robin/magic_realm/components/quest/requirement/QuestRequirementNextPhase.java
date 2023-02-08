package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.GamePhaseType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.DayKey;

public class QuestRequirementNextPhase extends QuestRequirement {
	public static final String PHASES_TO_SKIP = "_ph_skip";
	private static final String PHASE_STARTED = "_ph_started";
	private static final String DAY_STARTED = "_day_started";

	public QuestRequirementNextPhase(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		GamePhaseType currentPhase = character.getCurrentGamePhase();
		DayKey currentDay = new DayKey(reqParams.dayKey);
		if (getPhaseStarted() == GamePhaseType.Unspecified) {
			this.setString(PHASE_STARTED, currentPhase.toString());
			this.setString(DAY_STARTED, currentDay.toString());
			return false;
		}
		
		int targetPhase = (getPhaseNumber(getPhaseStarted())+getNumberOfSkipPhases())%4;
		DayKey targetDay = getDayStarted().addDays((getPhaseNumber(getPhaseStarted())+getNumberOfSkipPhases())/4);
				
		if ((getPhaseNumber(currentPhase) >= targetPhase && currentDay.equals(targetDay)) || currentDay.after(targetDay)) {
			return true;
		}
		
		return false;
	}

	private int getNumberOfSkipPhases() {
		return getInt(PHASES_TO_SKIP);
	}
	private GamePhaseType getPhaseStarted() {
		if (getString(PHASE_STARTED) == null || getString(PHASE_STARTED).isEmpty()) return GamePhaseType.Unspecified;
		return GamePhaseType.valueOf(getString(PHASE_STARTED));
	}
	private DayKey getDayStarted() {
		String dayKey = getString(DAY_STARTED);
		return dayKey == null ? null : new DayKey(dayKey);
	}
	private static int getPhaseNumber(GamePhaseType phase) {
		switch (phase) {
		case Birdsong:
			return 0;
		case EndOfPhase:
			return 1;
		case EndOfTurn:
			return 2;
		case StartOfEvening:
			return 3;
		case Unspecified:
			return -1;
		default: 
			return 4;
		}
	}
	
	protected String buildDescription() {
		int val = +getNumberOfSkipPhases();
		StringBuilder sb = new StringBuilder();
		sb.append("Must wait ");
		sb.append(val);
		sb.append(" phase");
		sb.append(val==1?"":"s");
		sb.append(" after first time testing this requirement.");
		return sb.toString();
	}
	
	public RequirementType getRequirementType() {
		return RequirementType.NextPhase;
	}
}