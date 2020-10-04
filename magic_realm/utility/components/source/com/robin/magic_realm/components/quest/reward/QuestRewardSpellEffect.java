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
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRewardSpellEffect extends QuestReward {
	
	public static final String SPELL_REGEX = "_spellrx";
	public static final String AFFECT_CHARACTER = "_affchar";
	public static final String AFFECT_CHARACTERS_LOCATION = "_affcharloc";
	public static final String TARGET_REGEX = "_targetrx";
	public static final String EXPIRE_IMMEDIATELY = "_eximdtly";
	public static final String AFFECT_HIRELINGS = "_affh";
	public static final String AFFECT_COMPANIONS = "_affc";
	public static final String AFFECT_LIMITED = "_affl";

	public QuestRewardSpellEffect(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		GamePool pool = new GamePool(getGameData().getGameObjects());
		ArrayList<GameObject> potentialSpells = pool.find(getSpellRegex());
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
			targets = pool.find(getTargetRegex());
		}
		GameWrapper gameWrapper = GameWrapper.findGame(getGameObject().getGameData());
		HostPrefWrapper hostPref = HostPrefWrapper.findHostPrefs(getGameData());
		for (GameObject sp : spells) {
			SpellWrapper spell = new SpellWrapper(sp);
			spell.setString(SpellWrapper.CASTER_ID, String.valueOf(character.getGameObject().getId()));
			spell.setString(SpellWrapper.SPELL_ALIVE,"");
			if (affectCharacter()) {
				spell.addTarget(hostPref, character.getGameObject());
			}
			for (GameObject target : targets) {
				if (!affectHirelings() && target.hasThisAttribute(Constants.CLONED)) {
					continue;
				}
				if (!affectCompanionsAndSummonedMonsters() && target.hasThisAttribute(Constants.COMPANION)) {
					continue;
				}
				if (affectOnlyHirelingsCompanionsSummonedMonsters() && !target.hasThisAttribute(Constants.CLONED) && !target.hasThisAttribute(Constants.COMPANION)) {
					continue;
				}
				
				spell.addTarget(hostPref, target);
			}
			spell.affectTargets(frame, gameWrapper, expireImmediately());
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
		return sb.toString();
	}
	public String getSpellRegex() {
		return getString(SPELL_REGEX);
	}
	private Boolean affectCharacter() {
		return getBoolean(AFFECT_CHARACTER);
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
	private Boolean affectCompanionsAndSummonedMonsters() {
		return getBoolean(AFFECT_COMPANIONS);
	}
	private Boolean affectOnlyHirelingsCompanionsSummonedMonsters() {
		return getBoolean(AFFECT_LIMITED);
	}
}