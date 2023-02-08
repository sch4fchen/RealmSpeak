package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardDiscoverTreasureSite extends QuestReward {
	
	public static final String SITE_REGEX = "_site_regex";

	public QuestRewardDiscoverTreasureSite(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> treasureSites = character.getGameData().getGameObjectsByNameRegex(getTreasureSiteRegex());
		for (GameObject site : treasureSites) {
			if (!site.hasThisAttribute("treasure_location")) continue;
			character.addTreasureLocationDiscovery(site.getName());
		}
	}
	
	public String getDescription() {
		return "Character discovers "+getTreasureSiteRegex();
	}

	public RewardType getRewardType() {
		return RewardType.DiscoverTreasureSite;
	}
	
	private String getTreasureSiteRegex() {
		return getString(SITE_REGEX);
	}
}