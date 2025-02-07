package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.quest.TargetValueType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.DayKey;

public class QuestRequirementKillGuardian extends QuestRequirement {
	
	private static Logger logger = Logger.getLogger(QuestRequirementKillGuardian.class.getName());

	public static final String GUARDIAN_AND_SITE = "_guardian_site";
	public static final String TARGET_VALUE_TYPE = "_tvt";
	
	public QuestRequirementKillGuardian(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame,CharacterWrapper character,QuestRequirementParams reqParams) {
		logger.fine(buildDescription());
		QuestStep step = getParentStep();
		DayKey earliestTime = new DayKey(1,1);
		TargetValueType tvt = getTargetValueType();
		switch (tvt) {
			case Game:
				earliestTime = new DayKey(1,1);
				break;
			case Quest:
				earliestTime = step.getQuestStartTime();
				break;
			case Step:
				earliestTime = step.getQuestStepStartTime();
				break;
			case Day:
				earliestTime = new DayKey(character.getCurrentDayKey());
				break;
		}
		ArrayList<String> allDayKeys = character.getAllDayKeys();
		if (allDayKeys==null) {
			logger.fine("Character hasn't had a turn yet.");
			return false;
		}
		for(String dayKeyString:allDayKeys) {
			DayKey dayKey = new DayKey(dayKeyString);
			if (dayKey.before(earliestTime)) continue; // ignore kills on days before the earliest allowable date
			ArrayList<GameObject> kills = character.getKills(dayKeyString);
			for(GameObject kill:kills) {
				if (kill.getName().toLowerCase().matches(getGuardian().trim().toLowerCase())
						&& kill.getThisAttribute("setup_start").toLowerCase().matches(getSite().toLowerCase())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must kill ");
		sb.append(getGuardian()+"("+getSite()+")");
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.KillGuardian;
	}
	
	public String getGuardian() {
		StringTokenizer tokenizer = new StringTokenizer(getString(GUARDIAN_AND_SITE), "(");
		return tokenizer.nextToken();
	}
	
	public String getSite() {
		StringTokenizer tokenizer = new StringTokenizer(getString(GUARDIAN_AND_SITE), "(");
		tokenizer.nextToken();
		String siteWithBracket = tokenizer.nextToken();
		return siteWithBracket.replace("(","").replace(")","");
	}
	
	public TargetValueType getTargetValueType() {
		return TargetValueType.valueOf(getString(TARGET_VALUE_TYPE));
	}

}