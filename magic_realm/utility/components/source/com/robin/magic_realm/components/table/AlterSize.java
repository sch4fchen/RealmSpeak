package com.robin.magic_realm.components.table;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class AlterSize extends RealmTable {
	
	public static final String KEY = "Alter Object";
	private static final String[] RESULT = {
			"Tougher",
			"Larger",
			"Fatter",
			"Thinner",
			"Smaller",
			"Weaker",
	};
	private SpellWrapper spell;
	
	public AlterSize(JFrame frame,GameObject caster, SpellWrapper spell) {
		super(frame,null);
		this.spell = spell;
	}
	public String getTableName(boolean longDescription) {
		return "Alter Object";
	}
	public String getTableKey() {
		return KEY;
	}
	
	public String applyOne(CharacterWrapper character) {
		boolean spellApplied = SpellUtility.ApplyNamedSpellEffectToTargetAndReturn(Constants.ALTER_SIZE, character.getGameObject(), spell);
		if (spellApplied) {
			SpellUtility.ApplyNamedSpellEffectToTarget(Constants.ALTER_SIZE_INCREASE_VULNERABILITY, character.getGameObject(), spell);
		}
		return RESULT[0];
	}

	public String applyTwo(CharacterWrapper character) {
		boolean spellApplied = SpellUtility.ApplyNamedSpellEffectToTargetAndReturn(Constants.ALTER_SIZE, character.getGameObject(), spell);
		if (spellApplied) {
			SpellUtility.ApplyNamedSpellEffectToTarget(Constants.ALTER_SIZE_INCREASE_WEIGHT, character.getGameObject(), spell);
			SpellUtility.ApplyNamedSpellEffectToTarget(Constants.ALTER_SIZE_INCREASE_VULNERABILITY, character.getGameObject(), spell);
		}
		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {
		boolean spellApplied = SpellUtility.ApplyNamedSpellEffectToTargetAndReturn(Constants.ALTER_SIZE, character.getGameObject(), spell);
		if (spellApplied) {
			SpellUtility.ApplyNamedSpellEffectToTarget(Constants.ALTER_SIZE_INCREASE_WEIGHT, character.getGameObject(), spell);
		}
		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		boolean spellApplied = SpellUtility.ApplyNamedSpellEffectToTargetAndReturn(Constants.ALTER_SIZE, character.getGameObject(), spell);
		if (spellApplied) {
			SpellUtility.ApplyNamedSpellEffectToTarget(Constants.ALTER_SIZE_DECREASE_WEIGHT, character.getGameObject(), spell);
		}
		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {
		boolean spellApplied = SpellUtility.ApplyNamedSpellEffectToTargetAndReturn(Constants.ALTER_SIZE, character.getGameObject(), spell);
		if (spellApplied) {
			SpellUtility.ApplyNamedSpellEffectToTarget(Constants.ALTER_SIZE_DECREASE_WEIGHT, character.getGameObject(), spell);
			SpellUtility.ApplyNamedSpellEffectToTarget(Constants.ALTER_SIZE_DECREASE_VULNERABILITY, character.getGameObject(), spell);
		}
		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		boolean spellApplied = SpellUtility.ApplyNamedSpellEffectToTargetAndReturn(Constants.ALTER_SIZE, character.getGameObject(), spell);
		if (spellApplied) {
			SpellUtility.ApplyNamedSpellEffectToTarget(Constants.ALTER_SIZE_DECREASE_VULNERABILITY, character.getGameObject(), spell);
		}
		return RESULT[5];
	}
	public static AlterSize doNow(JFrame parent,GameObject attacker,GameObject target,int redDie,SpellWrapper spell) {
		AlterSize alterSize = new AlterSize(parent,attacker,spell);
		CharacterWrapper victim = new CharacterWrapper(target);
		
		DieRoller roller = DieRollBuilder.getDieRollBuilder(parent,spell.getCaster(),redDie).createRoller(alterSize);
		String result = alterSize.apply(victim, roller);
		RealmLogging.logMessage(spell.getCaster().getName(),"Alter Size roll: "+roller.getDescription());
		RealmLogging.logMessage(spell.getCaster().getName(),"Alter Size result: "+result);
		return alterSize;
	}
}