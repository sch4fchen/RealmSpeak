package com.robin.magic_realm.RealmCharacterBuilder.EditPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.*;

import javax.swing.*;

import com.robin.general.util.StringBufferedList;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class TreasureLocationEditPanel extends AdvantageEditPanel {
	
	private static final String[] TREASURES =
		{"Hoard","Lair","Altar","Shrine","Pool","Vault","Cairns","Statue","Chest",
				"Remains of Thief:thief","Mouldy Skeleton:skeleton","Crypt of the Knight:crypt","Enchanted Meadow:meadow","Toadstool Circle:toadstool_circle",
				"Fountain of Health:fountain","Adventurer's Cache:adventurer_cache","Archeological Dig:dig","Stones","Maze","Tree","Web","Hive","Tomb","Pond",
				"Pit","Den","Tower","Spire","Garden","Temple",
				"Circle of Stones:circle_of_stones","Conjuror's Remains:conjuror", "Ethereal Abbey:abbey"};

	private Hashtable<String,JCheckBox> hash;
	
	public TreasureLocationEditPanel(CharacterWrapper pChar, String levelKey) {
		super(pChar, levelKey);
		setBorder(BorderFactory.createTitledBorder(toString())); // update name
		
		hash = new Hashtable<>();
		setLayout(new BorderLayout());
		JPanel main = new JPanel(new GridLayout(TREASURES.length,1));
		
		for (int i=0;i<TREASURES.length;i++) {
			String name = TREASURES[i];
			JCheckBox option = new JCheckBox(name.split(":")[0]);
			main.add(option);
			String key = name.split(":")[name.split(":").length-1].toLowerCase();
			hash.put(key,option);
		}
						
		add(main,"Center");
		updateSelection();
	}	
	
	private void updateSelection() {
		ArrayList<String> list = getAttributeList(Constants.TREASURE_LOCATION_FEAR);
		if (list != null) {
			for (String key : list) {
				JCheckBox option = hash.get(key);
				if (option!=null) {
					option.setSelected(true);
				}
			}
		}
	}

	protected void applyAdvantage() {
		ArrayList<String> list = new ArrayList<>();
		for (String key:hash.keySet()) {
			JCheckBox option = hash.get(key);
			if (option.isSelected()) {
				list.add(key.toLowerCase());
			}
		}
		setAttributeList(Constants.TREASURE_LOCATION_FEAR,list);
	}
	public String getSuggestedDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Fears the treasure locations ");
		StringBufferedList list = new StringBufferedList(", ","and ");
		for (String key:hash.keySet()) {
			JCheckBox option = hash.get(key);
			if (option.isSelected()) {
				list.append(option.getText());
			}
		}
		sb.append(list.toString());
		sb.append(".");
		
		return sb.toString();
	}

	public boolean isCurrent() {
		return hasAttribute(Constants.TREASURE_LOCATION_FEAR);
	}
	
	public String toString() {
		return "Treasure Location Fear";
	}
}