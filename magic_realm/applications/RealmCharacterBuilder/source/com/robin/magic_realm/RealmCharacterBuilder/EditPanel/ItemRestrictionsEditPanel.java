package com.robin.magic_realm.RealmCharacterBuilder.EditPanel;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import com.robin.general.util.StringBufferedList;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class ItemRestrictionsEditPanel extends AdvantageEditPanel {
	/*
	 * Embodies item restrictions (items that cannot be activated)
	 */
	
	private static final String[][][] ITEM_LIST = {
		{
			{"Melee Weapons","Spear","Mace","Axe","Great Axe","Morning Star","Staff"},
			{"Ranged Weapons","Crossbow","Light Bow","Medium Bow"},
			{"Expansion","Halberd","Sword of Legend"},
		},
		{
			{"Swords","Short Sword","Thrusting Sword","Broadsword","Great Sword"},
			{"Magic Swords","Bane Sword","Truesteel Sword","Devil Sword","Living Sword"},
		},
		{
			{"Armor","Helmet","Breastplate","Shield","Armor"},
			{"Horses","Pony","Horse","Warhorse"},
			{"Treasure","Boots","Gloves","Books"}, // These will require special handling
		},
	};
	
	private Hashtable<String,JCheckBox> hash;
	
	public ItemRestrictionsEditPanel(CharacterWrapper pChar, String levelKey) {
		super(pChar, levelKey);
		
		hash = new Hashtable<>();
		
		setLayout(new BorderLayout());
		
		JPanel main = new JPanel(new GridLayout(1,ITEM_LIST.length));
		
		for (int i=0;i<ITEM_LIST.length;i++) {
			Box box = Box.createVerticalBox();
			for (int n=0;n<ITEM_LIST[i].length;n++) {
				addOptionList(box,ITEM_LIST[i][n]);
			}
			box.add(Box.createVerticalGlue());
			main.add(box);
		}
		add(main,"Center");
		
		JLabel description = new JLabel("Items that cannot be activated:",JLabel.LEFT);
		description.setFont(new Font("Dialog",Font.BOLD,16));
		add(description,"North");
		
		ArrayList<String> list = getAttributeList(Constants.ITEM_RESTRICTIONS);
		if (list!=null) {
			for (String val : list) {
				JCheckBox option = hash.get(val);
				if (option!=null) {
					option.setSelected(true);
				}
			}
		}
	}
	private void addOptionList(Box box,String[] list) {
		JPanel panel = new JPanel(new GridLayout(list.length-1,1));
		panel.setBorder(BorderFactory.createTitledBorder(list[0]));
		for (int i=1;i<list.length;i++) {
			JCheckBox option = new JCheckBox(list[i],false);
			panel.add(option);
			hash.put(list[i],option);
		}
		box.add(panel);
	}

	protected void applyAdvantage() {
		ArrayList<String> list = new ArrayList<>();
		for (Iterator i=hash.keySet().iterator();i.hasNext();) {
			String val = (String)i.next();
			JCheckBox option = hash.get(val);
			if (option.isSelected()) {
				list.add(val);
			}
		}
		setAttributeList(Constants.ITEM_RESTRICTIONS,list);
	}
	public String getSuggestedDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Restricted from having an active ");
		StringBufferedList list = new StringBufferedList(", ","or ");
		for (Iterator i=hash.keySet().iterator();i.hasNext();) {
			String val = (String)i.next();
			JCheckBox option = hash.get(val);
			if (option.isSelected()) {
				list.append(option.getText());
			}
		}
		sb.append(list.toString());
		sb.append(" in inventory.");
		return sb.toString();
	}

	public boolean isCurrent() {
		return hasAttribute(Constants.ITEM_RESTRICTIONS);
	}

	public String toString() {
		return "Item Restrictions";
	}
}