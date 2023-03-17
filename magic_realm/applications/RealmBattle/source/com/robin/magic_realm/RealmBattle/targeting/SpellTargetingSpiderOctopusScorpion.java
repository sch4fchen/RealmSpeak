package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingSpiderOctopusScorpion extends SpellTargetingSingle {

	public SpellTargetingSpiderOctopusScorpion(CombatFrame combatFrame,SpellWrapper spell) {
		super(combatFrame, spell);
	}
	
	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> allDenizens = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		ArrayList<RealmComponent> allParticipantsSansDenizens = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(false),true);
		allDenizens.removeAll(allParticipantsSansDenizens);
		for (RealmComponent rc : allDenizens) {
			if (rc.isMonster() && !rc.isPlayerControlledLeader() && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell.getRequiredColorMagic())) {
				String name = rc.getGameObject().getName().toLowerCase();
				if (name.contains("spider") || name.contains("octopus") || name.contains("scorpion")) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		return true;
	}
}