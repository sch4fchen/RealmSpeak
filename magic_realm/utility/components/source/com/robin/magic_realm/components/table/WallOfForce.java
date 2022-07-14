/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.components.table;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class WallOfForce {
	public static boolean apply(SpellWrapper spell, GameObject target) {
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
		targetRc.clearTargets();
		
		RealmLogging.logMessage(RealmLogging.BATTLE, target.getName() +" was blocked by Wall of Force. Attacks are cancelled.");
		return true;
	}
}