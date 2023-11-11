package com.robin.magic_realm.components.table;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class AlterObject extends RealmTable {
	
	public static final String KEY = "Alter Object";
	private static final String[] RESULT = {
			"Negligible weight",
			"Light weight",
			"Medium weight",
			"Heavy weight",
			"Tremendous weight",
			"Maximum weight",
	};
	private static final String[] EFFECT = {
			"N",
			"L",
			"M",
			"H",
			"T",
			"X",
	};
	private SpellWrapper spell;
	
	public AlterObject(JFrame frame,GameObject caster, SpellWrapper spell) {
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
		SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ALTER_WEIGHT, character.getGameObject(), spell, EFFECT[0]);
		checkForMaximumItemWeight(RealmComponent.getRealmComponent(character.getGameObject()));
		return RESULT[0];
	}

	public String applyTwo(CharacterWrapper character) {
		SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ALTER_WEIGHT, character.getGameObject(), spell, EFFECT[1]);
		checkForMaximumItemWeight(RealmComponent.getRealmComponent(character.getGameObject()));
		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {
		SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ALTER_WEIGHT, character.getGameObject(), spell, EFFECT[2]);
		checkForMaximumItemWeight(RealmComponent.getRealmComponent(character.getGameObject()));
		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ALTER_WEIGHT, character.getGameObject(), spell, EFFECT[3]);
		checkForMaximumItemWeight(RealmComponent.getRealmComponent(character.getGameObject()));
		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {
		SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ALTER_WEIGHT, character.getGameObject(), spell, EFFECT[4]);
		checkForMaximumItemWeight(RealmComponent.getRealmComponent(character.getGameObject()));
		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		SpellUtility.ApplyNamedSpellEffectWithValueToTarget(Constants.ALTER_WEIGHT, character.getGameObject(), spell, EFFECT[5]);
		checkForMaximumItemWeight(RealmComponent.getRealmComponent(character.getGameObject()));
		return RESULT[5];
	}
	private void checkForMaximumItemWeight(RealmComponent target) {
		if (target.getWeight().equals(Strength.valueOf("X")) && target.isActivated() && !target.isHorse() && !target.isNativeHorse()) {
			target.setActivated(false);
		}
		TileLocation loc = spell.getCaster().getCurrentLocation();
		if (target.getWeight().equals(Strength.valueOf("X")) && loc!=null && loc.clearing!=null && !target.isHorse() && !target.isNativeHorse()) {
			loc.clearing.add(target.getGameObject(), spell.getCaster());
		}
	}
	public static AlterObject doNow(JFrame parent,GameObject attacker,GameObject target,int redDie,SpellWrapper spell) {
		AlterObject alterObject = new AlterObject(parent,attacker,spell);
		CharacterWrapper victim = new CharacterWrapper(target);
		
		DieRoller roller = DieRollBuilder.getDieRollBuilder(parent,spell.getCaster(),redDie).createRoller(alterObject);
		String result = alterObject.apply(victim, roller);
		RealmLogging.logMessage(spell.getCaster().getName(),"Alter Object roll: "+roller.getDescription());
		RealmLogging.logMessage(spell.getCaster().getName(),"Alter Object result: "+result);
		return alterObject;
	}
}