package com.robin.magic_realm.components.table;

import javax.swing.JFrame;

import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class StealReward extends RealmTable {
	
	public static final String KEY = "Stealing";
	private static final String[] RESULT = {
		"Choice",
		"Mount",
		"Armor (players choice)",
		"Treasure (randomly selected)",
		"10 gold",
		"Nothing",
	};
	private RealmComponent victim;
	
	public StealReward(JFrame frame,RealmComponent victim) {
		super(frame,null);
		this.victim = victim;
	}
	public String getTableName(boolean longDescription) {
		return "Steal Reward";
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
		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {
		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {
		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		return RESULT[5];
	}
}