package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementTeleport extends QuestRequirement {

	public static final String TELEPORT_TYPE = "_teleport_type";
	public static final String ANY_TYPE = "any";
	
	public QuestRequirementTeleport(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (reqParams != null && reqParams.actionType == CharacterActionType.Teleport) {
			if (teleportType()!=null && !reqParams.actionName.matches(teleportType())) {
				return false;
			}
			return true;
		}
		return false;
	}

	private String teleportType() {
		return getString(TELEPORT_TYPE);
	}
		
	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must teleport");
		if (teleportType()!=null) {
			sb.append(" ("+teleportType()+")");
		}
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Teleport;
	}

}