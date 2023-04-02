package com.robin.magic_realm.components.table;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class Mesmerize extends RealmTable {
	
	public static final String KEY = "Mesmerize";
	private static final String[] RESULT = {
		"Weakened",
		"Intoxicated",
		"Calmed",
		"Distracted",
		"Sapped",
		"Tired",
	};
	private boolean harm;
		
	public Mesmerize(JFrame frame,GameObject caster) {
		super(frame,null);
	}
	public boolean harmWasApplied() {
		return harm;
	}
	public String getTableName(boolean longDescription) {
		return "Mesmerize";
	}
	public String getTableKey() {
		return KEY;
	}
	public String getDestClientName(GameObject attacker,GameObject target) {
		RealmComponent attackerRc = RealmComponent.getRealmComponent(attacker);
		RealmComponent targetRc = RealmComponent.getRealmComponent(target);
		// Determine the destination client
		RealmComponent destOwner = attackerRc.getOwner();
		if (destOwner==null) {
			destOwner = targetRc.getOwner();
		}
		// destOwner should NOT be null at this point!  One or the other HAS to be owned // if monster attacks another monster it is null (e.g. duel spell)
		if (destOwner==null) {
			return attackerRc.getName();
		}
		CharacterWrapper destCharacter = new CharacterWrapper(destOwner.getGameObject());
		return destCharacter.getPlayerName();
	}
	public String apply(CharacterWrapper character,DieRoller roller) {
		harm = false;
		if (character.isMistLike()) {
			return "Unaffected - Mist";
		}
		else if (character.hasMagicProtection()) {
			return "Unaffected - Magic Protection";
		}
		else if (!character.isCharacter()) {
			return "Unaffected (denizen)";
		}
		harm = true;
		return super.apply(character,roller);
	}
	public String applyOne(CharacterWrapper character) {
		character.applyMesmerize(Constants.WEAKENED);
		return RESULT[0];
	}

	public String applyTwo(CharacterWrapper character) {
		character.applyMesmerize(Constants.INTOXICATED);
		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {
		character.applyMesmerize(Constants.CALMED);
		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		character.applyMesmerize(Constants.DISTRACTED);
		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {
		character.applyMesmerize(Constants.SAPPED);
		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		character.applyMesmerize(Constants.TIRED);
		return RESULT[5];
	}
	public static Mesmerize doNow(JFrame parent,GameObject attacker,GameObject target,boolean casterRolls,int redDie) {
		Mesmerize mesmerize = new Mesmerize(parent,attacker);
		CharacterWrapper caster = new CharacterWrapper(attacker);
		CharacterWrapper victim = new CharacterWrapper(target);
		DieRoller roller = DieRollBuilder.getDieRollBuilder(parent,casterRolls?caster:victim,redDie).createRoller(mesmerize);
		String result = mesmerize.apply(victim,roller);
		RealmLogging.logMessage(caster.getGameObject().getName(),"Mesmerize roll: "+roller.getDescription());
		RealmLogging.logMessage(caster.getGameObject().getName(),"Mesmerize result: "+result);
		return mesmerize;
	}
}