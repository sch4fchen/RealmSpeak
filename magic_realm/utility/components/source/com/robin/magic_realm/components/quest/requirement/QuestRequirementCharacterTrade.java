package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementCharacterTrade extends QuestRequirement {

	public enum TradeDirection {
		Give,
		Receive,
		Either,
		;
	}

	public static final String DIRECTION = "_ct_dir";
	public static final String ITEM_REGEX = "_ct_irx";
	public static final String ITEM_COUNT = "_ct_icnt";
	public static final String MIN_GOLD = "_ct_gold";
	public static final String PARTNER_REGEX = "_ct_prx";
	public static final String PARTNER_REQ_MARK = "_ct_preqm";
	public static final String PARTNER_ADD_MARK = "_ct_paddm";

	public QuestRequirementCharacterTrade(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (reqParams.actionType!=CharacterActionType.TradingWithCharacter) return false;

		GameObject partner = reqParams.targetOfSearch;
		if (partner==null) return false;
		String partnerRegex = getPartnerRegEx();
		if (!partnerRegex.isEmpty() && !Pattern.compile(partnerRegex).matcher(partner.getName()).find()) return false;
		String questId = getParentQuest().getGameObject().getStringId();
		if (partnerRequiresMark() && !Quest.GameObjectHasQuestMark(partner, questId)) return false;

		boolean met;
		switch(getDirection()) {
			case Give:
				met = sideFulfills(reqParams.objectList, reqParams.goldGiven);
				break;
			case Receive:
				met = sideFulfills(reqParams.receivedList, reqParams.goldReceived);
				break;
			default:
				met = sideFulfills(reqParams.objectList, reqParams.goldGiven)
						|| sideFulfills(reqParams.receivedList, reqParams.goldReceived);
				break;
		}
		if (!met) return false;

		if (partnerAddMark()) {
			Quest.GameObjectAddQuestMark(partner, questId);
		}
		return true;
	}

	private boolean sideFulfills(ArrayList<GameObject> items, int gold) {
		if (gold<getMinGold()) return false;
		int needed = getItemCount();
		if (needed<=0) {
			// No item condition, but an empty trade (nothing on this side) never counts
			return gold>0 || (items!=null && !items.isEmpty());
		}
		if (items==null) return false;
		String itemRegex = getItemRegEx();
		Pattern pattern = itemRegex.isEmpty()?null:Pattern.compile(itemRegex);
		int matches = 0;
		for (GameObject go : items) {
			if (go!=null && (pattern==null || pattern.matcher(go.getName()).find())) {
				matches++;
			}
		}
		return matches>=needed;
	}

	protected String buildDescription() {
		TradeDirection dir = getDirection();
		StringBuilder sb = new StringBuilder("Must ");
		switch(dir) {
			case Give:		sb.append("give"); break;
			case Receive:	sb.append("receive"); break;
			default:		sb.append("give or receive"); break;
		}
		boolean hasItems = getItemCount()>0;
		if (hasItems) {
			sb.append(" ").append(getItemCount()).append(" ");
			sb.append(getItemRegEx().isEmpty()?"item(s)":"/"+getItemRegEx()+"/");
		}
		if (getMinGold()>0) {
			sb.append(hasItems?" and ":" ").append(getMinGold()).append(" gold");
		}
		if (!hasItems && getMinGold()<=0) {
			sb.append(" something");
		}
		switch(dir) {
			case Give:		sb.append(" to"); break;
			case Receive:	sb.append(" from"); break;
			default:		sb.append(" with"); break;
		}
		sb.append(getPartnerRegEx().isEmpty()?" another character":" /"+getPartnerRegEx()+"/");
		if (partnerRequiresMark()) {
			sb.append(" (marked)");
		}
		sb.append(" in a finished trade.");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.CharacterTrade;
	}

	private TradeDirection getDirection() {
		String val = getString(DIRECTION);
		return val==null?TradeDirection.Either:TradeDirection.valueOf(val);
	}
	private String getItemRegEx() {
		String val = getString(ITEM_REGEX);
		return val==null?"":val.trim();
	}
	private int getItemCount() {
		return getInt(ITEM_COUNT);
	}
	private int getMinGold() {
		return getInt(MIN_GOLD);
	}
	private String getPartnerRegEx() {
		String val = getString(PARTNER_REGEX);
		return val==null?"":val.trim();
	}
	private boolean partnerRequiresMark() {
		return getBoolean(PARTNER_REQ_MARK);
	}
	private boolean partnerAddMark() {
		return getBoolean(PARTNER_ADD_MARK);
	}
}
