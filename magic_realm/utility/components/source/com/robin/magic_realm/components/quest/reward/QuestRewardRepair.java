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

import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ArmorChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardRepair extends QuestReward {
	
	public final static String ITEM = "_item";
	
	public QuestRewardRepair(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		if (!getRegex().isEmpty()) {
			character.getInventory().stream()
			.map(obj -> (GameObject)obj)
			.filter(go -> Pattern.compile(getRegex()).matcher(go.getName()).find())
			.map(go -> RealmComponent.getRealmComponent(go))
			.filter(rc -> rc.isArmor())
			.map(rc -> (ArmorChitComponent)rc)
			.filter(armor -> armor.isDamaged())
			.forEach(armor -> armor.setIntact(true));
		}
		else {
			SpellUtility.repair(character);
		}
	}
	
	private String getRegex() {
		return getString(ITEM);
	}
	
	public String getDescription() {
		if (!getRegex().isEmpty()) {
			return "Repairs '"+getRegex()+"' of the character.";
		}
		return "Repairs all items of the character.";
	}
	public RewardType getRewardType() {
		return RewardType.Repair;
	}

}