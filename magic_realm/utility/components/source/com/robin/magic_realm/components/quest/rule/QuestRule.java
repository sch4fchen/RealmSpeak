package com.robin.magic_realm.components.quest.rule;

import java.util.Hashtable;

import com.robin.game.objects.GameObject;

/**
 * Quest rules are active as soon as the quest is taken.
 */
public class QuestRule {
	
	public enum RuleType {
		ActiveMonster,				// Specified monster type regenerates every 7th day regardless of monster roll
		FameNotorietyRestricted,
		MovementRestricted,			// no fly or horse/pony bonus
		OpenVault,
		;
		public boolean affectsAllPlayers() {
			return this==ActiveMonster; // eventually others here...
		}
	}
	
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		// override if IDs need to be updated!
	}
}