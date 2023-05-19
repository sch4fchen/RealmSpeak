package com.robin.magic_realm.RealmQuestBuilder;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import com.robin.game.objects.*;
import com.robin.general.io.*;
import com.robin.general.swing.*;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.EmptyCardComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.GameFileFilters;
import com.robin.magic_realm.components.utility.RealmLoader;
import com.robin.magic_realm.components.utility.RealmUtility;

public class QuestBuilderFrame extends JFrame {
	public static final String LAST_DIR = "last_dir";

	public static Font HeaderFont = new Font("Arial", Font.BOLD, 18);
	public static Font LabelFont = new Font("Arial", Font.BOLD, 12);

	private Quest quest;
	private GameData realmSpeakData;

	private File lastQuestFile;
	private File lastQuestFilePath;

	private JPanel cardPanel;
	private JTextField questName;
	private SuggestionTextArea questDescription;

	private JButton testNowButton;
	private JButton testNowBerserkerButton;
	private JCheckBox testingFlag;
	private JCheckBox brokenFlag;

	private JTable questRulesTable;
	private QuestTableEditorPanel questRulesPanel;

	private JTable locationTable;
	private QuestTableEditorPanel locationPanel;

	private JTable minorCharacterTable;
	private QuestTableEditorPanel minorCharacterPanel;
	
	private JTable counterTable;
	private QuestTableEditorPanel counterPanel;

	// private QuestView questView;
	private QuestStepView questStepView;

	private JTabbedPane questSteps;

	private Action addStep;
	private Action deleteStep;
	private Action moveStepUp;
	private Action moveStepDown;
	private JCheckBox autoConnectStepOption;

	private PreferenceManager prefs;

	private JCheckBox bookOfQuests;
	private JCheckBox bookOfQuestsEvent;
	private JCheckBox superRealm;
	private JCheckBox questingTheRealm;
	private JCheckBox allPlayQuestOption;
	private JCheckBox secretQuestOption;
	private IntegerField cardCount;
	private IntegerField vpReward;
	private JCheckBox guildQuestOption;
	private JRadioButton magicGuildQuestOption;
	private JRadioButton thievesGuildQuestOption;
	private JRadioButton fightersGuildQuestOption;

	private JCheckBox originalVariant;
	private JCheckBox pruittsMonstersVariant;
	private JCheckBox expansionOneVariant;
	private JCheckBox superRealmVariant;

	private JCheckBox singleBoard;
	private JCheckBox doubleBoard;
	private JCheckBox tripleBoard;

	private JCheckBox fighterCharacterOption;
	private JCheckBox magicUserCharacterOption;
	private JCheckBox maleCharacterOption;
	private JCheckBox femaleCharacterOption;
	private JCheckBox specificCharacterListOption;
	private JTextField specificCharacterListField;
	private JButton specificCharacterHelperButton;
	private JCheckBox multipleUseOption;

	private JTable ruleLimitationTable;

	private boolean blockSaveOtherOptions = false;
	QuestTesterFrame soleTester;

	private ActionListener cardUpdateListener = new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			updateCard();
		}
	};
	private CaretListener cardUpdateCaretListener = new CaretListener() {
		public void caretUpdate(CaretEvent e) {
			updateCard();
		}
	};

	public QuestBuilderFrame() {
		setIconImage(IconFactory.findIcon("images/tab/record.gif").getImage());
		RealmLoader loader = new RealmLoader();
		realmSpeakData = loader.getData();
		initComponents();
		initNewQuest();
		updateControls();
		prefs = new PreferenceManager("QuestBuilder", "QuestBuilder.cfg") {
			protected void createDefaultPreferences(Properties props) {
				props.put(LAST_DIR, System.getProperty("user.home"));
			}
		};
		prefs.loadPreferences();
		String lastDir = prefs.get(LAST_DIR);
		if (lastDir == null)
			lastDir = ".";
		lastQuestFilePath = new File(lastDir);
	}

	public void exitApp() {
		// Should check all GameData windows, and verify that changes have been saved
		prefs.set(LAST_DIR, lastQuestFilePath.getPath());
		prefs.savePreferences();
		setVisible(false);
		dispose();
		System.exit(0);
	}

	private boolean setGameDataFile() {
		JFileChooser chooser = new JFileChooser(new File("./"));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(GameFileFilters.createGameDataFileFilter());
		if (chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
			RealmLoader.DATA_PATH = chooser.getSelectedFile().toPath().toString();
			return true;
		}
		return false;
	}
	
	private void initNewQuest() {
		GameData gameData = new GameData(Quest.GAME_DATA_NAME);
		quest = new Quest(gameData.createNewObject());
		quest.init();
		quest.setName("");
		quest.setDescription("");
		quest.createQuestStep(false);
		readQuest();
	}

	private void readQuest() {
		blockSaveOtherOptions = true;
		questName.setText(quest.getName());
		questDescription.setText(quest.getDescription());
		questDescription.setFont(QuestGuiConstants.QuestDescriptionFont);
		rebuildSteps();

		ruleLimitationTable.clearSelection();
		((RuleLimitationTableModel) ruleLimitationTable.getModel()).setQuest(quest);
		ruleLimitationTable.revalidate();
		
		locationTable.clearSelection();
		((LocationTableModel) locationTable.getModel()).setQuest(quest);
		locationTable.revalidate();
		
		minorCharacterTable.clearSelection();
		((MinorCharacterTableModel) minorCharacterTable.getModel()).setQuest(quest);
		minorCharacterTable.revalidate();
		
		counterTable.clearSelection();
		((CounterTableModel) counterTable.getModel()).setQuest(quest);
		counterTable.revalidate();

		readOtherOptions();

		updateControls();
		updateCard();
		cardPanel.removeAll();
		cardPanel.add(RealmComponent.getRealmComponent(quest.getGameObject()));

		blockSaveOtherOptions = false;

		quest.getGameData().setModified(false);
	}

	private void readOtherOptions() {
		testingFlag.setSelected(quest.getBoolean(QuestConstants.FLAG_TESTING));
		brokenFlag.setSelected(quest.getBoolean(QuestConstants.FLAG_BROKEN));

		bookOfQuests.setSelected(quest.getBoolean(QuestConstants.WORKS_WITH_BOQ));
		bookOfQuestsEvent.setSelected(quest.getBoolean(QuestConstants.BOQ_EVENT));
		questingTheRealm.setSelected(quest.getBoolean(QuestConstants.WORKS_WITH_QTR));
		superRealm.setSelected(quest.getBoolean(QuestConstants.WORKS_WITH_SR));
		cardCount.setText(quest.getString(QuestConstants.CARD_COUNT));
		vpReward.setText(quest.getString(QuestConstants.VP_REWARD));
		allPlayQuestOption.setSelected(quest.getBoolean(QuestConstants.QTR_ALL_PLAY));
		secretQuestOption.setSelected(quest.getBoolean(QuestConstants.QTR_SECRET_QUEST));

		guildQuestOption.setSelected(quest.getBoolean(QuestConstants.FOR_FIGHTERS_GUILD) || quest.getBoolean(QuestConstants.FOR_THIEVES_GUILD) || quest.getBoolean(QuestConstants.FOR_MAGIC_GUILD));
		fightersGuildQuestOption.setSelected(quest.getBoolean(QuestConstants.FOR_FIGHTERS_GUILD));
		thievesGuildQuestOption.setSelected(quest.getBoolean(QuestConstants.FOR_THIEVES_GUILD));
		magicGuildQuestOption.setSelected(quest.getBoolean(QuestConstants.FOR_MAGIC_GUILD));

		singleBoard.setSelected(quest.getBoolean(QuestConstants.SINGLE_BOARD));
		doubleBoard.setSelected(quest.getBoolean(QuestConstants.DOUBLE_BOARD));
		tripleBoard.setSelected(quest.getBoolean(QuestConstants.TRIPLE_BOARD));

		originalVariant.setSelected(quest.getBoolean(QuestConstants.VARIANT_ORIGINAL));
		pruittsMonstersVariant.setSelected(quest.getBoolean(QuestConstants.VARIANT_PRUITTS));
		expansionOneVariant.setSelected(quest.getBoolean(QuestConstants.VARIANT_EXP1));

		fighterCharacterOption.setSelected(quest.getBoolean(QuestConstants.CHARACTER_FIGHTER));
		magicUserCharacterOption.setSelected(quest.getBoolean(QuestConstants.CHARACTER_MAGIC));
		maleCharacterOption.setSelected(quest.getBoolean(QuestConstants.CHARACTER_MALE));
		femaleCharacterOption.setSelected(quest.getBoolean(QuestConstants.CHARACTER_FEMALE));
		
		multipleUseOption.setSelected(quest.getBoolean(QuestConstants.QUEST_MULTIPLE_USE));

		specificCharacterListOption.setSelected(quest.getBoolean(QuestConstants.CHARACTER_SPEC_REGEX));
		specificCharacterListField.setText(specificCharacterListOption.isSelected() ? quest.getString(QuestConstants.CHARACTER_SPEC_REGEX) : "");
	}

	private void saveOtherOptions() {
		if (blockSaveOtherOptions)
			return;
		quest.setBoolean(QuestConstants.FLAG_TESTING, testingFlag.isSelected());
		quest.setBoolean(QuestConstants.FLAG_BROKEN, brokenFlag.isSelected());

		quest.setBoolean(QuestConstants.WORKS_WITH_BOQ, bookOfQuests.isSelected());
		quest.setBoolean(QuestConstants.WORKS_WITH_QTR, questingTheRealm.isSelected());
		quest.setBoolean(QuestConstants.WORKS_WITH_SR, superRealm.isSelected());
		quest.setBoolean(QuestConstants.BOQ_EVENT, bookOfQuestsEvent.isSelected());
		quest.setBoolean(QuestConstants.QTR_ALL_PLAY, allPlayQuestOption.isSelected());
		quest.setBoolean(QuestConstants.QTR_SECRET_QUEST, secretQuestOption.isSelected());
		quest.setInt(QuestConstants.CARD_COUNT, cardCount.getInt());
		quest.setInt(QuestConstants.VP_REWARD, vpReward.getInt());

		quest.setBoolean(QuestConstants.FOR_FIGHTERS_GUILD, guildQuestOption.isSelected() && fightersGuildQuestOption.isSelected());
		quest.setBoolean(QuestConstants.FOR_THIEVES_GUILD, guildQuestOption.isSelected() && thievesGuildQuestOption.isSelected());
		quest.setBoolean(QuestConstants.FOR_MAGIC_GUILD, guildQuestOption.isSelected() && magicGuildQuestOption.isSelected());

		quest.setBoolean(QuestConstants.SINGLE_BOARD, singleBoard.isSelected());
		quest.setBoolean(QuestConstants.DOUBLE_BOARD, doubleBoard.isSelected());
		quest.setBoolean(QuestConstants.TRIPLE_BOARD, tripleBoard.isSelected());

		quest.setBoolean(QuestConstants.VARIANT_ORIGINAL, originalVariant.isSelected());
		quest.setBoolean(QuestConstants.VARIANT_PRUITTS, pruittsMonstersVariant.isSelected());
		quest.setBoolean(QuestConstants.VARIANT_EXP1, expansionOneVariant.isSelected());

		quest.setBoolean(QuestConstants.CHARACTER_FIGHTER, fighterCharacterOption.isSelected());
		quest.setBoolean(QuestConstants.CHARACTER_MAGIC, magicUserCharacterOption.isSelected());
		quest.setBoolean(QuestConstants.CHARACTER_MALE, maleCharacterOption.isSelected());
		quest.setBoolean(QuestConstants.CHARACTER_FEMALE, femaleCharacterOption.isSelected());
		quest.setString(QuestConstants.CHARACTER_SPEC_REGEX, specificCharacterListOption.isSelected() ? specificCharacterListField.getText() : null);
		
		quest.setBoolean(QuestConstants.QUEST_MULTIPLE_USE, multipleUseOption.isSelected());
	}

	private void rebuildSteps() {
		questSteps.removeAll();
		for (QuestStep step : quest.getSteps()) {
			QuestStepPanel stepPanel = new QuestStepPanel(this, questStepView, realmSpeakData, quest, step);
			stepPanel.setBorder(BorderFactory.createLineBorder(Color.green, 5));
			questSteps.addTab(String.valueOf(step.getId()), stepPanel);
		}
	}

	private void loadQuest() {
		JFileChooser chooser = new JFileChooser(lastQuestFilePath);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(questGameFileFilter);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file != null) {
				GameData data = new GameData();
				data.ignoreRandomSeed = true;
				if (data.zipFromFile(file)) {
					quest = new Quest(data.getGameObjects().iterator().next());
					quest.autoRepair(); // Just in case
					readQuest();
					setFile(file);
				}
			}
		}
	}

	protected FileFilter questGameFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || (f.isFile() && f.getPath().endsWith("rsqst"));
		}

		public String getDescription() {
			return "RealmSpeak Quest Files (*.rsqst)";
		}
	};
	
	private void saveAsQuest() {
		saveQuest(lastQuestFile, false);
	}

	private void saveQuest() {
		saveOtherOptions();
		saveQuest(lastQuestFile, true);
	}

	private void saveQuest(File selectedFile, boolean saveWithoutPrompt) {
		if (selectedFile == null || !saveWithoutPrompt) {
			JFileChooser chooser = new JFileChooser(selectedFile == null ? lastQuestFilePath : selectedFile) {
				@Override
				public void approveSelection() {
					if (getDialogType() == SAVE_DIALOG) {
						File selectedFile = getSelectedFile();
						if ((selectedFile != null) && selectedFile.exists()) {
							int response = JOptionPane.showConfirmDialog(this, "The file " + selectedFile.getName() + " already exists. Do you want to replace the existing file?", "Overwrite file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
							if (response != JOptionPane.YES_OPTION)
								return;
						}
					}

					super.approveSelection();
				}
			};
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(questGameFileFilter);
			if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				selectedFile = FileUtilities.fixFileExtension(chooser.getSelectedFile(), QuestConstants.DEFAULT_EXTENSION);
			}
			else
				return;
		}
		quest.autoRepair();
		if (quest.getGameData().zipToFile(selectedFile)) {
			setFile(selectedFile);
		}
	}

	private void setFile(File file) {
		lastQuestFile = file;
		String path = file.getPath();
		if (!path.endsWith(File.separator)) {
			int pathEnd = path.lastIndexOf(File.separator);
			if (pathEnd != -1) {
				path = path.substring(0, pathEnd + 1);
			}
		}
		lastQuestFilePath = new File(path);
	}

	private boolean canOverwriteQuest(String title) {
		if (quest.getGameData().isModified()) {
			int ret = JOptionPane.showConfirmDialog(this, "Save changes before continuing?", title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (ret == JOptionPane.YES_OPTION) {
				saveQuest();
			}
			else if (ret == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		return true;
	}

	private void updateControls() {
		counterPanel.getEditAction().setEnabled(counterTable.getSelectedRowCount() == 1);
		counterPanel.getDeleteAction().setEnabled(counterTable.getSelectedRowCount() == 1);
		
		minorCharacterPanel.getEditAction().setEnabled(minorCharacterTable.getSelectedRowCount() == 1);
		minorCharacterPanel.getDeleteAction().setEnabled(minorCharacterTable.getSelectedRowCount() == 1);

		locationPanel.getEditAction().setEnabled(locationTable.getSelectedRowCount() == 1);
		locationPanel.getDeleteAction().setEnabled(locationTable.getSelectedRowCount() == 1);

		questRulesPanel.getEditAction().setEnabled(questRulesTable.getSelectedRowCount() == 1);
		questRulesPanel.getDeleteAction().setEnabled(questRulesTable.getSelectedRowCount() == 1);

		deleteStep.setEnabled(quest.getSteps().size() > 0);
		moveStepUp.setEnabled(quest.getSteps().size() > 1);
		moveStepDown.setEnabled(quest.getSteps().size() > 1);

		questStepView.updateSteps(quest.getSteps());

		QuestStepPanel stepPanel = (QuestStepPanel) questSteps.getSelectedComponent();
		questStepView.setSelectedStep(stepPanel.getStep());

		fightersGuildQuestOption.setEnabled(guildQuestOption.isSelected());
		magicGuildQuestOption.setEnabled(guildQuestOption.isSelected());
		thievesGuildQuestOption.setEnabled(guildQuestOption.isSelected());
	}

	public void updateCard() {
		saveOtherOptions();
		cardPanel.setVisible(quest.getBoolean(QuestConstants.WORKS_WITH_QTR) || quest.getBoolean(QuestConstants.WORKS_WITH_SR));
		cardPanel.repaint();
	}

	private void initComponents() {
		setTitle("RealmSpeak Quest Builder");
		setSize(1024, 768);

		setJMenuBar(buildMenuBar());

		JSplitPane mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		JPanel topPanel = new JPanel(new GridLayout(1, 2));
		topPanel.add(buildQuestPanel());
		topPanel.add(buildQuestOptionsPane());
		mainPanel.add(topPanel);
		mainPanel.add(buildQuestStepPanel());

		add(mainPanel);
	}

	private JMenuBar buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem newQuestItem = new JMenuItem("New");
		newQuestItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (canOverwriteQuest("New Quest")) {
					initNewQuest();
				}
			}
		});
		newQuestItem.setMnemonic(KeyEvent.VK_N);
		newQuestItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		fileMenu.add(newQuestItem);
		JMenuItem openQuestItem = new JMenuItem("Open");
		openQuestItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (canOverwriteQuest("Open Quest")) {
					loadQuest();
				}
			}
		});
		fileMenu.add(openQuestItem);
		fileMenu.add(new JSeparator());
		JMenuItem saveQuestItem = new JMenuItem("Save");
		saveQuestItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveQuest();
			}
		});
		saveQuestItem.setMnemonic(KeyEvent.VK_S);
		saveQuestItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		fileMenu.add(saveQuestItem);
		JMenuItem saveAsQuestItem = new JMenuItem("Save As...");
		saveAsQuestItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveAsQuest();
			}
		});
		fileMenu.add(saveAsQuestItem);
		fileMenu.add(new JSeparator());
		JMenuItem loadGameDataFile = new JMenuItem("Load GameData file");
		loadGameDataFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (canOverwriteQuest("Load new game data?")) {
					boolean gameLoaded = setGameDataFile();
					if (!gameLoaded) return;
					RealmLoader loader = new RealmLoader();
					realmSpeakData = loader.getData();
					initNewQuest();
					updateControls();
				}
			}
		});
		fileMenu.add(loadGameDataFile);
		fileMenu.add(new JSeparator());
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (canOverwriteQuest("Exit Quest Builder")) {
					exitApp();
				}
			}
		});
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);

		JMenu toolsMenu = new JMenu("Tools");
		JMenuItem viewDeckItemQtR = new JMenuItem("View Quest Cards Deck for QtR");
		viewDeckItemQtR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				viewDeck(Constants.QuestDeckMode.QtR);
			}
		});
		viewDeckItemQtR.setMnemonic(KeyEvent.VK_V);
		viewDeckItemQtR.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		toolsMenu.add(viewDeckItemQtR);
		JMenuItem viewDeckItemBoQ = new JMenuItem("View Quest Cards Deck for BoQ");
		viewDeckItemBoQ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				viewDeck(Constants.QuestDeckMode.BoQ);
			}
		});
		viewDeckItemBoQ.setMnemonic(KeyEvent.VK_B);
		viewDeckItemBoQ.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		toolsMenu.add(viewDeckItemBoQ);
		JMenuItem viewDeckItemGQ = new JMenuItem("View Quest Cards Deck for Guild Quests");
		viewDeckItemGQ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				viewDeck(Constants.QuestDeckMode.GQ);
			}
		});
		viewDeckItemGQ.setMnemonic(KeyEvent.VK_G);
		viewDeckItemGQ.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		toolsMenu.add(viewDeckItemGQ);
		JMenuItem viewDeckItemSR = new JMenuItem("View Quest Cards Deck for Super Realm Quests");
		viewDeckItemSR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				viewDeck(Constants.QuestDeckMode.SR);
			}
		});
		viewDeckItemSR.setMnemonic(KeyEvent.VK_R);
		viewDeckItemSR.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		toolsMenu.add(viewDeckItemSR);
		
		toolsMenu.add(new JSeparator());
		JMenuItem launchQuestTesterItem = new JMenuItem("Test Quest");
		launchQuestTesterItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				testQuest();
			}
		});
		launchQuestTesterItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
		toolsMenu.add(launchQuestTesterItem);
		menuBar.add(toolsMenu);

		return menuBar;
	}

	private void testQuest() {
		testQuest(null);
	}
	private void testQuest(String charName) {
		if (soleTester != null) {
			soleTester.setVisible(false);
			soleTester.dispose();
		}
		soleTester = new QuestTesterFrame(quest,charName);
		if (soleTester.ready) {
			soleTester.setLocationRelativeTo(this);
			soleTester.setVisible(true);
		}
	}

	private void viewDeck(Constants.QuestDeckMode mode) {
		System.setProperty("questFolder", lastQuestFilePath.getAbsolutePath());
		ArrayList<Quest> quests = QuestLoader.loadAllQuestsFromQuestFolder();
		ArrayList<Quest> questCards = new ArrayList<Quest>();
		switch (mode) {
		case QtR:
			for (Quest quest : quests) {
				if (quest.getBoolean(QuestConstants.WORKS_WITH_QTR) || quest.getBoolean(QuestConstants.WORKS_WITH_SR)) {
					questCards.add(quest);
				}
			}
			break;
		case BoQ:
			for (Quest quest : quests) {
				if (quest.getBoolean(QuestConstants.WORKS_WITH_BOQ)) {
					questCards.add(quest);
				}
			}
			break;
		case GQ:
			for (Quest quest : quests) {
				if (quest.getGuild()!=null) {
					questCards.add(quest);
				}
			}
			break;
		}
		QuestDeckViewer viewer = new QuestDeckViewer(this, questCards, mode);
		viewer.setLocationRelativeTo(this);
		viewer.setVisible(true);
		
		Quest sel = viewer.getSelectedQuest();
		if (sel!=null) {
			if (canOverwriteQuest("Open Quest")) { 
				quest = sel;
				quest.autoRepair(); // Just in case
				File file = new File(quest.filepath);
				setFile(file);
				readQuest();
			}
		}
	}

	private JPanel buildQuestPanel() {
		JPanel questPanel = new JPanel(new BorderLayout());
		Box line;
		UniformLabelGroup group = new UniformLabelGroup();
		group.setLabelFont(LabelFont);

		Box form = Box.createVerticalBox();
		form.add(Box.createVerticalStrut(10));

		line = group.createLabelLine("Quest Name");
		questName = new JTextField();
		questName.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				quest.setName(questName.getText());
				updateCard();
			}
		});
		ComponentTools.lockComponentSize(questName, 300, 25);
		line.add(questName);
		line.add(Box.createHorizontalGlue());
		form.add(line);
		form.add(Box.createVerticalStrut(10));

		line = group.createLabelLine("Quest Description");
		line.setToolTipText("Placeholder "+Constants.CHARACTERS_NAME_PLACEHOLDER+" will be replaced with the characters name.");
		line.add(Box.createHorizontalGlue());
		form.add(line);

		line = Box.createHorizontalBox();
		cardPanel = new JPanel();
		cardPanel.add(new EmptyCardComponent());
		line.add(cardPanel);
		line.add(Box.createHorizontalStrut(10));
		questDescription = new SuggestionTextArea(10, 40);
		questDescription.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent ev) {
				questDescription.setWords(quest.getLocationTags());
			}

			public void focusLost(FocusEvent ev) {
				quest.setDescription(questDescription.getText());
			}
		});
		questDescription.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				quest.setDescription(questDescription.getText());
				updateCard();
			}
		});
		questDescription.setLineWrap(true);
		questDescription.setWrapStyleWord(true);
		line.add(new JScrollPane(questDescription));
		line.add(Box.createHorizontalStrut(10));
		form.add(line);
		form.add(Box.createVerticalStrut(5));

		line = Box.createHorizontalBox();
		testNowButton = new JButton("Test Now!");
		testNowButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				testQuest();
			}
		});
		line.add(Box.createHorizontalGlue());
		line.add(testNowButton);
		line.add(Box.createHorizontalGlue());
		testNowBerserkerButton = new JButton("Test Now! (Berserker)");
		testNowBerserkerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				testQuest("Berserker");
			}
		});
		line.add(testNowBerserkerButton);
		line.add(Box.createHorizontalGlue());
		testingFlag = new JCheckBox("Testing");
		testingFlag.addActionListener(cardUpdateListener);
		line.add(testingFlag);
		brokenFlag = new JCheckBox("Broken");
		brokenFlag.addActionListener(cardUpdateListener);
		line.add(brokenFlag);
		form.add(line);
		form.add(Box.createVerticalStrut(5));

		form.add(Box.createVerticalGlue());
		questPanel.add(form, BorderLayout.CENTER);

		return questPanel;
	}

	private JTabbedPane buildQuestOptionsPane() {
		JTabbedPane optionsPane = new JTabbedPane();
		optionsPane.addTab("Quest Diagram", buildQuestDiagramPanel());
		optionsPane.addTab("Game Limitations", buildLimitationCheckOptions());
		//		optionsPane.addTab("Rule Limitations", buildLimitationRules());
		buildLimitationRules(); // just so there are no NPEs
		//		optionsPane.addTab("Quest Rules", buildQuestRulesPanel());
		buildQuestRulesPanel(); // just so there are no NPEs
		optionsPane.addTab("Locations", buildLocationPanel());
		optionsPane.addTab("Minor Characters (+ability)", buildMinorCharacterPanel());
		optionsPane.addTab("Counters", buildCounterPanel());
		return optionsPane;
	}

	private JPanel buildLimitationCheckOptions() {
		JPanel panel = new JPanel(new GridLayout(1, 3));

		JPanel reallyLeft = new JPanel(new GridLayout(12, 1));
		reallyLeft.setBorder(BorderFactory.createTitledBorder("Quest Type"));
		bookOfQuests = new JCheckBox("Book of Quests");
		bookOfQuests.addActionListener(cardUpdateListener);
		reallyLeft.add(bookOfQuests);
		
		UniformLabelGroup groupBoQ = new UniformLabelGroup(31);
		Box lineBoQ = groupBoQ.createLine();
		bookOfQuestsEvent = new JCheckBox("Event");
		lineBoQ.add(bookOfQuestsEvent);
		reallyLeft.add(lineBoQ);
		
		superRealm = new JCheckBox("Super Realm");
		superRealm.addActionListener(cardUpdateListener);
		reallyLeft.add(superRealm);
		questingTheRealm = new JCheckBox("Questing the Realm");
		questingTheRealm.addActionListener(cardUpdateListener);
		reallyLeft.add(questingTheRealm);

		UniformLabelGroup groupQtR = new UniformLabelGroup();
		Box lineQtR = groupQtR.createLine();
		allPlayQuestOption = new JCheckBox("All Play");
		allPlayQuestOption.addActionListener(cardUpdateListener);
		lineQtR.add(allPlayQuestOption);
		lineQtR.add(Box.createHorizontalGlue());
		reallyLeft.add(lineQtR);

		lineQtR = groupQtR.createLine();
		secretQuestOption = new JCheckBox("Secret Quest");
		lineQtR.add(secretQuestOption);
		lineQtR.add(Box.createHorizontalGlue());
		reallyLeft.add(lineQtR);

		lineQtR = groupQtR.createLabelLine("# of Cards");
		cardCount = new IntegerField("1");
		ComponentTools.lockComponentSize(cardCount, 50, 22);
		lineQtR.add(cardCount);
		lineQtR.add(Box.createHorizontalGlue());
		reallyLeft.add(lineQtR);
		lineQtR = groupQtR.createLabelLine("VP Value");
		vpReward = new IntegerField("1");
		vpReward.addCaretListener(cardUpdateCaretListener);
		ComponentTools.lockComponentSize(vpReward, 50, 22);
		lineQtR.add(vpReward);
		lineQtR.add(Box.createHorizontalGlue());
		reallyLeft.add(lineQtR);

		guildQuestOption = new JCheckBox("Guild Quest:");
		guildQuestOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				updateControls();
			}
		});
		reallyLeft.add(guildQuestOption);
		ButtonGroup guildGroup = new ButtonGroup();
		Box boxGuilds = Box.createHorizontalBox();
		fightersGuildQuestOption = new JRadioButton("Fighters Guild", true);
		boxGuilds.add(Box.createHorizontalStrut(25));
		boxGuilds.add(fightersGuildQuestOption);
		reallyLeft.add(boxGuilds);
		guildGroup.add(fightersGuildQuestOption);

		boxGuilds = Box.createHorizontalBox();
		magicGuildQuestOption = new JRadioButton("Magic Guild");
		boxGuilds.add(Box.createHorizontalStrut(25));
		boxGuilds.add(magicGuildQuestOption);
		reallyLeft.add(boxGuilds);
		guildGroup.add(magicGuildQuestOption);

		boxGuilds = Box.createHorizontalBox();
		thievesGuildQuestOption = new JRadioButton("Thieves Guild");
		boxGuilds.add(Box.createHorizontalStrut(25));
		boxGuilds.add(thievesGuildQuestOption);
		reallyLeft.add(boxGuilds);
		guildGroup.add(thievesGuildQuestOption);

		panel.add(reallyLeft);

		JPanel left = new JPanel(new GridLayout(2, 1));
		Box variantBox = Box.createVerticalBox();
		variantBox.setBorder(BorderFactory.createTitledBorder("Game Variant Restrictions"));
		originalVariant = new JCheckBox("Original");
		variantBox.add(originalVariant);
		pruittsMonstersVariant = new JCheckBox("Pruitt's Monsters");
		variantBox.add(pruittsMonstersVariant);
		expansionOneVariant = new JCheckBox("Expansion One");
		variantBox.add(expansionOneVariant);
		superRealmVariant = new JCheckBox("Super Realm");
		variantBox.add(superRealmVariant);
		left.add(variantBox);

		Box boardSize = Box.createVerticalBox();
		boardSize.setBorder(BorderFactory.createTitledBorder("Board Size Limits"));
		singleBoard = new JCheckBox("Single Board");
		boardSize.add(singleBoard);
		doubleBoard = new JCheckBox("Double Board");
		boardSize.add(doubleBoard);
		tripleBoard = new JCheckBox("Triple Board");
		boardSize.add(tripleBoard);
		boardSize.add(Box.createVerticalGlue());
		left.add(boardSize);

		panel.add(left);

		Box right = Box.createVerticalBox();

		JPanel charLim = new JPanel(new GridLayout(6, 1));
		charLim.setMaximumSize(new Dimension(1000, 100));
		charLim.setBorder(BorderFactory.createTitledBorder("Character Limitations"));
		fighterCharacterOption = new JCheckBox("Fighters");
		charLim.add(fighterCharacterOption);
		magicUserCharacterOption = new JCheckBox("Magic Users");
		charLim.add(magicUserCharacterOption);
		maleCharacterOption = new JCheckBox("Male Characters");
		charLim.add(maleCharacterOption);
		femaleCharacterOption = new JCheckBox("Female Characters");
		charLim.add(femaleCharacterOption);

		specificCharacterListOption = new JCheckBox("Specific Characters");
		charLim.add(specificCharacterListOption);
		Box lineCharacters = Box.createHorizontalBox();
		specificCharacterListField = new JTextField();
		lineCharacters.add(specificCharacterListField);
		specificCharacterHelperButton = new JButton("...");
		specificCharacterHelperButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GamePool pool = new GamePool(realmSpeakData.getGameObjects());
				ArrayList<String> names = new ArrayList<>();
				for (GameObject go : pool.find("character")) {
					names.add(go.getName());
				}
				Collections.sort(names);
				RealmRegexHelper helper = new RealmRegexHelper(QuestBuilderFrame.this, specificCharacterListField.getText(), names);
				helper.setLocationRelativeTo(QuestBuilderFrame.this);
				helper.setVisible(true);
				specificCharacterListField.setText(helper.getText());
			}
		});
		lineCharacters.add(specificCharacterHelperButton);
		charLim.add(lineCharacters);

		right.add(charLim);
		right.add(Box.createVerticalGlue());
		
		JPanel clonedQuests = new JPanel(new GridLayout(1, 1));
		clonedQuests.setMaximumSize(new Dimension(1000, 100));
		clonedQuests.setBorder(BorderFactory.createTitledBorder("Multiple Use Quests (All-Play,BoQ)"));
		multipleUseOption = new JCheckBox("Multiple Use");
		clonedQuests.add(multipleUseOption);
		right.add(clonedQuests);
		right.add(Box.createVerticalGlue());
		
		panel.add(right);

		return panel;
	}

	private JPanel buildLimitationRules() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Rule Requirements (not implemented yet)"));
		ruleLimitationTable = new JTable(new RuleLimitationTableModel(quest, realmSpeakData));
		ruleLimitationTable.setFocusable(false);
		ruleLimitationTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				ruleLimitationTable.clearSelection();
			}
		});
		ComponentTools.lockColumnWidth(ruleLimitationTable, 0, 35);
		ComponentTools.lockColumnWidth(ruleLimitationTable, 1, 35);
		panel.add(new JScrollPane(ruleLimitationTable), BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildQuestRulesPanel() {
		questRulesTable = new JTable(new QuestRulesTableModel(quest));
		questRulesTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2) {
					questRulesPanel.edit();
				}
			}
		});
		questRulesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				updateControls();
			}
		});
		questRulesPanel = new QuestTableEditorPanel("QuestRules", questRulesTable) {
			public void add() {
				JOptionPane.showMessageDialog(this, "Not implemented yet");
			}

			public void edit() {
				JOptionPane.showMessageDialog(this, "Not implemented yet");
			}

			public void delete() {
				JOptionPane.showMessageDialog(this, "Not implemented yet");
			}
		};
		return questRulesPanel;
	}

	private JPanel buildLocationPanel() {
		locationTable = new JTable(new LocationTableModel(quest));
		ComponentTools.lockColumnWidth(locationTable, 0, 100);
		locationTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2) {
					locationPanel.edit();
				}
			}
		});
		locationTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				updateControls();
			}
		});
		locationPanel = new QuestTableEditorPanel("Locations", locationTable) {
			public void add() {
				QuestLocation loc = quest.createQuestLocation();
				QuestLocationEditor editor = new QuestLocationEditor(QuestBuilderFrame.this, realmSpeakData, quest, loc);
				editor.setVisible(true);
				if (editor.getCanceledEdit()) {
					quest.deleteQuestLocation(loc);
				}
				locationTable.clearSelection();
				locationTable.revalidate();
				updateControls();
			}

			public void edit() {
				int selRow = locationTable.getSelectedRow();
				QuestLocation loc = quest.getLocations().get(selRow);
				String oldTag = loc.getTagName();
				QuestLocationEditor editor = new QuestLocationEditor(QuestBuilderFrame.this, realmSpeakData, quest, loc);
				editor.setVisible(true);
				if (!editor.canceledEdit && oldTag != loc.getTagName()) {
					updateTagName(oldTag, loc.getTagName());
				}
				locationTable.clearSelection();
				locationTable.revalidate();
				updateControls();
			}

			public void delete() {
				int selRow = locationTable.getSelectedRow();
				QuestLocation loc = quest.getLocations().get(selRow);
				if (quest.usesLocationTag(loc.getTagName())) {
					JOptionPane.showMessageDialog(QuestBuilderFrame.this, "Cannot delete a location that is in use.  Remove from description and/or steps before deleting.", "Location cannot be deleted.", JOptionPane.ERROR_MESSAGE);
				}
				else {
					quest.deleteQuestLocation(loc);
					locationTable.clearSelection();
					locationTable.revalidate();
					updateControls();
				}
			}
		};
		return locationPanel;
	}

	private JPanel buildMinorCharacterPanel() {
		minorCharacterTable = new JTable(new MinorCharacterTableModel(quest));
		ComponentTools.lockColumnWidth(minorCharacterTable, 0, 100);
		minorCharacterTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2) {
					minorCharacterPanel.edit();
				}
			}
		});
		minorCharacterTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				updateControls();
			}
		});
		minorCharacterPanel = new QuestTableEditorPanel("Minor Characters", minorCharacterTable) {
			public void add() {
				QuestMinorCharacter mc = quest.createMinorCharacter();
				MinorCharacterEditor editor = new MinorCharacterEditor(QuestBuilderFrame.this, realmSpeakData, quest, mc);
				editor.setVisible(true);
				if (editor.getCanceledEdit()) {
					quest.deleteMinorCharacter(mc);
				}
				minorCharacterTable.clearSelection();
				minorCharacterTable.revalidate();
				updateControls();
			}

			public void edit() {
				int selRow = minorCharacterTable.getSelectedRow();
				QuestMinorCharacter mc = quest.getMinorCharacters().get(selRow);
				MinorCharacterEditor editor = new MinorCharacterEditor(QuestBuilderFrame.this, realmSpeakData, quest, mc);
				editor.setVisible(true);
				minorCharacterTable.clearSelection();
				minorCharacterTable.revalidate();
				updateControls();
			}

			public void delete() {
				int selRow = minorCharacterTable.getSelectedRow();
				QuestMinorCharacter mc = quest.getMinorCharacters().get(selRow);
				if (quest.usesMinorCharacter(mc)) {
					JOptionPane.showMessageDialog(QuestBuilderFrame.this, "Cannot delete a minor character that is in use.  Remove from rewards before deleting.", "Minor Character cannot be deleted.", JOptionPane.ERROR_MESSAGE);
				}
				else {
					quest.deleteMinorCharacter(mc);
					minorCharacterTable.clearSelection();
					minorCharacterTable.revalidate();
					updateControls();
				}
			}
		};
		return minorCharacterPanel;
	}

	private JPanel buildCounterPanel() {
		counterTable = new JTable(new CounterTableModel(quest));
		ComponentTools.lockColumnWidth(counterTable, 0, 100);
		counterTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2) {
					counterPanel.edit();
				}
			}
		});
		counterTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				updateControls();
			}
		});
		counterPanel = new QuestTableEditorPanel("Counters", counterTable) {
			public void add() {
				QuestCounter counter = quest.createQuestCounter();
				QuestCounterEditor editor = new QuestCounterEditor(QuestBuilderFrame.this, realmSpeakData, quest, counter);
				editor.setVisible(true);
				if (editor.getCanceledEdit()) {
					quest.deleteQuestCounter(counter);
				}
				counterTable.clearSelection();
				counterTable.revalidate();
				updateControls();
			}

			public void edit() {
				int selRow = counterTable.getSelectedRow();
				QuestCounter counter = quest.getCounters().get(selRow);
				String oldTag = counter.getTagName();
				QuestCounterEditor editor = new QuestCounterEditor(QuestBuilderFrame.this, realmSpeakData, quest, counter);
				editor.setVisible(true);
				if (!editor.canceledEdit && oldTag != counter.getTagName()) {
					updateTagName(oldTag, counter.getTagName());
				}
				counterTable.clearSelection();
				counterTable.revalidate();
				updateControls();
			}

			public void delete() {
				int selRow = counterTable.getSelectedRow();
				QuestCounter counter = quest.getCounters().get(selRow);
				if (quest.usesCounterTag(counter.getTagName())) {
					JOptionPane.showMessageDialog(QuestBuilderFrame.this, "Cannot delete a counter that is in use.  Remove from description and/or steps before deleting.", "Counter cannot be deleted.", JOptionPane.ERROR_MESSAGE);
				}
				else {
					quest.deleteQuestCounter(counter);
					counterTable.clearSelection();
					counterTable.revalidate();
					updateControls();
				}
			}
		};
		return counterPanel;
	}
	
	private void updateTagName(String oldTagName, String newTagName) {
		if (oldTagName.equals(newTagName))
			return;
		String desc = quest.getDescription();
		if (desc != null) {
			desc = StringUtilities.findAndReplace(desc, oldTagName, newTagName);
			quest.setDescription(desc);
			questDescription.setText(desc);
		}
		for (QuestStep step : quest.getSteps()) {
			desc = step.getDescription();
			if (desc != null) {
				desc = StringUtilities.findAndReplace(desc, oldTagName, newTagName);
				step.setDescription(desc);
			}
		}
		rebuildSteps();
	}
	
	private JPanel buildQuestDiagramPanel() {
		questStepView = new QuestStepView();
		questStepView.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ev) {
				QuestStep selected = questStepView.getSelectedStep();
				for (Component c : questSteps.getComponents()) {
					QuestStepPanel panel = (QuestStepPanel) c;
					if (panel.getStep() == selected) {
						questSteps.setSelectedComponent(c);
						break;
					}
				}
			}
		});
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(questStepView);
		return panel;
	}

	private JPanel buildQuestStepPanel() {
		JPanel questStepPanel = new JPanel(new BorderLayout());
		questStepPanel.add(createHeaderLabelWithToolbar("Quest Steps:", buildStepToolBar()), BorderLayout.NORTH);
		questSteps = new JTabbedPane(SwingConstants.LEFT);
		questSteps.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				QuestStepPanel panel = (QuestStepPanel) questSteps.getSelectedComponent();
				if (panel == null)
					return;
				QuestStep selected = panel.getStep();
				questStepView.setSelectedStep(selected);
			}
		});
		questSteps.setFont(new Font("Arial", Font.PLAIN, 24));
		questStepPanel.add(questSteps, BorderLayout.CENTER);
		return questStepPanel;
	}

	private JToolBar buildStepToolBar() {
		JToolBar stepToolbar = new JToolBar("Quest Steps");
		addStep = new AbstractAction("Add", IconFactory.findIcon("icons/plus.gif")) {
			public void actionPerformed(ActionEvent e) {
				quest.createQuestStep(autoConnectStepOption.isSelected());
				rebuildSteps();
				questSteps.setSelectedIndex(quest.getSteps().size() - 1);
				updateControls();
			}
		};
		stepToolbar.add(new JButton(addStep));
		stepToolbar.add(Box.createHorizontalStrut(10));
		deleteStep = new AbstractAction("Remove", IconFactory.findIcon("icons/minus.gif")) {
			public void actionPerformed(ActionEvent e) {
				int n = questSteps.getSelectedIndex();
				quest.deleteStepAt(n);
				if (quest.getSteps().size() == 0) {
					quest.createQuestStep(autoConnectStepOption.isSelected());
				}
				if (n >= quest.getSteps().size()) {
					n = quest.getSteps().size() - 1;
				}
				rebuildSteps();
				questSteps.setSelectedIndex(n);
				quest.autoRepair();
				updateControls();
			}
		};
		stepToolbar.add(new JButton(deleteStep));
		stepToolbar.add(Box.createHorizontalStrut(10));
		moveStepUp = new AbstractAction("Up", IconFactory.findIcon("icons/s_arrow8.gif")) {
			public void actionPerformed(ActionEvent e) {
				int n = questSteps.getSelectedIndex();
				if (n == 0)
					return;
				quest.moveStep(n, -1);
				rebuildSteps();
				questSteps.setSelectedIndex(n - 1);
				updateControls();
			}
		};
		stepToolbar.add(new JButton(moveStepUp));
		stepToolbar.add(Box.createHorizontalStrut(10));
		moveStepDown = new AbstractAction("Down", IconFactory.findIcon("icons/s_arrow2.gif")) {
			public void actionPerformed(ActionEvent e) {
				int n = questSteps.getSelectedIndex();
				if (n >= quest.getSteps().size() - 1)
					return;
				quest.moveStep(n, 1);
				rebuildSteps();
				questSteps.setSelectedIndex(n + 1);
				updateControls();
			}
		};
		stepToolbar.add(new JButton(moveStepDown));
		autoConnectStepOption = new JCheckBox("Auto-connect",true);
		stepToolbar.add(autoConnectStepOption);
		return stepToolbar;
	}

	private static JPanel createHeaderLabelWithToolbar(String value, JToolBar toolBar) {
		JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.add(createHeaderLabel(value));
		panel.add(toolBar);
		return panel;
	}

	private static JLabel createHeaderLabel(String value) {
		JLabel header = new JLabel(value);
		header.setFont(HeaderFont);
		header.setOpaque(true);
		header.setBackground(Color.lightGray);
		return header;
	}

	private static void initLogging() {
		LoggingHandler loggingHandler = new LoggingHandler();
		loggingHandler.setLevel(Level.ALL);
		loggingHandler.setFormatter(new EasyLoggingFormatter());
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			rootLogger.removeHandler(handlers[0]);
		}
		rootLogger.addHandler(loggingHandler);
		rootLogger.setLevel(Level.WARNING); // default to warning

		Logger questLogger = Logger.getLogger("com.robin.magic_realm.components.quest");
		questLogger.setLevel(Level.FINEST);
	}

	public static class EasyLoggingFormatter extends Formatter {
		private final static String columnSeparator = "\t";
		private final String lineSeparator = "\n";

		public synchronized String format(LogRecord record) {
			StringBuffer sb = new StringBuffer();
			String message = formatMessage(record);
			sb.append(message);
			if (record.getThrown() != null) {
				sb.append(columnSeparator);
				sb.append(record.getThrown().getClass().getName());
				sb.append(columnSeparator);
				sb.append(record.getThrown().getMessage());
				StackTraceElement[] traceFrames = record.getThrown().getStackTrace();
				for (int i = 0; i < traceFrames.length; i++) {
					StackTraceElement element = traceFrames[i];
					sb.append(columnSeparator);
					sb.append(element.toString());
				}
			}
			sb.append(lineSeparator);
			return sb.toString();
		}
	}

	public static void main(String[] args) {
		initLogging();
		ComponentTools.setSystemLookAndFeel();
		RealmUtility.setupTextType();
		final QuestBuilderFrame frame = new QuestBuilderFrame();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				frame.exitApp();
			}
		});
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}