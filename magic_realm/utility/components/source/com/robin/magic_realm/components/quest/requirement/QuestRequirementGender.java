package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.GenderType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementGender extends QuestRequirement {
	
	public static final String GENDER = "_gen";

	public QuestRequirementGender(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		return character.getGender() == getRequiredGender();
	}

	protected String buildDescription() {
		return "Character must be "+getRequiredGender().toString().toLowerCase()+".";
	}

	public RequirementType getRequirementType() {
		return RequirementType.Gender;
	}
	
	public GenderType getRequiredGender() {
		return GenderType.valueOf(getString(GENDER));
	}
}