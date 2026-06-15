package com.robin.magic_realm.components.table;

import javax.swing.JFrame;

import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class OffroadTravel extends RealmTable {
	
	public static final String KEY = "Offroad";
	private static final String[] RESULT_MOUNTAINS = {
			"Choice/Mark Path",
			"Choose Clearing",
			"Random Clearing",
			"Same Clearing",
			"Avalanche (1d6 wounds)",
			"Lost",
	};
	private static final String[] RESULT_CAVES = {
			"Choice/Mark Path",
			"Choose Clearing",
			"Random Clearing",
			"Lost (1 wound)",
			"Lost (1 wound)",
			"Same Clearing",
	};
	private static final String[] RESULT_OTHERS = {
			"Choice/Mark Path",
			"Choose Clearing",
			"Choose Clearing",
			"Random Clearing",
			"Lost",
			"Lost",
	};
	private ClearingDetail clearing;
	
	public OffroadTravel(JFrame frame, ClearingDetail clearing) {
		super(frame,null);
		this.clearing = clearing;
	}
	public String getTableName(boolean longDescription) {
		return "Offroad";
	}
	public String getTableKey() {
		return KEY;
	}
	
	public String applyOne(CharacterWrapper character) {
		if (clearing.isMountain()) {
			choiceOrMarkPath(character);
			return RESULT_MOUNTAINS[0];
		}
		if (clearing.isCave()) {
			choiceOrMarkPath(character);
			return RESULT_CAVES[0];
		}
		choiceOrMarkPath(character);
		return RESULT_OTHERS[0];
	}

	public String applyTwo(CharacterWrapper character) {
		if (clearing.isMountain()) {
			chooseClearing(character);
			return RESULT_MOUNTAINS[1];
		}
		if (clearing.isCave()) {
			chooseClearing(character);
			return RESULT_CAVES[1];
		}
		chooseClearing(character);
		return RESULT_OTHERS[1];
	}

	public String applyThree(CharacterWrapper character) {
		if (clearing.isMountain()) {
			randomClearing(character);
			return RESULT_MOUNTAINS[2];
		}
		if (clearing.isCave()) {
			randomClearing(character);
			return RESULT_CAVES[2];
		}
		chooseClearing(character);
		return RESULT_OTHERS[2];
	}

	public String applyFour(CharacterWrapper character) {
		if (clearing.isMountain()) {
			sameClearing(character);
			return RESULT_MOUNTAINS[3];
		}
		if (clearing.isCave()) {
			lost(character,1);
			return RESULT_CAVES[3];
		}
		randomClearing(character);
		return RESULT_OTHERS[3];
	}

	public String applyFive(CharacterWrapper character) {
		if (clearing.isMountain()) {
			avalanche(character);
			return RESULT_MOUNTAINS[4];
		}
		if (clearing.isCave()) {
			lost(character,1);
			return RESULT_CAVES[4];
		}
		lost(character,0);
		return RESULT_OTHERS[4];
	}

	public String applySix(CharacterWrapper character) {
		if (clearing.isMountain()) {
			lost(character,0);
			return RESULT_MOUNTAINS[5];
		}
		if (clearing.isCave()) {
			sameClearing(character);
			return RESULT_CAVES[5];
		}
		lost(character,0);
		return RESULT_OTHERS[5];
	}
	
	private void choiceOrMarkPath(CharacterWrapper character) {	
	}
	private void chooseClearing(CharacterWrapper character) {	
	}
	private void randomClearing(CharacterWrapper character) {	
	}
	private void sameClearing(CharacterWrapper character) {	
	}
	private void lost(CharacterWrapper character, int wounds) {	
	}
	private void avalanche(CharacterWrapper character) {	
	}
}