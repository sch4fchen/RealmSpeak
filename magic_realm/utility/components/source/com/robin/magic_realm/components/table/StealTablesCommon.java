package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TreasureCardComponent;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class StealTablesCommon {
	public static void stealChoice(JFrame frame, CharacterWrapper character, RealmComponent victim, String tableName) {
		stealChoice(frame,character,victim,tableName,true,false,false,false);
	}
	public static void stealChoiceHorse(JFrame frame, CharacterWrapper character, RealmComponent victim, String tableName) {
		stealChoice(frame,character,victim,tableName,false,false,true,false);
	}
	public static void stealChoiceArmor(JFrame frame, CharacterWrapper character, RealmComponent victim, String tableName) {
		stealChoice(frame,character,victim,tableName,false,false,false,true);
	}
	private static void stealChoice(JFrame frame, CharacterWrapper character, RealmComponent victim, String tableName, boolean allItems, boolean treasures, boolean horse, boolean armor) {
		GameObject holder = SetupCardUtility.getDenizenHolder(victim.getGameObject());
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Select item to steal:",false);
		ArrayList<GameObject> holdToNote = new ArrayList<>();
		for(GameObject item:holder.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(item);
			if ((allItems || (treasures && rc.isTreasure()) || (horse && rc.isHorse()) || (armor && rc.isArmor())) && (rc.isItem() || rc.isTreasure())) {
				if (rc.isTreasure()) {
					TreasureCardComponent treasure = (TreasureCardComponent)rc;
					treasure.setFaceUp();
					holdToNote.add(item);
				}
				chooser.addRealmComponent(RealmComponent.getRealmComponent(item),item.getName());
			}
		}
		if (chooser.getComponentCount()==0) {
			String itemType = "";
			if (allItems) {
				itemType = "Nothing";
			} else if (treasures) {
				itemType = "No treasures";
			} else if (horse) {
				itemType = "No horse";
			} else if (armor) {
				itemType = "No armor";
			}
			JOptionPane.showMessageDialog(frame,itemType+" to steal from "+victim.getGameObject().getNameWithNumber(),tableName,JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			character.addNoteSteal(victim.getGameObject(),holdToNote);
			chooser.setVisible(true);
			RealmComponent selectedItem = chooser.getFirstSelectedComponent();
			character.getGameObject().add(selectedItem.getGameObject());
		}
	}
}