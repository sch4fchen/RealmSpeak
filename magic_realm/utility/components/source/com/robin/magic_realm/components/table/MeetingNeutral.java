package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MeetingNeutral extends Meeting {
	public MeetingNeutral(JFrame frame,TradeInfo tradeInfo,GameObject merchandise,Collection<RealmComponent> hireGroup) {
		super(frame,tradeInfo,merchandise,hireGroup);
	}
	public String getMeetingTableName() {
		return "Neutral";
	}
	public String applyOne(CharacterWrapper character) {
		MeetingFriendly table = new MeetingFriendly(getParentFrame(),tradeInfo,merchandise,hireGroup);
		return doOpportunity(character,table);
	}

	public String applyTwo(CharacterWrapper character) {
		return applyPrice(character,3);
	}

	public String applyThree(CharacterWrapper character) {
		return applyPrice(character,4);
	}

	public String applyFour(CharacterWrapper character) {
		return applyNoDeal(character);
	}

	public String applyFive(CharacterWrapper character) {
		return applyNoDeal(character);
	}

	public String applySix(CharacterWrapper character) {
		String text = "Trouble!";
		String result = useCompletedActiveTask(character,text);
		if (result!=null && !result.isEmpty()) {
			return result;
		}
		MeetingUnfriendly table = new MeetingUnfriendly(getParentFrame(),tradeInfo,merchandise,hireGroup);
		setNewTable(table);
		return text;
	}
}