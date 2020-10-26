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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardSummonFromAppearance extends QuestReward {
	public static final String CHIT = "_chit";
	public static final String DENIZEN = "_denizen";
	public static final String SUMMON_LIVING_DENIZENS = "_summon_living_denizens";
	public static final String SUMMON_TO = "_summon_to";
	public static final String LOCATION = "_loc";
	
	public enum SummonTo {
		Anywhere,
		CharactersClearing,
		CharactersTile,
		LocationsClearings,
		LocationsTiles
	}
	
	public QuestRewardSummonFromAppearance(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<TileLocation> allQuestLocations = new ArrayList<TileLocation>();
		if (toLocation()) {
			QuestLocation loc = getQuestLocation();
			if (loc == null) return;
			allQuestLocations = loc.fetchAllLocations(character.getGameData());
		}
		
		ArrayList<GameObject> validChits = new ArrayList<GameObject>();
		if (!getChit().isEmpty()) {
			ArrayList<GameObject> chits = character.getGameData().getGameObjectsByNameRegex(getChit());
			for (GameObject chit : chits) {
				RealmComponent rc = RealmComponent.getRealmComponent(chit);
				if (!chit.hasThisAttribute("dwelling") && (rc == null || (!rc.isWarning() && !rc.isSound() && !rc.isTreasureLocation()))) continue; 
				
				switch(summonTo()) {
				case CharactersClearing:
					if (rc.getCurrentLocation() == character.getCurrentLocation()) {
						validChits.add(chit);
					};
				break;
				case CharactersTile:
					if (rc.getCurrentLocation().tile == character.getCurrentLocation().tile) {
						validChits.add(chit);
					};
				break;
				case LocationsClearings:
					for (TileLocation tl : allQuestLocations)
						if (rc.getCurrentLocation() == tl) {	
							validChits.add(chit);
							break;
					};
				break;
				case LocationsTiles:
					for (TileLocation tl : allQuestLocations)
						if (rc.getCurrentLocation().tile == tl.tile) {	
							validChits.add(chit);
							break;
					};
				break;
				case Anywhere:
				default:
					validChits.add(chit);
					break;
				}
			}
		}
		else {
			if (toLocation()) {
				switch(summonTo()) {
					case LocationsClearings:
						for (TileLocation tl : allQuestLocations) {
							ArrayList<RealmComponent> clearingComponents = tl.clearing.getClearingComponents();
							for (RealmComponent rc : clearingComponents) {
								if (rc.isWarning() || rc.isSound() || rc.isTreasureLocation() || rc.isDwelling()) {
									validChits.add(rc.getGameObject());
								}
							}
						};
						break;
					case LocationsTiles:
						for (TileLocation tl : allQuestLocations) {
							ArrayList<RealmComponent> clearingComponents = tl.tile.getAllClearingComponents();
							for (RealmComponent rc : clearingComponents) {
								if (rc.isWarning() || rc.isSound() || rc.isTreasureLocation() || rc.isDwelling()) {
									validChits.add(rc.getGameObject());
								}
							}
						};
					break;
					default: break;
				}
			}
			else {
				GamePool pool = new GamePool(getGameData().getGameObjects());
				ArrayList<String> query = new ArrayList<String>();
				query.add("warning");
				validChits.addAll(pool.find(query));
				query.clear();
				query.add("sound");
				validChits.addAll(pool.find(query));
				query.clear();
				query.add("treasure_location");
				validChits.addAll(pool.find(query));
				query.clear();
				query.add("dwelling");
				validChits.addAll(pool.find(query));
			}
		}
		
		ArrayList<GameObject> validDenizens = new ArrayList<GameObject>();
		if (!getDenizenName().isEmpty()) {
			validDenizens = character.getGameData().getGameObjectsByNameRegex(getDenizenName());
		}
		else {
			GamePool pool = new GamePool(getGameData().getGameObjects());
			ArrayList<String> query = new ArrayList<String>();
			query.add("vulnerability");
			query.add("setup_start");
			validDenizens.addAll(pool.find(query));
		}
		
		for (GameObject chit : validChits) {
			RealmComponent rcChit = RealmComponent.getRealmComponent(chit);	
			for (GameObject denizen : validDenizens) {
				GameObject denizenHolder = SetupCardUtility.getDenizenHolder(denizen);
				RealmComponent rcDenizenHolder = RealmComponent.getRealmComponent(denizenHolder);
				String bn = denizenHolder.getThisAttribute(Constants.BOARD_NUMBER);
				if (bn != null && denizen.getThisAttribute(Constants.BOARD_NUMBER) != bn) continue;
				ClearingDetail clearingSummonTo = null;
				if (chit.hasThisAttribute("dwelling") && denizen.getThisAttribute("setup_start").toLowerCase().matches(chit.getName().toLowerCase())) {
					clearingSummonTo = rcDenizenHolder.getCurrentLocation().clearing;
				}
				if (rcChit.isTreasureLocation() && denizen.getThisAttribute("setup_start").toLowerCase().matches(chit.getName().toLowerCase())) {
					clearingSummonTo = rcDenizenHolder.getCurrentLocation().clearing;
				}
				if (rcChit.isSound()) {
					String soundsList = denizenHolder.getThisAttribute("summon");
					List<String> sounds = Arrays.asList(soundsList.split("\\s*,\\s*"));
					String sound = chit.getThisAttribute("sound");;
					for (String soundName : sounds) {
						if (soundName.toLowerCase().matches(sound.toLowerCase())) {
							int clearingNumber = chit.getThisInt("clearing");
							clearingSummonTo = rcChit.getCurrentLocation().tile.getClearing(clearingNumber);
							break;
						}
					}
				}
				if (rcChit.isWarning()) {
					// Large Campfire, Stink
					String warningsList = denizen.getThisAttribute("warning");
					List<String> warnings = Arrays.asList(warningsList.split("\\s*,\\s*"));
					String warning = chit.getThisAttribute("warning");;
					for (String warningName : warnings) {
						warningName = warningName.split("\\s+")[0];
						if (warningName.toLowerCase().matches(warning.toLowerCase())) {
							int clearingNumber = chit.getThisInt("clearing");
							clearingSummonTo = rcChit.getCurrentLocation().tile.getClearing(clearingNumber);
						}
					}
				}

				if (clearingSummonTo != null) {
					if (summonLivingDenizens()) {
						SetupCardUtility.resetDenizen(denizen);
					}
					if (denizen.getHeldBy() == denizenHolder) {
						clearingSummonTo.add(denizen, null);
					}
				}
			}
		}
	}
	
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Summons ");
		if (!getDenizenName().isEmpty()) {
			sb.append(getDenizenName()+"(s) ");
		}
		if (!getChit().isEmpty()) {
			sb.append("to "+getChit());
		}
		if (summonTo() != SummonTo.Anywhere && !getChit().isEmpty()) {
			sb.append(" of ");
		}
		if (summonTo() != SummonTo.Anywhere && getChit().isEmpty()) {
			sb.append(" to ");
		}
		if (summonTo() != SummonTo.Anywhere) {
			if (toLocation() && getQuestLocation() != null) {
				sb.append(getQuestLocation().getName());
			}
			else {
				sb.append(summonTo());
			}
		}
		sb.append(".");
		return sb.toString();
	}

	private String getChit() {
		return getString(CHIT) != null ?  getString(CHIT) : new String();
	}
	private String getDenizenName() {
		return getString(DENIZEN) != null ?  getString(DENIZEN) : new String();
	}
	private boolean summonLivingDenizens() {
		return getBoolean(SUMMON_LIVING_DENIZENS);
	}
	
	private SummonTo summonTo() {
		return SummonTo.valueOf(getString(SUMMON_TO));
	}
	private boolean toLocation() {
		return summonTo() == SummonTo.LocationsClearings || summonTo() == SummonTo.LocationsTiles;
	}
	
	public RewardType getRewardType() {
		return RewardType.SummonFromAppearance;
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