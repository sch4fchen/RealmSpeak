package com.robin.magic_realm.components.quest.requirement;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.TradeType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementTrade extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementTrade.class.getName());
	
	public static final String TRADE_TYPE = "_tt";
	public static final String TRADE_WITH_REGEX = "_trx";
	public static final String TRADE_ITEM_REGEX = "_irx";
	public static final String TRADE_ITEM = "_trade_item";
	public static final String TRADE_TREASURE = "_trade_treasure";
	public static final String TRADE_SPELL = "_trade_spell";
	public static final String ADD_MARK = "_add_mark";

	public QuestRequirementTrade(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (reqParams.actionType!=CharacterActionType.Trading) {
			logger.fine("Character is not trading.");
			return false;
		}
		
		TradeType tt = getTradeType();
		if (!tt.toString().equals(reqParams.actionName)) {
			logger.fine("Character is not doing a "+tt.toString()+" trade.");
			return false;
		}
		
		String trader = getTradeWithRegEx();
		Pattern pattern = trader!=null && trader.trim().length()>0?Pattern.compile(trader):null;
		if (pattern==null || (reqParams.targetOfSearch!=null && pattern.matcher(reqParams.targetOfSearch.getName()).find())) {
			if (reqParams.objectList!=null && reqParams.objectList.size()>0) {
				String itemRegex = getTradeItemRegEx();
				Pattern itemPattern = itemRegex!=null && itemRegex.trim().length()>0?Pattern.compile(itemRegex):null;

				for (GameObject go:reqParams.objectList) {
					RealmComponent rc = RealmComponent.getRealmComponent(go);
					if ((!tradeItem() && !tradeTreasure() && !tradeSpell()) ||
							(tradeItem() && rc.isItem()) || (tradeTreasure() && rc.isTreasure()) || (tradeSpell() && rc.isSpell())) {	
						if (itemPattern==null || itemPattern.matcher(go.getName()).find()) {
							if (markItem()) {
								go.setThisAttribute(QuestConstants.QUEST_MARK,getParentQuest().getGameObject().getStringId());
							}
							return true;
						}
					}
				}
				if (itemRegex==null) {
					logger.fine("No objects of correct type were traded.");
				} else {
					logger.fine("No objects matching regex /"+itemRegex+"/ or of correct type were traded.");
				}
			}
			else {
				logger.fine("No objects were traded.");
			}
		}
		else {
			logger.fine("Trader doesn't match /"+trader+"/.");
		}
		
		return false;
	}
	
	protected String buildDescription() {
		TradeType tt = getTradeType();
		StringBuilder sb = new StringBuilder();
		sb.append("Must ");
		sb.append(tt.toString().toLowerCase());
		sb.append(" the /");
		sb.append(getTradeItemRegEx());
		sb.append("/ ");
		sb.append(tt==TradeType.Buy?"from":"to");
		sb.append(" the ");
		sb.append("/");
		sb.append(getTradeWithRegEx());
		sb.append("/.");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Trade;
	}
	public TradeType getTradeType() {
		return TradeType.valueOf(getString(TRADE_TYPE));
	}
	public String getTradeWithRegEx() {
		return getString(TRADE_WITH_REGEX);
	}
	public String getTradeItemRegEx() {
		return getString(TRADE_ITEM_REGEX);
	}
	public boolean tradeItem() {
		return getBoolean(TRADE_ITEM);
	}
	public boolean tradeTreasure() {
		return getBoolean(TRADE_TREASURE);
	}
	public boolean tradeSpell() {
		return getBoolean(TRADE_SPELL);
	}
	public boolean markItem() {
		return getBoolean(ADD_MARK);
	}
}