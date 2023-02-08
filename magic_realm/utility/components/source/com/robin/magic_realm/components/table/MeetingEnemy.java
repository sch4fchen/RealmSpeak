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
		doInsult(character);
		return "Insult";
	}

	public String applyTwo(CharacterWrapper character) {
		doChallenge(character);
		return "Challenge";
	}

	public String applyThree(CharacterWrapper character) {
		doBlockBattle(character);
		return BLOCK_BATTLE;
	}

	public String applyFour(CharacterWrapper character) {
		doBlockBattle(character);
		return BLOCK_BATTLE;
	}

	public String applyFive(CharacterWrapper character) {
		doBlockBattle(character);
		return BLOCK_BATTLE;
	}

	public String applySix(CharacterWrapper character) {
		doBlockBattle(character);
		return BLOCK_BATTLE;
	}
}