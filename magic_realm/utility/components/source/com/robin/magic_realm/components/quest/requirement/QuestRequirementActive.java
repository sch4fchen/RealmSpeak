package com.robin.magic_realm.components.quest.requirement;

import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestState;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementActive extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementActive.class.getName());

	public QuestRequirementActive(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		boolean ret = getParentQuest().getState()==QuestState.Active;
		if (!ret) {
			logger.fine("Quest is not active.");
		}
		return ret;
	}

	protected String buildDescription() {
		return "Only when quest is active.";
	}

	public RequirementType getRequirementType() {
		return RequirementType.Active;
	}

}