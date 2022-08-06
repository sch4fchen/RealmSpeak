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
import com.robin.general.swing.DieRoller;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.quest.DieRollType;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementHideResult extends QuestRequirement {

	public static final String DIE_ROLL = "_dr";
	
	public QuestRequirementHideResult(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		DieRoller roller = DieRollBuilder.getDieRollBuilder(frame,character).createHideRoller();
		if (roller.getHighDieResult() < getDieRoll()) { 
			return true;
		}
		return false;
	}

	protected String buildDescription() {
		if (getString(DIE_ROLL)!=DieRollType.Random.toString()) {
			return "Requires a successful hide roll below or equal to "+getDieRoll()+".";
		}
		return "Requires a successful hide roll below or equal to a random value (1-6).";
	}

	public RequirementType getRequirementType() {
		return RequirementType.HideResult;
	}
	
	public int getDieRoll() {
		String dieRoll = getString(DIE_ROLL);
		switch (DieRollType.valueOf(dieRoll)) {
			case One:
				return 1;
			case Two:
				return 2;
			case Three:
				return 3;
			case Four:
				return 4;
			case Five:
				return 5;
			case Six:
				return 6;
			default:
				return RandomNumber.getDieRoll(6);
		}
	}
}