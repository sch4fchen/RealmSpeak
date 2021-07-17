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
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.quest.VulnerabilityType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementChit extends QuestRequirement {	
	public static final String TYPE = "_type";
	public static final String AMOUNT = "_amount";
	public static final String STRENGTH = "_strength";
	public static final String SPEED = "_speed";
	public static final String MAGIC_TYPE = "_magic_type";
	public static final String MAGIC_LEVEL = "_magic_level";
	public static final String ONLY_ACTIVE = "_only_active";
	public static final String NOT_FATIGUED = "_not_fatigued";
	public static final String NOT_WOUNDED = "_not_wounded";
	
	public enum ChitType {
		Any,
		Move,
		Fight,
		Magic,
		Fly
	}
		
	public QuestRequirementChit(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		ArrayList<CharacterActionChitComponent> chitsToCheck = character.getAllChits();
		ArrayList<CharacterActionChitComponent> chits = new ArrayList<>();
		switch(getType()) {
		case Move:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isMove()) chits.add(chit);
			}
			break;
		case Fight:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isFight()) chits.add(chit);
			}
			break;
		case Magic:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isMagic() && chit.getMagicNumber()>=getMagicLevel() && (getMagicType().matches("Any") || chit.getColorMagic().getColorName().matches(getMagicType()))) {
					chits.add(chit);
				}
			}
			break;
		case Fly:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isFly()) chits.add(chit);
			}
			break;
		case Any:
		default:
			chits.addAll(chitsToCheck);
			break;
		}
		chitsToCheck.clear();
		chitsToCheck.addAll(chits);
		chits.clear();
		for (CharacterActionChitComponent chit : chitsToCheck) {
				if (getStrength() != VulnerabilityType.Any && chit.getStrength().weakerTo(new Strength(getStrength().toString()))) continue;
				if (getSpeed() != 0 && chit.getSpeed().getNum()>getSpeed()) continue;
				if (onlyActive() && !chit.isActive()) continue;
				if (notFatigued() && chit.isFatigued()) continue;
				if (notWounded() && chit.isWounded()) continue;
				chits.add(chit);
		}
		
		if (chits.size()>=getAmount()) {
			return true;
		}
		return false;
	}

	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must have "+getAmount()+" ");
		if (getType() != ChitType.Any) {
			sb.append(getType()+" ");
		}
		sb.append("chit(s).");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Chit;
	}
	public ChitType getType() {
		return ChitType.valueOf(getString(TYPE));
	}
	private int getAmount() {
		return getInt(AMOUNT);
	}
	private VulnerabilityType getStrength() {
		return VulnerabilityType.valueOf(getString(STRENGTH));
	}
	private int getSpeed() {
		return getInt(SPEED);
	}
	private String getMagicType() {
		return getString(MAGIC_TYPE);
	}
	private int getMagicLevel() {
		return getInt(MAGIC_LEVEL);
	}
	private boolean onlyActive() {
		return getBoolean(ONLY_ACTIVE);
	}
	private boolean notFatigued() {
		return getBoolean(NOT_FATIGUED);
	}
	private boolean notWounded() {
		return getBoolean(NOT_WOUNDED);
	}
}