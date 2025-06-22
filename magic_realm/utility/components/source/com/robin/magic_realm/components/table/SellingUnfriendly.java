package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class SellingUnfriendly extends Commerce {
	public SellingUnfriendly(JFrame frame,TradeInfo tradeInfo,Collection<RealmComponent> merchandise,HostPrefWrapper hostPrefs) {
		super(frame,tradeInfo,merchandise,hostPrefs);
	}
	public String getCommerceTableName() {
		return "Unfriendly";
	}
	public String applyOne(CharacterWrapper character) {
		return "Price x 1 - " + sell(character,1);
	}

	public String applyTwo(CharacterWrapper character) {
		return "Price x 1/2 - " + sell(character,0.5);
	}

	public String applyThree(CharacterWrapper character) {
		return "Price x 1/2 - " + sell(character,0.5);
	}

	public String applyFour(CharacterWrapper character) {
		return "Price x 1/10 or Block - " + sellOrBlock(character,0.1);
	}

	public String applyFive(CharacterWrapper character) {
		return "Price x 0 or Block - " + sellOrBlock(character,0);
	}

	public String applySix(CharacterWrapper character) {
		doBlockBattle(character);
		return "Block/Battle";
	}
}