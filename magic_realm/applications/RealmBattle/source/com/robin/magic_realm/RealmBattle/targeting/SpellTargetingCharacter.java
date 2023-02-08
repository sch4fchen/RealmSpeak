package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingCharacter extends SpellTargetingSingle {
	
	private boolean lightOnly;

	public SpellTargetingCharacter(CombatFrame combatFrame, SpellWrapper spell,boolean lightOnly) {
		super(combatFrame, spell);
		this.lightOnly = lightOnly;
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> allCharacters = combatFrame.findCanBeSeen(battleModel.getAllParticipatingCharactersAsRc(),true);
		for (RealmComponent rc : allCharacters) {
			CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
			if (!character.hasMagicProtection() && (!lightOnly || !character.getVulnerability().strongerThan(new Strength("L")))) {
				gameObjects.add(rc.getGameObject());
			}
		}
		return true;
	}
}