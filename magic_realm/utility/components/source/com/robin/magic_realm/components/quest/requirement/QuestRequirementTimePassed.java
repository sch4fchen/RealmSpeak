package com.robin.magic_realm.components.quest.requirement;

import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.DayKey;

public class QuestRequirementTimePassed extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementTimePassed.class.getName());
	
	public static final String VALUE = "_rq";

	public QuestRequirementTimePassed(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		int daysNeeded = getValue();
		QuestStep step = getParentStep();
		DayKey start = step.getQuestStepStartTime();
		if (start==null) {
			logger.fine("Quest step has no start time?  This is a bug.");
			return false;
		}
		DayKey now = new DayKey(reqParams.dayKey);
		int daysPassed = now.compareTo(start);
		boolean ret = daysPassed>=daysNeeded;
		if (!ret) {
			logger.fine("Only "+daysPassed+" days have passed.  Expecting "+daysNeeded+".");
		}
		return ret;
	}

	protected String buildDescription() {
		int val = getValue();
		StringBuilder sb = new StringBuilder();
		sb.append("Must wait ");
		sb.append(val);
		sb.append(" day");
		sb.append(val==1?"":"s");
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.TimePassed;
	}
	public int getValue() {
		return getInt(VALUE);
	}
}