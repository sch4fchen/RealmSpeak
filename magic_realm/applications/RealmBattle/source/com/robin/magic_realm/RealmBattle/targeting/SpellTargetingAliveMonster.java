package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingAliveMonster extends SpellTargetingSingle {

	public SpellTargetingAliveMonster(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> allBattleParticipants = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		for (RealmComponent rc : allBattleParticipants) {
			if (rc.isMonster() && !rc.hasMagicProtection() && !rc.getGameObject().hasThisAttribute(Constants.UNDEAD)) {
				gameObjects.add(rc.getGameObject());
			}
		}
		return true;
	}
}