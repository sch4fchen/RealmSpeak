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
package com.robin.magic_realm.RealmGm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileFilter;

import com.robin.game.objects.GameData;
import com.robin.general.io.FileUtilities;
import com.robin.general.io.PreferenceManager;
import com.robin.general.swing.AggressiveDialog;
import com.robin.general.swing.ComponentTools;
import com.robin.general.swing.DieRoller;
import com.robin.general.swing.FlashingButton;
import com.robin.general.swing.IconFactory;
import com.robin.general.util.RandomNumber;
import com.robin.general.util.RandomNumberType;
import com.robin.magic_realm.RealmCharacterBuilder.RealmCharacterBuilderModel;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.swing.HostGameSetupDialog;
import com.robin.magic_realm.components.CharacterChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.GameFileFilters;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class RealmGmFrame extends JFrame {
	private static final String MetalLookAndFeel = "MLAF";
	private static final String TilesDisplayStyle = "TS";
	private static final String PreferredFilePath = "PFP";
	
	private static final String ChitDisplayStyle = "CDS";
	private static final String CharacterChitDisplayStyle = "CDS";
	
	private PreferenceManager prefs;
	private JDesktopPane desktop;
	private RealmGameEditor editor;
	private boolean fileExists;
	
	private JMenuItem newGame;
	private JMenuItem openGame;
	private JMenuItem closeGame;
	private JMenuItem saveGame;
	private JMenuItem saveAsGame;
	
	private JMenuItem scenarioDescription;
	private JCheckBox scenarioRegenerateRandomNumbers;
	private JCheckBox scenarioRandomGoldSpecialPlacement;
	private JCheckBox scenarioAddNewQuests;
	private JCheckBox scenarioRebuildQuestDeck;
	private JCheckBox scenarioShuffleQuestDeck;
	private JMenuItem startGame;
	private JMenuItem undoStartGame;
	
	private JRadioButton classicChitsOption;
	private JRadioButton colorChitsOption;
	private JRadioButton frenzelChitsOption;
	private JRadioButton legendaryChitsOption;
	private JRadioButton classicCharacterChitsOption;
	private JRadioButton legendaryClassicCharacterChitsOption;
	private JRadioButton legendaryCharacterChitsOption;
	private JButton gameOptions;
	
	protected FileFilter saveGameFileFilter = GameFileFilters.createSaveGameFileFilter();
	
	public RealmGmFrame() {
		prefs = new PreferenceManager("RealmSpeak","RealmGm");
		prefs.loadPreferences();
		initComponents();
	}
	private void savePrefs() {
		prefs.savePreferences();
	}
	private void initComponents() {
		updateLookAndFeel();
		setTilesStyle();
		setChitDisplayStyle();
		setCharacterChitDisplayStyle();
		setTitle("RealmSpeak GM");
		setIconImage(IconFactory.findIcon("images/badges/elvish_studies.gif").getImage());
		setSize(1024,768);
		setLocationRelativeTo(null);
		
		setJMenuBar(buildMenuBar());
		setLayout(new BorderLayout());
		desktop = new JDesktopPane();
		add(desktop,BorderLayout.CENTER);
		
		fileExists = false;
		updateControls();
	}
	public void updateControls() {
		openGame.setEnabled(editor==null);
		closeGame.setEnabled(editor!=null);
		saveGame.setEnabled(editor!=null && editor.getGameData().isModified() && fileExists);
		saveAsGame.setEnabled(editor!=null);
		scenarioDescription.setEnabled(editor!=null);
		scenarioRegenerateRandomNumbers.setSelected(editor!=null && editor.getGameData().getScenarioRegenerateRandomNumbers());
		scenarioRandomGoldSpecialPlacement.setSelected(editor!=null && editor.getGameData().getScenarioRandomGoldSpecialPlacement());
		scenarioAddNewQuests.setSelected(editor!=null && editor.getGameData().getScenarioAddNewQuests());
		scenarioRebuildQuestDeck.setSelected(editor!=null && editor.getGameData().getScenarioRebuildQuestDeck());
		scenarioShuffleQuestDeck.setSelected(editor!=null && editor.getGameData().getScenarioShuffleQuestDeck());
		GameWrapper game = null;
		if (editor != null) {
			game = GameWrapper.findGame(editor.getGameData());
		}
		startGame.setEnabled(editor!=null && !game.getGameStarted());
		undoStartGame.setEnabled(editor!=null && game.getGameStarted());
		gameOptions.setEnabled(editor!=null);
	}
	private void editDescription() {
		AggressiveDialog dialog = new AggressiveDialog(this,"Scenario Description",true);
		dialog.setLayout(new BorderLayout());
		dialog.setSize(500, 500);
		dialog.setLocationRelativeTo(this);
		
		JPanel panel = new JPanel(new BorderLayout());
		Box box = Box.createVerticalBox();
		JTextArea input = new JTextArea();
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
		input.setText(editor.getGameData().getScenarioDescription()==null?"":editor.getGameData().getScenarioDescription());
		box.add(new JScrollPane(input));
		
		Box buttons = Box.createHorizontalBox();
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				editor.getGameData().setScenarioDescription(input.getText());
				dialog.dispose();
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				dialog.dispose();
			}
		});
		buttons.add(ok);
		buttons.add(cancel);
		box.add(buttons);
		
		panel.add(box);
		dialog.add(panel);
		dialog.setVisible(true);
	}
	private void updateRegenerateRandomNumbers() {
		editor.getGameData().setScenarioRegenerateRandomNumbers(scenarioRegenerateRandomNumbers.isSelected());
	}
	private void updateRandomGoldSpecialPlacement() {
		editor.getGameData().setScenarioRandomGoldSpecialPlacement(scenarioRandomGoldSpecialPlacement.isSelected());
	}
	private void updateAddingNewQuests() {
		editor.getGameData().setScenarioAddNewQuests(scenarioAddNewQuests.isSelected());
	}
	private void updateRebuildQuestDeck() {
		editor.getGameData().setScenarioRebuildQuestDeck(scenarioRebuildQuestDeck.isSelected());
	}
	private void updateShuffleQuestDeck() {
		editor.getGameData().setScenarioShuffleQuestDeck(scenarioShuffleQuestDeck.isSelected());
	}
	private void updateLookAndFeel() {
		if (prefs.getBoolean(MetalLookAndFeel)) {
			ComponentTools.setMetalLookAndFeel();
		}
		else {
			ComponentTools.setSystemLookAndFeel();
		}
		SwingUtilities.updateComponentTreeUI(this);
	}
	private void reinitMap() {
		if (editor != null) {
			editor.reinitMap();
		}	
	}
	private void updateTilesStyle() {
		setTilesStyle();
		reinitMap();
	}
	private void setTilesStyle() {
		switch(prefs.getInt(TilesDisplayStyle)) {
		case TileComponent.DISPLAY_TILES_STYLE_LEGENDARY:
			TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_LEGENDARY;
			break;
		case TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS:
			TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS;
			break;
		default:
			TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_CLASSIC;
			break;
		}
	}
	private void setChitDisplayStyle() {
		switch(prefs.getInt(ChitDisplayStyle)) {
		case RealmComponent.DISPLAY_STYLE_CLASSIC:
			RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_CLASSIC;
			break;
		case RealmComponent.DISPLAY_STYLE_COLOR:
			RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_COLOR;
			break;
		case RealmComponent.DISPLAY_STYLE_FRENZEL:
			RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_FRENZEL;
			break;
		case RealmComponent.DISPLAY_STYLE_LEGENDARY:
			RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_LEGENDARY;
			break;
		default:
			RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_CLASSIC;
			break;
		}
	}
	private void setCharacterChitDisplayStyle() {
		switch(prefs.getInt(CharacterChitDisplayStyle)) {
		case CharacterChitComponent.DISPLAY_STYLE_CLASSIC:
			CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_CLASSIC;
			break;
		case CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC:
			CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC;
			break;
		case CharacterChitComponent.DISPLAY_STYLE_LEGENDARY:
			CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_LEGENDARY;
			break;
		default:
			CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_CLASSIC;
			break;
		}
	}
	private JMenuBar buildMenuBar() {
		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		newGame = new JMenuItem("New Game");
		newGame.setMnemonic(KeyEvent.VK_N);
		newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (closeGame()) {
					newGame();
				};
			}
		});
		fileMenu.add(newGame);
		openGame = new JMenuItem("Open Game");
		openGame.setMnemonic(KeyEvent.VK_O);
		openGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
		openGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				openGame();
			}
		});
		fileMenu.add(openGame);
		closeGame = new JMenuItem("Close Game");
		closeGame.setMnemonic(KeyEvent.VK_C);
		closeGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
		closeGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				closeGame();
			}
		});
		fileMenu.add(closeGame);
		saveGame = new JMenuItem("Save Game");
		saveGame.setMnemonic(KeyEvent.VK_S);
		saveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		saveGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveGame(false);
			}
		});
		fileMenu.add(saveGame);
		saveAsGame = new JMenuItem("Save Game As...");
		saveAsGame.setMnemonic(KeyEvent.VK_A);
		saveAsGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_MASK));
		saveAsGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveGame(true);
			}
		});
		fileMenu.add(saveAsGame);
		fileMenu.add(new JSeparator());
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_X);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				close();
			}
		});
		fileMenu.add(exit);
		menu.add(fileMenu);
		JMenu scenarioMenu = new JMenu("Scenario");
		scenarioDescription = new JMenuItem("Description");
		scenarioDescription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				editDescription();
			}
		});
		scenarioMenu.add(scenarioDescription);
		startGame = new JMenuItem("Start game");
		startGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GameWrapper game = GameWrapper.findGame(editor.getGameData());
				game.setPlaceGoldSpecials(false);
				game.setGameStarted(true);
				editor.getMap().setShowEmbellishments(true);
			}
		});
		scenarioMenu.add(startGame);
		undoStartGame = new JMenuItem("Undo start game");
		undoStartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GameWrapper game = GameWrapper.findGame(editor.getGameData());
				game.setPlaceGoldSpecials(true);
				game.setGameStarted(false);
				editor.getMap().setShowEmbellishments(false);
			}
		});
		scenarioMenu.add(undoStartGame);
		scenarioRegenerateRandomNumbers = new JCheckBox("Regenerate random numbers");
		scenarioRegenerateRandomNumbers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				updateRegenerateRandomNumbers();
			}
		});
		scenarioMenu.add(scenarioRegenerateRandomNumbers);
		scenarioRandomGoldSpecialPlacement = new JCheckBox("Random visitor/mission placement");
		scenarioRandomGoldSpecialPlacement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				updateRandomGoldSpecialPlacement();
			}
		});
		scenarioMenu.add(scenarioRandomGoldSpecialPlacement);
		scenarioAddNewQuests = new JCheckBox("Add new quests from quest folder");
		scenarioAddNewQuests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				updateAddingNewQuests();
			}
		});
		scenarioMenu.add(scenarioAddNewQuests);
		scenarioRebuildQuestDeck = new JCheckBox("Rebuild quest deck");
		scenarioRebuildQuestDeck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				updateRebuildQuestDeck();
			}
		});
		scenarioMenu.add(scenarioRebuildQuestDeck);
		scenarioShuffleQuestDeck = new JCheckBox("Reshuffle quest deck");
		scenarioShuffleQuestDeck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				updateShuffleQuestDeck();
			}
		});
		scenarioMenu.add(scenarioShuffleQuestDeck);
		menu.add(scenarioMenu);
		JMenu optionMenu = new JMenu("Options");
		final JCheckBoxMenuItem toggleLookAndFeel = new JCheckBoxMenuItem("Cross Platform Look and Feel",prefs.getBoolean(MetalLookAndFeel));
		toggleLookAndFeel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(MetalLookAndFeel,toggleLookAndFeel.isSelected());
				updateLookAndFeel();
			}
		});
		optionMenu.add(toggleLookAndFeel);
		JPanel gameButtons = new JPanel(new GridLayout(1,1));
		gameOptions = new JButton("Game Options");
		gameOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				HostGameSetupDialog setup = new HostGameSetupDialog(new JFrame(),"Game Options",editor.getGameData());
				setup.loadPrefsFromData();
				setup.setVisible(true);
			}
		});
		gameButtons.add(gameOptions);
		
		optionMenu.add(getTilesOptionsPanel());
		optionMenu.add(getChitOptionsPanel());
		optionMenu.add(getCharacterChitOptionsPanel());
		optionMenu.add(gameButtons);
		menu.add(optionMenu);
		return menu;
	}
	private JPanel getTilesOptionsPanel() {
		int selected = prefs.getInt(TilesDisplayStyle);
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createTitledBorder("Tiles Style"));
		ButtonGroup group = new ButtonGroup();
		JRadioButton classicTilesOption = new JRadioButton("Classic");
		classicTilesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(TilesDisplayStyle,TileComponent.DISPLAY_TILES_STYLE_CLASSIC);
				updateTilesStyle();
			}
		});
		if (selected == TileComponent.DISPLAY_TILES_STYLE_CLASSIC) {
			classicTilesOption.setSelected(true);
		}
		group.add(classicTilesOption);
		panel.add(classicTilesOption);
		JRadioButton legendaryTilesOption = new JRadioButton("Legendary Realm");
		legendaryTilesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(TilesDisplayStyle,TileComponent.DISPLAY_TILES_STYLE_LEGENDARY);
				updateTilesStyle();
			}
		});
		if (selected == TileComponent.DISPLAY_TILES_STYLE_LEGENDARY) {
			legendaryTilesOption.setSelected(true);
		}
		group.add(legendaryTilesOption);
		panel.add(legendaryTilesOption);
		JRadioButton legendaryWithIconsTilesOption = new JRadioButton("Legendary Realm (with Icons)");
		legendaryWithIconsTilesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(TilesDisplayStyle,TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS);
				updateTilesStyle();
			}
		});
		if (selected == TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS) {
			legendaryWithIconsTilesOption.setSelected(true);
		}
		group.add(legendaryWithIconsTilesOption);
		panel.add(legendaryWithIconsTilesOption);
		return panel;
	}
	private JPanel getChitOptionsPanel() {
		int selected = prefs.getInt(ChitDisplayStyle);
		JPanel panel = new JPanel(new GridLayout(4,1));
		panel.setBorder(BorderFactory.createTitledBorder("Game Chits"));
		ButtonGroup group = new ButtonGroup();
		classicChitsOption = new JRadioButton("Classic Chits");
		classicChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(ChitDisplayStyle,RealmComponent.DISPLAY_STYLE_CLASSIC);
				setChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == RealmComponent.DISPLAY_STYLE_CLASSIC) {
			classicChitsOption.setSelected(true);
		}
		group.add(classicChitsOption);
		panel.add(classicChitsOption);
		colorChitsOption = new JRadioButton("Color Chits");
		colorChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(ChitDisplayStyle,RealmComponent.DISPLAY_STYLE_COLOR);
				setChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == RealmComponent.DISPLAY_STYLE_COLOR) {
			colorChitsOption.setSelected(true);
		}
		group.add(colorChitsOption);
		panel.add(colorChitsOption);
		frenzelChitsOption = new JRadioButton("Remodeled Chits");
		frenzelChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(ChitDisplayStyle,RealmComponent.DISPLAY_STYLE_FRENZEL);
				setChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == RealmComponent.DISPLAY_STYLE_FRENZEL) {
			frenzelChitsOption.setSelected(true);
		}
		group.add(frenzelChitsOption);
		panel.add(frenzelChitsOption);
		legendaryChitsOption = new JRadioButton("Legendary Chits");
		legendaryChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(ChitDisplayStyle,RealmComponent.DISPLAY_STYLE_LEGENDARY);
				setChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == RealmComponent.DISPLAY_STYLE_LEGENDARY) {
			legendaryChitsOption.setSelected(true);
		}
		legendaryChitsOption.setEnabled(false);
		group.add(legendaryChitsOption);
		panel.add(legendaryChitsOption);
		return panel;
	}
	private JPanel getCharacterChitOptionsPanel() {
		int selected = prefs.getInt(CharacterChitDisplayStyle);
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.setBorder(BorderFactory.createTitledBorder("Character Game Chits Style"));
		ButtonGroup group = new ButtonGroup();
		classicCharacterChitsOption = new JRadioButton("Classic");
		classicCharacterChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(CharacterChitDisplayStyle,CharacterChitComponent.DISPLAY_STYLE_CLASSIC);
				setCharacterChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == CharacterChitComponent.DISPLAY_STYLE_CLASSIC) {
			classicCharacterChitsOption.setSelected(true);
		}
		group.add(classicCharacterChitsOption);
		panel.add(classicCharacterChitsOption);
		legendaryClassicCharacterChitsOption = new JRadioButton("Legendary (classic hidden)");
		legendaryClassicCharacterChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(CharacterChitDisplayStyle,CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC);
				setCharacterChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC) {
			legendaryClassicCharacterChitsOption.setSelected(true);
		}
		group.add(legendaryClassicCharacterChitsOption);
		panel.add(legendaryClassicCharacterChitsOption);
		legendaryCharacterChitsOption = new JRadioButton("Legendary");
		legendaryCharacterChitsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				prefs.set(CharacterChitDisplayStyle,CharacterChitComponent.DISPLAY_STYLE_LEGENDARY);
				setCharacterChitDisplayStyle();
				reinitMap();
			}
		});
		if (selected == CharacterChitComponent.DISPLAY_STYLE_LEGENDARY) {
			legendaryCharacterChitsOption.setSelected(true);
		}
		group.add(legendaryCharacterChitsOption);
		panel.add(legendaryCharacterChitsOption);
		return panel;
	}
	private boolean closeGame() {
		if (editor == null) return true;
		if (validateOkayToClose("Close Game")) {
			editor.setVisible(false);
			desktop.remove(editor);
			editor = null;
			updateControls();
			return true;
		}
		return false;
	}
	private boolean validateOkayToClose(String title) {
		if (editor != null && editor.getGameData().isModified()) {
			int ret = JOptionPane.showConfirmDialog(this,"The current game hasn't been saved.  Save now?",title,JOptionPane.YES_NO_CANCEL_OPTION);
			if (ret==JOptionPane.YES_OPTION) {
				saveGame(false);
			}
			else if (ret==JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		return true;
	}
	private void openGame() {
		JFileChooser chooser;
		String lastSaveGame = prefs.get(PreferredFilePath);
		if (lastSaveGame!=null) {
			String filePath = FileUtilities.getFilePathString(new File(lastSaveGame),false,false);
			chooser = new JFileChooser(new File(filePath));
		}
		else {
			chooser = new JFileChooser();
		}
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(saveGameFileFilter);
		if (chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
			File file = FileUtilities.fixFileExtension(chooser.getSelectedFile(),".rsgame");
			prefs.set(PreferredFilePath,file.getAbsolutePath());
			GameData gameData = new GameData();
			gameData.zipFromFile(file);
//			gameData.setTracksChanges(true);
			addGame(FileUtilities.getFilename(file,true),gameData);
		}
		fileExists = true;
		updateControls();
	}
	private File queryFileName() {
		JFileChooser chooser;
		String lastSaveGame = prefs.get(PreferredFilePath);
		if (lastSaveGame!=null) {
			String filePath = FileUtilities.getFilePathString(new File(lastSaveGame),false,false);
			chooser = new JFileChooser(new File(filePath));
			chooser.setSelectedFile(new File(lastSaveGame));
		}
		else {
			chooser = new JFileChooser();
		}
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(saveGameFileFilter);
		if (chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
			File file = FileUtilities.fixFileExtension(chooser.getSelectedFile(),".rsgame");
			prefs.set(PreferredFilePath,file.getAbsolutePath());
			return file;
		}
		return null;
	}
	private void saveGame(boolean queryFilename) {
		File file;
		String lastSaveGame = prefs.get(PreferredFilePath);
		if (queryFilename || lastSaveGame==null) {
			file = queryFileName();
		}
		else {
			file = new File(lastSaveGame);
		}
		if (file!=null) {
			editor.setTitle(FileUtilities.getFilename(file,true));
			editor.getGameData().zipToFile(file);
			editor.getGameData().commit();
			fileExists = true;
			updateControls();
		}
	}
	private void addGame(String title,GameData gameData) {
		if (editor!=null) {
			closeGame();
		}
		
		editor = new RealmGameEditor(this,title,gameData);
		editor.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		editor.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				closeGame();
			}
		});
		desktop.add(editor);
		editor.setVisible(true);
		try {
			editor.setSelected(true);
			editor.setMaximum(true);
		}
		catch(PropertyVetoException ex) {
			ex.printStackTrace();
		}
	}
	private void close() {
		savePrefs();
		setVisible(false);
		System.exit(0);
	}
	public static void main(String[] args) {
		RealmCharacterBuilderModel.loadAllCustomCharacters();
		RealmUtility.setupTextType();
		final RealmGmFrame frame = new RealmGmFrame();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				frame.close();
			}
		});
		frame.setVisible(true);
	}
	
	public void newGame() {
		RealmUtility.resetGame();
		
		FlashingButton.setFlashEnabled(false); // at the start, disable all flashing buttons
		
		RealmSpeakInit init = new RealmSpeakInit();
		
		// Load XML from resources
		init.loadData();
		
		// Get the host prefs
		HostGameSetupDialog prefChooser = new HostGameSetupDialog(this,"Host New Game",init.getGameData());
		
		// Setup the local prefs (if any)
		prefChooser.loadPrefsFromLocalConfiguration();
		
		prefChooser.setVisible(true);
		if (prefChooser.getDidStart()) {
			prefChooser.savePrefsToLocalConfiguration();
			
			// Setup the random number generator
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(init.getGameData());
			if (hostPrefs.hasPref(Constants.RANDOM_R250_521)) {
				RandomNumber.setRandomNumberGenerator(RandomNumberType.R250_521);
			}
			else if (hostPrefs.hasPref(Constants.RANDOM_MERSENNE_TWISTER)) {
				RandomNumber.setRandomNumberGenerator(RandomNumberType.MersenneTwister);
			}
			else if (hostPrefs.hasPref(Constants.RANDOM_ON_THE_FLY)) {
				RandomNumber.setRandomNumberGenerator(RandomNumberType.RandomOnTheFly);
			}
			else {
				RandomNumber.setRandomNumberGenerator(RandomNumberType.System);
			}
			if (hostPrefs.hasPref(Constants.RANDOM_GEN_FOR_SETUP)) {
				RandomNumber.setUseRandomNumberGeneratorForSetup(true);
			} else {
				RandomNumber.setUseRandomNumberGeneratorForSetup(false);
			}
			
			// Make sure there is a DieRoller logger
			DieRoller.setDieRollerLog(RealmUtility.getDieRollerLog(init.getGameData()));
			
			// Do all the pregame work
			init.buildGame();
			
			addGame("<empty>",init.getGameData());
			fileExists = false;
			updateControls();
		}
	}
}