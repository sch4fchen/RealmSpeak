package com.robin.magic_realm.components.table;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class Meditate extends RealmTable {
	
	public static final String KEY = "Meditate";
	private static final String[] RESULT = {
			"Revelation",
			"Peaceful Aura",
			"Insight",
			"Energized",
			"Prepared",
			"Strained",
	};
	private SpellWrapper spell;
	
	public Meditate(JFrame frame,GameObject caster, SpellWrapper spell) {
		super(frame,null);
		this.spell = spell;
	}
	public String getTableName(boolean longDescription) {
		return "Meditate";
	}
	public String getTableKey() {
		return KEY;
	}
	
	public String applyOne(CharacterWrapper character) {

		return RESULT[0];
	}

	public String applyTwo(CharacterWrapper character) {

		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {

		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		boolean spellApplied = SpellUtility.ApplyNamedSpellEffectToTargetAndReturn(Constants.MEDITATE, character.getGameObject(), spell);
		if (spellApplied) {
			SpellUtility.ApplyNamedSpellEffectToTarget(Constants.MEDITATE_EXTRA_PHASE, character.getGameObject(), spell);
		}
		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {

		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		return RESULT[5];
	}
	public static Meditate doNow(JFrame parent,GameObject attacker,GameObject target,int redDie,SpellWrapper spell) {
		Meditate meditate = new Meditate(parent,attacker,spell);
		CharacterWrapper victim = new CharacterWrapper(target);
		
		DieRoller roller = DieRollBuilder.getDieRollBuilder(parent,spell.getCaster(),redDie).createRoller(meditate);
		String result = meditate.apply(victim, roller);
		RealmLogging.logMessage(spell.getCaster().getName(),"Meditate roll: "+roller.getDescription());
		RealmLogging.logMessage(spell.getCaster().getName(),"Meditate result: "+result);
		return meditate;
	}
}