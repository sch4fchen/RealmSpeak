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
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.quest.ArmoredType;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.quest.TargetValueType;
import com.robin.magic_realm.components.quest.VulnerabilityType;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.DayKey;

public class QuestRequirementKillInCombat extends QuestRequirement {
	public static final String AMOUNT = "_amount";
	public static final String REGEX_FILTER = "_regex";
	public static final String REQUIRE_MARK = "_rqm";
	public static final String VULNERABILITY = "_vul";
	public static final String ARMORED = "_arm";
	public static final String SINGLE_ROUND = "_round";
	
	public QuestRequirementKillInCombat(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame,CharacterWrapper character,QuestRequirementParams reqParams) {
		if (reqParams==null || "KillInCombat".equals(reqParams.actionName)) {
			return false;
		}
		
		ArrayList<GameObject> kills = null;
		if (onlyInSingleRound()) {
			kills = reqParams.objectList;
		}
		else {
			CombatWrapper combat = new CombatWrapper(character.getGameObject());
			kills = combat.getAllKills();
		}
		if (kills == null) return false;
		
		ArrayList<GameObject> validKills = new ArrayList<>();
		String regex = getRegExFilter().trim();
		Pattern pattern = regex.length()==0?null:Pattern.compile(regex);
		String questId = getParentQuest().getGameObject().getStringId();
		
		for (GameObject kill : kills) {
			if (pattern!=null && !pattern.matcher(kill.getName()).find()) continue;
			if (getRequireMark()) {
				String mark = kill.getThisAttribute(QuestConstants.QUEST_MARK);
				if (mark==null || !mark.equals(questId)) continue;
			}
			if (getVulnerability()!=VulnerabilityType.Any && VulnerabilityType.valueOf(kill.getThisAttribute("vulnerability"))!=getVulnerability()) continue;
			if ((getArmored() == ArmoredType.Armored && !kill.hasThisAttribute("armored"))|| (getArmored() == ArmoredType.Unarmored && kill.hasThisAttribute("armored"))) continue;
			validKills.add(kill);
		}
				
		if(validKills.size()>=getAmountOfKills()) {
			return true;
		}
		
		return false;
	}
	
	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		int amount = getAmountOfKills();
		sb.append("Must kill ");
		sb.append(amount);
		sb.append(getRequireMark()?" marked":" ");
		if (getArmored() != ArmoredType.Any) {
			sb.append(" "+getArmored().toString().toLowerCase());
		}
		sb.append(" denizen");
		sb.append(amount==1?"":"s");
		if(!getRegExFilter().isEmpty()) {
			sb.append(" that match");
			sb.append(amount==1?"es":"");
			sb.append(" regex: /"+getRegExFilter()+"/");
		}
		sb.append(getVulnerability()!=VulnerabilityType.Any?" with vulnerability "+getVulnerability():"");
		sb.append(onlyInSingleRound()?" in a single combat round.":" in a single combat.");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.KillInCombat;
	}
	
	private int getAmountOfKills() {
		return getInt(AMOUNT);
	}
	private String getRegExFilter() {
		return getString(REGEX_FILTER);
	}
	private boolean getRequireMark() {
		return getBoolean(REQUIRE_MARK);
	}
	private VulnerabilityType getVulnerability() {
		return VulnerabilityType.valueOf(getString(VULNERABILITY));
	}
	private ArmoredType getArmored() {
		return ArmoredType.valueOf(getString(ARMORED));
	}
	private boolean onlyInSingleRound() {
		return getBoolean(SINGLE_ROUND);
	}
}