package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MeetingEnemy extends Meeting {
	public MeetingEnemy(JFrame frame,TradeInfo tradeInfo,GameObject merchandise,Collection<RealmComponent> hireGroup) {
		super(frame,tradeInfo,merchandise,hireGroup);
	}
	public String getMeetingTableName() {
		return "Enemy";
	}
	public String applyOne(CharacterWrapper character) {
		return applyInsult(character);
	}

	public String applyTwo(CharacterWrapper character) {
		return applyChallenge(character);
	}

	public String applyThree(CharacterWrapper character) {
		return applyBlockBattle(character);
	}

	public String applyFour(CharacterWrapper character) {
		return applyBlockBattle(character);
	}

	public String applyFive(CharacterWrapper character) {
		return applyBlockBattle(character);
	}

	public String applySix(CharacterWrapper character) {
		return applyBlockBattle(character);
	}
}