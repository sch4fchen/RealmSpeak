package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class CommerceFriendly extends Commerce {
	public CommerceFriendly(JFrame frame,TradeInfo tradeInfo,Collection<RealmComponent> merchandise,HostPrefWrapper hostPrefs) {
		super(frame,tradeInfo,merchandise,hostPrefs);
	}
	public String getCommerceTableName() {
		return "Friendly";
	}
	public String applyOne(CharacterWrapper character) {
		CommerceAlly table = new CommerceAlly(getParentFrame(),tradeInfo,merchandise,hostPrefs);
		return doOpportunity(character,table);
	}

	public String applyTwo(CharacterWrapper character) {
		return "Offer Gold +5 - " + offerGold(character,5);
	}

	public String applyThree(CharacterWrapper character) {
		return "Offer Gold - " + offerGold(character,0);
	}

	public String applyFour(CharacterWrapper character) {
		return "Offer Gold - " + offerGold(character,0);
	}

	public String applyFive(CharacterWrapper character) {
		return "Offer Gold -5 - " + offerGold(character,-5);
	}

	public String applySix(CharacterWrapper character) {
		return "Offer Gold -10 - " + offerGold(character,-10);
	}
}