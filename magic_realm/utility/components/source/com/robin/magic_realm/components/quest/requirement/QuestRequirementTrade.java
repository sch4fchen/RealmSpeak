package com.robin.magic_realm.components.quest.requirement;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.TradeType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementTrade extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementTrade.class.getName());
	
	public static String TRADE_TYPE = "_tt";
	public static String TRADE_WITH_REGEX = "_trx";
	public static String TRADE_ITEM_REGEX = "_irx";

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
					if (itemPattern==null || itemPattern.matcher(go.getName()).find()) {
						return true;
					}
				}
				logger.fine("No objects matching regex /"+itemRegex+"/ were traded.");
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
}