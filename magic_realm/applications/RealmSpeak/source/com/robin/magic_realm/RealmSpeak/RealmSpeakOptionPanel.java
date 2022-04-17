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
package com.robin.magic_realm.RealmSpeak;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.robin.general.sound.SoundCache;
import com.robin.magic_realm.components.CharacterChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.attribute.ChatLine.HeaderMode;
import com.robin.magic_realm.components.utility.CustomUiUtility;

public class RealmSpeakOptionPanel extends JDialog {

	protected JRadioButton systemLookAndFeelOption;
	protected JRadioButton crossPlatformLookAndFeelOption;
	
	protected JCheckBox responsive;
	
	protected JRadioButton backgroundColor0;
	protected JRadioButton backgroundColor1;
	protected JRadioButton backgroundColor2;
	protected JRadioButton backgroundColor3;

	protected JRadioButton largeIconsOption;
	protected JRadioButton mediumIconsOption;
	protected JRadioButton smallIconsOption;
	protected JRadioButton fullTextOption;
	protected JRadioButton shortTextOption;
	
	protected JRadioButton classicChitsOption;
	protected JRadioButton colorChitsOption;
	protected JRadioButton frenzelChitsOption;
	protected JRadioButton legendaryChitsOption;
	protected JCheckBox killedByOption;
	protected JCheckBox monsterNumbersOption;
	
	protected JRadioButton classicCharacterChitsOption;
	protected JRadioButton legendaryClassicCharacterChitsOption;
	protected JRadioButton legendaryCharacterChitsOption;
	
	protected JRadioButton classicTilesOption;
	protected JRadioButton legendaryTilesOption;
	protected JRadioButton legendaryWithIconsTilesOption;
	
	protected JCheckBox mapSliderOption;
	protected JCheckBox highlightClearingNumbersOption;
	protected JCheckBox showSeasonIconOption;
	
	protected ChatLineViewOption[] showChatLinesOption;
	protected JRadioButton showChatLineHeaderCharacterNameOption; 
	protected JRadioButton showChatLineHeaderPlayerNameOption; 
	protected JRadioButton showChatLineHeaderBothNameOption; 
	
	protected JCheckBox showHeavyInvWarningOption;
	protected JCheckBox showIncompleteRecordWarningOption;
	protected JCheckBox showMoveAfterHireWarningOption;
	protected JCheckBox showUnassignedHirelingsWarningOption;
	protected JCheckBox showTurnEndResultsOption;
	
	protected JRadioButton dailyCombatOffOption;
	protected JRadioButton dailyCombatOnOption;
	protected JRadioButton dailyCombatOnSpellcastersOption;
	
	protected JCheckBox enableSoundItem;
	protected JSlider adjustVolumeItem;
	
	private RealmSpeakFrame mainFrame;
	private RealmSpeakOptions options;
	
	public RealmSpeakOptionPanel(RealmSpeakFrame frame,RealmSpeakOptions options) {
		super(frame,"Game Play Options",true);
		initComponents();
		setLocationRelativeTo(frame);
		mainFrame = frame;
		this.options = options;
		updateControls();
	}
	private void updateControls() {
		crossPlatformLookAndFeelOption.setSelected(options.getOptions().isPref(RealmSpeakOptions.METAL_LNF));
		systemLookAndFeelOption.setSelected(!crossPlatformLookAndFeelOption.isSelected());
		responsive.setSelected(options.getOptions().getBoolean(CustomUiUtility.RESPONSIVE));
		switch(options.getOptions().getInt(CustomUiUtility.BACKGROUND_COLOR)) {
			default:
			case CustomUiUtility.BACKGROUND_COLOR_0:
				backgroundColor0.setSelected(true);
				break;
			case CustomUiUtility.BACKGROUND_COLOR_1:
				backgroundColor1.setSelected(true);
				break;
			case CustomUiUtility.BACKGROUND_COLOR_2:
				backgroundColor2.setSelected(true);
				break;
			case CustomUiUtility.BACKGROUND_COLOR_3:
				backgroundColor3.setSelected(true);
				break;
		}
		
		switch(options.getOptions().getInt(RealmSpeakOptions.ACTION_ICONS)) {
			case ActionIcon.ACTION_ICON_MEDIUM:
				mediumIconsOption.setSelected(true);
				break;
			case ActionIcon.ACTION_ICON_SMALL:
				smallIconsOption.setSelected(true);
				break;
			case ActionIcon.ACTION_ICON_FULL_TEXT:
				fullTextOption.setSelected(true);
				break;
			case ActionIcon.ACTION_ICON_ABBREV_TEXT:
				shortTextOption.setSelected(true);
				break;
			case ActionIcon.ACTION_ICON_NORMAL:
			default:
				largeIconsOption.setSelected(true);
				break;
		}
		
		switch(options.getOptions().getInt(RealmSpeakOptions.CHIT_DISPLAY_STYLE)) {
			case RealmComponent.DISPLAY_STYLE_CLASSIC:
				classicChitsOption.setSelected(true);
				break;
			case RealmComponent.DISPLAY_STYLE_COLOR:
				colorChitsOption.setSelected(true);
				break;
			case RealmComponent.DISPLAY_STYLE_FRENZEL:
				frenzelChitsOption.setSelected(true);
				break;
			case RealmComponent.DISPLAY_STYLE_LEGENDARY:
				legendaryChitsOption.setSelected(true);
				break;
			default:
				classicChitsOption.setSelected(true);
				break;
		}
		
		switch(options.getOptions().getInt(RealmSpeakOptions.CHARACTER_CHIT_DISPLAY_STYLE)) {
			case CharacterChitComponent.DISPLAY_STYLE_CLASSIC:
				classicCharacterChitsOption.setSelected(true);
				break;
			case CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC:
				legendaryClassicCharacterChitsOption.setSelected(true);
				break;
			case CharacterChitComponent.DISPLAY_STYLE_LEGENDARY:
				legendaryCharacterChitsOption.setSelected(true);
				break;
			default:
				classicCharacterChitsOption.setSelected(true);
				break;
		}

		switch(options.getOptions().getInt(RealmSpeakOptions.TILES_DISPLAY_STYLE)) {
			case TileComponent.DISPLAY_TILES_STYLE_LEGENDARY:
				legendaryTilesOption.setSelected(true);
				break;
			case TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS:
				legendaryWithIconsTilesOption.setSelected(true);
				break;
			default:
				classicTilesOption.setSelected(true);
				break;
		}
		
		mapSliderOption.setSelected(options.getOptions().getBoolean(RealmSpeakOptions.MAP_SLIDER));
		highlightClearingNumbersOption.setSelected(options.getOptions().getBoolean(RealmSpeakOptions.HIGHLIGHT_CLEARING_NUMBERS));
		showSeasonIconOption.setSelected(options.getOptions().getBoolean(RealmSpeakOptions.SHOW_SEASON_ICON));
		int lines = options.getOptions().getInt(RealmSpeakOptions.NUMBER_OF_CHAT_LINES);
		for (int i=0;i<showChatLinesOption.length;i++) {
			showChatLinesOption[i].setSelected(showChatLinesOption[i].getLines()==lines);
		}
		String headerMode = options.getOptions().get(RealmSpeakOptions.HEADER_CHAT_LINES);
		if (headerMode==null) headerMode = "PlayerName";
		switch(HeaderMode.valueOf(headerMode)) {
			case PlayerName:
				showChatLineHeaderPlayerNameOption.setSelected(true);
				break;
			case Both:
				showChatLineHeaderBothNameOption.setSelected(true);
				break;
			case CharacterName:
			default:
				showChatLineHeaderCharacterNameOption.setSelected(true);
				break;
		}
		String val = options.getOptions().get(RealmSpeakOptions.DAILY_COMBAT);
		if ("ON".equals(val)) {
			dailyCombatOnOption.setSelected(true);
		}
		else if ("OFF".equals(val)) {
			dailyCombatOffOption.setSelected(true);
		}
		else if ("ON_S".equals(val)) {
			dailyCombatOnSpellcastersOption.setSelected(true);
		}
		
		monsterNumbersOption.setSelected(options.getOptions().getBoolean(RealmSpeakOptions.MONSTER_NUMBERS,false));
		killedByOption.setSelected(options.getOptions().getBoolean(RealmSpeakOptions.CHIT_KILLED_BY,false));
		
		showHeavyInvWarningOption.setSelected(options.getOptions().getBoolean(RealmSpeakOptions.HEAVY_INV_WARNING,true));
		showIncompleteRecordWarningOption.setSelected(options.getOptions().getBoolean(RealmSpeakOptions.INCOMPLETE_PHASE_WARNING,true));
		showMoveAfterHireWarningOption.setSelected(options.getOptions().getBoolean(RealmSpeakOptions.MOVE_AFTER_HIRE_WARNING,true));
		showUnassignedHirelingsWarningOption.setSelected(options.getOptions().getBoolean(RealmSpeakOptions.UNASSIGNED_HIRELINGS_WARNING,true));
		showTurnEndResultsOption.setSelected(options.getOptions().getBoolean(RealmSpeakOptions.TURN_END_RESULTS,false));
		
		boolean sound = options.getOptions().getBoolean(RealmSpeakOptions.ENABLE_SOUND,true);
		enableSoundItem.setSelected(sound);
		adjustVolumeItem.setEnabled(sound);
	}
	private void saveOptions() {
		options.getOptions().set(RealmSpeakOptions.ACTION_ICONS,getActionIconState());
		options.getOptions().set(RealmSpeakOptions.CHIT_DISPLAY_STYLE,getChitDisplayStyle());
		options.getOptions().set(RealmSpeakOptions.CHARACTER_CHIT_DISPLAY_STYLE,getCharacterChitDisplayStyle());
		options.getOptions().set(RealmSpeakOptions.TILES_DISPLAY_STYLE,getTilesDisplayStyle());
		options.getOptions().set(RealmSpeakOptions.METAL_LNF,crossPlatformLookAndFeelOption.isSelected());
		options.getOptions().set(CustomUiUtility.RESPONSIVE,isResponsive());
		options.getOptions().set(CustomUiUtility.BACKGROUND_COLOR,getSelectedBackgroundColor());
		options.getOptions().set(RealmSpeakOptions.MAP_SLIDER,mapSliderOption.isSelected());
		options.getOptions().set(RealmSpeakOptions.HIGHLIGHT_CLEARING_NUMBERS,highlightClearingNumbersOption.isSelected());
		options.getOptions().set(RealmSpeakOptions.SHOW_SEASON_ICON,showSeasonIconOption.isSelected());
		options.getOptions().set(RealmSpeakOptions.NUMBER_OF_CHAT_LINES,getSelectedNumberOfChatLines());
		options.getOptions().set(RealmSpeakOptions.HEADER_CHAT_LINES,getSelectedChatHeaderMode().toString());
		if (dailyCombatOffOption.isSelected()) {
			options.getOptions().set(RealmSpeakOptions.DAILY_COMBAT,"OFF");
		}
		else if (dailyCombatOnOption.isSelected()) {
			options.getOptions().set(RealmSpeakOptions.DAILY_COMBAT,"ON");
		}
		else if (dailyCombatOnSpellcastersOption.isSelected()) {
			options.getOptions().set(RealmSpeakOptions.DAILY_COMBAT,"ON_S");
		}
		options.getOptions().set(RealmSpeakOptions.MONSTER_NUMBERS,monsterNumbersOption.isSelected());
		options.getOptions().set(RealmSpeakOptions.CHIT_KILLED_BY,killedByOption.isSelected());
		options.getOptions().set(RealmSpeakOptions.HEAVY_INV_WARNING,showHeavyInvWarningOption.isSelected());
		options.getOptions().set(RealmSpeakOptions.INCOMPLETE_PHASE_WARNING,showIncompleteRecordWarningOption.isSelected());
		options.getOptions().set(RealmSpeakOptions.MOVE_AFTER_HIRE_WARNING,showMoveAfterHireWarningOption.isSelected());
		options.getOptions().set(RealmSpeakOptions.UNASSIGNED_HIRELINGS_WARNING,showUnassignedHirelingsWarningOption.isSelected());
		options.getOptions().set(RealmSpeakOptions.TURN_END_RESULTS,showTurnEndResultsOption.isSelected());
		options.getOptions().set(RealmSpeakOptions.ENABLE_SOUND,enableSoundItem.isSelected());
		mainFrame.saveFramePreferences();
	}
	private boolean isResponsive() {
		if (responsive.isSelected()) {
			return true;
		}
		return false;
	}
	private int getSelectedBackgroundColor() {
		if (backgroundColor0.isSelected()) {
			return CustomUiUtility.BACKGROUND_COLOR_0;
		}
		else if (backgroundColor1.isSelected()) {
			return CustomUiUtility.BACKGROUND_COLOR_1;
		}
		else if (backgroundColor2.isSelected()) {
			return CustomUiUtility.BACKGROUND_COLOR_2;
		}
		else if (backgroundColor3.isSelected()) {
			return CustomUiUtility.BACKGROUND_COLOR_3;
		}
		else {
			return CustomUiUtility.BACKGROUND_COLOR_0;
		}
	}
	private int getActionIconState() {
		if (largeIconsOption.isSelected()) {
			return ActionIcon.ACTION_ICON_NORMAL;
		}
		else if (mediumIconsOption.isSelected()) {
			return ActionIcon.ACTION_ICON_MEDIUM;
		}
		else if (smallIconsOption.isSelected()) {
			return ActionIcon.ACTION_ICON_SMALL;
		}
		else if (fullTextOption.isSelected()) {
			return ActionIcon.ACTION_ICON_FULL_TEXT;
		}
		else if (shortTextOption.isSelected()) {
			return ActionIcon.ACTION_ICON_ABBREV_TEXT;
		}
		return ActionIcon.ACTION_ICON_NORMAL; // default
	}
	private int getChitDisplayStyle() {
		if (colorChitsOption.isSelected()) {
			return RealmComponent.DISPLAY_STYLE_COLOR;
		}
		else if (frenzelChitsOption.isSelected()) {
			return RealmComponent.DISPLAY_STYLE_FRENZEL;
		}
		else if (legendaryChitsOption.isSelected()) {
			return RealmComponent.DISPLAY_STYLE_LEGENDARY;
		}
		return RealmComponent.DISPLAY_STYLE_CLASSIC;
	}
	private int getCharacterChitDisplayStyle() {
		if (legendaryClassicCharacterChitsOption.isSelected()) {
			return CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC;
		}
		if (legendaryCharacterChitsOption.isSelected()) {
			return CharacterChitComponent.DISPLAY_STYLE_LEGENDARY;
		}
		return CharacterChitComponent.DISPLAY_STYLE_CLASSIC;
	}
	private int getTilesDisplayStyle() {
		if (legendaryTilesOption.isSelected()) {
			return TileComponent.DISPLAY_TILES_STYLE_LEGENDARY;
		}
		else if (legendaryWithIconsTilesOption.isSelected()) {
			return TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS;
		}
		return TileComponent.DISPLAY_TILES_STYLE_CLASSIC;
	}
	private int getSelectedNumberOfChatLines() {
		for (int i=0;i<showChatLinesOption.length;i++) {
			if (showChatLinesOption[i].isSelected()) {
				return showChatLinesOption[i].getLines();
			}
		}
		return 0;
	}
	private HeaderMode getSelectedChatHeaderMode() {
		if (showChatLineHeaderPlayerNameOption.isSelected()) {
			return HeaderMode.PlayerName;
		}
		if (showChatLineHeaderBothNameOption.isSelected()) {
			return HeaderMode.Both;
		}
		return HeaderMode.CharacterName;
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		
		JPanel center = new JPanel(new GridLayout(1,2));
		
		Box left = Box.createVerticalBox();
		left.add(getLookAndFeelOptions());
		left.add(getResponsiveOption());
		left.add(getBackgroundColorChooser());
		left.add(getSoundOptionPanel());
		left.add(getActionIconOptions());
		left.add(getChitsOptionsPanel());
		left.add(getCharacterChitsOptionPanel());
		left.add(getTilesOptionsPanel());
		left.add(Box.createVerticalGlue());
		center.add(left);
		
		Box right = Box.createVerticalBox();
		right.add(getMapOptionsPanel());
		right.add(getChatDisplayOptionsPanel());
		right.add(getChatLineOptionsPanel());
		right.add(getPopupWindowOptions());
		right.add(getDailyCombatOptionPanel());
		right.add(Box.createVerticalGlue());
		center.add(right);
		
		add(center,BorderLayout.CENTER);
		
		Box buttons = Box.createHorizontalBox();
		buttons.add(Box.createHorizontalGlue());
		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveOptions();
				options.apply(mainFrame);
				SwingUtilities.updateComponentTreeUI(RealmSpeakOptionPanel.this);
			}
		});
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveOptions();
				options.apply(mainFrame);
				close();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				close();
			}
		});
		buttons.add(applyButton);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(cancelButton);
		buttons.add(okButton);
		getRootPane().setDefaultButton(okButton);
		add(buttons,BorderLayout.SOUTH);
		pack();
	}
	private void close() {
		setVisible(false);
		dispose();
	}
	private JPanel getLookAndFeelOptions() {
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.setBorder(BorderFactory.createTitledBorder("Look and Feel"));
		ButtonGroup group = new ButtonGroup();
		crossPlatformLookAndFeelOption = new JRadioButton("Cross Platform");
		crossPlatformLookAndFeelOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
			}
		});
		group.add(crossPlatformLookAndFeelOption);
		panel.add(crossPlatformLookAndFeelOption);
		systemLookAndFeelOption = new JRadioButton("System"); 
		systemLookAndFeelOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
			}
		});
		group.add(systemLookAndFeelOption);
		panel.add(systemLookAndFeelOption);
		return panel;
	}
	private JPanel getResponsiveOption() {
		JPanel panel = new JPanel(new GridLayout(1,1));
		panel.setBorder(BorderFactory.createTitledBorder("Responsive UI"));
		responsive = new JCheckBox("Enabled");
		panel.add(responsive);
		return panel;
	}
	private JPanel getBackgroundColorChooser() {
		JPanel panel = new JPanel(new GridLayout(4,1));
		panel.setBorder(BorderFactory.createTitledBorder("Color scheme (requires restart)"));
		ButtonGroup group = new ButtonGroup();
		backgroundColor0 = new JRadioButton("Default");
		group.add(backgroundColor0);
		panel.add(backgroundColor0);
		backgroundColor1 = new JRadioButton("White");
		group.add(backgroundColor1);
		panel.add(backgroundColor1);
		backgroundColor2 = new JRadioButton("Light gray"); 
		group.add(backgroundColor2);
		panel.add(backgroundColor2);
		backgroundColor3 = new JRadioButton("Gray"); 
		group.add(backgroundColor3);
		panel.add(backgroundColor3);
		return panel;
	}
	private JPanel getActionIconOptions() {
		JPanel panel = new JPanel(new GridLayout(5,1));
		panel.setBorder(BorderFactory.createTitledBorder("Action Icons"));
		ButtonGroup group = new ButtonGroup();
		largeIconsOption = new JRadioButton("Large Action Icons");
		group.add(largeIconsOption);
		panel.add(largeIconsOption);
		mediumIconsOption = new JRadioButton("Medium Action Icons");
		group.add(mediumIconsOption);
		panel.add(mediumIconsOption);
		smallIconsOption = new JRadioButton("Small Action Icons");
		group.add(smallIconsOption);
		panel.add(smallIconsOption);
		fullTextOption = new JRadioButton("Text Only (full)");
		group.add(fullTextOption);
		panel.add(fullTextOption);
		shortTextOption = new JRadioButton("Text Only (short)");
		group.add(shortTextOption);
		panel.add(shortTextOption);
		return panel;
	}
	private JPanel getChitsOptionsPanel() {
		JPanel panel = new JPanel(new GridLayout(6,1));
		panel.setBorder(BorderFactory.createTitledBorder("Game Chits"));
		ButtonGroup group = new ButtonGroup();
		classicChitsOption = new JRadioButton("Classic Chits");
		group.add(classicChitsOption);
		panel.add(classicChitsOption);
		colorChitsOption = new JRadioButton("Color Chits");
		group.add(colorChitsOption);
		panel.add(colorChitsOption);
		frenzelChitsOption = new JRadioButton("Remodeled Chits");
		group.add(frenzelChitsOption);
		panel.add(frenzelChitsOption);
		legendaryChitsOption = new JRadioButton("Legendary Chits");
		legendaryChitsOption.setEnabled(false);
		group.add(legendaryChitsOption);
		panel.add(legendaryChitsOption);
		monsterNumbersOption = new JCheckBox("Show Monster Numbers");
		panel.add(monsterNumbersOption);
		killedByOption = new JCheckBox("Show 'killed by'");
		panel.add(killedByOption);
		return panel;
	}
	private JPanel getCharacterChitsOptionPanel() {
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createTitledBorder("Character Chits Style"));
		ButtonGroup group = new ButtonGroup();
		classicCharacterChitsOption = new JRadioButton("Classic");
		group.add(classicCharacterChitsOption);
		panel.add(classicCharacterChitsOption);
		legendaryClassicCharacterChitsOption = new JRadioButton("Legendary Realm (classic hidden)");
		group.add(legendaryClassicCharacterChitsOption);
		panel.add(legendaryClassicCharacterChitsOption);
		legendaryCharacterChitsOption = new JRadioButton("Legendary Realm");
		group.add(legendaryCharacterChitsOption);
		panel.add(legendaryCharacterChitsOption);
		return panel;
	}
	private JPanel getTilesOptionsPanel() {
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createTitledBorder("Tiles Style"));
		ButtonGroup group = new ButtonGroup();
		classicTilesOption = new JRadioButton("Classic");
		group.add(classicTilesOption);
		panel.add(classicTilesOption);
		legendaryTilesOption = new JRadioButton("Legendary Realm");
		group.add(legendaryTilesOption);
		panel.add(legendaryTilesOption);
		legendaryWithIconsTilesOption = new JRadioButton("Legendary Realm (with Icons)");
		group.add(legendaryWithIconsTilesOption);
		panel.add(legendaryWithIconsTilesOption);
		return panel;
	}
	private JPanel getMapOptionsPanel() {
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createTitledBorder("Map View"));
		mapSliderOption = new JCheckBox("Show Zoom Slider");
		panel.add(mapSliderOption);
		highlightClearingNumbersOption = new JCheckBox("Show Clearing Numbers on Mouseover");
		panel.add(highlightClearingNumbersOption);
		showSeasonIconOption = new JCheckBox("Show Season Icon");
		panel.add(showSeasonIconOption);
		return panel;
	}
	private JPanel getChatDisplayOptionsPanel() {
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createTitledBorder("Chat Display"));
		ButtonGroup group = new ButtonGroup();
		showChatLineHeaderCharacterNameOption = new JRadioButton("Chat shows Character Name Only");
		group.add(showChatLineHeaderCharacterNameOption);
		panel.add(showChatLineHeaderCharacterNameOption);
		showChatLineHeaderPlayerNameOption = new JRadioButton("Chat shows Player Name Only");
		group.add(showChatLineHeaderPlayerNameOption);
		panel.add(showChatLineHeaderPlayerNameOption);
		showChatLineHeaderBothNameOption = new JRadioButton("Chat shows Both Names");
		group.add(showChatLineHeaderBothNameOption);
		panel.add(showChatLineHeaderBothNameOption);
		
		return panel;
	}
	private JPanel getChatLineOptionsPanel() {
		showChatLinesOption = ChatLineViewOption.generateOptions();
		JPanel panel = new JPanel(new GridLayout(showChatLinesOption.length,1));
		panel.setBorder(BorderFactory.createTitledBorder("Chat Lines"));
		ButtonGroup chatLinesOptionGroup = new ButtonGroup();
		for (int i=0;i<showChatLinesOption.length;i++) {
			showChatLinesOption[i].setSelected(i==0);
			chatLinesOptionGroup.add(showChatLinesOption[i]);
			panel.add(showChatLinesOption[i]);
		}
		return panel;
	}
	private JPanel getPopupWindowOptions() {
		JPanel panel = new JPanel(new GridLayout(5,1));
		panel.setBorder(BorderFactory.createTitledBorder("Notifications"));
		showHeavyInvWarningOption = new JCheckBox("Show Heavy Inventory Warning");
		panel.add(showHeavyInvWarningOption);
		showIncompleteRecordWarningOption = new JCheckBox("Show Incomplete Recording Warning");
		panel.add(showIncompleteRecordWarningOption);
		showMoveAfterHireWarningOption = new JCheckBox("Show Move-After-Hire Warning");
		panel.add(showMoveAfterHireWarningOption);
		showUnassignedHirelingsWarningOption = new JCheckBox("Show Unassigned Hirelings Warning");
		panel.add(showUnassignedHirelingsWarningOption);
		showTurnEndResultsOption = new JCheckBox("Show Turn End Results");
		panel.add(showTurnEndResultsOption);
		return panel;
	}
	private JPanel getDailyCombatOptionPanel() {
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createTitledBorder("Daily Combat"));
		ButtonGroup group = new ButtonGroup();
		dailyCombatOffOption = new JRadioButton("OFF for all characters");
		group.add(dailyCombatOffOption);
		panel.add(dailyCombatOffOption);
		dailyCombatOnOption = new JRadioButton("ON for all characters");
		group.add(dailyCombatOnOption);
		panel.add(dailyCombatOnOption);
		dailyCombatOnSpellcastersOption = new JRadioButton("ON for spellcasters");
		group.add(dailyCombatOnSpellcastersOption);
		panel.add(dailyCombatOnSpellcastersOption);
		return panel;
	}
	private JPanel getSoundOptionPanel() {
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.setBorder(BorderFactory.createTitledBorder("Sound"));
		enableSoundItem = new JCheckBox("Sound Enabled",SoundCache.isSoundEnabled());
		enableSoundItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				boolean sound = enableSoundItem.isSelected();
				adjustVolumeItem.setEnabled(sound);
				if (sound) {
					SoundCache.setSoundEnabled(sound);
					SoundCache.playSound("attention_quick.wav");
					SoundCache.setSoundEnabled(options.getOptions().getBoolean(RealmSpeakOptions.ENABLE_SOUND));
				}
			}
		});
		panel.add(enableSoundItem);
		
		double gain = SoundCache.getVolume();
		adjustVolumeItem = new JSlider(0,100,(int)(100*gain));
		adjustVolumeItem.setMajorTickSpacing(10);
		adjustVolumeItem.setMinorTickSpacing(5);
		adjustVolumeItem.setPaintTicks(true);
		adjustVolumeItem.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ev) {
				JSlider slider = (JSlider)ev.getSource();
				if (!slider.getValueIsAdjusting()) {
					SoundCache.setSoundEnabled(true);
					double val = slider.getValue();
					SoundCache.setVolume(val/100.0);
					SoundCache.playSound("attention_quick.wav");
					SoundCache.setSoundEnabled(options.getOptions().getBoolean(RealmSpeakOptions.ENABLE_SOUND));
				}
			}
		});
		panel.add(adjustVolumeItem);
		
		return panel;
	}
}