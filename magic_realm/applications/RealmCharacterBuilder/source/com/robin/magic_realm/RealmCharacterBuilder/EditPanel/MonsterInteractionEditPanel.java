/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.RealmCharacterBuilder.EditPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.*;

import javax.swing.*;

import com.robin.general.swing.ComponentTools;
import com.robin.general.swing.IntegerField;
import com.robin.general.util.StringBufferedList;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MonsterInteractionEditPanel extends AdvantageEditPanel {
	
	private static final String[][] MONSTERS = {
		{"Animals","Giant Bat","Wolf","T Serpent","H Serpent","H Spider","T Spider","Viper","Octopus","Crow","Scorpion","Carnoplant","Sabertooth","Wasp Queen","Rat","Bear","Alligator","T Scorpion","H Scorpion"},
		
		{"Dragons","T Flying Dragon","T Dragon","H Flying Dragon","H Dragon","Firedrake","Wyrm","Basilisk"},
		{"Fantastic","Minotaur","Griffon","Behemoth","Cockatrice","Harpy","Gargrath","Swamp Thing","Giant Pod"},
		
		{"Humanoids","Giant","Ogre","Spear Goblin","Axe Goblin","Sword Goblin","T Troll","H Troll","Lizardman","Rat Man","Sword Orc","Orc Archer","Kobold","Frost Giant","Bow Goblin","Orc"},
		
		{"Spirits/Undead","Ghost","Shade","Skeleton","Skeletal Archer","Skeletal Swordsman","Swamp Haunt","Tomb Guard","Wraith","Zombie","Sword Skeleton","Spear Skeleton","Axe Skeleton","Bow Skeleton","Vampire","Skeleton Knight"},
		
		{"Demons","Winged Demon","Demon","Imp","Balrog","Gargyle","T Gargyle","H Gargyle","Succubus"},
		{"Elementals","Earth Elemental","Air Elemental","Fire Elemental","Water Elemental","Prism Anomaly","Purple Anomaly","Gold Anomaly","Grey Anomaly","Golem","Titan","Colossus"},
	};

	private Hashtable<String,JCheckBox> hash;
	private String selection;
	private String duration;
	private JCheckBox enhancedControl;
	JTextField durationComponent = new JTextField("Duration");
	
	public MonsterInteractionEditPanel(CharacterWrapper pChar, String levelKey, String selected) {
		super(pChar, levelKey);
		this.selection = selected;
		setBorder(BorderFactory.createTitledBorder(toString())); // update name
		
		hash = new Hashtable<>();
		setLayout(new BorderLayout());
		
		if (controlSelected()) {
			Box box = Box.createHorizontalBox();
			JLabel label = new JLabel("Duration (empty or 0 = forever):  ");
			box.add(label);
			duration = getAttribute(Constants.MONSTER_CONTROL_DURATION);
			durationComponent = new IntegerField(duration == null ? "1" : duration);
			durationComponent.setVisible(true);
			ComponentTools.lockComponentSize(durationComponent,36,18);
			box.add(durationComponent);
			enhancedControl = new JCheckBox("Enhanced Command");
			if (hasAttribute(Constants.MONSTER_CONTROL_ENHANCED)) {
				enhancedControl.setSelected(true);
			}
			box.add(Box.createHorizontalStrut(40));
			box.add(enhancedControl);
			add(box,"North");
		}
		
		JPanel main = new JPanel(new GridLayout(1,5));
		
		Box box = Box.createVerticalBox();
		addOptionList(box,MONSTERS[0]);
		box.add(Box.createVerticalGlue());
		main.add(box);
		
		box = Box.createVerticalBox();
		addOptionList(box,MONSTERS[1]);
		addOptionList(box,MONSTERS[2]);
		box.add(Box.createVerticalGlue());
		main.add(box);
		
		box = Box.createVerticalBox();
		addOptionList(box,MONSTERS[3]);
		box.add(Box.createVerticalGlue());
		main.add(box);
		
		box = Box.createVerticalBox();
		addOptionList(box,MONSTERS[4]);
		box.add(Box.createVerticalGlue());
		main.add(box);
		
		box = Box.createVerticalBox();
		addOptionList(box,MONSTERS[5]);
		addOptionList(box,MONSTERS[6]);
		box.add(Box.createVerticalGlue());
		main.add(box);
		
		add(main,"Center");
		
		updateSelection();
	}
	
	private boolean immunitySelected() {
		return selection == Constants.MONSTER_IMMUNITY;
	}
	private boolean controlSelected() {
		return selection == Constants.MONSTER_CONTROL;
	}
	private boolean fearSelected() {
		return selection == Constants.MONSTER_FEAR;
	}
	
	
	private void updateSelection() {
		ArrayList<String> list = new ArrayList<>();
		if (immunitySelected()) {
			list = getAttributeList(Constants.MONSTER_IMMUNITY);
		}
		else if (controlSelected()) {
			list = getAttributeList(Constants.MONSTER_CONTROL);
		}
		else if (fearSelected()) {
			list = getAttributeList(Constants.MONSTER_FEAR);
		}
		
		if (list!=null) {
			for (String name : list) {
				JCheckBox option = hash.get(name);
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
			String name = list[i];
			JCheckBox option = new JCheckBox(name);
			panel.add(option);
			hash.put(name,option);
		}
		box.add(panel);
	}

	protected void applyAdvantage() {
		ArrayList<String> list = new ArrayList<>();
		for (String name:hash.keySet()) {
			JCheckBox option = hash.get(name);
			if (option.isSelected()) {
				list.add(name);
			}
		}
		if (immunitySelected()) {
			setAttributeList(Constants.MONSTER_IMMUNITY,list);
		}
		else if (controlSelected()) {
			setAttributeList(Constants.MONSTER_CONTROL,list);
			duration = durationComponent.getText();
			setAttribute(Constants.MONSTER_CONTROL_DURATION,duration == null || duration == "0" ? String.valueOf(Constants.TEN_YEARS) : duration);
			if (enhancedControl.isSelected()) {
				setAttribute(Constants.MONSTER_CONTROL_ENHANCED);
			}
			else {
				removeAttribute(Constants.MONSTER_CONTROL_ENHANCED);
			}
		}
		else if (fearSelected()) {
			setAttributeList(Constants.MONSTER_FEAR,list);
		}
	}
	public String getSuggestedDescription() {
		StringBuffer sb = new StringBuffer();
		if (immunitySelected()) {
			sb.append("Is immune to the ");
		}
		else if (controlSelected()) {
			sb.append("Can command the ");
		}
		else if (controlSelected()) {
			sb.append("Cannot attack the ");
		}
		StringBufferedList list = new StringBufferedList(", ","and ");
		for (String name:hash.keySet()) {
			JCheckBox option = hash.get(name);
			if (option.isSelected()) {
				list.append(option.getText());
			}
		}
		sb.append(list.toString());
		sb.append(".");
		
		return sb.toString();
	}

	public boolean isCurrent() {
		if (controlSelected()) {
			return hasAttribute(Constants.MONSTER_CONTROL);
		}
		else if (fearSelected()) {
			return hasAttribute(Constants.MONSTER_FEAR);
		}
		return hasAttribute(Constants.MONSTER_IMMUNITY);
	}
	
	public String toString() {
		if (controlSelected()) {
			return "Monster Command";
		}
		else if (fearSelected()) {
			return "Monster Fear";
		}
		return "Monster Immunity";
	}
}