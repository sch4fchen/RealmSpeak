package com.robin.magic_realm.components.table;

import javax.swing.JFrame;

import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class StealAttempt extends RealmTable {
	
	public static final String KEY = "Stealing";
	private static final String[] RESULT = {
		"Success - Take desired item",
		"Success - Roll for item (susbstract 1)",
		"Success - Roll for item",
		"Success - Roll for item",
		"Suspect - Lose one level of friendliness",
		"Caught/Block - Natives become Enemy",
	};
	private RealmComponent victim;	
	
	//Native
	//Visitor
	//Traveler
	//Guild
	
	public StealAttempt(JFrame frame,RealmComponent victim) {
		super(frame,null);
		this.victim = victim;
	}
	public String getTableName(boolean longDescription) {
		return "Steal Attempt";
	}
	public String getTableKey() {
		return KEY;
	}
	public String apply(CharacterWrapper character,DieRoller roller) {
		return super.apply(character,roller);
	}
	public String applyOne(CharacterWrapper character) {
		return RESULT[0];
	}

	public String applyTwo(CharacterWrapper character) {
		super.setNewTable(new StealReward(getParentFrame(),victim));
		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {
		super.setNewTable(new StealReward(getParentFrame(),victim));
		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		super.setNewTable(new StealReward(getParentFrame(),victim));
		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {
		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		return RESULT[5];
	}
}