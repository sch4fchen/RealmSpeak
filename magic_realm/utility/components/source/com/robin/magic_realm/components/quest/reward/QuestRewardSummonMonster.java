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
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.utility.TemplateLibrary;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardSummonMonster extends QuestReward {
	private static Logger logger = Logger.getLogger(QuestStep.class.getName());
	public static final String MONSTER_NAME = "_mn";
	public static final String SUMMON_TYPE = "_summon_from_chart";
	public static final String RANDOM_CLEARING = "_rc";
	public static final String SUMMON_TO_LOCATION = "_summon_loc";
	public static final String RANDOM_LOCATION = "_rnd_loc";
	public static final String LOCATION = "_loc";
	
	public static enum SummonType {
		NewMonster,
		SummonFromSetupCard,
		SummonFromSetupCardOrMap
	}
	
	public QuestRewardSummonMonster(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		GameObject monster = null;
		if (getSummonType() == SummonType.NewMonster) {
			GameObject template = TemplateLibrary.getSingleton().getCompanionTemplate(getMonsterKeyName(),getMonsterQuery());
			monster = TemplateLibrary.getSingleton().createCompanionFromTemplate(getGameData(),template);
			monster.removeThisAttribute(Constants.COMPANION);
			monster.setThisAttribute(Constants.SUMMONED);
		}
		else {
			ArrayList<GameObject> monsters = getGameData().getGameObjectsByName(getMonsterKeyName());
			for (GameObject validMonster : monsters) {
				if (getSummonType() == SummonType.SummonFromSetupCard && validMonster.getHeldBy() != SetupCardUtility.getDenizenHolder(validMonster)) continue;
				monster = validMonster;
				SetupCardUtility.resetDenizen(monster);
				break;
			}
			if (monster == null) return;
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
			if (randomLocation()) {
				int random = RandomNumber.getRandom(validLocations.size());
				TileLocation tileLocation = validLocations.get(random);
				tileLocation.clearing.add(monster,null);
			}
			else {
				for (TileLocation location : validLocations) {
					GameObject summonMonster = monster.copy();
					location.clearing.add(summonMonster, null);
				}
			}
			return;
		}
		
		if (randomClearing()) {
			ArrayList<ClearingDetail> clearings = character.getCurrentLocation().tile.getClearings();
			int random = RandomNumber.getRandom(clearings.size());
			clearings.get(random).add(monster,null);
			return;
		}
		character.getCurrentLocation().clearing.add(monster,null);
	}
	
	public ImageIcon getIcon() {
		GameObject template = TemplateLibrary.getSingleton().getCompanionTemplate(getMonsterKeyName(),getMonsterQuery());
		RealmComponent rc = RealmComponent.getRealmComponent(template);
		return rc.getIcon();
	}
	
	public String getDescription() {
		if (locationOnly() && getQuestLocation() != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(getMonsterKeyName()+" is summoned in ");
			if (randomLocation()) {
				sb.append("a random clearing of ");
			}
			sb.append(getQuestLocation().getName());
			return sb.toString();
		}
		if (randomClearing()) {
			return getMonsterKeyName()+" is summoned in a random clearing of characters tile.";
		}
		return getMonsterKeyName()+" is summoned in the characters clearing.";
	}

	public RewardType getRewardType() {
		return RewardType.SummonMonster;
	}
	
	private String getMonsterKeyName() {
		return getString(QuestConstants.KEY_PREFIX+MONSTER_NAME).trim();
	}
	
	private String getMonsterQuery() {
		return getString(QuestConstants.VALUE_PREFIX+MONSTER_NAME).trim();
	}

	private SummonType getSummonType() {
		String summonType = getString(SUMMON_TYPE);
		if (summonType == null) { // compatibility for old quests
			return SummonType.NewMonster;
		}
		return SummonType.valueOf(getString(SUMMON_TYPE));
	}
	
	private boolean randomClearing() {
		return getBoolean(RANDOM_CLEARING);
	}
	
	private boolean locationOnly() {
		return getBoolean(SUMMON_TO_LOCATION);
	}
	
	private boolean randomLocation() {
		return getBoolean(RANDOM_LOCATION);
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