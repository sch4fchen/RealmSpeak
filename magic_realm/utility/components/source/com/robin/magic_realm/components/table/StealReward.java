package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class StealReward extends RealmTable {
	
	public static final String KEY = "StealReward";
	private static final String[] RESULT = {
		"Choice",
		"Mount",
		"Armor (players choice)",
		"Treasure (randomly selected)",
		"10 gold",
		"Nothing",
	};
	private RealmComponent victim;
	private int modifier = 0;
	
	public StealReward(JFrame frame,RealmComponent victim) {
		super(frame,null);
		this.victim = victim;
	}
	public StealReward(JFrame frame,RealmComponent victim,int modifier) {
		super(frame,null);
		this.victim = victim;
		this.modifier = modifier;
	}
	public String getTableName(boolean longDescription) {
		return "Steal Reward";
	}
	public String getTableKey() {
		return KEY;
	}
	public String apply(CharacterWrapper character,DieRoller roller) {
		roller.addModifier(modifier);
		return super.apply(character,roller);
	}
	public String applyOne(CharacterWrapper character) {
		StealTablesCommon.stealChoice(getParentFrame(),character,victim,"Steal Reward");
		return RESULT[0];
	}

	public String applyTwo(CharacterWrapper character) {
		StealTablesCommon.stealChoiceHorse(getParentFrame(),character,victim,"Steal Reward");
		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {
		StealTablesCommon.stealChoiceArmor(getParentFrame(),character,victim,"Steal Reward");
		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		GameObject holder = SetupCardUtility.getDenizenHolder(victim.getGameObject());
		ArrayList<RealmComponent> treasures = new ArrayList<>();
		for(GameObject item:holder.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(item);
			if ( rc.isTreasure()) {
				treasures.add(rc);
			}
		}
		if (treasures.size()==0) {
			JOptionPane.showMessageDialog(getParentFrame(),"No treasure to steal from "+victim.getGameObject().getNameWithNumber(),"Steal Reward",JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			Loot.addItemToCharacter(getParentFrame(), null, character, treasures.get(RandomNumber.getRandom(treasures.size())).getGameObject());
		}
		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {
		JOptionPane.showMessageDialog(getParentFrame(),"You have stolen 10 gold.","Steal Reward",JOptionPane.INFORMATION_MESSAGE);
		character.addGold(10);
		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		JOptionPane.showMessageDialog(getParentFrame(),"Nothing found.","Steal Reward",JOptionPane.INFORMATION_MESSAGE);
		return RESULT[5];
	}
}