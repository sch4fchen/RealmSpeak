package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class AlterObjectEffect implements ISpellEffect {

	private static final String[] RESULT = {
			"Negligible",
			"Light",
			"Medium",
			"Heavy",
			"Tremendous",
			"Maximum",
	};
	private static final String[] EFFECT = {
			"N",
			"L",
			"M",
			"H",
			"T",
			"X",
	};	
	
	@Override
	public void apply(SpellEffectContext context) {
		CharacterWrapper caster = context.Spell.getCaster();
		GameObject target = context.Target.getGameObject();
		int redDie = context.Spell.getRedDieLock();
		DieRoller roller = DieRollBuilder.getDieRollBuilder(context.Parent,context.Spell.getCaster(),redDie).createRoller("Alter Weight");
		int die = roller.getHighDieResult();
		SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ALTER_WEIGHT, target, context.Spell, EFFECT[die-1]);
		String result = RESULT[die-1];
		RealmLogging.logMessage(caster.getName(),"Alter Object roll: "+roller.getDescription());
		RealmLogging.logMessage(caster.getName(),"Alter Object result: "+result);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		if(context.Target.getGameObject().hasThisAttribute(Constants.ALTER_WEIGHT)){
			context.Target.getGameObject().removeThisAttribute(Constants.ALTER_WEIGHT);
		}
	}
}
