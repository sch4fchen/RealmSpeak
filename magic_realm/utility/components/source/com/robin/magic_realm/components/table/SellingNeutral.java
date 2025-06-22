package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class SellingNeutral extends Commerce {
	public SellingNeutral(JFrame frame,TradeInfo tradeInfo,Collection<RealmComponent> merchandise,HostPrefWrapper hostPrefs) {
		super(frame,tradeInfo,merchandise,hostPrefs);
	}
	public String getCommerceTableName() {
		return "Neutral";
	}
	public String applyOne(CharacterWrapper character) {
		SellingFriendly table = new SellingFriendly(getParentFrame(),tradeInfo,merchandise,hostPrefs);
		return doOpportunity(character,table);
	}

	public String applyTwo(CharacterWrapper character) {
		return "Price x 1 - " + sell(character,1);
	}

	public String applyThree(CharacterWrapper character) {
		return "Price x 1 - " + sell(character,1);
	}

	public String applyFour(CharacterWrapper character) {
		return "Price x 1/2 - " + sell(character,0.5);
	}

	public String applyFive(CharacterWrapper character) {
		return "Price x 1/2 - " + sell(character,0.5);
	}

	public String applySix(CharacterWrapper character) {
		SellingUnfriendly table = new SellingUnfriendly(getParentFrame(),tradeInfo,merchandise,hostPrefs);
		setNewTable(table);
		return "Trouble";
	}
}