package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.utility.TreasureUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class SellingAlly extends Commerce {
	private int conditionalBonus = 0;
	public SellingAlly(JFrame frame,TradeInfo tradeInfo,Collection<RealmComponent> merchandise,HostPrefWrapper hostPrefs) {
		super(frame,tradeInfo,merchandise,hostPrefs);
		this.conditionalBonus = 0;
		for (RealmComponent item : merchandise) {
			if (TreasureUtility.getFamePrice(item.getGameObject(),tradeInfo.getGameObject())>0) {
				this.conditionalBonus = 1;
				break;
			}
		}
	}
	public String getCommerceTableName() {
		return "Ally";
	}
	public String applyOne(CharacterWrapper character) {
		if (conditionalBonus>0) {
			return "Price x 3 - (Conditional Fame Bonus)" + sell(character,3);
		}
		return "Price x 3 - " + sell(character,3);
	}

	public String applyTwo(CharacterWrapper character) {
		if (conditionalBonus>0) {
			return "Price x 3 - (Conditional Fame Bonus)" + sell(character,3);
		}
		return "Price x 2 - " + sell(character,2);
	}

	public String applyThree(CharacterWrapper character) {
		if (conditionalBonus>0) {
			return "Price x 2 - (Conditional Fame Bonus)" + sell(character,2);
		}
		return "Price x 1.5 - " + sell(character,1.5);
	}

	public String applyFour(CharacterWrapper character) {
		if (conditionalBonus>0) {
			return "Price x 1.5 - (Conditional Fame Bonus)" + sell(character,1.5);
		}
		return "Price x 1 - " + sell(character,1);
	}

	public String applyFive(CharacterWrapper character) {
		if (conditionalBonus>0) {
			return "Price x 1 - (Conditional Fame Bonus)" + sell(character,1);
		}
		return "Price x 1 - " + sell(character,1);
	}

	public String applySix(CharacterWrapper character) {
		if (conditionalBonus>0) {
			return "Price x 1 - (Conditional Fame Bonus)" + sell(character,1);
		}
		return "Price x 1 - " + sell(character,1);
	}
}