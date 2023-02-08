package com.robin.magic_realm.components.table;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class WallOfForce {
	public static boolean apply(SpellWrapper spell, GameObject target) {
		CharacterWrapper caster = spell.getCaster();
		RealmComponent casterRc = RealmComponent.getRealmComponent(caster.getGameObject()); 
		RealmComponent targetRc = RealmComponent.getRealmComponent(target);
		if (targetRc.isCharacter()) {
			CombatWrapper characterCombat = new CombatWrapper(target);
			if (characterCombat.getCastSpell() != null) {
				SpellWrapper castSpell = new SpellWrapper(characterCombat.getCastSpell());
				if (castSpell.isAttackSpell()) {
					castSpell.cancelSpell();
				}
			}
		}
		
		if (targetRc.getTarget() == casterRc) {
			targetRc.clearTarget();
		}
		if (targetRc.get2ndTarget() == casterRc) {
			targetRc.clear2ndTarget();
		}
		
		RealmLogging.logMessage(RealmLogging.BATTLE, target.getName() +" was blocked by "+spell.getName()+". Attack cancelled.");
		return true;
	}
}