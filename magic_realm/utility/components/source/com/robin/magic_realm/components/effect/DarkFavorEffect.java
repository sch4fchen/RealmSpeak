package com.robin.magic_realm.components.effect;

import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class DarkFavorEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		CharacterWrapper target = new CharacterWrapper(context.Target.getGameObject());
		DieRoller roller = DieRollBuilder.getDieRollBuilder(context.Parent,target,0).createRoller("DarkFavor");
		RealmLogging.logMessage(target.getName(),"Dark Favor result: "+roller.getHighDieResult());
		int fatigue = roller.getHighDieResult();
		target.setWeatherFatigue(target.getWeatherFatigue()+fatigue);
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}