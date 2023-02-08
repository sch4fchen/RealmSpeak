package com.robin.magic_realm.RealmBattle.targeting;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingDeadMonster extends SpellTargetingSingle {

	public SpellTargetingDeadMonster(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		for (RealmComponent rc:battleModel.getAllParticipatingCharacters()) {
			CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
			for (GameObject go:character.getKills(character.getCurrentDayKey())) {
				RealmComponent kill = RealmComponent.getRealmComponent(go);
				if (kill.isMonster()) {
					gameObjects.add(go);
				}
			}
		}
		return true;
	}
}