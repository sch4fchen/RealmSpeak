package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementEnchant extends QuestRequirement {

	public static final String TYPE = "_type";
	
	public QuestRequirementEnchant(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		return reqParams.actionType == CharacterActionType.Enchant && getType().matches(reqParams.actionName);
	}

	protected String buildDescription() {
		if (getType().matches("chit")) {
			return "Character must enchant a chit.";
		}
		return "Character must enchant a tile.";
	}

	public RequirementType getRequirementType() {
		return RequirementType.Enchant;
	}
	
	private String getType() {
		return getString(TYPE);
	}
}