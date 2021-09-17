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
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementCastMultipleSpells extends QuestRequirement {
	public static final String NUMBER_OF_SPELLS = "_nos";
	public static String UNIQUE = "_unique";
	private int numberOfSpellsToBeCasted = 0;
	private List<String> spellsCasted = new ArrayList<>();

	public QuestRequirementCastMultipleSpells(GameObject go) {
		super(go);
	}

	@Override
	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (reqParams.actionType != CharacterActionType.CastSpell) {
			return numberOfSpellsToBeCasted == getAmount();
		}
		
		if (getUnique()) {
			String spell = reqParams.objectList.get(0).getName();
			if (!spellsCasted.contains(spell)) {
				spellsCasted.add(spell);
				numberOfSpellsToBeCasted = numberOfSpellsToBeCasted+1;
			}	
		}
		else {
			numberOfSpellsToBeCasted = numberOfSpellsToBeCasted+1;
		}
		
		return numberOfSpellsToBeCasted == getAmount();
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
}