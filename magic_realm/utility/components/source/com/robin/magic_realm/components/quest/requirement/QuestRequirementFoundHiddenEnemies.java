package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementFoundHiddenEnemies extends QuestRequirement {

	public QuestRequirementFoundHiddenEnemies(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		return character.foundHiddenEnemies();
	}

	protected String buildDescription() {
		return "Character must have found hidden enemies.";
	}

	public RequirementType getRequirementType() {
		return RequirementType.FoundHiddenEnemies;
	}
}