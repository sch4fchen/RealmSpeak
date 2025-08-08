package com.robin.magic_realm.components.table;

import java.util.*;

import javax.swing.*;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.TradeType;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public abstract class Commerce extends Trade {
	
	protected HostPrefWrapper hostPrefs;
	protected Collection<RealmComponent> merchandise;
	private boolean blockBattle = false;
	
	public Commerce(JFrame frame,TradeInfo tradeInfo,Collection<RealmComponent> merchandise,HostPrefWrapper hostPrefs) {
		super(frame,tradeInfo);
		this.merchandise = merchandise;
		this.hostPrefs = hostPrefs;
	}
	/**
	 * Override method here to guarantee specificAction is applied
	 */
	public void setNewTable(RealmTable newTable) {
		if (specificAction.length()>0) {
			if (newTable instanceof Commerce) { // guarantees specific action is translated across
				((Commerce)newTable).setSpecificAction(specificAction.substring(1)); // trim comma
			}
		}
		super.setNewTable(newTable);
	}
	protected String doOpportunity(CharacterWrapper character,Commerce newTable) {
		if (useDeclineOpportunityRule()) {
			int ret = JOptionPane.showConfirmDialog(
					getParentFrame(),
					"You have rolled OPPORTUNITY.  Do wish to keep this result, or decline it and\n"
					+"take the result from next result down on the "+getCommerceTableName()+" table?\n\n"
					+"   Answer YES to take the opportunity, and get a new roll on the "+newTable.getCommerceTableName()+" table"
					+"   Answer NO to decline the opportunity, take the result from rolling a 2 instead",
					"OPPORTUNITY",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret==JOptionPane.NO_OPTION) {
				return "Opportunity(declined) - "+applyTwo(character);
			}
		}
		setNewTable(newTable);
		return "Opportunity";
	}
	public String getTableName(boolean longDescription) {
		if (getCommerceTableName().length()==0) {
			return "Sell to "+tradeInfo.getName();
		}
		return "Sell to "+tradeInfo.getName()+" (as "+getCommerceTableName()+")";
	}
	public String getTableKey() {
		return "Meeting"+specificAction; // Commerce is really a meeting table type
	}
	protected boolean useDeclineOpportunityRule() {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(tradeInfo.getGameData());
		return hostPrefs.hasPref(Constants.HOUSE2_DECLINE_OPPORTUNITY);
	}
	public abstract String getCommerceTableName();
	public boolean isBlockBattle() {
		return blockBattle;
	}
	protected void doBlockBattle(CharacterWrapper character) {
		character.setBlocked(true);
		if (character.isHidden()) { // Since Commerce is ALWAYS about TRADE, then you become unhidden here always
			character.setHidden(false);
		}
		blockBattle = true;
	}
	protected int getTotalBasePrice() {
		return getTotalBasePrice(true);
	}
	protected int getTotalBasePrice(boolean includeBonus) {
		int totalPrice = 0;
		for (RealmComponent merchandise : merchandise) {
			totalPrice += TreasureUtility.getBasePrice(tradeInfo.getTrader(),merchandise,includeBonus);
		}
		return totalPrice;
	}
	protected String offerGold(CharacterWrapper character,int bonus) {
		int total = getTotalBasePrice()+bonus;
		if (total>0) {
			String offer = (bonus>0?"+":"")+(bonus==0?"":String.valueOf(bonus));
			String offerFull = offer+" (total="+total+")";
			int ret = JOptionPane.showConfirmDialog(getParentFrame(),"They offer gold "+offerFull+".\n\nWill you sell?","Offer Gold "+offer,JOptionPane.YES_NO_OPTION);
			if (ret==JOptionPane.YES_OPTION) {
				return process(character,bonus);
			}
			return "Declined Offer";
		}
		return "No Deal";
	}
	protected String demandGold(CharacterWrapper character,int bonus) {
		int total = getTotalBasePrice()+bonus;
		if (total>0) {
			String offer = (bonus>0?"+":"")+(bonus==0?"":String.valueOf(bonus));
			String offerFull = offer+" (total="+total+")";
			int ret = JOptionPane.showConfirmDialog(getParentFrame(),"They demand gold "+offerFull+".\n\nWill you sell?","Demand Gold "+offer,JOptionPane.YES_NO_OPTION);
			if (ret==JOptionPane.YES_OPTION) {
				return process(character,bonus);
			}
		}
		doBlockBattle(character);
		return "Block/Battle";
	}
	protected String sell(CharacterWrapper character, double multi) {
		return selling(character,multi,false);
	}
	protected String sellOrBlock(CharacterWrapper character, double multi) {
		return selling(character,multi,true);
	}
	private String selling(CharacterWrapper character, double multi,boolean block) {
		double total = 0;
		if (multi==0) {
			total = 0;
		}
		else if (multi<1) {
			total = getTotalBasePrice(true) * multi;
		}
		else {
			total = (getTotalBasePrice(false) * multi);
			for (RealmComponent merchandise : merchandise) {
				total += TreasureUtility.getCommerceBonusPrice(tradeInfo.getTrader(),merchandise);
			}
		}
		
		int totalGold = (int) Math.ceil(total);
		
		String headline =  "Sellling for "+totalGold+" gold?";
		if (block) {
			headline = "Sell ("+totalGold+" gold) or Block"; 
		}
		int ret = JOptionPane.showConfirmDialog(getParentFrame(),"They offer gold "+totalGold+" gold.\n\nWill you sell?",headline,JOptionPane.YES_NO_OPTION);
		if (ret==JOptionPane.YES_OPTION) {
			return sellItems(character,totalGold);
		}
		if (block) {
			doBlockBattle(character);
			return "Block/Battle";
		}
		return "Declined Offer";
	}
	protected String sellItems(CharacterWrapper character, int price) {
		String result = "";
		StringBuffer sb = new StringBuffer();
		ArrayList<GameObject> itemList = new ArrayList<GameObject>();
		for (RealmComponent merchandise : merchandise) {
			itemList.add(merchandise.getGameObject());
			sb.append("You sold the "+merchandise.getGameObject().getName()+" for "+price+" gold.\n");
			character.addGold(price);
			TradeUtility.loseItem(character,merchandise.getGameObject(),tradeInfo.getGameObject(),hostPrefs.hasPref(Constants.OPT_GRUDGES));
		}
		
		// Finally, make sure the inventory status is still legal 
		character.checkInventoryStatus(getParentFrame(),null,getListener());
		
		if (merchandise.size()==1) {
			RealmComponent item = merchandise.iterator().next();
			result = "Total received for the "+item.getGameObject().getName()+" was "+price+" gold.";
		}
		else {
			result = "Sold "+merchandise.size()+" items for "+price+" gold: ";
		}
		checkQuestRequirementsAfterSelling(itemList, character);
		showConfirmationDialog(character,sb);
		handleFoolsGold(character);
		return result;
	}
	protected String process(CharacterWrapper character,int bonus) {
		String result = "";
		// Can be multiple items sold
		int totalGoldReceieved = bonus;
		StringBuffer sb = new StringBuffer();
		ArrayList<GameObject> itemList = new ArrayList<GameObject>();
		for (RealmComponent merchandise : merchandise) {
			itemList.add(merchandise.getGameObject());
			int basePrice = TreasureUtility.getBasePrice(tradeInfo.getTrader(),merchandise); // Without commerce rules, the basePrice is the selling price (already figured in by the getBasePrice method)
			sb.append("You sold the "+merchandise.getGameObject().getName()+" for "+basePrice+" gold.\n");
			character.addGold(basePrice);
			totalGoldReceieved += basePrice;
			TradeUtility.loseItem(character,merchandise.getGameObject(),tradeInfo.getGameObject(),hostPrefs.hasPref(Constants.OPT_GRUDGES));
		}
		
		// Finally, make sure the inventory status is still legal 
		character.checkInventoryStatus(getParentFrame(),null,getListener());
		
		if (bonus<0) {
			sb.append("Minus "+bonus+" gold.");
		}
		if (bonus>0) {
			sb.append("Plus "+bonus+" gold.");
		}
		if (bonus!=0) {
			character.addGold(bonus,true); // ignore curses here, because its a NET amount which involves some subtraction
		}
		if (merchandise.size()==1) {
			RealmComponent item = merchandise.iterator().next();
			result = "Total received for the "+item.getGameObject().getName()+" was "+totalGoldReceieved+" gold.";
		}
		else {
			result = "Sold "+merchandise.size()+" items for "+totalGoldReceieved+" gold: ";
		}
		checkQuestRequirementsAfterSelling(itemList, character);
		showConfirmationDialog(character,sb);
		handleFoolsGold(character);
		return result;
	}
	private void checkQuestRequirementsAfterSelling(ArrayList<GameObject> itemList, CharacterWrapper character) {
		QuestRequirementParams params = new QuestRequirementParams();
		params.actionType = CharacterActionType.Trading;
		params.actionName = TradeType.Sell.toString();
		params.objectList = itemList;
		params.targetOfSearch = tradeInfo.getGameObject();
		character.testQuestRequirements(getParentFrame(),params);
	}
	private void showConfirmationDialog(CharacterWrapper character, StringBuffer sb) {
		JTextArea area = new JTextArea();
		area.setFont(UIManager.getFont("Label.font"));
		area.setText(sb.toString());
		area.setEditable(false);
		area.setOpaque(false);
		JOptionPane.showMessageDialog(getParentFrame(),
				area,
				"Selling goods",
				JOptionPane.INFORMATION_MESSAGE);
	}
	private void handleFoolsGold(CharacterWrapper character) {
		boolean runAway = false;
		for (RealmComponent merchandise : merchandise) {
			if (merchandise.getGameObject().hasThisAttribute(Constants.FOOLS_GOLD)) {
				runAway = true;
				break;
			}
		}
		if (runAway) {
			RunningFromNative newTable = new RunningFromNative(character,tradeInfo.getTrader());
			setNewTable(newTable);
		}
	}
	public static Commerce createCommerceTable(JFrame frame,CharacterWrapper character,TileLocation currentLocation,RealmComponent trader,Collection<RealmComponent> merchandise,int ignoreBuyDrinksLimit,HostPrefWrapper hostPrefs) {
		if (!hostPrefs.hasPref(Constants.OPT_COMMERCE) && !hostPrefs.hasPref(Constants.SR_SELLING)) {
			return new CommerceNone(frame,new TradeInfo(trader),merchandise,hostPrefs);
		}
		
		TradeInfo tradeInfo = getTradeInfo(frame,character,trader,currentLocation,ignoreBuyDrinksLimit);
		
		Commerce commerce = null;
		
		if (hostPrefs.hasPref(Constants.SR_SELLING)) {		
			String visitor = trader.getGameObject().getThisAttribute(Constants.VISITOR);
			if (visitor!=null) {
				String priceKey = visitor.toLowerCase()+"_price";
				for (RealmComponent item : merchandise) {
					if (item.getGameObject().hasThisAttribute(priceKey)) {
						return new SellingVisitor(frame,tradeInfo,merchandise,hostPrefs,tradeInfo.getRelationshipType());
					}
				}
			}
			
			int conditionalBonus = 0;
			for (RealmComponent item : merchandise) {
				if (TreasureUtility.getFamePrice(item.getGameObject(),trader.getGameObject())>0) {
					conditionalBonus = 1;
					break;
				}
			}
			
			int relationship = tradeInfo.getRelationshipType()+conditionalBonus;
			if (relationship>RelationshipType.ALLY) {
				relationship = RelationshipType.ALLY;
			}
			
			switch(relationship) {
				case RelationshipType.ENEMY:
					commerce = new SellingEnemy(frame,tradeInfo,merchandise,hostPrefs);
					break;
				case RelationshipType.UNFRIENDLY:
					commerce = new SellingUnfriendly(frame,tradeInfo,merchandise,hostPrefs);
					break;
				case RelationshipType.NEUTRAL:
					commerce = new SellingNeutral(frame,tradeInfo,merchandise,hostPrefs);
					break;
				case RelationshipType.FRIENDLY:
					commerce = new SellingFriendly(frame,tradeInfo,merchandise,hostPrefs);
					break;
				case RelationshipType.ALLY:
					commerce = new SellingAlly(frame,tradeInfo,merchandise,hostPrefs);
					break;
			}
		}
		else {
			switch(tradeInfo.getRelationshipType()) {
				case RelationshipType.ENEMY:
					commerce = new CommerceEnemy(frame,tradeInfo,merchandise,hostPrefs);
					break;
				case RelationshipType.UNFRIENDLY:
					commerce = new CommerceUnfriendly(frame,tradeInfo,merchandise,hostPrefs);
					break;
				case RelationshipType.NEUTRAL:
					commerce = new CommerceNeutral(frame,tradeInfo,merchandise,hostPrefs);
					break;
				case RelationshipType.FRIENDLY:
					commerce = new CommerceFriendly(frame,tradeInfo,merchandise,hostPrefs);
					break;
				case RelationshipType.ALLY:
					commerce = new CommerceAlly(frame,tradeInfo,merchandise,hostPrefs);
					break;
			}
		}
		return commerce;
	}
}