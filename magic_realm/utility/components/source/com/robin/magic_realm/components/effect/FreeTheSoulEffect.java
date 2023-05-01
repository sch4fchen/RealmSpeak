package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class FreeTheSoulEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		if (context.Target.getGameObject().hasThisAttribute("ghost")||context.Target.getGameObject().hasThisAttribute("wraith")
				||context.Target.getGameObject().hasThisAttribute("ghoul")||context.Target.getGameObject().hasThisAttribute("skeleton")
				||context.Target.getGameObject().hasThisAttribute("undead")) {
			DieRoller roller = DieRollBuilder.getDieRollBuilder(context.Parent,(new CharacterWrapper(context.Caster)),0).createRoller("Free the Soul");
			RealmLogging.logMessage(context.Caster.getName(),"Free the Soul result: "+roller.getHighDieResult());
			if (roller.getHighDieResult()==6) {
				RealmLogging.logMessage(context.Caster.getName(),"Free the Soul: Kills "+context.Target.getGameObject().getName());
				CombatWrapper combat = context.getCombatTarget();
				combat.setKilledBy(context.Caster);
				combat.setKilledLength(18);
				combat.setKilledSpeed(context.Spell.getAttackSpeed());
				if (context.Target.getHorse()!=null) {
					killHorse(context);
				}
			}
			else {
				RealmLogging.logMessage(context.Caster.getName(),"Free the Soul: Does not kill "+context.Target.getGameObject().getName());
				if (context.Target.getHorse()!=null && !context.Target.getHorse().isDead()) {
					roller = DieRollBuilder.getDieRollBuilder(context.Parent,(new CharacterWrapper(context.Caster)),0).createRoller("Free the Soul");
					RealmLogging.logMessage(context.Caster.getName(),"Free the Soul result: "+roller.getHighDieResult());
					if (roller.getHighDieResult()==6) {
						killHorse(context);
					}
					else {
						RealmLogging.logMessage(context.Caster.getName(),"Free the Soul: Does not kill "+context.Target.getHorse().getGameObject().getName());
					}
				}
			}
		}
	}
	
	private static void killHorse(SpellEffectContext context) {
		GameObject horseGo = context.Target.getHorse().getGameObject();
		RealmLogging.logMessage(context.Caster.getName(),"Free the Soul: Kills "+horseGo.getName());
		CombatWrapper combatHorse = new CombatWrapper(horseGo);
		combatHorse.setKilledBy(context.Caster);
		combatHorse.setKilledLength(18);
		combatHorse.setKilledSpeed(context.Spell.getAttackSpeed());
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
