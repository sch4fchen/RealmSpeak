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
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ChitComponent;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardControl extends QuestReward {
	
	public static final String DENIZEN_REGEX = "_regex";
	public static final String REMOVE_CONTROL = "_remove";
	public static final String AMOUNT = "_amount";
	public static final String LOCATION_ONLY = "_location_only";
	public static final String LOCATION = "_location";
	
	public QuestRewardControl(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<RealmComponent> targets = new ArrayList<RealmComponent>();
		Pattern pattern = Pattern.compile(getTargetRegex());
		int affectedDenizens = 0;
		if (remove()) {
			targets.addAll(character.getAllHirelings());
			for (RealmComponent target : targets) {
				if (!pattern.matcher(target.toString()).find()) continue;
				character.removeHireling(target.getGameObject());
				affectedDenizens++;
				if (affectedDenizens>=numberOfDenizens()) return;
			}
			return;
		}
		
		if (location()) {
			QuestLocation loc = getQuestLocation();
			ArrayList<TileLocation> locations = loc.fetchAllLocations(frame, character, character.getGameData());
			for (TileLocation location : locations) {
				if (location.clearing != null) {
					targets.addAll(location.clearing.getClearingComponents());
				}
			}
		}
		else {
			ClearingDetail clearing = character.getCurrentClearing();
			if (clearing != null) {
				targets.addAll(clearing.getClearingComponents());
			}
		}
		
		for (RealmComponent target : targets) {
			if (!pattern.matcher(target.toString()).find()) continue;
			
			if (target.isMonster() || target.isNative()) {
				ChitComponent chit = (ChitComponent)target;
				if (chit.isDarkSideUp()) { // Always flip to light side on control!
					chit.setLightSideUp();
				}
			}
			character.addHireling(target.getGameObject(),Constants.TEN_YEARS);
			affectedDenizens++;
			if (affectedDenizens>=numberOfDenizens()) return;
		}
	}
	
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character gets the control of "+getTargetRegex());
		if (location() && getQuestLocation()!=null) {
			sb.append(" in "+getQuestLocation().getName()+".");
		}
		else {
			sb.append(" in his clearing.");
		}
		return sb.toString();
	}
	public RewardType getRewardType() {
		return RewardType.Control;
	}

	private String getTargetRegex() {
		return getString(DENIZEN_REGEX);
	}
	private boolean remove() {
		return getBoolean(REMOVE_CONTROL);
	}
	private int numberOfDenizens() {
		return getInt(AMOUNT);
	}
	private boolean location() {
		return getBoolean(LOCATION_ONLY);
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