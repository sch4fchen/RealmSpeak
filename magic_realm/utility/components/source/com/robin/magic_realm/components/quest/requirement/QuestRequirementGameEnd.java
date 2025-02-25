package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementGameEnd extends QuestRequirement {
	public QuestRequirementGameEnd(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		return character.isGameOver();
	}

	protected String buildDescription() {
		return "Game must be ended.";
	}

	public RequirementType getRequirementType() {
		return RequirementType.GameEnd;
	}
}