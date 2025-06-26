package com.robin.magic_realm.components.table;

import java.util.Collection;

import javax.swing.JFrame;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.attribute.TradeInfo;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class SellingVisitor extends Commerce {
	private int relationship;
	public SellingVisitor(JFrame frame,TradeInfo tradeInfo,Collection<RealmComponent> merchandise,HostPrefWrapper hostPrefs,int relationship) {
		super(frame,tradeInfo,merchandise,hostPrefs);
		this.relationship = relationship;
	}
	public String getCommerceTableName() {
		return "Friendly (Visitor)";
	}
	public String applyOne(CharacterWrapper character) {
		if (relationship == RelationshipType.FRIENDLY || relationship == RelationshipType.ALLY) {
			return "Price x 3 (Friendly or Ally) - " + sell(character,3);
		}
		if (relationship == RelationshipType.UNFRIENDLY || relationship == RelationshipType.ENEMY) {
			return "Price x 1.5 (Unfriendly or Enemy) - " + sell(character,1.5);
		}
		return "Price x 2 - " + sell(character,2);
	}

	public String applyTwo(CharacterWrapper character) {
		if (relationship == RelationshipType.FRIENDLY || relationship == RelationshipType.ALLY) {
			return "Price x 3 (Friendly or Ally) - " + sell(character,3);
		}
		if (relationship == RelationshipType.UNFRIENDLY || relationship == RelationshipType.ENEMY) {
			return "Price x 1.5 (Unfriendly or Enemy) - " + sell(character,1.5);
		}
		return "Price x 1.5 - " + sell(character,1.5);
	}

	public String applyThree(CharacterWrapper character) {
		if (relationship == RelationshipType.FRIENDLY || relationship == RelationshipType.ALLY) {
			return "Price x 1.5 (Friendly or Ally) - " + sell(character,1.5);
		}
		if (relationship == RelationshipType.UNFRIENDLY || relationship == RelationshipType.ENEMY) {
			return "Price x 1 (Unfriendly or Enemy) - " + sell(character,1);
		}
		return "Price x 1.5 - " + sell(character,1.5);
	}

	public String applyFour(CharacterWrapper character) {
		if (relationship == RelationshipType.FRIENDLY || relationship == RelationshipType.ALLY) {
			return "Price x 1.5 (Friendly or Ally) - " + sell(character,1.5);
		}
		if (relationship == RelationshipType.UNFRIENDLY || relationship == RelationshipType.ENEMY) {
			return "Price x 1 (Unfriendly or Enemy) - " + sell(character,1);
		}
		return "Price x 1 - " + sell(character,1);
	}

	public String applyFive(CharacterWrapper character) {
		if (relationship == RelationshipType.FRIENDLY || relationship == RelationshipType.ALLY) {
			return "Price x 1 (Friendly or Ally) - " + sell(character,1);
		}
		if (relationship == RelationshipType.UNFRIENDLY || relationship == RelationshipType.ENEMY) {
			return "Price x 1/2 (Unfriendly or Enemy) - " + sell(character,0.5);
		}
		return "Price x 1 - " + sell(character,1);
	}

	public String applySix(CharacterWrapper character) {
		if (relationship == RelationshipType.FRIENDLY || relationship == RelationshipType.ALLY) {
			return "Price x 1 (Friendly or Ally) - " + sell(character,1);
		}
		if (relationship == RelationshipType.UNFRIENDLY || relationship == RelationshipType.ENEMY) {
			return "Price x 1/2 (Unfriendly or Enemy) - " + sell(character,0.5);
		}
		return "Price x 1/2 - " + sell(character,0.5);
	}
}