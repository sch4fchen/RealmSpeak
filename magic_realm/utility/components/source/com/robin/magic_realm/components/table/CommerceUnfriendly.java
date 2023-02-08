package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class CommerceUnfriendly extends Commerce {
	public CommerceUnfriendly(JFrame frame,TradeInfo tradeInfo,Collection merchandise,HostPrefWrapper hostPrefs) {
		super(frame,tradeInfo,merchandise,hostPrefs);
	}
	public String getCommerceTableName() {
		return "Unfriendly";
	}
	public String applyOne(CharacterWrapper character) {
		return "Offer Gold -5 - " + offerGold(character,-5);
	}

	public String applyTwo(CharacterWrapper character) {
		return "Offer Gold -10 - " + offerGold(character,-10);
	}

	public String applyThree(CharacterWrapper character) {
		return "Offer Gold -10 - " + offerGold(character,-10);
	}

	public String applyFour(CharacterWrapper character) {
		return "Demand Gold -5 - " + demandGold(character,-5);
	}

	public String applyFive(CharacterWrapper character) {
		return "Demand Gold -10 - " + demandGold(character,-10);
	}

	public String applySix(CharacterWrapper character) {
		return "Demand Gold -20 - " + demandGold(character,-20);
	}
}