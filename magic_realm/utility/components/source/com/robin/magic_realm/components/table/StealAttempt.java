package com.robin.magic_realm.components.table;

import javax.swing.JFrame;

import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.SearchResultType;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class StealAttempt extends RealmTable {
	
	public static final String KEY = "Stealing";
	private static final String[] RESULT = {
		"Success - Take desired item",
		"Success - Roll for item (substract 1)",
		"Success - Roll for item",
		"Success - Roll for item",
		"Suspect - Lose one level of friendliness",
		"Caught/Blocked - Natives become Enemy",
	};
	private RealmComponent victim;
		
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
		StealTablesCommon.stealChoice(getParentFrame(),character,victim,"Steal Attempt");
		testQuestRequirements(character,SearchResultType.Choice);
		return RESULT[0];
	}

	public String applyTwo(CharacterWrapper character) {
		super.setNewTable(new StealReward(getParentFrame(),victim,-1));
		testQuestRequirements(character,SearchResultType.Item);
		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {
		super.setNewTable(new StealReward(getParentFrame(),victim));
		testQuestRequirements(character,SearchResultType.Item);
		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		super.setNewTable(new StealReward(getParentFrame(),victim));
		testQuestRequirements(character,SearchResultType.Item);
		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {
		character.changeRelationship(victim.getGameObject(), -1);
		testQuestRequirements(character,SearchResultType.LooseFriendliness);
		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		character.changeRelationshipTo(victim.getGameObject(), RelationshipType.ENEMY);
		character.setBlocked(true);
		character.setHidden(false);
		testQuestRequirements(character,SearchResultType.CaughtBlocked);
		return RESULT[5];
	}
	
	private void testQuestRequirements(CharacterWrapper character, SearchResultType searchResult) {
		QuestRequirementParams params = new QuestRequirementParams();
		params.actionType = CharacterActionType.SearchTable;
		params.actionName = getTableKey();
		params.targetOfSearch = victim.getGameObject();
		params.searchType = searchResult;
		params.searchHadAnEffect = true;
		character.testQuestRequirements(getParentFrame(),params);
	}
}