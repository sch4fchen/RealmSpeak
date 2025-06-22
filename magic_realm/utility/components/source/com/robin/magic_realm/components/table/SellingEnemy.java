package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class SellingEnemy extends Commerce {
	public SellingEnemy(JFrame frame,TradeInfo tradeInfo,Collection<RealmComponent> merchandise,HostPrefWrapper hostPrefs) {
		super(frame,tradeInfo,merchandise,hostPrefs);
	}
	public String getCommerceTableName() {
		return "Enemy";
	}
	public String applyOne(CharacterWrapper character) {
		return "Price x 1/10 or Block - " + sellOrBlock(character,0.1);
	}

	public String applyTwo(CharacterWrapper character) {
		return "Price x 0 or Block - " + sellOrBlock(character,0);
	}

	public String applyThree(CharacterWrapper character) {
		doBlockBattle(character);
		return "Block/Battle";
	}

	public String applyFour(CharacterWrapper character) {
		doBlockBattle(character);
		return "Block/Battle";
	}

	public String applyFive(CharacterWrapper character) {
		doBlockBattle(character);
		return "Block/Battle";
	}

	public String applySix(CharacterWrapper character) {
		doBlockBattle(character);
		return "Block/Battle";
	}
}