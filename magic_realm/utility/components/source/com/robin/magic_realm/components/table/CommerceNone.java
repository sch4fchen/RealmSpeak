package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class CommerceNone extends Commerce {
	public CommerceNone(JFrame frame,TradeInfo tradeInfo,Collection<RealmComponent> merchandise,HostPrefWrapper hostPrefs) {
		super(frame,tradeInfo,merchandise,hostPrefs);
	}
	public boolean hideRoller() {
		return true;
	}
	public String getCommerceTableName() {
		return "";
	}
	public String applyOne(CharacterWrapper character) {
		return process(character,0);
	}

	public String applyTwo(CharacterWrapper character) {
		return process(character,0);
	}

	public String applyThree(CharacterWrapper character) {
		return process(character,0);
	}

	public String applyFour(CharacterWrapper character) {
		return process(character,0);
	}

	public String applyFive(CharacterWrapper character) {
		return process(character,0);
	}

	public String applySix(CharacterWrapper character) {
		return process(character,0);
	}
}