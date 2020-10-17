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
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementRelationship extends QuestRequirement {

	public static final String NATIVE_GROUP = "_native_grp_rgx";
	public static final String RELATIONSHIP_LEVEL = "_rel_lvl";
	public static final String EXCEED_LEVEL = "_excd_lvl";
	public static final String SUBCEED_LEVEL = "_sucd_lvl";
	
	public QuestRequirementRelationship(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		boolean fulfilled = false;
		for (GameObject nativeGroup : getRepresentativeNatives(character)) {
			if (getNativesRegex().isEmpty() || nativeGroup.getName().toLowerCase().matches(getNativesRegex().toLowerCase())) {
					if (exceedAllowed() && character.getRelationship(nativeGroup) >= getRelationshipLevel()) {
						fulfilled = true;
					}
					if (subceedAllowed() && character.getRelationship(nativeGroup) <= getRelationshipLevel()) {
						fulfilled = true;
					}
					if(character.getRelationship(nativeGroup) == getRelationshipLevel()) {
						fulfilled = true;
					}
			}
		}
		return fulfilled;
	}

	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Characters relationship with ");
		if (!getNativesRegex().isEmpty()) {
				sb.append(getNativesRegex());
		}
		else {
			sb.append("any natives");
		}
		sb.append(" must be "+getRelationship());
		if (exceedAllowed()) {
			sb.append(" or better");
		}
		if (subceedAllowed()) {
			sb.append(" or worse");
		}
		sb.append(".");
		return sb.toString();
	}
	public RequirementType getRequirementType() {
		return RequirementType.Relationship;
	}
	private String getNativesRegex() {
		return getString(NATIVE_GROUP);
	}
	public static ArrayList<GameObject> getRepresentativeNatives(CharacterWrapper character) {
		GamePool pool = new GamePool(character.getGameData().getGameObjects());
		ArrayList<String> queryNatives = new ArrayList<String>();
		ArrayList<String> queryVisitors = new ArrayList<String>();
		queryNatives.add("native");
		queryNatives.add("rank=HQ");
		queryVisitors.add("visitor");
		ArrayList<GameObject> representativeNatives = pool.find(queryNatives);
		representativeNatives.addAll(pool.find(queryVisitors));
		return representativeNatives;
	}
	private int getRelationshipLevel() {
		return RelationshipType.getIntFor(getRelationship());
	}
	private String getRelationship() {
		return getString(RELATIONSHIP_LEVEL);
	}
	private boolean exceedAllowed() {
		return getBoolean(EXCEED_LEVEL);
	}
	private boolean subceedAllowed() {
		return getBoolean(SUBCEED_LEVEL);
	}
}