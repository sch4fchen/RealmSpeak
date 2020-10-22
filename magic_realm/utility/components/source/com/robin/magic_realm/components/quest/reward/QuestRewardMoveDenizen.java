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
package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMoveDenizen extends QuestReward {
	
	public static final String DENIZEN_REGEX = "_drx";
	public static final String MOVE_TO_SAME_CLEARING = "_msc";
	public static final String MOVE_HIRELINGS = "_mh";
	public static final String MOVE_COMPANIONS = "_mc";
	public static final String MOVE_SUMMONED = "_ms";
	public static final String MOVE_LIMITED = "_ml";
	public static final String LOCATION = "_loc";
	
	public QuestRewardMoveDenizen(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> denizens = character.getGameData().getGameObjectsByNameRegex(getDenizenNameRegex());
		QuestLocation loc = getQuestLocation();
		ArrayList<TileLocation> locations = loc.fetchAllLocations(frame, character, character.getGameData());
		ArrayList<GameObject> denizensToMove = new ArrayList<GameObject>();
		for (GameObject denizen : denizens) {
			if (!moveHirelings() && denizen.hasThisAttribute(Constants.HIRELING)) {
				continue;
			}
			if (!moveCompanions() && denizen.hasThisAttribute(Constants.COMPANION)) {
				continue;
			}
			if (!moveSummoned() && denizen.hasThisAttribute(Constants.SUMMONED)) {
				continue;
			}
			if (moveOnlyHirelingsCompanionsSummonedMonsters() && !denizen.hasThisAttribute(Constants.HIRELING) && !denizen.hasThisAttribute(Constants.COMPANION) && !denizen.hasThisAttribute(Constants.SUMMONED)) {
				continue;
			}
			
			denizensToMove.add(denizen);
		}
		
		if(moveToSameClearing()) {
			int random = RandomNumber.getRandom(denizensToMove.size());
			TileLocation locationToMove = locations.get(random);
			for (GameObject denizen : denizensToMove) {
				locationToMove.clearing.add(denizen, null);
			}
		}
		else {
			for (GameObject denizen : denizensToMove) {
				int random = RandomNumber.getRandom(denizensToMove.size());
				TileLocation locationToMove = locations.get(random);
				locationToMove.clearing.add(denizen, null);
			}
		}
	}
	
	public String getDescription() {
		return getDenizenNameRegex() +" is/are moved to "+getQuestLocation().getName()+".";
	}
	private String getDenizenNameRegex() {
		return getString(DENIZEN_REGEX);
	}
	private Boolean moveToSameClearing() {
		return getBoolean(MOVE_TO_SAME_CLEARING);
	}
	private Boolean moveHirelings() {
		return getBoolean(MOVE_HIRELINGS);
	}
	private Boolean moveCompanions() {
		return getBoolean(MOVE_COMPANIONS);
	}
	private Boolean moveSummoned() {
		return getBoolean(MOVE_SUMMONED);
	}
	private Boolean moveOnlyHirelingsCompanionsSummonedMonsters() {
		return getBoolean(MOVE_LIMITED);
	}
	public RewardType getRewardType() {
		return RewardType.MoveDenizen;
	}
	public boolean usesLocationTag(String tag) {
		QuestLocation loc = getQuestLocation();
		return loc!=null && tag.equals(loc.getName());
	}
	public QuestLocation getQuestLocation() {
		String id = getString(LOCATION);
		if (id!=null) {
			GameObject go = getGameData().getGameObject(Long.valueOf(id));
			if (go!=null) {
				return new QuestLocation(go);
			}
		}
		return null;
	}
	
	public void setQuestLocation(QuestLocation location) {
		setString(LOCATION,location.getGameObject().getStringId());
	}
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup,LOCATION);
	}
	
}