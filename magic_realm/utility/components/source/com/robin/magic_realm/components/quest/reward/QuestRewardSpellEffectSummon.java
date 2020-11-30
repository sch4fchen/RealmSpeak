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

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.SpellCreator;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRewardSpellEffectSummon extends QuestReward {
	
	public static final String SUMMON_TYPE = "_type";
	public static final String REMOVE = "_unsommon";
		
	public QuestRewardSpellEffectSummon(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		String spell;
		switch (getSummonType()) {
		case animal:
			spell = "Summon Animal";
			break;
		case elemental:
			spell = "Summon Elemental";
			break;
		case undead:
			spell = "Raise Dead";
			break;
		default:
			return;
		}
		SpellWrapper spellWrapper = SpellCreator.CreateSpellWrapper(spell, character);
		if (spellWrapper == null) return;
		
		if (remove()) {
			SpellUtility.unsummonCompanions(spellWrapper);
			return;
		}
		SpellUtility.summonRandomCompanions(frame,  character.getGameObject(),  character, spellWrapper,  getSummonType().toString());
	}
	
	private SpellUtility.SummonType getSummonType() {
		return SpellUtility.SummonType.valueOf(getString(SUMMON_TYPE));
	}
	private boolean remove() {
		return getBoolean(REMOVE);
	}
	
	public String getDescription() {
		if (remove()) {
			return "Removes the creatures from the character.";
		}
		return "Summons creatures for the character";
	}

	public RewardType getRewardType() {
		return RewardType.SpellEffectSummon;
	}
}