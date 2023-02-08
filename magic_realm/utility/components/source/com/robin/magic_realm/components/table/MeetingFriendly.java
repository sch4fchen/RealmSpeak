package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MeetingFriendly extends Meeting {
	public MeetingFriendly(JFrame frame,TradeInfo tradeInfo,GameObject merchandise,Collection<RealmComponent> hireGroup) {
		super(frame,tradeInfo,merchandise,hireGroup);
	}
	public String getMeetingTableName() {
		return "Friendly";
	}
	public String applyOne(CharacterWrapper character) {
		MeetingAlly table = new MeetingAlly(getParentFrame(),tradeInfo,merchandise,hireGroup);
		return doOpportunity(character,table);
	}

	public String applyTwo(CharacterWrapper character) {
		processPrice(character,2);
		return "Price x 2";
	}

	public String applyThree(CharacterWrapper character) {
		processPrice(character,2);
		return "Price x 2";
	}

	public String applyFour(CharacterWrapper character) {
		processPrice(character,3);
		return "Price x 3";
	}

	public String applyFive(CharacterWrapper character) {
		processPrice(character,4);
		return "Price x 4";
	}

	public String applySix(CharacterWrapper character) {
		return "No Deal";
	}
}