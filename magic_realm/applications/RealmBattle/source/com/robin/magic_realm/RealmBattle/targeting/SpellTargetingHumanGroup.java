package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.RealmBattle.CombatSheet;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingHumanGroup extends SpellTargetingSingle {

	public SpellTargetingHumanGroup(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		// Giants, or Ogres, or Native Group
		ArrayList<RealmComponent> potentialTargets = battleModel.getAllBattleParticipants(true);
		potentialTargets = CombatSheet.filterNativeFriendly(activeParticipant, potentialTargets);
		String ownerId = activeParticipant.getGameObject().getStringId();
		for (RealmComponent rc:potentialTargets) {
			if (!rc.isCharacter() && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell) && (rc.getOwnerId()==null || rc.getOwnerId().equals(ownerId))) {
				String groupName = null;
				if (rc.isMonster() && !rc.isPlayerControlledLeader()) {
					if (rc.getGameObject().hasThisAttribute("ogre")) {
						groupName="Ogres";
					}
					if (rc.getGameObject().hasThisAttribute("giant")) {
						groupName="Giants";
					}
				}
				else if (rc.isNative()) {
					groupName = rc.getGameObject().getAttribute(rc.getThisBlock(),"native");
				}
				
				if (groupName!=null) {
					ArrayList list = secondaryTargets.get(groupName);
					if (list==null) {
						list = new ArrayList<>();
						secondaryTargets.put(groupName,list);
					}
					list.add(rc);
				}
			}
		}
		return true;
	}
}