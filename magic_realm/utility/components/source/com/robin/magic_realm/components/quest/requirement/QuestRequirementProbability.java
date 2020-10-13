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

public class QuestRequirementProbability extends QuestRequirement {
	public static final String CHANCE = "_rq";

	public QuestRequirementProbability(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		int chance = getValue();
		if (RandomNumber.getRandom(100)+1 <= chance) {
			return true;
		}
		return false;
	}

	protected String buildDescription() {
		int val = getValue();
		StringBuilder sb = new StringBuilder();
		sb.append("Probability of ");
		sb.append(val);
		sb.append("% that requirement is fullfilled, when tested.");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Probability;
	}
	public int getValue() {
		return getInt(CHANCE);
	}
}