package com.robin.magic_realm.components.table;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public abstract class Trade extends RealmTable {
	
	protected TradeInfo tradeInfo;
	protected String specificAction = "";
	
	protected Trade(JFrame frame,TradeInfo tradeInfo) {
		super(frame, null);
		this.tradeInfo = tradeInfo;
	}
	public TradeInfo getTradeInfo() {
		return tradeInfo;
	}
	public void setSpecificAction(String val) {
		specificAction = ","+val;
	}
	protected String getDenizenName() {
		String name = tradeInfo.getThisAttribute("native");
		if (name==null) {
			name = tradeInfo.getName(); // visitors
		}
		return name;
	}
	protected static TradeInfo getTradeInfo(JFrame frame,CharacterWrapper character,RealmComponent tradeInfo,TileLocation currentLocation,int ignoreBuyDrinksLimit) {
		return getTradeInfo(frame,character,tradeInfo,currentLocation,ignoreBuyDrinksLimit,0);
	}
	protected static TradeInfo getTradeInfo(JFrame frame,CharacterWrapper character,RealmComponent tradeInfo,TileLocation currentLocation,int ignoreBuyDrinksLimit,int hireGroupSize) {
		TradeInfo info = new TradeInfo(tradeInfo);
		info.setHireGroupSize(hireGroupSize);
		
		if (tradeInfo.isNative() || tradeInfo.isMonster()) {
			if (tradeInfo.isNative()) {
				info.setGroupName(tradeInfo.getGameObject().getThisAttribute("native"));
			}
			else {
				// Monsters
				info.setGroupName(tradeInfo.getGameObject().getName());
			}
			for (RealmComponent rc:currentLocation.clearing.getClearingComponents()) {
				if (tradeInfo.isNative() && rc.isNative() && info.getGroupName().equals(rc.getGameObject().getThisAttribute("native"))) {
					info.bumpGroupCount();
				}
				else if (tradeInfo.isMonster() && rc.isMonster() && info.getGroupName().equals(rc.getGameObject().getName())) {
					info.bumpGroupCount();
				}
			}
		}
		else if (tradeInfo.isTraveler() || tradeInfo.isGuild()) {
			info.setGroupName(tradeInfo.getGameObject().getName());
			info.bumpGroupCount();
		}
		else {
			info.setGroupName(tradeInfo.getGameObject().getThisAttribute(Constants.VISITOR));
			info.bumpGroupCount();
		}
		
		info.setRelationship(RealmUtility.getRelationshipBetween(character,tradeInfo));
		info.setNoDrinksReason(handleBuyDrinks(frame,info,currentLocation,character,ignoreBuyDrinksLimit));

		if (info.getNoDrinksReason()!=null) {
			RealmLogging.logMessage(character.getGameObject().getName(),info.getNoDrinksReason());
		}

		return info;
	}
	private static String handleBuyDrinks(JFrame frame,TradeInfo info,TileLocation currentLocation,CharacterWrapper character,int ignoreBuyDrinksLimit) {
		if (info.getTrader().isVisitor() || info.getTrader().isTraveler() || info.getTrader().isGuild()) return "Buying drinks only affects native groups.";
		if (info.getTrader().getGameObject().hasThisAttribute(Constants.UNDEAD)) return "You cannot buy drinks for the "+info.getGroupName()+"s.";
		if (info.getRelationship()>=ignoreBuyDrinksLimit) return "The native group will be no more favorable with drinks.";
			
		TileLocation here = character.getCurrentLocation();
		// Make sure character is actually here (might just be a hireling)
		if (!here.equals(currentLocation)) return "The "+character.getGameObject().getName()+" is not in the clearing to buy drinks.";
		if (character.hasCurse(Constants.ASHES)) return "The "+character.getGameObject().getName()+" cannot buy drinks with the ASHES curse.";
		if (character.affectedByKey(Constants.DAZZLE)) return "The "+character.getGameObject().getName()+" cannot buy drinks when affected by the DAZZLE spell.";
		
		int drinkPrice = info.getGroupCount();
		if (character.hasActiveInventoryThisKey(Constants.LOW_DRINK_COST)) {
			drinkPrice = 1;
		}
		if (character.getGold()<drinkPrice) return "The "+character.getGameObject().getName()+" cannot afford to buy drinks for the "+info.getDisplayName()+" ("+drinkPrice+" gold).";
		int ret = JOptionPane.showConfirmDialog(frame,
				"Will the "+character.getGameObject().getName()
				+" buy drinks for the "+StringUtilities.capitalize(info.getDisplayName())
				+" ("+info.getRelationshipName()+") for "+drinkPrice+" gold?",
				"Buy Drinks?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
		if (ret!=JOptionPane.YES_OPTION) return "The "+character.getGameObject().getName()+" chose not to buy drinks.";
		
		character.addGold(-drinkPrice);
		character.getGameObject().setThisAttribute(Constants.DRINKS_BOUGHT);
		if (character.affectedByKey(Constants.BARDS_LUTE)) {
			info.setRelationship(RelationshipType.FRIENDLY);
		}
		else {
			info.addRelationship(1);
		}
		return null;
	}
}