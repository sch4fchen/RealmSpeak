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

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.*;

import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class MiscellaneousEditPanel extends AdvantageEditPanel {
	
	private static int COL_NAME = 0;
	private static int COL_KEY = 1;
	private static int COL_VAL = 2;
	private static String[][] MISC_ADVANTAGE_1 = {
		{"Choose when to take your turn",Constants.CHOOSE_TURN,""},
		{"No need to record your turn.",Constants.DAYTIME_ACTIONS,""},
		{"You can change one action once a turn.",Constants.CHANGE_RECORDED_ACTION,""},
		{"Magic sight",Constants.MAGIC_SIGHT,""},
		{"No monster summoning for sound/warning chits",Constants.PEACE_WITH_NATURE,""},
		{"No monster summoning for treasure locations",Constants.DRUID_LULL,""},
		{"Immune to Curses",Constants.CURSE_IMMUNITY,""},
		{"Familiar",Constants.USE_FAMILIAR,""},
		{"Cannot use sunlight phases",Constants.NO_SUNLIGHT,""},
		{"MAGIC chits don't fatigue when alerted and unused",Constants.NO_MAGIC_FATIGUE,""},
		{"Knows all paths/passages",Constants.KNOWS_ROADS,""},
		{"No SPX requirement",Constants.NO_SPX,""},
		{"Extra Phase at Dwellings",Constants.EXTRA_DWELLING_PHASE,""},
		{"Extra Phase in Caves",Constants.EXTRA_CAVE_PHASE,""},
		{"Rest Counts Double",Constants.REST_DOUBLE,""},
		{"-1 Mountain MOVE Cost",Constants.MOUNTAIN_MOVE_ADJ,"-1"},
		{"+1 Non-Cave MOVE Cost",Constants.NONCAVE_MOVE_DISADVANTAGE,""},
		{"Effort Limit of 3",Constants.EFFORT_LIMIT,"3"},
	};
	private static String[][] MISC_ADVANTAGE_2 = {
		{"FIGHT chits without a weapon do full harm",Constants.FIGHT_NO_WEAPON,""},
		{"Can open CHEST, CRYPT, or VAULT without the Lost Keys.",Constants.PICKS_LOCKS,""},
		{"Can walk woods on the 7th day of every week.",Constants.WALK_WOODS,"7th"},
		{"Has natural armor (like armored monster).",Constants.ARMORED,""},
		{"Suffer wounds instead of death when harm matches vulnerability.",Constants.TOUGHNESS,""},
		{"A successful HIDE roll on missile attacks, allows to stay hidden.",Constants.SNEAKY,""},
		{"Become unhidden when attacking from an ambush.",Constants.NO_AMBUSH,""},
		{"Can activate a two-handed weapon and a shield.",Constants.STRONG,""},
		{"Can use two weapons, one in each hand.",Constants.DUAL_WIELDING,""},
		{"When dual wielding both weapons can be alerted.",Constants.DUAL_WIELDING_ALERT,""},
		{"Can attack with a staff additionally to casting a spell if having no active armor chits.",Constants.BATTLE_MAGE,""},
		{"Can penetrate targets armor with missile weapons.",Constants.SHARPSHOOTER,""},
		{"Can boost MOVE chits with MAGIC chits.",Constants.MAGIC_MOVE,""},
		{"Can cast multiple instances of a single spell.",Constants.ENHANCED_MAGIC,""},
		{"Can use Artifacts and Spell Books as extra MAGIC chits.",Constants.ENHANCED_ARTIFACTS,""},
		{"Cannot play a MAGIC counter, if he has any weapon counter except a staff activated.",Constants.STAFF_RESTRICTED_SPELLCASTING,""},
		{"Gains points for recording certain discoveries.",Constants.ADVENTURER,""},
		{"The Lost City and Lost Castle chits count as shelters.",Constants.ADVANCED_SHELTERS,""},
		{"Cannot lure or target friendly and allied natives.",Constants.ADVANCED_SHELTERS,""},
	};
	private ButtonGroup group;
	private ArrayList<JRadioButton> buttonList;

	public MiscellaneousEditPanel(CharacterWrapper pChar,String levelKey) {
		super(pChar,levelKey);
		
		group = new ButtonGroup();
		buttonList = new ArrayList<>();
		
		setLayout(new GridLayout(1,2));
		
		String[] advKey = getAdvantageKey();
		Box box1 = Box.createVerticalBox();
		for (int i=0;i<MISC_ADVANTAGE_1.length;i++) {
			addChoice(box1,MISC_ADVANTAGE_1[i],MISC_ADVANTAGE_1[i]==advKey);
		}
		box1.add(Box.createVerticalGlue());
		add(box1);
		Box box2 = Box.createVerticalBox();
		for (int i=0;i<MISC_ADVANTAGE_2.length;i++) {
			addChoice(box2,MISC_ADVANTAGE_2[i],MISC_ADVANTAGE_2[i]==advKey);
		}
		box2.add(Box.createVerticalGlue());
		add(box2);
		if (advKey==null) {
			buttonList.get(0).setSelected(true);
		}
	}
	public String toString() {
		return "Miscellaneous";
	}
	private void addChoice(Box box,String[] adv,boolean sel) {
		JRadioButton button = new JRadioButton(adv[COL_NAME],sel);
		group.add(button);
		buttonList.add(button);
		box.add(button);
	}
	private String[] getAdvantageKey() {
		for (int i=0;i<MISC_ADVANTAGE_1.length;i++) {
			String[] adv = MISC_ADVANTAGE_1[i];
			if (hasAttribute(adv[COL_KEY])) {
				return adv;
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_2.length;i++) {
			String[] adv = MISC_ADVANTAGE_2[i];
			if (hasAttribute(adv[COL_KEY])) {
				return adv;
			}
		}
		return null;
	}
	public boolean isCurrent() {
		return getAdvantageKey()!=null;
	}
	protected void applyAdvantage() {
		for (int i=0;i<MISC_ADVANTAGE_1.length;i++) {
			JRadioButton button = buttonList.get(i);
			if (button.isSelected()) {
				setAttribute(MISC_ADVANTAGE_1[i][COL_KEY],MISC_ADVANTAGE_1[i][COL_VAL]);
				return;
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_2.length;i++) {
			JRadioButton button = buttonList.get(i+MISC_ADVANTAGE_1.length);
			if (button.isSelected()) {
				setAttribute(MISC_ADVANTAGE_2[i][COL_KEY],MISC_ADVANTAGE_2[i][COL_VAL]);
				return;
			}
		}
	}
	public String getSuggestedDescription() {
		for (int i=0;i<MISC_ADVANTAGE_1.length;i++) {
			JRadioButton button = buttonList.get(i);
			if (button.isSelected()) {
				return button.getText();
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_2.length;i++) {
			JRadioButton button = buttonList.get(i+MISC_ADVANTAGE_1.length);
			if (button.isSelected()) {
				return button.getText();
			}
		}
		return null;
	}
}