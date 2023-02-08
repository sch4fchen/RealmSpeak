package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardTeleportChoose extends QuestReward {
	
	public static final String TELEPORT_TYPE = "_type";
	public static final String REASON = "_reason";
	
	public QuestRewardTeleportChoose(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		if (frame.getTitle().matches("RealmSpeak Quest Tester")) return;
		SpellUtility.doTeleport(frame, getReason(), character,getTeleportType(),0);
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