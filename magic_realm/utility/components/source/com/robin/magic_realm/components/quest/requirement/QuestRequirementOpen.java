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

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;

public class QuestRequirementOpen extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementOpen.class.getName());

	public static final String LOCATION_REGEX = "_regex";
	public static final String OPEN_BY_ANYONE = "_opnanyone";

	public QuestRequirementOpen(GameObject go) {
		super(go);
	}

	@Override
	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (characterHasToOpenIt() && reqParams.actionType != CharacterActionType.Open && reqParams.actionType != CharacterActionType.ActivatingItem && reqParams.actionType != CharacterActionType.SearchTable) {
			return false;
		}

		String regex = getRegExFilter();
				
			if (characterHasToOpenIt() && (reqParams.actionType == CharacterActionType.Open || reqParams.actionType == CharacterActionType.ActivatingItem || reqParams.actionType == CharacterActionType.SearchTable)) {
				if (regex != null && regex.trim().length() > 0) {
					Pattern pattern = Pattern.compile(regex);
					if (reqParams.actionType == CharacterActionType.Open || reqParams.actionType == CharacterActionType.SearchTable) {
						if (!pattern.matcher(reqParams.targetOfSearch.getName()).find()) {
							logger.fine(reqParams.targetOfSearch.getName()+" does not match regex /"+regex+"/");
						}
						else {
							return true;
						}
					}
					else if (reqParams.actionType == CharacterActionType.ActivatingItem) {
						GameObject item = reqParams.objectList.get(0);
						String itemName = item != null ? item.getName() : "Item";
							if (item == null || !pattern.matcher(itemName).find()) {
								logger.fine(itemName+" does not match regex /"+regex+"/");
							}
							else {
								return true;
							}
						}
					}
			}
			else {
				ArrayList<GameObject> needsToBeOpened = character.getGameData().getGameObjectsByNameRegex(getRegExFilter());
				for (GameObject location : needsToBeOpened) {
					if (!location.hasThisAttribute(Constants.NEEDS_OPEN) && !location.hasThisAttribute(Constants.SEARCH)) {
						return true;
					}
				}
			}
			
		return false;
	}

	@Override
	public RequirementType getRequirementType() {
		return RequirementType.Open;
	}
	@Override
	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getRegExFilter()+" must be opened");
		if (characterHasToOpenIt()) {
			sb.append(" by the character");
		}
		sb.append(".");
		return sb.toString();
	}
	public String getRegExFilter() {
		return getString(LOCATION_REGEX);
	}
	private Boolean characterHasToOpenIt() {
		return !getBoolean(OPEN_BY_ANYONE);
	}
}