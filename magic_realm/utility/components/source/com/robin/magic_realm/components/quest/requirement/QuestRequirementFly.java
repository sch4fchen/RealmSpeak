package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementFly extends QuestRequirement {
	
	public static final String FLYING = "Flying";
	public static final String ABILITY_TO_FLY = "Ability to fly";
	public static final String FLY = "_fly";

	public QuestRequirementFly(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (mustFly() == true) {
			return character.getCurrentLocation()!=null && character.getCurrentLocation().isFlying();
		}
		return character.canFly(character.getCurrentLocation());
	}

	protected String buildDescription() {
		if (mustFly() == true) {
			return "The character must fly.";
		}
		return "Character must have the ability to fly in his current location.";		
	}

	public RequirementType getRequirementType() {
		return RequirementType.Fly;
	}
	
	private boolean mustFly() {
		return getString(FLY) == FLYING;
	}
}