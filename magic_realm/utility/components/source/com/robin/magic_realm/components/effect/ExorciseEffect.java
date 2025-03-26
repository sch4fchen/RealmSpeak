package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class ExorciseEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		CombatWrapper combat = context.getCombatTarget();
		
		if (context.Target.getGameObject().hasThisAttribute(Constants.MAGIC_PROTECTION_EXTENDED)) {
			System.out.println("No effect on target.");
			return;
		}
		if (context.Target.getGameObject().hasThisAttribute(Constants.DEMON)||context.Target.getGameObject().hasThisAttribute(Constants.IMP)||context.Target.getGameObject().hasThisAttribute(Constants.DEVIL)||context.Target.getGameObject().hasThisAttribute(Constants.VAMPIRE)||context.Target.getGameObject().hasThisAttribute(Constants.SUCCUBUS)) {
			combat.setKilledBy(context.Caster);
			combat.setKilledLength(18);
			combat.setKilledSpeed(context.Spell.getAttackSpeed());
		}
		else if (context.Target.isCharacter()) {
			CharacterWrapper targChar = new CharacterWrapper(context.Target.getGameObject());
			
			// Cancel Spellcasting (do NOT include this spell!!)
			GameObject castSpell = combat.getCastSpell();
			if (castSpell!=null && !castSpell.equals(context.Spell.getGameObject())) {
				SpellWrapper otherSpell = new SpellWrapper(castSpell);
				otherSpell.expireSpell();
			}
			
			// Cancel curses
			targChar.removeAllCurses();
			
			// Fatigue Color Chits
			targChar.getColorChits().stream()
				.forEach(chit -> chit.makeFatigued());
		}
		else if (context.Target.isSpell()) {
			SpellWrapper otherSpell = new SpellWrapper(context.Target.getGameObject());
			otherSpell.expireSpell();
		}
		else {
			System.out.println("No effect on target.");
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
