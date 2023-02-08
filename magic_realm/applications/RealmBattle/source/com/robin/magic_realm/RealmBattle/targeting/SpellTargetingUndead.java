package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.magic_realm.RealmBattle.*;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingUndead extends SpellTargetingMultiple {

	public SpellTargetingUndead(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		BattleGroup bg = battleModel.getParticipantsBattleGroup(activeParticipant);
		ArrayList<RealmComponent> otherOpponents = combatFrame.findCanBeSeen(battleModel.getAllOtherBattleParticipants(bg,true,combatFrame.allowsTreachery()),true);
		otherOpponents = CombatSheet.filterNativeFriendly(activeParticipant, otherOpponents);
		for (RealmComponent rc : otherOpponents) {
			if (rc.isMonster() && rc.getGameObject().hasThisAttribute(Constants.UNDEAD) && !rc.hasMagicProtection()) {
				gameObjects.add(rc.getGameObject());
			}
		}
		return true;
	}
}