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

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.quest.HealType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardHeal extends QuestReward {
	
	public static final String HEAL = "_heal";
	
	public QuestRewardHeal(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		Collection<CharacterActionChitComponent> chits;
		switch (getHealType()) {
		case Fatigued:
			chits = character.getFatiguedChits();
			break;
		case Wounded:
			chits = character.getWoundedChits();
			break;
		case Restable:
			chits = character.getRestableChits();
			break;
		case Magic:
			chits = character.getAllMagicChits();
			break;
		case All:
		default:
			chits = character.getAllChits();
		}

		for (CharacterActionChitComponent chit : chits) {
			chit.makeActive();
		}
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.Heal;
	}
	@Override
	public String getDescription() {
		return "Heals all chits of the character.";
	}
	private HealType getHealType() {
		String armor = getString(HEAL);
		if (armor == null) {
			return HealType.All;
		}
		return HealType.valueOf(getString(HEAL));
	}	
}