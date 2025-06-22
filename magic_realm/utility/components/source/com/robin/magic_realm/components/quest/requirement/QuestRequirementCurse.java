package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementCurse extends QuestRequirement {

	public static final String CURSE = "_curse";
	
	public QuestRequirementCurse(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		return character.hasCurse(getCurse());
	}

	protected String buildDescription() {
		return "Character must have the curse "+getCurse();		
	}

	public RequirementType getRequirementType() {
		return RequirementType.Curse;
	}
	
	private String getCurse() {
		return getString(CURSE);
	}
}