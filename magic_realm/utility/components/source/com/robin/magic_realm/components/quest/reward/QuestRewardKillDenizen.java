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
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.Spoils;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class QuestRewardKillDenizen extends QuestReward {
	
	public static final String DENIZEN_REGEX = "_drx";
	public static final String REWARD_CHARACTER = "_rc";
	public static final String KILL_MARKED = "_km";
	public static final String KILL_HIRELINGS = "_kh";
	public static final String KILL_COMPANIONS = "_kc";
	public static final String KILL_SUMMONED = "_ks";
	public static final String KILL_CLONED = "_kcloned";
	public static final String KILL_LIMITED = "_kl";
	public static final String KILL_IN_CHAR_LOCATION = "_k_i_cloc";
	public static final String KILL_IN_CHAR_TILE = "_k_i_ctile";
	public static final String KILL_IN_LOCATION = "_k_i_loc";
	public static final String LOCATION = "_loc";
	
	public QuestRewardKillDenizen(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> denizens = new ArrayList<>();
		if (!getDenizenNameRegex().isEmpty()) {
			denizens = character.getGameData().getGameObjectsByNameRegex(getDenizenNameRegex());
		}
		else {
			GamePool pool = new GamePool(getGameData().getGameObjects());
			ArrayList<String> query = new ArrayList<>();
			query.add("vulnerability");
			query.add("denizen");
			denizens.addAll(pool.find(query));
		}
		
		String questId = getParentQuest().getGameObject().getStringId();
		for (GameObject denizen : denizens) {
			if (!denizen.hasThisAttribute("vulnerability") || (!denizen.hasThisAttribute("denizen") && !isATransformation(denizen)) || denizen.hasThisAttribute(Constants.DEAD)) continue;
			GameObject denizenHolder = SetupCardUtility.getDenizenHolder(denizen);
			if (denizenHolder != null && denizen.getHeldBy() == denizenHolder) continue;
			if (requiresMark()) {
				String mark = denizen.getThisAttribute(QuestConstants.QUEST_MARK);
				if (mark==null || !mark.equals(questId)) continue;
			}
			if (!killHirelings() && denizen.hasThisAttribute(Constants.HIRELING)) {
				continue;
			}
			if (!killCompanions() && denizen.hasThisAttribute(Constants.COMPANION)) {
				continue;
			}
			if (!killSummoned() && denizen.hasThisAttribute(Constants.SUMMONED)) {
				continue;
			}
			if (!killCloned() && denizen.hasThisAttribute(Constants.CLONED)) {
				continue;
			}
			if (killOnlyHirelingsCompanionsSummonedMonsters() && !denizen.hasThisAttribute(Constants.HIRELING) && !denizen.hasThisAttribute(Constants.COMPANION) && !denizen.hasThisAttribute(Constants.SUMMONED)  && !denizen.hasThisAttribute(Constants.CLONED)) {
				continue;
			}
			RealmComponent denizenRc = RealmComponent.getRealmComponent(denizen);
			if (denizenRc == null) continue;
			if (charLocationOnly()) {
				TileLocation charLoc = character.getCurrentLocation();
				TileLocation denizenLoc = denizenRc.getCurrentLocation();
				if (charLoc == null || denizenLoc == null || charLoc.tile == null || denizenLoc.tile == null || charLoc.tile != denizenLoc.tile || charLoc.clearing != denizenLoc.clearing) continue;
			}
			if (charTileOnly()) {
				TileLocation charLoc = character.getCurrentLocation();
				TileLocation denizenLoc = denizenRc.getCurrentLocation();
				if (charLoc == null || denizenLoc == null || charLoc.tile == null || denizenLoc.tile == null || charLoc.tile != denizenLoc.tile) continue;
			}
			
			if (locationOnly()) {
				QuestLocation loc = getQuestLocation();
				if (loc.locationMatchAddressForRealmComponent(frame, character, denizenRc)) {
					RealmUtility.makeDead(denizenRc);
					if (rewardCharacter()) {
						giveReward(character, denizen);
					}
					CombatWrapper.clearRoundCombatInfo(denizen);
				}
			}
			else {
				RealmUtility.makeDead(denizenRc);
				if (rewardCharacter()) {
					giveReward(character, denizen);
				}
				CombatWrapper.clearRoundCombatInfo(denizen);
			}
		}
	}
	
	private static boolean isATransformation(GameObject denizen) {
		if (!denizen.hasThisAttribute("query")) {
			return false;
		};
		return denizen.getThisAttribute("query").toLowerCase().matches("transform.*");
	}
	
	private static void giveReward(CharacterWrapper character, GameObject denizen) {
		Spoils spoils = Spoils.getSpoils(character.getGameObject(),denizen);
		character.addKill(denizen,spoils);
		if (spoils.hasFameOrNotoriety()) {
			character.addFame(spoils.getFame());
			character.addNotoriety(spoils.getNotoriety());
		}
		if (spoils.hasGold()) {
			character.addGold(spoils.getGoldBounty());
			character.addGold(spoils.getGoldRecord());
		}
	}
	
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		if (getDenizenNameRegex().isEmpty()) {
			sb.append("All denizens are killed");
		}
		else {
			sb.append(getDenizenNameRegex() +" is/are killed");
		}
		if (requiresMark()) {
			sb.append(" which have a quest mark");
		}
		if (locationOnly() && getQuestLocation() != null) {
			sb.append(" in "+getQuestLocation().getName());
		}
		sb.append(".");
		return sb.toString();
	}
	private String getDenizenNameRegex() {
		return getString(DENIZEN_REGEX);
	}
	private boolean rewardCharacter() {
		return getBoolean(REWARD_CHARACTER);
	}
	private Boolean requiresMark() {
		return getBoolean(KILL_MARKED);
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
	private Boolean killCloned() {
		return getBoolean(KILL_CLONED);
	}
	private Boolean killOnlyHirelingsCompanionsSummonedMonsters() {
		return getBoolean(KILL_LIMITED);
	}
	public RewardType getRewardType() {
		return RewardType.KillDenizen;
	}
	private boolean charLocationOnly() {
		return getBoolean(KILL_IN_CHAR_LOCATION);
	}
	private boolean charTileOnly() {
		return getBoolean(KILL_IN_CHAR_TILE);
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