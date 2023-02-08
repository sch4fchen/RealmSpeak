package com.robin.magic_realm.RealmQuestBuilder;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.*;

import com.robin.magic_realm.components.quest.ChitItemType;

public class ChitTypePanel extends JPanel {
	
	JCheckBox treasureType;
	JCheckBox weaponType;
	JCheckBox armorType;
	JCheckBox sizeType;
	JCheckBox horseType;
	
	public ChitTypePanel(ArrayList<String> types) {
		initComponents();
		if (types==null) return;
		for(String type:types) {
			ChitItemType cit = ChitItemType.valueOf(type);
			switch(cit) {
				case None:
					treasureType.setSelected(false);
					weaponType.setSelected(false);
					armorType.setSelected(false);
					sizeType.setSelected(false);
					horseType.setSelected(false);
					break;
				case Treasure:
					treasureType.setSelected(true);
					break;
				case Weapon:
					weaponType.setSelected(true);
					break;
				case Armor:
					armorType.setSelected(true);
					break;
				case Great:
					sizeType.setSelected(true);
					break;
				case Horse:
					horseType.setSelected(true);
					break;
			}
		}
	}
	private void initComponents() {
		setLayout(new GridLayout(2,2));
		treasureType = new JCheckBox("Treasure");
		add(treasureType);
		horseType = new JCheckBox("Horse");
		add(horseType);
		weaponType = new JCheckBox("Weapon");
		add(weaponType);
		armorType = new JCheckBox("Armor");
		add(armorType);
		sizeType = new JCheckBox("Great");
		add(sizeType);
		setBorder(BorderFactory.createEtchedBorder());
	}
	public ArrayList<ChitItemType> getChitItemTypes() {
		boolean allUnchecked = !treasureType.isSelected() && !weaponType.isSelected() && !armorType.isSelected( )&& !sizeType.isSelected() && !horseType.isSelected();
		ArrayList<ChitItemType> types = new ArrayList<ChitItemType>();
		if (allUnchecked) {
			types.add(ChitItemType.None);
		}
		else {
			if (treasureType.isSelected()) types.add(ChitItemType.Treasure);
			if (weaponType.isSelected()) types.add(ChitItemType.Weapon);
			if (armorType.isSelected()) types.add(ChitItemType.Armor);
			if (sizeType.isSelected()) types.add(ChitItemType.Great);
			if (horseType.isSelected()) types.add(ChitItemType.Horse);
		}
		return types;
	}
	public ArrayList<String> getChitTypeList() {
		return ChitItemType.listToStrings(getChitItemTypes());
	}
}