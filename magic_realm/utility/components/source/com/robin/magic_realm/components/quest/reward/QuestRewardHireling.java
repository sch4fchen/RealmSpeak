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
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.ChitAcquisitionType;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.quest.TermOfHireType;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardHireling extends QuestReward {
	private static Logger logger = Logger.getLogger(QuestStep.class.getName());
	public static final String HIRELING_REGEX = "_hrx";
	public static final String EXCLUDE_CLONED = "_hrx_ec";
	public static final String ACQUISITION_TYPE = "_goc";
	public static final String TERM_OF_HIRE = "_toh";
	public static final String EXCLUDE_HORSE = "_eh";
	public static final String HIRELING_RENAME = "_hname";
	public static final String LOCATION_ONLY = "_loc_only";
	public static final String LOCATION = "_loc";

	public QuestRewardHireling(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		String actionDescription;
		ChitAcquisitionType at = getAcquisitionType();
		ArrayList<GameObject> objects;
		if (at == ChitAcquisitionType.Lose) {
			actionDescription = ": Select ONE hireling to lose.";
			objects = new ArrayList<>();
			for (RealmComponent rc : character.getAllHirelings()) {
				objects.add(rc.getGameObject());
			}
		}
		else {
			actionDescription = ": Select ONE hireling to join you.";
			objects = getGameData().getGameObjects();
		}
		ArrayList<GameObject> selectionObjects = getObjectList(objects, at, getHirelingRegex(), excludeCloned());
		if (selectionObjects.size() == 0)
			return; // no real reward!
		GameObject selected = null;
		if (selectionObjects.size() == 1) {
			selected = selectionObjects.get(0);
		}
		else {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame, getTitleForDialog() + actionDescription, false);
			chooser.addGameObjects(selectionObjects, true);
			chooser.setVisible(true);
			selected = chooser.getFirstSelectedComponent().getGameObject();
		}

		if (at == ChitAcquisitionType.Lose) {
			character.removeHireling(selected);
			if (renameHirelingTo() != null && !renameHirelingTo().isEmpty()) {
				selected.setName(renameHirelingTo());
			}
		}
		else {
			RealmComponent rc = RealmComponent.getRealmComponent(selected);
			if (at == ChitAcquisitionType.Clone) {
				GameObject go = getGameData().createNewObject();
				go.copyAttributesFrom(selected);
				go.setThisAttribute(Constants.CLONED); // tag as cloned, so that the removeHireling method will expunge the clone
				selected = go;
				if (!excludeHorse()) {
					for (GameObject heldGo : rc.getHold()) {
						RealmComponent heldRc = RealmComponent.getRealmComponent(heldGo);
						if (heldRc.isNativeHorse() || heldRc.isHorse()) {
							GameObject horse = getGameData().createNewObject();
							horse.copyAttributesFrom(heldGo);
							horse.setThisAttribute(Constants.CLONED);
							selected.add(horse);
						}
					}
					
				}
			}
			
			selected.setThisAttribute(Constants.HIRELING);
			TermOfHireType termofHire = getTermOfHireType();
			if (termofHire == TermOfHireType.Normal || termofHire == TermOfHireType.Permanent) {
				if (!rc.isNativeLeader()) {
					character.getGameObject().add(selected);
				}
				character.addHireling(selected, termofHire == TermOfHireType.Normal ? Constants.TERM_OF_HIRE : Constants.TEN_YEARS); // permanent enough? :-)
				character.getCurrentLocation().clearing.add(selected,null);
			}
			else if (termofHire == TermOfHireType.PlaceInClearing) {
					character.getCurrentLocation().clearing.add(selected,character);
			}
			
			if (renameHirelingTo() != null && !renameHirelingTo().isEmpty()) {
				selected.setName(renameHirelingTo());
			}
			
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
				tileLocation.clearing.add(selected,null);
			}
		}
	}

	private static ArrayList<GameObject> getObjectList(ArrayList<GameObject> sourceObjects, ChitAcquisitionType at, String regEx, boolean excludeCloned) {
		Pattern pattern = (regEx == null || regEx.length() == 0) ? null : Pattern.compile(regEx);
		GamePool pool = new GamePool(sourceObjects);
		ArrayList<GameObject> objects = new ArrayList<>();
		for (GameObject go : pool.find("native,rank")) {
			if (pattern == null || pattern.matcher(go.getName()).find()) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (at == ChitAcquisitionType.Available) {
					// Make sure these are not already hired
					if (rc.getOwnerId() != null)
						continue;
				}
				if (excludeCloned && rc.isCloned())
					continue;
				objects.add(go);
			}
		}
		return objects;
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getHirelingRegex());
		ChitAcquisitionType at = getAcquisitionType();
		if (at == ChitAcquisitionType.Lose) {
			sb.append(" leaves the character");
		}
		else if (getTermOfHireType() == TermOfHireType.PlaceInClearing) {
			sb.append(" is placed in the clearing");
		}
		else {
			sb.append(" joins as a ");
			sb.append(getTermOfHireType().toString().toLowerCase());
			sb.append(" hireling");
		}
		if (locationOnly() && getQuestLocation() != null) {
			sb.append(" in "+getQuestLocation().getName());
		}
		sb.append(".");
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.Hireling;
	}

	private ChitAcquisitionType getAcquisitionType() {
		return ChitAcquisitionType.valueOf(getString(ACQUISITION_TYPE));
	}

	private TermOfHireType getTermOfHireType() {
		return TermOfHireType.valueOf(getString(TERM_OF_HIRE));
	}
	
	private boolean locationOnly() {
		return getBoolean(LOCATION_ONLY);
	}

	private String getHirelingRegex() {
		return getString(HIRELING_REGEX);
	}
	
	private boolean excludeCloned() {
		return getBoolean(EXCLUDE_CLONED);
	}
	
	private boolean excludeHorse() {
		return getBoolean(EXCLUDE_HORSE);
	}
	
	private String renameHirelingTo() {
		return getString(HIRELING_RENAME);
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