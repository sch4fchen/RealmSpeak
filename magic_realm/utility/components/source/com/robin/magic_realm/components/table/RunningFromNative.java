package com.robin.magic_realm.components.table;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class RunningFromNative extends RealmTable {
	
	public static final String KEY = "Running";
	private static final String[] RESULT = {
			"Ran away",
			"Ran away",
			"Ran away",
			"Ran away",
			"Ran away",
			"Caught (Battle)",
	};
	private RealmComponent trader;
	private String specificAction = "";
	
	public RunningFromNative(CharacterWrapper character,RealmComponent trader) {
		super(null,null);
		this.trader = trader;
	}
	public String getTableName(boolean longDescription) {
		return "Running from Native";
	}
	public String getTableKey() {
		return KEY+specificAction;
	}
	public void setSpecificAction(String val) {
		specificAction = ","+val;
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
		character.setBlocked(true);
		if (character.isHidden()) {
			character.setHidden(false);
		}
		character.changeRelationshipTo(trader.getGameObject(), RelationshipType.ENEMY);
		return RESULT[5];
	}
}