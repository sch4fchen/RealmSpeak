package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementAction extends QuestRequirement {

	public static final String ACTION = "_action";
	public static final String ACTION_WITHOUT_NATIVES = "_action_without_natives";
	
	public QuestRequirementAction(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		boolean actionExecuted = reqParams != null & reqParams.actionType == getAction();
		
		if ((getAction() == CharacterActionType.Hire || getAction() == CharacterActionType.Trading) && !actionWithoutNatives()) {
			boolean actionExecutedWithNatives = reqParams.objectList != null && !reqParams.objectList.isEmpty();
			return actionExecuted && actionExecutedWithNatives;
		}
		
		return actionExecuted;
	}

	private CharacterActionType getAction() {
		return CharacterActionType.valueOf(getString(ACTION));
	}
	
	private boolean actionWithoutNatives() {
		return getBoolean(ACTION_WITHOUT_NATIVES);
	}
	
	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must execute a '");
		sb.append(getAction().toString());
		sb.append("' action.");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Action;
	}
}