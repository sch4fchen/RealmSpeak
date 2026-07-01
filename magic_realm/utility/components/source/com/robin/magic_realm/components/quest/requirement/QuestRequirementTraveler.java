package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementTraveler extends QuestRequirement {

	public QuestRequirementTraveler(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {		
		return false;
	}
	
	protected String buildDescription() {
		return "";
	}

	public RequirementType getRequirementType() {
		return RequirementType.Traveler;
	}
}