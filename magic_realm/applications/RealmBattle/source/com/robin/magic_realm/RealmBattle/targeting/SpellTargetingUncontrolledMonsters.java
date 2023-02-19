package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingUncontrolledMonsters extends SpellTargetingSingle {

	public SpellTargetingUncontrolledMonsters(CombatFrame combatFrame,SpellWrapper spell) {
		super(combatFrame, spell);
	}
	
	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> allDenizens = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		ArrayList<RealmComponent> allParticipantsSansDenizens = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(false),true);
		allDenizens.removeAll(allParticipantsSansDenizens);
		String validTargets = spell.getGameObject().getThisAttribute("targeted_monsters");
		String[] targetNames = validTargets.split(",");
		for (RealmComponent rc : allDenizens) {
			if (rc.isMonster() && !rc.isPlayerControlledLeader() && !rc.hasMagicProtection()) {
				String name = rc.getGameObject().getName().toLowerCase();
				for (String targetName : targetNames) {
					if (name.contains(targetName.trim())) {
						gameObjects.add(rc.getGameObject());
						continue;
					}
				}
			}
		}
		return true;
	}
}