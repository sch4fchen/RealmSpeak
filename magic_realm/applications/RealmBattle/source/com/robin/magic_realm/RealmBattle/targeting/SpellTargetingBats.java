package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingBats extends SpellTargetingAll {

	public SpellTargetingBats(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}
	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> allBattleParticipants = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		for (RealmComponent rc : allBattleParticipants) {
			if (rc.isMonster() && !rc.isPlayerControlledLeader() && !rc.hasMagicProtection()) {
				String icon = rc.getGameObject().getAttribute(rc.getThisBlock(),"icon_type");
				if ("bat".equals(icon)) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		return true;
	}
}