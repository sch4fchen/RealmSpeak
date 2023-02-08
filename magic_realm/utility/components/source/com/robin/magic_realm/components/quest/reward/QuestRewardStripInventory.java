package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.TreasureUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardStripInventory extends QuestReward {
	public static final String STRIP_GOLD = "_sg";

	public QuestRewardStripInventory(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> list = new ArrayList<GameObject>(character.getInventory());
		for(GameObject go:list) {
			if (TreasureUtility.doDeactivate(null,character,go)) { // null JFrame, so that the character isn't hit with any message popups!
				lostItem(go);
			}
		}
	}
	
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("All inventory ");
		if (isStripGold()) sb.append("and gold ");
		sb.append("is stripped from the character.");
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.StripInventory;
	}
	
	public void setStripGold(boolean val) {
		setBoolean(STRIP_GOLD,val);
	}
	
	public boolean isStripGold() {
		return getBoolean(STRIP_GOLD);
	}
}