package com.robin.magic_realm.components.effect;

import java.util.Collection;

import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class DarkFavorEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		CharacterWrapper target = new CharacterWrapper(context.Target.getGameObject());
		DieRoller roller = DieRollBuilder.getDieRollBuilder(context.Parent,target,0).createRoller("Dark Favor");
		RealmLogging.logMessage(target.getName(),"Dark Favor result: "+roller.getHighDieResult());
		CombatWrapper combat = new CombatWrapper(context.Target.getGameObject());
		Collection<CharacterActionChitComponent> c = target.getNonWoundedChits();
		int wounds = roller.getHighDieResult();
		if (c.size() > wounds) {
			combat.addNewWounds(wounds);
		}
		else {
			RealmLogging.logMessage(target.getName(),"Dark Favor killed "+target.getName()+".");
			target.makeDead("Killed by Dark Favor.");
		}
		
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}