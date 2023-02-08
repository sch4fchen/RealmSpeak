package com.robin.magic_realm.RealmBattle.targeting;

import java.util.Collection;

import com.robin.game.objects.GameData;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellMasterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingSpellOrCurse extends SpellTargetingSingle {

	public SpellTargetingSpellOrCurse(CombatFrame combatFrame,SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		String targetType = spell.getGameObject().getThisAttribute("target");
		GameData gameData = spell.getGameObject().getGameData();
		if (targetType.indexOf("spell")>=0) {
			SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(gameData);
			for (SpellWrapper targetSpell : sm.getAllSpellsInClearing(battleModel.getBattleLocation(),true)) {
				if (targetSpell.isAlive()) {
					identifiers.add(targetSpell.getTargetsName());
					gameObjects.add(targetSpell.getGameObject());
				}
			}
		}
		if (targetType.indexOf("curse")>=0) {
			for (RealmComponent rc : battleModel.getAllParticipatingCharacters()) {
				CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
				Collection<String> curses = character.getAllCurses();
				if (curses.size()>0) {
					for (String curse : curses) {
						identifiers.add(curse);
						gameObjects.add(rc.getGameObject());
					}
				}
			}
		}
		return true;
	}
}