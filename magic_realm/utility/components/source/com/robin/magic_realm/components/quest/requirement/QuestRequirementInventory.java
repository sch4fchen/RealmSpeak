package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.TreasureType;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementInventory extends QuestRequirementLoot {
	private static Logger logger = Logger.getLogger(QuestRequirementInventory.class.getName());
	
	public static final String NUMBER = "_num";
	public static final String ITEM_ACTIVE = "_iact";
	public static final String ITEM_DEACTIVE = "_idact";
	public static final String EXACT_NUMBER = "_exact_num";

	public QuestRequirementInventory(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		boolean reqActive = mustBeActive();
		boolean reqDeactive = mustBeDeactive();
		ArrayList<GameObject> matches;
		if (reqActive && reqParams.actionType==CharacterActionType.ActivatingItem) {
			reqActive = false; // since we already know we are activating the item, don't test for it later (actually causes a problem with the Chest which is opened instead of activated!)
			matches = filterObjectsForRequirement(character,reqParams.objectList,logger);
			ArrayList<GameObject> inventory = filterObjectsForRequirement(character,character.getInventory(),logger);
			for (GameObject item : inventory) {
				if (!item.hasThisAttribute(Constants.ACTIVATED)) {
					logger.fine(item.getName()+" must be activated.");
				}
				else {
					matches.add(item);
				}
			}
		}
		else {
			matches = filterObjectsForRequirement(character,character.getInventory(),logger);
		}
		int n = getNumber();
		int found = matches.size();
		ArrayList<GameObject> validMatches = new ArrayList<>();
		if (found>=n) {
			for(GameObject match:matches) {
					if (reqActive && !match.hasThisAttribute(Constants.ACTIVATED)) {
						logger.fine(match.getName()+" must be activated.");
						found--;
						continue;
					}
					if (reqDeactive && match.hasThisAttribute(Constants.ACTIVATED)) {
						logger.fine(match.getName()+" must be deactivated.");
						found--;
						continue;
					}
					validMatches.add(match);
			}			
			if (mustBeExactTheNumber()) {
				if (found==n) {
					if (markItems()) {
						for (GameObject validItem : validMatches) {
							validItem.setThisAttribute(QuestConstants.QUEST_MARK,getParentQuest().getGameObject().getStringId());
						}
					}
					return true;
				}
			}
			else {
				if (found>=n) {
					if (markItems()) {
						for (GameObject validItem : validMatches) {
							validItem.setThisAttribute(QuestConstants.QUEST_MARK,getParentQuest().getGameObject().getStringId());
						}
					}
					return true;
				}
			}
		}
		logger.fine("Only "+found+" items were found, when "+n+" was expected.");
		return false;
	}

	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must ");
		sb.append(mustBeActive()?"activate ":"own ");
		int num = getNumber();
		sb.append(num);
		if(mustBeDeactive()) {
			sb.append(" deactivated");
		}
		TreasureType tt = getTreasureType();
		if (tt!=TreasureType.Any) {
			sb.append(" ");
			sb.append(getTreasureType().toString().toLowerCase());
		}
		if (tt==TreasureType.Small || tt==TreasureType.Large) {
			sb.append(" treasure");
		}
		else {
			sb.append(" item");
		}
		sb.append(num==1?"":"s");
		String regex = getRegExFilter();
		if (regex!=null && regex.trim().length()>0) {
			sb.append(", matching /");
			sb.append(regex);
			sb.append("/");
		}
		if (getRequiredAbility()!=null && !getRequiredAbility().isEmpty()) {
			sb.append(" with the ability "+getRequiredAbility());
		}
		sb.append(".");
		return sb.toString();

	}

	public RequirementType getRequirementType() {
		return RequirementType.Inventory;
	}
	public int getNumber() {
		return getInt(NUMBER);
	}
	public boolean mustBeActive() {
		return getBoolean(ITEM_ACTIVE);
	}
	public boolean mustBeDeactive() {
		return getBoolean(ITEM_DEACTIVE);
	}
	public boolean mustBeExactTheNumber() {
		return getBoolean(EXACT_NUMBER);
	}
}