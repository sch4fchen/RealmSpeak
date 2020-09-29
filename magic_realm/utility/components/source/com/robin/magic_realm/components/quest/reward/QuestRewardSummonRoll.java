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
import java.util.Random;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.DieRollType;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class QuestRewardSummonRoll extends QuestReward {
	
	public static final String DIE_ROLL = "_dr";
	
	public QuestRewardSummonRoll(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameData());
		ArrayList<GameObject> summoned = new ArrayList<GameObject>();
		SetupCardUtility.summonMonsters(hostPrefs, summoned, character, getDieRoll());
	}
	
	public String getDescription() {
		if (getString(DIE_ROLL)!=DieRollType.Random.toString()) {
			return "Summon roll with a die roll of "+getDieRoll()+".";
		}
		else {
			return "Summon roll with a random die roll.";
		}
	}

	public RewardType getRewardType() {
		return RewardType.SummonRoll;
	}
	
	public int getDieRoll() {
		String dieRoll = getString(DIE_ROLL);
		Random rnd = new Random();	
		switch (DieRollType.valueOf(dieRoll)) {
			case One:
				return 1;
			case Two:
				return 2;
			case Three:
				return 3;
			case Four:
				return 4;
			case Five:
				return 5;
			case Six:
				return 6;
			default:
				return rnd.nextInt(6)+1;
		}
	}
}