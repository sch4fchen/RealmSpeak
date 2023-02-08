package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingDragon extends SpellTargetingSingle {

	public SpellTargetingDragon(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> allDenizens = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		ArrayList<RealmComponent> allParticipantsSansDenizens = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(false),true);
		allDenizens.removeAll(allParticipantsSansDenizens);
		for (RealmComponent rc : allDenizens) {
			if (rc.isMonster() && !rc.isPlayerControlledLeader() && !rc.hasMagicProtection()) {
				String icon = rc.getGameObject().getAttribute(rc.getThisBlock(),"icon_type");
				if (icon!=null &&
						(icon.indexOf("dragon")>=0
							|| icon.indexOf("drake")>=0
							|| icon.indexOf("wyrm")>=0)) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		return true;
	}
}