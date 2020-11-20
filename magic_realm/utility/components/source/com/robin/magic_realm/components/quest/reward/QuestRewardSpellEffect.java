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
import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRewardSpellEffect extends QuestReward {
	
	public static final String SPELL_REGEX = "_spellrx";
	public static final String UNEFFECT = "_uneffect";
	public static final String AFFECT_CHARACTER = "_affchar";
	public static final String AFFECT_ALL_TARGETS_IN_CHARACTERS_CLEARING = "_aff_all_targets_at_char";
	public static final String TARGET_REGEX = "_targetrx";
	public static final String EXPIRE_IMMEDIATELY = "_eximdtly";
	public static final String AFFECT_HIRELINGS = "_affh";
	public static final String AFFECT_COMPANIONS = "_affc";
	public static final String AFFECT_SUMMONED = "_affs";
	public static final String AFFECT_LIMITED = "_affl";
	public static final String TARGET_IN_LOCATION = "_target_in_loc";
	public static final String ALL_TARGETS_IN_LOCATION = "_aff_all_targets_in_loc";
	public static final String ALL_TARGETS_IN_RANDOM_LOCATION = "_aff_target_in_rnd_loc";
	public static final String LOCATION = "_loc";

	public QuestRewardSpellEffect(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		ArrayList<GameObject> potentialSpells = character.getGameData().getGameObjectsByNameRegex(getSpellRegex());
		ArrayList<GameObject> spells = new ArrayList<GameObject>();
		for (GameObject sp : potentialSpells) {
			if (sp.hasThisAttribute("spell")) {
				spells.add(sp);
			}
		}
		if (spells.isEmpty()) {
			JOptionPane.showMessageDialog(frame,"No spell found!","Quest Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		ArrayList<GameObject> targets = new ArrayList<GameObject>();
		if (!getTargetRegex().isEmpty()) {
			targets = character.getGameData().getGameObjectsByNameRegex(getTargetRegex());
		}
		GameWrapper gameWrapper = GameWrapper.findGame(getGameObject().getGameData());
		HostPrefWrapper hostPref = HostPrefWrapper.findHostPrefs(getGameData());
		QuestLocation loc = getQuestLocation();
		for (GameObject sp : spells) {
			SpellWrapper spell = new SpellWrapper(sp);
			spell.setString(SpellWrapper.CASTER_ID, String.valueOf(character.getGameObject().getId()));
			spell.setString(SpellWrapper.SPELL_ALIVE,"");
			if (affectCharacter()) {
				if (!targetMustBeInLocation() || loc == null || loc.locationMatchAddress(frame, character)) {
					spell.addTarget(hostPref, character.getGameObject());
			}
				
			ArrayList<RealmComponent> validTargets = new ArrayList<RealmComponent>();
			if (affectAllTargetsInCharactersLocation()) {
				TileLocation location = character.getCurrentLocation();
				validTargets.addAll(location.clearing.getClearingComponents());
			}	
			if (affectAllTargetsInLocation()) {
				ArrayList<TileLocation> validLocations = loc.fetchAllLocations(frame, character, character.getGameData());
				for (TileLocation location : validLocations) {
					validTargets.addAll(location.clearing.getClearingComponents());
				}
			}
			if (affectTargetsInRandomLocation()) {
				ArrayList<TileLocation> validLocations = loc.fetchAllLocations(frame, character, character.getGameData());
				int random = RandomNumber.getRandom(validLocations.size());
				TileLocation location = validLocations.get(random);
				validTargets.addAll(location.clearing.getClearingComponents());
	
			}
			for (RealmComponent validTarget : validTargets) {
				if (getTargetRegex().isEmpty() || validTarget.getName().matches(getTargetRegex())) {
					targets.add(validTarget.getGameObject());
				}
			}
				
			for (GameObject target : targets) {
				if (!affectHirelings() && target.hasThisAttribute(Constants.HIRELING)) {
					continue;
				}
				if (!affectCompanions() && target.hasThisAttribute(Constants.COMPANION)) {
					continue;
				}
				if (!affectSummoned() && target.hasThisAttribute(Constants.SUMMONED)) {
					continue;
				}
				if (affectOnlyHirelingsCompanionsSummonedMonsters() && !target.hasThisAttribute(Constants.HIRELING) && !target.hasThisAttribute(Constants.COMPANION) && !target.hasThisAttribute(Constants.SUMMONED)) {
					continue;
				}
				RealmComponent targetRc = RealmComponent.getRealmComponent(target);
				if (targetMustBeInLocation() && loc != null || !loc.locationMatchAddressForRealmComponent(frame, character, targetRc)) {
					continue;
				}
				
				spell.addTarget(hostPref, target);
			}
			if (uneffect()) {
				spell.unaffectTargets();
				return;
			}
			spell.affectTargets(frame, gameWrapper, expireImmediately());
			}
		}
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.SpellEffect;
	}
	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Cast the spell(s) /");
		sb.append(getSpellRegex()+"/");
		if (affectCharacter()) {
			sb.append(" on the character");
		}
		if (affectCharacter() && !getTargetRegex().isEmpty()) {
			sb.append(" and");
		}
		if (!getTargetRegex().isEmpty()) {
			sb.append(" on target(s) /"+getTargetRegex()+"/");
		}
		sb.append(".");
		if (targetMustBeInLocation() && getQuestLocation() != null) {
			sb.append("Targets must be in "+getQuestLocation().getName()+".");
		}
		return sb.toString();
	}
	public String getSpellRegex() {
		return getString(SPELL_REGEX);
	}
	private Boolean uneffect() {
		return getBoolean(UNEFFECT);
	}
	private Boolean affectCharacter() {
		return getBoolean(AFFECT_CHARACTER);
	}
	private Boolean affectAllTargetsInCharactersLocation() {
		return getBoolean(AFFECT_ALL_TARGETS_IN_CHARACTERS_CLEARING);
	}
	private String getTargetRegex() {
		return getString(TARGET_REGEX);
	}
	private Boolean expireImmediately() {
		return getBoolean(EXPIRE_IMMEDIATELY);
	}
	private Boolean affectHirelings() {
		return getBoolean(AFFECT_HIRELINGS);
	}
	private Boolean affectCompanions() {
		return getBoolean(AFFECT_COMPANIONS);
	}
	private Boolean affectSummoned() {
		return getBoolean(AFFECT_SUMMONED);
	}
	private Boolean affectOnlyHirelingsCompanionsSummonedMonsters() {
		return getBoolean(AFFECT_LIMITED);
	}
	private Boolean targetMustBeInLocation() {
		return getBoolean(TARGET_IN_LOCATION);
	}
	private Boolean affectAllTargetsInLocation() {
		return getBoolean(ALL_TARGETS_IN_LOCATION);
	}
	private Boolean affectTargetsInRandomLocation() {
		return getBoolean(ALL_TARGETS_IN_RANDOM_LOCATION);
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