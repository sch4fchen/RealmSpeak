package com.robin.magic_realm.components.effect;

import java.util.ArrayList;

import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.MonsterPartChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class DuelEffect implements ISpellEffect {

	private static String DUELLING = "duelling";
	
	@Override
	public void apply(SpellEffectContext context) {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(context.getGameData());
		
		ArrayList<RealmComponent> targets = context.Spell.getTargets();
		if (targets.size() != 2 || context.Spell.getExtraIdentifier() == DUELLING) return;
		
		targets.get(0).clearTargets();
		targets.get(1).clearTargets();
		targets.get(0).setTarget(targets.get(1));
		targets.get(1).setTarget(targets.get(0));
		
		CombatWrapper combat0 = new CombatWrapper(targets.get(0).getGameObject());
		combat0.setSheetOwner(true);
		CombatWrapper combat1 = new CombatWrapper(targets.get(1).getGameObject());
		combat1.setSheetOwnerId(targets.get(0));
		if (hostPrefs.hasPref(Constants.SR_COMBAT)) {
			combat0.setCombatBoxAttack(RandomNumber.getRandom(3));
			combat0.setCombatBoxDefense(RandomNumber.getRandom(3));
			combat1.setCombatBoxAttack(RandomNumber.getRandom(3));
			combat1.setCombatBoxDefense(RandomNumber.getRandom(3));
		}
		else {
			int random1 = RandomNumber.getRandom(3);
			combat0.setCombatBoxAttack(random1);
			combat0.setCombatBoxDefense(random1);
			int random2 = RandomNumber.getRandom(3);
			combat1.setCombatBoxAttack(random2);
			combat1.setCombatBoxDefense(random2);
		}
		
		for (RealmComponent target : targets) {
			if (target instanceof MonsterChitComponent) {
				MonsterPartChitComponent weapon = ((MonsterChitComponent) target).getWeapon();
				if (weapon != null) {
					CombatWrapper combat = new CombatWrapper(target.getGameObject());
					CombatWrapper combatWeapon = new CombatWrapper(weapon.getGameObject());
					combatWeapon.setCombatBoxAttack(RandomNumber.getRandom(3));
					while (combat.getCombatBoxAttack() == combatWeapon.getCombatBoxAttack()) {
						combatWeapon.setCombatBoxAttack(RandomNumber.getRandom(3));
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
