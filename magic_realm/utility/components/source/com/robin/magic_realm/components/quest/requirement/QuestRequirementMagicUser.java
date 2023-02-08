package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementMagicUser extends QuestRequirement {

	public QuestRequirementMagicUser(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		return character.isMagicUser();
	}

	protected String buildDescription() {
		return "Character must be a magic user.";
	}

	public RequirementType getRequirementType() {
		return RequirementType.MagicUser;
	}
}