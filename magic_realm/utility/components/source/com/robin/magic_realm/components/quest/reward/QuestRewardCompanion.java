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
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.GainType;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.TemplateLibrary;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardCompanion extends QuestReward {
	private static Logger logger = Logger.getLogger(QuestStep.class.getName());
	public static final String COMPANION_NAME = "_cn";
	public static final String GAIN_TYPE = "_goc";
	public static final String EXCLUDE_HORSE = "_eh";
	public static final String COMPANION_STAYS_INGAME = "_cig";
	public static final String COMPANION_RENAME = "_cname";
	public static final String LOCATION_ONLY = "_loc_only";
	public static final String LOCATION = "_loc";
	
	public QuestRewardCompanion(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		if (getGainType()==GainType.Gain) {
			GameObject template = TemplateLibrary.getSingleton().getCompanionTemplate(getCompanionKeyName(),getCompanionQuery(),!excludeHorse());
			GameObject companion = TemplateLibrary.getSingleton().createCompanionFromTemplate(getGameData(),template);
			character.addHireling(companion,Constants.TEN_YEARS);
			character.getGameObject().add(companion);
			character.getCurrentLocation().clearing.add(companion,null);
			if (locationOnly()) {
				QuestLocation loc = getQuestLocation();
				if (loc == null) return;
				ArrayList<TileLocation> validLocations = new ArrayList<>();
				validLocations = loc.fetchAllLocations(frame, character, getGameData());
				if(validLocations.isEmpty()) {
					logger.fine("QuestLocation "+loc.getName()+" doesn't have any valid locations!");
					return;
				}
				int random = RandomNumber.getRandom(validLocations.size());
				TileLocation tileLocation = validLocations.get(random);
				tileLocation.clearing.add(companion,null);
			}
			if (renameCompanionTo() != null && !renameCompanionTo().isEmpty()) {
				companion.setName(renameCompanionTo());
			}
		}
		else {
			GamePool pool = new GamePool(character.getGameData().getGameObjects());
			ArrayList<GameObject> companionsExisting = new ArrayList<>();
			companionsExisting.addAll(pool.find(getCompanionQuery()));
			companionsExisting.addAll(pool.find("name="+getCompanionKeyName()));
			for (GameObject companion : companionsExisting ) {
				RealmComponent companionRc = RealmComponent.getRealmComponent(companion);
				if (companionRc != null && companionRc.getOwnerId() != null && companionRc.getOwnerId().matches(String.valueOf(character.getGameObject().getId()))) {
					character.removeHireling(companion);
					// Companions must be removed from the map as well, since they are not rehired!
					if (!leaveCompanionInGameWhenLost()) {
						ClearingUtility.moveToLocation(companion,null);
					}
					if (renameCompanionTo() != null && !renameCompanionTo().isEmpty()) {
						companion.setName(renameCompanionTo());
					}
					return;
				}
			}
		}
	}
	
	public ImageIcon getIcon() {
		GameObject template = TemplateLibrary.getSingleton().getCompanionTemplate(getCompanionKeyName(),getCompanionQuery());
		RealmComponent rc = RealmComponent.getRealmComponent(template);
		return rc.getIcon();
	}
	
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getCompanionKeyName());
		if (getGainType()==GainType.Gain) {
			sb.append(" joins as a companion");
			if (locationOnly() && getQuestLocation() != null ) {
				sb.append(" in "+getQuestLocation().getName());
			}
		}
		else {
			sb.append(" leaves the character");
			if (leaveCompanionInGameWhenLost()) {
				sb.append(" (stays in game)");
			}
		}
		sb.append(".");
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.Companion;
	}
	
	private GainType getGainType() {
		return GainType.valueOf(getString(GAIN_TYPE));
	}

	private String getCompanionKeyName() {
		return getString(QuestConstants.KEY_PREFIX+COMPANION_NAME);
	}
	
	private String getCompanionQuery() {
		return getString(QuestConstants.VALUE_PREFIX+COMPANION_NAME);
	}
	
	private boolean leaveCompanionInGameWhenLost() {
		return getBoolean(COMPANION_STAYS_INGAME);
	}
	
	private boolean excludeHorse() {
		return getBoolean(EXCLUDE_HORSE);
	}
	
	private String renameCompanionTo() {
		return getString(COMPANION_RENAME);
	}
	
	private boolean locationOnly() {
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