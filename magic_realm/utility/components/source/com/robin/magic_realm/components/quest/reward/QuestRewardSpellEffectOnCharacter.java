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
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardSpellEffectOnCharacter extends QuestReward {
	
	public static final String SPELL = "_spell";
	public static final String REMOVE = "_remove";
	
	public enum EffectOnCharacter {
		BlazingLightX,
		DivineMight,
		DivineProtection,
		PeaceWithNature,
		Premonition,
		Prophecy,
		Shrink,
		Slowed,
		SpiritGuide,
		ValeWalker
	}
	
	public QuestRewardSpellEffectOnCharacter(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		String effect;
		switch (getSpell()) {
		case BlazingLightX:
			effect = Constants.TORCH_BEARER;
			break;
		case DivineMight:
			effect = Constants.STRONG_MF;
			break;
		case DivineProtection:
			effect = Constants.ADDS_ARMOR;
			break;
		case PeaceWithNature:
			effect = Constants.PEACE_WITH_NATURE;
			break;
		case Premonition:
			effect = Constants.CHOOSE_TURN;
			break;
		case Prophecy:
			effect = Constants.DAYTIME_ACTIONS;
			break;
		case Shrink:
			effect = Constants.SHRINK;
			break;
		case Slowed:
			effect = Constants.SLOWED;
			break;
		case SpiritGuide:
			effect = Constants.SPIRIT_GUIDE;
			break;
		case ValeWalker:
			effect = Constants.VALE_WALKER;
			break;
		default:
			return;
		}
		
		if (remove()) {
			character.getGameObject().removeThisAttribute(effect);
			return;
		}
		character.getGameObject().setThisAttribute(effect);
	}
	
	private EffectOnCharacter getSpell() {
		return EffectOnCharacter.valueOf(getString(SPELL));
	}
	
	private Boolean remove() {
		return getBoolean(REMOVE);
	}
	
	public String getDescription() {
		return "Applies the spell effect "+getSpell()+" on the character.";
	}
	public RewardType getRewardType() {
		return RewardType.SpellEffectOnCharacter;
	}

}