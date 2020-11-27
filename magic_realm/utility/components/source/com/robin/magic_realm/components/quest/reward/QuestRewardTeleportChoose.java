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
import com.robin.magic_realm.RealmQuestBuilder.QuestTesterFrame;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardTeleportChoose extends QuestReward {
	
	public static final String TELEPORT_TYPE = "_type";
	public static final String REASON = "_reason";
	
	public QuestRewardTeleportChoose(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		if (frame instanceof QuestTesterFrame) return;
		SpellUtility.doTeleport(frame, getReason(), character,getTeleportType());
	}
	
	private SpellUtility.TeleportType getTeleportType() {
		return SpellUtility.TeleportType.valueOf(getString(TELEPORT_TYPE));
	}
	private String getReason() {
		return getString(REASON);
	}
	
	public String getDescription() {
		return "Teleports the character.";
	}

	public RewardType getRewardType() {
		return RewardType.TeleportChoose;
	}
}