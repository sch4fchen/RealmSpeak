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
import com.robin.magic_realm.components.quest.reward.QuestRewardSummonMonster.SummonType;
import com.robin.magic_realm.components.table.RaiseDead;
import com.robin.magic_realm.components.table.SummonAnimal;
import com.robin.magic_realm.components.table.SummonElemental;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.MonsterCreator;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.utility.TemplateLibrary;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardSummonGeneratedMonster extends QuestReward {
	private static Logger logger = Logger.getLogger(QuestStep.class.getName());
	public static final String MONSTER_TYPE = "_monster";
	public static final String AMOUNT = "_amount";
	public static final String RANDOM_CLEARING = "_rc";
	public static final String SUMMON_TO_LOCATION = "_summon_loc";
	public static final String RANDOM_LOCATION = "_rnd_loc";
	public static final String LOCATION = "_loc";
		
	public static enum MonsterType {
		Basilisk,
		Eagle,
		Bear,
		Wolf,
		Hawk,
		Squirrel,
		Skeleton,
		SkeletonArcher,
		SkeletonSwordsman,
		ZombieM5H6,
		ZombieM5T5,
		ZombieM4M6,
		Blob,
		Wasp,
		AirElemental,
		EarthElemental,
		FireElemental,
		WaterElemental,
	}
	
	public QuestRewardSummonGeneratedMonster(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> monsters = new ArrayList<>();
		
		MonsterCreator mc = null;
		switch (getMonsterType()) {
			case Skeleton:
			case SkeletonArcher:
			case SkeletonSwordsman:
			case ZombieM5H6:
			case ZombieM5T5:
			case ZombieM4M6:
			case Blob:
			case Wasp:
				mc = new MonsterCreator("QuestRewardSummonGeneratedMonster");
				break;
			default:
				break;
		}
		
		for (int i = 0; i < getAmount(); i++) {
			switch (getMonsterType()) {
			case Basilisk:
				monsters.add((new SummonAnimal(frame)).createAnimal(getGameData(), SummonAnimal.AnimalType.Basilisk));
				break;
			case Eagle:
				monsters.add((new SummonAnimal(frame)).createAnimal(getGameData(), SummonAnimal.AnimalType.Basilisk));
				break;
			case Bear:
				monsters.add((new SummonAnimal(frame)).createAnimal(getGameData(), SummonAnimal.AnimalType.Basilisk));
				break;
			case Wolf:
				monsters.add((new SummonAnimal(frame)).createAnimal(getGameData(), SummonAnimal.AnimalType.Basilisk));
				break;
			case Hawk:
				monsters.add((new SummonAnimal(frame)).createAnimal(getGameData(), SummonAnimal.AnimalType.Basilisk));
				break;
			case Squirrel:
				monsters.add((new SummonAnimal(frame)).createAnimal(getGameData(), SummonAnimal.AnimalType.Basilisk));
				break;
			case Skeleton:
				monsters.add(RaiseDead.createSkeleton(mc, getGameData()));
				break;
			case SkeletonArcher:
				monsters.add(RaiseDead.createSkeletonArcher(mc, getGameData()));
				break;
			case SkeletonSwordsman:
				monsters.add(RaiseDead.createSkeletonSwordsman(mc, getGameData()));
				break;
			case ZombieM5H6:
				monsters.add(RaiseDead.createZombie(mc, getGameData(),0));
				break;
			case ZombieM5T5:
				monsters.add(RaiseDead.createZombie(mc, getGameData(),1));
				break;
			case ZombieM4M6:
				monsters.add(RaiseDead.createZombie(mc, getGameData(),2));
				break;
			case Blob:
				monsters.add(SetupCardUtility.createBlob(mc, getGameData()));
				break;
			case Wasp:
				monsters.add(SetupCardUtility.createWasp(mc, getGameData()));
				break;
			case AirElemental:
				monsters.add((new SummonElemental(frame)).createElemental(getGameData(), SummonElemental.ElementalType.Water));
				break;
			case EarthElemental:
				monsters.add((new SummonElemental(frame)).createElemental(getGameData(), SummonElemental.ElementalType.Water));
				break;
			case FireElemental:
				monsters.add((new SummonElemental(frame)).createElemental(getGameData(), SummonElemental.ElementalType.Fire));
				break;
			case WaterElemental:
				monsters.add((new SummonElemental(frame)).createElemental(getGameData(), SummonElemental.ElementalType.Water));
				break;
			}
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
				for (GameObject monster : monsters) {
					tileLocation.clearing.add(monster,null);
				}
			}
			else {
				for (TileLocation location : validLocations) {
					for (GameObject monster : monsters) {
						GameObject summonMonster = monster.copy();
						location.clearing.add(summonMonster, null);
					}
				}
			}
			return;
		}
		
		if (randomClearing()) {
			ArrayList<ClearingDetail> clearings = character.getCurrentLocation().tile.getClearings();
			int random = RandomNumber.getRandom(clearings.size());
			for (GameObject monster : monsters) {
				clearings.get(random).add(monster,null);
			}
			return;
		}
		
		for (GameObject monster : monsters) {
			character.getCurrentLocation().clearing.add(monster,null);
		}
	}
		
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getAmount()+" ");
		sb.append(getMonsterType());
		if (getAmount()>1) {
			sb.append("s are");
		}
		else {
			sb.append(" is");
		}
		if (locationOnly() && getQuestLocation() != null) {
			sb.append(" summoned in ");
			if (randomLocation()) {
				sb.append("a random clearing of ");
			}
			sb.append(getQuestLocation().getName());
			return sb.toString();
		}
		if (randomClearing()) {
			sb.append(" summoned in a random clearing of characters tile.");
			return sb.toString();
		}
		sb.append(" summoned in the characters clearing.");
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.SummonGeneratedMonster;
	}
	
	private MonsterType getMonsterType() {
		return MonsterType.valueOf(getString(MONSTER_TYPE));
	}
	
	private int getAmount() {
		return getInt(AMOUNT);
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