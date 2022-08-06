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
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.DayKey;

public class QuestRequirementProbability extends QuestRequirement {
	public static final String CHANCE = "_rq";
	public static final String MAX_NUMBER_OF_CHECKS = "_max_number_of_checks";
	private static final String TIME_OF_CHECK = "_time_of_check";
	private static final String NUMBER_OF_CHECKS = "_number_of_checks";

	public QuestRequirementProbability(GameObject go) {
		super(go);
		setNumberOfChecks(0);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (getTimeOfCheck() == null) {
			setTimeOfCheck(character.getCurrentDayKey());
			setNumberOfChecks(0);
		}
		if (getTimeOfCheck().before(new DayKey(character.getCurrentDayKey()))) {
			setTimeOfCheck(character.getCurrentDayKey());
			setNumberOfChecks(0);
		}
		
		if (getMaxNumberOfChecks() != 0 && getNumberOfChecks() >= getMaxNumberOfChecks()) {
			return false;
		}
		setNumberOfChecks(getNumberOfChecks()+1);
		
		int chance = getChance();
		if (RandomNumber.getRandom(100)+1 <= chance) {
			return true;
		}
		return false;
	}

	protected String buildDescription() {
		int val = getChance();
		StringBuilder sb = new StringBuilder();
		sb.append("Probability of ");
		sb.append(val);
		sb.append("% that requirement is fullfilled, when tested.");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Probability;
	}
	public int getChance() {
		return getInt(CHANCE);
	}
	private int getMaxNumberOfChecks() {
		return getInt(MAX_NUMBER_OF_CHECKS);
	}
	private DayKey getTimeOfCheck() {
		if (getString(TIME_OF_CHECK)==null) return null;
		return new DayKey(getString(TIME_OF_CHECK));
	}
	private void setTimeOfCheck(String dayKey) {
		setString(TIME_OF_CHECK, dayKey);
	}
	private int getNumberOfChecks() {
		return  getInt(NUMBER_OF_CHECKS);
	}
	private void setNumberOfChecks(int number) {
		setInt(NUMBER_OF_CHECKS, number);
	}
}