package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class CommerceNeutral extends Commerce {
	public CommerceNeutral(JFrame frame,TradeInfo tradeInfo,Collection<RealmComponent> merchandise,HostPrefWrapper hostPrefs) {
		super(frame,tradeInfo,merchandise,hostPrefs);
	}
	public String getCommerceTableName() {
		return "Neutral";
	}
	public String applyOne(CharacterWrapper character) {
		CommerceFriendly table = new CommerceFriendly(getParentFrame(),tradeInfo,merchandise,hostPrefs);
		return doOpportunity(character,table);
	}

	public String applyTwo(CharacterWrapper character) {
		return "Offer Gold - " + offerGold(character,0);
	}

	public String applyThree(CharacterWrapper character) {
		return "Offer Gold -5 - " + offerGold(character,-5);
	}

	public String applyFour(CharacterWrapper character) {
		return "Offer Gold -10 - " + offerGold(character,-10);
	}

	public String applyFive(CharacterWrapper character) {
		return "Offer Gold -15 - " + offerGold(character,-15);
	}

	public String applySix(CharacterWrapper character) {
		CommerceUnfriendly table = new CommerceUnfriendly(getParentFrame(),tradeInfo,merchandise,hostPrefs);
		setNewTable(table);
		return "Trouble";
	}
}