package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MeetingUnfriendly extends Meeting {
	public MeetingUnfriendly(JFrame frame,TradeInfo tradeInfo,GameObject merchandise,Collection<RealmComponent> hireGroup) {
		super(frame,tradeInfo,merchandise,hireGroup);
	}
	public String getMeetingTableName() {
		return "Unfriendly";
	}
	public String applyOne(CharacterWrapper character) {
		processPrice(character,4);
		return "Price x 4";
	}

	public String applyTwo(CharacterWrapper character) {
		return "No Deal";
	}

	public String applyThree(CharacterWrapper character) {
		return "No Deal";
	}

	public String applyFour(CharacterWrapper character) {
		doInsult(character);
		return "Insult";
	}

	public String applyFive(CharacterWrapper character) {
		doChallenge(character);
		return "Challenge";
	}

	public String applySix(CharacterWrapper character) {
		doBlockBattle(character);
		return BLOCK_BATTLE;
	}
}