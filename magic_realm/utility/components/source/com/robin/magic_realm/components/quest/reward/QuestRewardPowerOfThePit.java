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
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.quest.DieRollType;
import com.robin.magic_realm.components.table.PowerOfThePit;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardPowerOfThePit extends QuestReward {
	
	public static final String DIE_ROLL = "_dr";
	
	public QuestRewardPowerOfThePit(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		PowerOfThePit powerOfthePit = new PowerOfThePit(frame, character.getGameObject(),0,new Speed(0));
		DieRoller roller;
		if (getString(DIE_ROLL).equals(DieRollType.Random.toString())) {
			roller = DieRollBuilder.getDieRollBuilder(frame, character, getDieRoll()).createRoller(powerOfthePit);
		}
		else {
			roller = DieRollBuilder.getDieRollBuilder(frame, character, getDieRoll()).createRoller(powerOfthePit,1);
		}
		powerOfthePit.apply(character,roller);
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.PowerOfThePit;
	}
	@Override
	public String getDescription() {
		return "Grants the character a Power of the Pit.";
	}
	private int getDieRoll() {
		String dieRoll = getString(DIE_ROLL);
		return getDieRoll(DieRollType.valueOf(dieRoll));
	}
}