package com.robin.magic_realm.components.effect;

import java.util.ArrayList;

import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.MonsterPartChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class DuelEffect implements ISpellEffect {

	private static String DUELLING = "duelling";
	
	@Override
	public void apply(SpellEffectContext context) {
		ArrayList<RealmComponent> targets = context.Spell.getTargets();
		if (targets.size() != 2 || context.Spell.getExtraIdentifier() == DUELLING) return;
		
		targets.get(0).clearTargets();
		targets.get(1).clearTargets();
		targets.get(0).setTarget(targets.get(1));
		targets.get(1).setTarget(targets.get(0));
		
		CombatWrapper combat0 = new CombatWrapper(targets.get(0).getGameObject());
		combat0.setSheetOwner(true);
		combat0.setCombatBox(RandomNumber.getRandom(3));
		CombatWrapper combat1 = new CombatWrapper(targets.get(1).getGameObject());
		combat1.setSheetOwnerId(targets.get(0));
		combat1.setCombatBox(RandomNumber.getRandom(3));
		
		for (RealmComponent target : targets) {
			if (target instanceof MonsterChitComponent) {
				MonsterPartChitComponent weapon = ((MonsterChitComponent) target).getWeapon();
				if (weapon != null) {
					CombatWrapper combat = new CombatWrapper(target.getGameObject());
					CombatWrapper combatWeapon = new CombatWrapper(weapon.getGameObject());
					combatWeapon.setCombatBox(RandomNumber.getRandom(3));
					while (combat.getCombatBox() == combatWeapon.getCombatBox()) {
						combatWeapon.setCombatBox(RandomNumber.getRandom(3));
					}
				}
			}
		}
		context.Spell.setExtraIdentifier(DUELLING);
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
