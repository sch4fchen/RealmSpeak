package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementFighter extends QuestRequirement {

	public QuestRequirementFighter(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		return character.isFighter();
	}

	protected String buildDescription() {
		return "Character must be a fighter.";
	}

	public RequirementType getRequirementType() {
		return RequirementType.Fighter;
	}
}