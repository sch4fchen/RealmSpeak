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
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardKillDenizen extends QuestReward {
	
	public static final String DENIZEN_REGEX = "_drx";
	public static final String KILL_HIRELINGS = "_kh";
	public static final String KILL_COMPANIONS = "_kc";
	public static final String KILL_SUMMONED = "_ks";
	public static final String KILL_LIMITED = "_kl";
	public static final String KILL_IN_LOCATION = "_k_i_loc";
	public static final String LOCATION = "_loc";
	
	public QuestRewardKillDenizen(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> denizens = character.getGameData().getGameObjectsByNameRegex(getDenizenNameRegex());
		for (GameObject denizen : denizens) {
			if (!killHirelings() && denizen.hasThisAttribute(Constants.HIRELING)) {
				continue;
			}
			if (!killCompanions() && denizen.hasThisAttribute(Constants.COMPANION)) {
				continue;
			}
			if (!killSummoned() && denizen.hasThisAttribute(Constants.SUMMONED)) {
				continue;
			}
			if (killOnlyHirelingsCompanionsSummonedMonsters() && !denizen.hasThisAttribute(Constants.HIRELING) && !denizen.hasThisAttribute(Constants.COMPANION) && !denizen.hasThisAttribute(Constants.SUMMONED)) {
				continue;
			}
			
			if (locationOnly()) {
				QuestLocation loc = getQuestLocation();
				RealmComponent denizenRc = RealmComponent.getRealmComponent(denizen);
				if (loc.locationMatchAddressForRealmComponent(frame, character, denizenRc)) {
					RealmUtility.makeDead(denizenRc);
				}
			}
			else {
			RealmComponent denizenRc = RealmComponent.getRealmComponent(denizen);
			RealmUtility.makeDead(denizenRc);
			}
		}
	}
	
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append(getDenizenNameRegex() +" is/are killed");
		if (locationOnly()) {
			sb.append(" in "+getQuestLocation().getName());
		}
		sb.append(".");
		return sb.toString();
	}
	private String getDenizenNameRegex() {
		return getString(DENIZEN_REGEX);
	}
	private Boolean killHirelings() {
		return getBoolean(KILL_HIRELINGS);
	}
	private Boolean killCompanions() {
		return getBoolean(KILL_COMPANIONS);
	}
	private Boolean killSummoned() {
		return getBoolean(KILL_SUMMONED);
	}
	private Boolean killOnlyHirelingsCompanionsSummonedMonsters() {
		return getBoolean(KILL_LIMITED);
	}
	public RewardType getRewardType() {
		return RewardType.KillDenizen;
	}
	private boolean locationOnly() {
		return getBoolean(KILL_IN_LOCATION);
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