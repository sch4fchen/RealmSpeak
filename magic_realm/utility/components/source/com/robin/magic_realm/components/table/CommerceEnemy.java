package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class CommerceEnemy extends Commerce {
	public CommerceEnemy(JFrame frame,TradeInfo tradeInfo,Collection merchandise,HostPrefWrapper hostPrefs) {
		super(frame,tradeInfo,merchandise,hostPrefs);
	}
	public String getCommerceTableName() {
		return "Enemy";
	}
	public String applyOne(CharacterWrapper character) {
		return "Demand Gold -10 - " + demandGold(character,-10);
	}

	public String applyTwo(CharacterWrapper character) {
		return "Demand Gold -15 - " + demandGold(character,-15);
	}

	public String applyThree(CharacterWrapper character) {
		return "Demand Gold -20 - " + demandGold(character,-20);
	}

	public String applyFour(CharacterWrapper character) {
		return "Demand Gold -30 - " + demandGold(character,-30);
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