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
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementFly extends QuestRequirement {
	
	public static final String FLYING = "Flying";
	public static final String ABILITY_TO_FLY = "Ability to fly";
	public static final String FLY = "_fly";

	public QuestRequirementFly(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (mustFly() == true) {
			return character.getCurrentLocation()!=null && character.getCurrentLocation().isFlying();
		}
		return character.canFly(character.getCurrentLocation());
	}

	protected String buildDescription() {
		if (mustFly() == true) {
			return "The character must fly.";
		}
		return "Character must have the ability to fly in his current location.";		
	}

	public RequirementType getRequirementType() {
		return RequirementType.Fly;
	}
	
	private boolean mustFly() {
		return getString(FLY) == FLYING;
	}
}