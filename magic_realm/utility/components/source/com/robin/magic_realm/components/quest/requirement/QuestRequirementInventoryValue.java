package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.TreasureType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementInventoryValue extends QuestRequirementLoot {
	private static Logger logger = Logger.getLogger(QuestRequirementInventory.class.getName());
	
	public static final String GOLD_VALUE = "_gold_value";
	public static final String FAME_VALUE = "_fame_value";
	
	public QuestRequirementInventoryValue(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {		
		ArrayList<GameObject> items = filterObjectsForRequirement(character,character.getInventory(),logger);
		int goldValue = 0;
		int fameValue = 0;
		for (GameObject item : items) {
			goldValue = goldValue + item.getThisInt("base_price");
			fameValue = fameValue + item.getThisInt("fame");
		}
		
		return goldValue>=getGoldValue() && fameValue>=getFameValue();
	}
	
	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must own");
		TreasureType tt = getTreasureType();
		if (tt!=TreasureType.Any) {
			sb.append(" ");
			sb.append(getTreasureType().toString().toLowerCase());
		}
		if (tt==TreasureType.Small || tt==TreasureType.Large) {
			sb.append(" treasure");
		}
		else {
			sb.append(" item");
		}
		sb.append("s worth of");
		if (getGoldValue()>0) {
			sb.append(" "+getGoldValue()+" gold");
		}
		if (getGoldValue()>0 && getFameValue()>0) {
			sb.append("and");
		}
		if (getFameValue()>0) {
			sb.append(" "+getFameValue()+" fame");
		}
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.InventoryValue;
	}
	
	public int getGoldValue() {
		return getInt(GOLD_VALUE);
	}
	
	public int getFameValue() {
		return getInt(FAME_VALUE);
	}
}