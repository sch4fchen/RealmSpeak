package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;

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
		SetupCardUtility.summonMonsters(hostPrefs, summoned, character, getDieRoll(),0);
	}
	
	public String getDescription() {
		if (getString(DIE_ROLL)!=DieRollType.Random.toString()) {
			return "Summon roll with a die roll of "+getDieRoll()+".";
		}
		return "Summon roll with a random die roll.";
	}

	public RewardType getRewardType() {
		return RewardType.SummonRoll;
	}
	
	private int getDieRoll() {
		String dieRoll = getString(DIE_ROLL);
		return getDieRoll(DieRollType.valueOf(dieRoll));
	}
}