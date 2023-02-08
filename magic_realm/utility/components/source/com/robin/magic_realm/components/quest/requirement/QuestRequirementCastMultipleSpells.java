package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.quest.TargetValueType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.DayKey;

public class QuestRequirementCastMultipleSpells extends QuestRequirement {
	public static final String NUMBER_OF_SPELLS = "_nos";
	public static String UNIQUE = "_unique";
	public static final String TARGET_VALUE_TYPE = "_tvt";

	public QuestRequirementCastMultipleSpells(GameObject go) {
		super(go);
	}

	@Override
	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
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
		
		List<String> spellsCasted = new ArrayList<>();
		ArrayList<String> allDayKeys = character.getAllDayKeys();
		if (allDayKeys==null) {
			return false;
		}
		for(String dayKeyString:allDayKeys) {
			DayKey dayKey = new DayKey(dayKeyString);
			if (dayKey.before(earliestTime)) continue; // ignore spells on days before the earliest allowable date
			for (GameObject spell : character.getCastedSpells(dayKeyString)) {
				if (!getUnique() || !spellsCasted.contains(spell.getName())) {
					spellsCasted.add(spell.getName());
				}
			}
		}

		return spellsCasted.size() >= getAmount();
	}

	@Override
	public RequirementType getRequirementType() {
		return RequirementType.CastMultipleSpells;
	}

	@Override
	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must cast "+getAmount());
		if (getUnique()) {
			sb.append(" different");
		}
		sb.append(" spell(s).");
		return sb.toString();
	}

	public int getAmount() {
		return getInt(NUMBER_OF_SPELLS);
	}
	public boolean getUnique() {
		return getBoolean(UNIQUE);
	}
	public TargetValueType getTargetValueType() {
		if (getString(TARGET_VALUE_TYPE) == null) { // compatibility for old quests
			return TargetValueType.Game;
		}
		return TargetValueType.valueOf(getString(TARGET_VALUE_TYPE));
	}
}