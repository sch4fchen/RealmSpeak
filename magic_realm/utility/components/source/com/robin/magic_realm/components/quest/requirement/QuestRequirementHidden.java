package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementHidden extends QuestRequirement {

	public QuestRequirementHidden(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		return character.isHidden();
	}

	protected String buildDescription() {
		return "Character must be hidden.";
	}

	public RequirementType getRequirementType() {
		return RequirementType.Hidden;
	}
}