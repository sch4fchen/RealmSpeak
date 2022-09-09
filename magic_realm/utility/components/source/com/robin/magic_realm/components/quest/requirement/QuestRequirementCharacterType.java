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

import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.DenizenType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementCharacterType extends QuestRequirement {

	public static final String REGEX_FILTER = "_regex";
	public static final String TYPE = "_type";
	
	public QuestRequirementCharacterType(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		RealmComponent rc = RealmComponent.getRealmComponent(character.getGameObject());
		GameObject transmorph = character.getTransmorph();
		RealmComponent transmorphRc = null;
		if (transmorph != null) {
			transmorphRc = RealmComponent.getRealmComponent(transmorph);
		}
		switch(getType()) {
			case Denizen:
				if (!rc.isDenizen() && transmorph != null && !transmorphRc.isDenizen()) return false;
				break;
			case Native:
				if (!rc.isNative() && transmorph != null && !transmorphRc.isNative()) return false;
				break;
			case Monster:
				if (!rc.isMonster() && transmorph != null && !transmorphRc.isMonster()) return false;
				break;
			case Animal:
				if (transmorph != null && !transmorphRc.isTransformAnimal()) return false;
				break;
			case Character:
				if (!rc.isCharacter()) return false;
				break;
			case Mist:
				if (!character.isMistLike()) return false;
				break;
			case Statue:
				if (!character.isStatue()) return false;
				break;
			case Any:
			default:
				break;
		}
		if (getRegExFilter() != null && !getRegExFilter().isEmpty()) {
			Pattern pattern = Pattern.compile(getRegExFilter());
			return pattern.matcher(character.getName()).find() || (transmorph != null && pattern.matcher(transmorph.getName()).find());
		}
		return true;
	}

	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must be ");
		if (getType() != DenizenType.Any) {
			sb.append("a "+getType());
		}
		if (getRegExFilter() != null && !getRegExFilter().isEmpty()) {
			if (getType() != DenizenType.Any) {
				sb.append(" and ");
			}
			sb.append("match " +getRegExFilter());
		}
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.CharacterType;
	}
	
	private DenizenType getType() {
		return DenizenType.valueOf(getString(TYPE));
	}
	
	public String getRegExFilter() {
		return getString(REGEX_FILTER);
	}
}