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

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementHirelings extends QuestRequirement {
	
	public static final String HIRELING_REGEX = "_regex";
	public static final String AMOUNT = "_amount";
	public static final String MUST_FOLLOW = "_must_follow";

	public QuestRequirementHirelings(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		ArrayList<RealmComponent> hirelings;
		int amount = 0;
		if (mustFollow())
			hirelings = character.getFollowingHirelings();
		else {
			hirelings = character.getAllHirelings();
		}
		for (RealmComponent hireling : hirelings) {
			if (getRegExFilter().isEmpty() || hireling.getGameObject().getName().matches("(.*)"+getRegExFilter()+"(.*)")) {
				amount++;
			}
		}
		return amount >= getAmount();
	}

	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must own "+getAmount()+" "+getRegExFilter()+" hireling(s)");
		if (mustFollow()) {
			sb.append("following the character");
		}
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Hirelings;
	}
	
	private String getRegExFilter() {
		return getString(HIRELING_REGEX).trim();
	}
	private int getAmount() {
		return getInt(AMOUNT);
	}
	private boolean mustFollow() {
		return getBoolean(MUST_FOLLOW);
	}
}