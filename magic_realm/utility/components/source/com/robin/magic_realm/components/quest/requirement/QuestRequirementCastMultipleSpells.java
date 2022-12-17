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