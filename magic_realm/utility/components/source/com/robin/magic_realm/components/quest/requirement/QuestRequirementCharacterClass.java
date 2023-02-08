package com.robin.magic_realm.components.quest.requirement;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementCharacterClass extends QuestRequirement {
	
	public static final String REGEX_FILTER = "_regex";

	public QuestRequirementCharacterClass(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		return Pattern.compile(getRegExFilter()).matcher(character.getCharacterName()).find();
	}

	protected String buildDescription() {
		return "Character must be a "+getRegExFilter()+".";
	}

	public RequirementType getRequirementType() {
		return RequirementType.CharacterClass;
	}
	
	public String getRegExFilter() {
		return getString(REGEX_FILTER);
	}
}