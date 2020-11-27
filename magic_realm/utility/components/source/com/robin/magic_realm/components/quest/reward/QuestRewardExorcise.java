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
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.quest.SpellCreator;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRewardExorcise extends QuestReward {
	
	public static final String LOCATION_ONLY = "_location_only";
	public static final String LOCATION = "_location";
	
	public QuestRewardExorcise(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		GameWrapper gameWrapper = GameWrapper.findGame(getGameObject().getGameData());
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameData());
		SpellWrapper spell = SpellCreator.CreateSpellWrapper("exorcise", character);
		
		if (location()) {
			QuestLocation loc = getQuestLocation();
			ArrayList<TileLocation> locations = loc.fetchAllLocations(frame, character, character.getGameData());
			for (TileLocation location : locations) {
				if (location.clearing != null) {
					for (RealmComponent target : location.clearing.getClearingComponents()) {
						spell.addTarget(hostPrefs, target.getGameObject());
					}
				}
			}
		}
		else {
			ClearingDetail clearing = character.getCurrentClearing();
			if (clearing != null) {
				for (RealmComponent target : clearing.getClearingComponents()) {
					spell.addTarget(hostPrefs, target.getGameObject());
				}
			}
		}
		
		spell.affectTargets(frame, gameWrapper, true);
	}
	
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Exorcise is casted in");
		if (location() && getQuestLocation()!=null) {
			sb.append(getQuestLocation().getName()+".");
		}
		else {
			sb.append("characters clearing.");
		}
		return sb.toString();
	}
	public RewardType getRewardType() {
		return RewardType.Exorcise;
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