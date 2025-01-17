package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MeetingAlly extends Meeting {
	public MeetingAlly(JFrame frame,TradeInfo tradeInfo,GameObject merchandise,Collection<RealmComponent> hireGroup) {
		super(frame,tradeInfo,merchandise,hireGroup);
	}
	public String getMeetingTableName() {
		return "Ally";
	}
	public String applyOne(CharacterWrapper character) {
		String text = "Boon (x 1)";
		String result = useCompletedActiveTask(character,text);
		if (result!=null && !result.isEmpty()) {
			return result;
		}
		processPrice(character,0);
		return text;
	}

	public String applyTwo(CharacterWrapper character) {
		return applyPrice(character,1);
	}

	public String applyThree(CharacterWrapper character) {
		return applyPrice(character,2);
	}

	public String applyFour(CharacterWrapper character) {
		return applyPrice(character,3);
	}

	public String applyFive(CharacterWrapper character) {
		return applyPrice(character,4);
	}

	public String applySix(CharacterWrapper character) {
		return applyPrice(character,4);
	}
}