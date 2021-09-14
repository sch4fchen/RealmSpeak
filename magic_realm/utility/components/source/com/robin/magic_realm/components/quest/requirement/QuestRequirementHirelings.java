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
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.BattleChit;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.MonsterPartChitComponent;
import com.robin.magic_realm.components.NativeChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.quest.VulnerabilityType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementHirelings extends QuestRequirement {
	
	public static final String HIRELING_REGEX = "_regex";
	public static final String AMOUNT = "_amount";
	public static final String MUST_FOLLOW = "_must_follow";
	public static final String SAME_LOCATION = "_with_character";
	public static final String VULNERARBILITY = "_vulnerability";
	public static final String ATTACK_STRENGTH = "_strength";
	public static final String ATTACK_SPEED = "_attack_speed";
	public static final String ATTACK_LENGTH = "_attack_length";
	public static final String SHARPNESS = "_sharpness";
	public static final String MISSILE = "_missile";
	public static final String MOVE_SPEED = "_move_speed";
	public static final String FLY_SPEED = "_fly_speed";
	public static final String ARMORED = "_armored";
	public static final String CHECK_BOTH_SIDES = "_both_sides";
	public static final String INCLUDE_WEAPONS = "_include_weapons";

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
		Pattern pattern = Pattern.compile(getRegExFilter());
		for (RealmComponent hireling : hirelings) {
			if (getRegExFilter().isEmpty() || pattern.matcher(hireling.getGameObject().getName()).find()) {
				if (sameLocation() && !(hireling.getCurrentLocation().tile == character.getCurrentLocation().tile && hireling.getCurrentLocation().clearing == character.getCurrentLocation().clearing)) continue;
				if (checkStats()) {
					BattleChit denizen = null;
					Strength vul = new Strength();
					Strength str = new Strength();
					int sharp = 0;
					Boolean armored = false;	
					if (hireling.isNative()) {
						denizen = (NativeChitComponent) hireling;
						vul = ((NativeChitComponent) denizen).getVulnerability();
						str = ((NativeChitComponent) denizen).getStrength();
						sharp = ((NativeChitComponent) denizen).getSharpness();
						armored = ((NativeChitComponent) denizen).isArmored();
						if (checkBothSides()) {
							denizen.flip();
							Strength vul2 = ((NativeChitComponent) denizen).getVulnerability();
							vul = vul.strongerOrEqualTo(vul2) ? vul : vul2;
							Strength str2 = ((NativeChitComponent) denizen).getStrength();
							str = str.strongerOrEqualTo(str2) ? str : str2;
							sharp = Math.max(sharp, ((NativeChitComponent) denizen).getSharpness());
							armored = armored ? armored : ((NativeChitComponent) denizen).isArmored();
							denizen.flip();
						}
					}
					else if (hireling.isMonster()) {
						denizen = (MonsterChitComponent) hireling;
						vul = ((MonsterChitComponent) denizen).getVulnerability();
						str = ((MonsterChitComponent) denizen).getStrength();
						sharp = ((MonsterChitComponent) denizen).getSharpness();
						armored = ((MonsterChitComponent) denizen).isArmored();
						if (checkBothSides()) {
							denizen.flip();
							Strength vul2 = ((MonsterChitComponent) denizen).getVulnerability();
							vul = vul.strongerOrEqualTo(vul2) ? vul : vul2;
							Strength str2 = ((MonsterChitComponent) denizen).getStrength();
							str = str.strongerOrEqualTo(str2) ? str : str2;
							sharp = Math.max(sharp, ((MonsterChitComponent) denizen).getSharpness());
							armored = armored ? armored : ((MonsterChitComponent) denizen).isArmored();
							denizen.flip();
						}
					}
					else {
						continue;
					}
					Speed attackSpeed = denizen.getAttackSpeed();
					Integer length = denizen.getLength();
					boolean isMissile = denizen.isMissile();
					Speed moveSpeed = denizen.getMoveSpeed();
					Speed flySpeed = denizen.getFlySpeed();
					if (checkBothSides()) {
						denizen.flip();
						Speed attackSpeed2 = denizen.getAttackSpeed();
						attackSpeed = attackSpeed.fasterThanOrEqual(attackSpeed2) ? attackSpeed : attackSpeed2;
						Speed moveSpeed2 = denizen.getMoveSpeed();
						length = Math.max(length, denizen.getLength());
						isMissile = isMissile ? isMissile : denizen.isMissile();
						moveSpeed = moveSpeed.fasterThanOrEqual(moveSpeed2) ? moveSpeed : moveSpeed2;
						Speed flySpeed2 = denizen.getFlySpeed();
						flySpeed = (flySpeed != null && flySpeed.fasterThanOrEqual(flySpeed2)) ? flySpeed : flySpeed2;
						denizen.flip();
					}
					
					if (hireling.isMonster() && includeWeapons()) {
						MonsterPartChitComponent weapon = ((MonsterChitComponent) denizen).getWeapon();
						if (weapon != null && !weapon.isDestroyed()) {
							Strength weaponStrength = weapon.getStrength();
							str = str.strongerOrEqualTo(weaponStrength) ? str : weaponStrength;
							Speed weaponSpeed = weapon.getAttackSpeed();
							attackSpeed = attackSpeed.fasterThanOrEqual(weaponSpeed) ? attackSpeed : weaponSpeed;
							Integer weaponLength = weapon.getLength();
							length = Math.max(length, weaponLength);
						}
					}
					
					if (getVulnerability() != VulnerabilityType.Any && vul.weakerTo(new Strength(getVulnerability().toString()))) continue;
					if (getAttackStrength() != VulnerabilityType.Any && str.weakerTo(new Strength(getAttackStrength().toString()))) continue;
					if (getAttackSpeed() != 0 && attackSpeed.getNum()>getAttackSpeed()) continue;
					if (getAttackLength() != 0 && length<getAttackLength()) continue;
					if (getSharpness() != 0 && sharp<getSharpness()) continue;
					if (getMissile() && !isMissile) continue;
					if (getMoveSpeed() != 0 && moveSpeed.getNum()>getMoveSpeed()) continue;
					if (getFlySpeed() != 0 && (flySpeed == null || flySpeed.getNum()>getFlySpeed())) continue;
					if (getArmored() && !armored) continue;
				}
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
	private boolean sameLocation() {
		return getBoolean(SAME_LOCATION);
	}
	private boolean checkStats() {
		return getVulnerability() != VulnerabilityType.Any || getAttackStrength() != VulnerabilityType.Any
				|| getAttackSpeed() != 0 || getAttackLength() != 0 || getSharpness() != 0
				|| getMissile() || getMoveSpeed() != 0 || getFlySpeed() != 0 || getArmored();
	}
	private VulnerabilityType getVulnerability() {
		if (getString(VULNERARBILITY) == null) {
			return VulnerabilityType.Any;
		}
		return VulnerabilityType.valueOf(getString(VULNERARBILITY));
	}
	private VulnerabilityType getAttackStrength() {
		if (getString(ATTACK_STRENGTH) == null) {
			return VulnerabilityType.Any;
		}
		return VulnerabilityType.valueOf(getString(ATTACK_STRENGTH));
	}
	private int getAttackSpeed() {
		return getInt(ATTACK_SPEED);
	}
	private int getAttackLength() {
		return getInt(ATTACK_LENGTH);
	}
	private int getSharpness() {
		return getInt(SHARPNESS);
	}
	private Boolean getMissile() {
		return getBoolean(MISSILE);
	}
	private int getMoveSpeed() {
		return getInt(MOVE_SPEED);
	}
	private int getFlySpeed() {
		return getInt(FLY_SPEED);
	}
	private Boolean getArmored() {
		return getBoolean(ARMORED);
	}
	private Boolean checkBothSides() {
		return getBoolean(CHECK_BOTH_SIDES);
	}
	private Boolean includeWeapons() {
		return getBoolean(INCLUDE_WEAPONS);
	}
}