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
	private static String[][] MISC_ADVANTAGE_ACTION_RECORDING = {
		{"Choose when to take your turn",Constants.CHOOSE_TURN,""},
		{"No need to record your turn.",Constants.DAYTIME_ACTIONS,""},
		{"You can cancel planned actions during your turn.",Constants.CANCEL_RECORDED_ACTION,""},
		{"Extra Phase at Dwellings",Constants.EXTRA_DWELLING_PHASE,""},
		{"Extra Phase in Caves",Constants.EXTRA_CAVE_PHASE,""},
		{"Cannot use sunlight phases",Constants.NO_SUNLIGHT,""},
		{"Rest Counts Double",Constants.REST_DOUBLE,""},
		{"-1 Mountain MOVE Cost",Constants.MOUNTAIN_MOVE_ADJ,"-1"},
		{"-1 Water MOVE Cost",Constants.WATER_MOVE_ADJ,"-1"},
		{"+1 Non-Cave MOVE Cost",Constants.NONCAVE_MOVE_DISADVANTAGE,""},
		{"Can walk woods on the 7th day of every week.",Constants.WALK_WOODS,"7th"},
		{"Can walk woods on the every day of every week.",Constants.WALK_WOODS,""},
	};
	private static String[][] MISC_ADVANTAGE_DISCOVERIES = {
		{"Knows all paths/passages",Constants.KNOWS_ROADS,""},
		{"Use any GATE without discovering.",Constants.ALL_GATE,""},
		{"Can open CHEST, CRYPT, or VAULT without the Lost Keys.",Constants.PICKS_LOCKS,""},
		{"Gains points for recording certain discoveries.",Constants.ADVENTURER,""},
		{"The Lost City and Lost Castle chits count as shelters.",Constants.ADVANCED_SHELTERS,""},
		{"A 5 result on Search, Peer and Locate also discovers secret passages.",Constants.ADVENTURE_GUIDE,""},
		{"A 5 result on Search, Peer and Locate also discovers hidden paths.",Constants.TRAVELERS_GUIDE,""},
		{"When Reading Runes, you may look at all spells, even not awakened.",Constants.MAGE_DIARY,""},
	};
	private static String[][] MISC_ADVANTAGE_OTHER_ABILITIES = {
		{"Magic sight",Constants.MAGIC_SIGHT,""},
		{"No monster summoning for sound/warning chits",Constants.PEACE_WITH_NATURE,""},
		{"No monster summoning for treasure locations",Constants.DRUID_LULL,""},
		{"Immune to Curses",Constants.CURSE_IMMUNITY,""},
		{"Immune to Mesmerize",Constants.MESMERIZE_IMMUNITY,""},
		{"Immunity to Transmorph spells (transform, melt into mist)",Constants.TRANSMORPH_IMMUNITY,""},
		{"Immunity to Transmorph spells casted on yourself",Constants.TRANSMORPH_IMMUNITY_SELF,""},
		{"Familiar",Constants.USE_FAMILIAR,""},
		{"MAGIC chits don't fatigue when alerted and unused",Constants.NO_MAGIC_FATIGUE,""},
		{"No SPX requirement",Constants.NO_SPX,""},
		{"You do not fatigue color chits when enchanting tiles.",Constants.TALISMAN,""},
		{"Flying",Constants.FLYING,""},
		{"Natives become Friendly when you buy drinks.",Constants.BARDS_LUTE,""},
	};
	private static String[][] MISC_ADVANTAGE_COMBAT_AND_SPELLCASTING = {
		{"Effort Limit of 3",Constants.EFFORT_LIMIT,"3"},
		{"Become unhidden when attacking from an ambush.",Constants.NO_AMBUSH,""},
		{"Cannot lure or target friendly and allied natives.",Constants.NATIVE_FRIENDLY,""},
		{"Has natural armor (like armored monster).",Constants.ARMORED,""},
		{"Suffer wounds instead of death when harm matches vulnerability.",Constants.TOUGHNESS,""},
		{"FIGHT chits without a weapon do full harm.",Constants.FIGHT_NO_WEAPON,""},
		{"Can use additional FIGHT chit without a weapon for blocking attacks.",Constants.BLOCK_NO_WEAPON,""},
		{"Can parry with a weapon like with a shield.",Constants.PARRY_LIKE_SHIELD,""},
		{"Can parry target's attack with a weapon.",Constants.PARRY,""},
		{"When parrying, missile attacks can be parried.",Constants.PARRY_MISSILE,""},
		{"When parrying, missile weapons can be used for parrying.",Constants.PARRY_WITH_MISSILE,""},
		{"Can activate a two-handed weapon and a shield.",Constants.STRONG,""},
		{"Can use two weapons, one in each hand.",Constants.DUAL_WIELDING,""},
		{"When dual wielding both weapons can be alerted.",Constants.DUAL_WIELDING_ALERT,""},
		{"Can throw corresponding weapons.",Constants.THROWING_WEAPONS,""},
		{"A successful HIDE roll on missile attacks, allows to stay hidden.",Constants.SNEAKY,""},
		{"Can penetrate targets armor with missile weapons.",Constants.SHARPSHOOTER,""},
		{"Can attack with a staff additionally to casting without any active armor.",Constants.BATTLE_MAGE,""},
		{"Cannot play a MAGIC counter, if any weapon except a staff is activated.",Constants.STAFF_RESTRICTED_SPELLCASTING,""},
		{"Can boost MOVE chits with MAGIC chits.",Constants.MAGIC_MOVE,""},
		{"Can cast multiple instances of a single spell.",Constants.ENHANCED_MAGIC,""},
		{"Can use Artifacts and Spell Books as extra MAGIC chits.",Constants.ENHANCED_ARTIFACTS,""},
		{"Can raise dead once each evening.",Constants.RAISE_DEAD,""},
		{"-1 to horse speed (min. 2)",Constants.SPURS,""},
		{"horse armored, +1 to horse speed, ",Constants.HORSE_ARMOR,""},
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
		box1.add(new JLabel("RECODRING ACTIONS"));
		for (int i=0;i<MISC_ADVANTAGE_ACTION_RECORDING.length;i++) {
			addChoice(box1,MISC_ADVANTAGE_ACTION_RECORDING[i],MISC_ADVANTAGE_ACTION_RECORDING[i]==advKey);
		}
		box1.add(Box.createVerticalGlue());
		box1.add(new JLabel("OTHER ABILITIES"));
		for (int i=0;i<MISC_ADVANTAGE_OTHER_ABILITIES.length;i++) {
			addChoice(box1,MISC_ADVANTAGE_OTHER_ABILITIES[i],MISC_ADVANTAGE_OTHER_ABILITIES[i]==advKey);
		}
		box1.add(Box.createVerticalGlue());
		add(box1);
		Box box2 = Box.createVerticalBox();
		box2.add(new JLabel("COMBAT & SPELLCASTING"));
		for (int i=0;i<MISC_ADVANTAGE_COMBAT_AND_SPELLCASTING.length;i++) {
			addChoice(box2,MISC_ADVANTAGE_COMBAT_AND_SPELLCASTING[i],MISC_ADVANTAGE_COMBAT_AND_SPELLCASTING[i]==advKey);
		}
		box2.add(Box.createVerticalGlue());
		box2.add(new JLabel("DISCOVERIES"));
		for (int i=0;i<MISC_ADVANTAGE_DISCOVERIES.length;i++) {
			addChoice(box2,MISC_ADVANTAGE_DISCOVERIES[i],MISC_ADVANTAGE_DISCOVERIES[i]==advKey);
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
		for (int i=0;i<MISC_ADVANTAGE_ACTION_RECORDING.length;i++) {
			String[] adv = MISC_ADVANTAGE_ACTION_RECORDING[i];
			if (hasAttribute(adv[COL_KEY])) {
				return adv;
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_DISCOVERIES.length;i++) {
			String[] adv = MISC_ADVANTAGE_DISCOVERIES[i];
			if (hasAttribute(adv[COL_KEY])) {
				return adv;
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_OTHER_ABILITIES.length;i++) {
			String[] adv = MISC_ADVANTAGE_OTHER_ABILITIES[i];
			if (hasAttribute(adv[COL_KEY])) {
				return adv;
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_COMBAT_AND_SPELLCASTING.length;i++) {
			String[] adv = MISC_ADVANTAGE_COMBAT_AND_SPELLCASTING[i];
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
		for (int i=0;i<MISC_ADVANTAGE_ACTION_RECORDING.length;i++) {
			JRadioButton button = buttonList.get(i);
			if (button.isSelected()) {
				setAttribute(MISC_ADVANTAGE_ACTION_RECORDING[i][COL_KEY],MISC_ADVANTAGE_ACTION_RECORDING[i][COL_VAL]);
				return;
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_DISCOVERIES.length;i++) {
			JRadioButton button = buttonList.get(i+MISC_ADVANTAGE_ACTION_RECORDING.length);
			if (button.isSelected()) {
				setAttribute(MISC_ADVANTAGE_DISCOVERIES[i][COL_KEY],MISC_ADVANTAGE_DISCOVERIES[i][COL_VAL]);
				return;
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_OTHER_ABILITIES.length;i++) {
			JRadioButton button = buttonList.get(i+MISC_ADVANTAGE_ACTION_RECORDING.length+MISC_ADVANTAGE_DISCOVERIES.length);
			if (button.isSelected()) {
				setAttribute(MISC_ADVANTAGE_OTHER_ABILITIES[i][COL_KEY],MISC_ADVANTAGE_OTHER_ABILITIES[i][COL_VAL]);
				return;
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_COMBAT_AND_SPELLCASTING.length;i++) {
			JRadioButton button = buttonList.get(i+MISC_ADVANTAGE_ACTION_RECORDING.length+MISC_ADVANTAGE_DISCOVERIES.length+MISC_ADVANTAGE_OTHER_ABILITIES.length);
			if (button.isSelected()) {
				setAttribute(MISC_ADVANTAGE_COMBAT_AND_SPELLCASTING[i][COL_KEY],MISC_ADVANTAGE_COMBAT_AND_SPELLCASTING[i][COL_VAL]);
				return;
			}
		}
	}
	public String getSuggestedDescription() {
		for (int i=0;i<MISC_ADVANTAGE_ACTION_RECORDING.length;i++) {
			JRadioButton button = buttonList.get(i);
			if (button.isSelected()) {
				return button.getText();
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_DISCOVERIES.length;i++) {
			JRadioButton button = buttonList.get(i+MISC_ADVANTAGE_ACTION_RECORDING.length);
			if (button.isSelected()) {
				return button.getText();
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_OTHER_ABILITIES.length;i++) {
			JRadioButton button = buttonList.get(i+MISC_ADVANTAGE_ACTION_RECORDING.length+MISC_ADVANTAGE_DISCOVERIES.length);
			if (button.isSelected()) {
				return button.getText();
			}
		}
		for (int i=0;i<MISC_ADVANTAGE_COMBAT_AND_SPELLCASTING.length;i++) {
			JRadioButton button = buttonList.get(i+MISC_ADVANTAGE_ACTION_RECORDING.length+MISC_ADVANTAGE_DISCOVERIES.length+MISC_ADVANTAGE_OTHER_ABILITIES.length);
			if (button.isSelected()) {
				return button.getText();
			}
		}
		return null;
	}
}