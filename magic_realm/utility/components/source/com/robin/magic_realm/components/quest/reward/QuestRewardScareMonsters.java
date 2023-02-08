package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardScareMonsters extends QuestReward {
	
	public QuestRewardScareMonsters(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		TileLocation current = character.getCurrentLocation();
		if (!current.isInClearing()) return;
		
		// Get all the monsters
		ArrayList<MonsterChitComponent> monsters = new ArrayList<MonsterChitComponent>();
		for(RealmComponent rc:current.clearing.getClearingComponents()) {
			if (rc.isMonster() && rc.getOwnerId()==null) { // I'm going for uncontrolled monsters only here
				monsters.add((MonsterChitComponent)rc);
			}
		}
		
		// Get all the other clearings
		ArrayList<ClearingDetail> otherClearings = new ArrayList<ClearingDetail>();
		for(ClearingDetail clearing:current.tile.getClearings()) {
			if (clearing.getNum()==current.clearing.getNum()) continue;
			otherClearings.add(clearing);
		}
		
		// Now distribute! (jeese, excited enough)
		for(MonsterChitComponent monster:monsters) {
			int r = RandomNumber.getRandom(otherClearings.size());
			ClearingDetail otherClearing = otherClearings.get(r);
			ClearingUtility.moveToLocation(monster.getGameObject(),otherClearing.getTileLocation());
		}
	}
	
	public String getDescription() {
		return "All monsters in current clearing are moved to other clearings.";
	}

	public RewardType getRewardType() {
		return RewardType.ScareMonsters;
	}
}