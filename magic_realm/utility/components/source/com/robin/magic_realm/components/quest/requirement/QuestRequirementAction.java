/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
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