package com.robin.magic_realm.RealmQuestBuilder;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import com.robin.game.objects.*;
import com.robin.general.graphics.GraphicsUtil;
import com.robin.general.swing.*;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.GuildLevelType;
import com.robin.magic_realm.components.attribute.GuildLevelType.GuildLevel;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.attribute.Spoils;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.quest.requirement.*;
import com.robin.magic_realm.components.quest.reward.QuestReward;
import com.robin.magic_realm.components.swing.CharacterChooser;
import com.robin.magic_realm.components.table.Loot;
import com.robin.magic_realm.components.table.Search;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.*;

public class QuestTesterFrame extends JFrame {
	Quest questToTest;

	GameData gameData;
	Quest quest;
	CharacterWrapper character;

	JButton resetButton;
	JLabel questName;
	JButton activateButton;
	JTextArea questDescription;
	QuestStepInteractiveView questStepView;
	JTextArea stepDetails;

	JTextArea debugOutput;

	// Stats
	JLabel charName;
	JLabel currentLocation;
	JLabel currentDay;
	JLabel currentWeather;
	JLabel gtAmount;
	JLabel spellAmount;
	JLabel fameAmount;
	JLabel notorietyAmount;
	JLabel goldAmount;
	JLabel fatigue;
	JLabel wounds;
	JLabel relationship;
	JLabel guild;

	// Inventory
	JList<GameObject> activeInventory;
	JList<GameObject> inactiveInventory;

	// Hirelings
	JList<RealmComponent> hirelings;
	JButton hirelingAdd;
	JButton hirelingUnhire;
	JButton hirelingKill;
	JButton hirelingToggleFollow;
	JList<QuestJournalEntry> journalList;

	// Clearing
	JList<RealmComponent> clearingComponents;
	JLabel clearingTitle;
	JButton pickupFromClearingButton;
	JButton removeFromClearingButton;
	JButton searchClearingButton;
	JButton killDenizenButton;
	JButton discoverButton;
	JButton openLocationButton;
	JToggleButton enchantLocationButton;
	JButton magicForClearingButton;

	JToggleButton unspecifiedTime;
	JToggleButton birdsongTime;
	JToggleButton phaseTime;
	JToggleButton turnTime;
	JToggleButton eveningTime;
	JToggleButton midnightTime;

	boolean inventorySelectionLock = false;

	public final boolean ready;

	public QuestTesterFrame(Quest quest, String charName) {
		questToTest = quest;
		initComponents();
		redirectSystemStreams();
		ready = initQuestTest(charName);
	}

	private void initComponents() {
		setTitle("RealmSpeak Quest Tester");
		setSize(1400, 1080);

		setLayout(new BorderLayout());

		JPanel top = new JPanel(new BorderLayout());
		ComponentTools.lockComponentSize(top, 2000, 200);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		questName = new JLabel();
		questName.setFont(QuestGuiConstants.QuestTitleFont);
		questName.setHorizontalAlignment(SwingConstants.CENTER);
		questName.setVerticalAlignment(SwingConstants.CENTER);
		buttonPanel.add(questName, BorderLayout.CENTER);
		resetButton = new JButton("RESET");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				initQuestTest(character.getGameObject().getName());
			}
		});
		buttonPanel.add(resetButton, BorderLayout.NORTH);
		activateButton = new JButton("Activate!");
		activateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				quest.setState(QuestState.Active, character.getCurrentDayKey(), character);
				retestQuest();
				questName.setIcon(RealmComponent.getRealmComponent(quest.getGameObject()).getFaceUpIcon());
				activateButton.setEnabled(quest.getState() == QuestState.Assigned);
			}
		});
		buttonPanel.add(activateButton, BorderLayout.SOUTH);
		top.add(buttonPanel, BorderLayout.WEST);
		questDescription = new JTextArea();
		questDescription.setEditable(false);
		questDescription.setLineWrap(true);
		questDescription.setWrapStyleWord(true);
		questDescription.setFont(QuestGuiConstants.QuestDescriptionFont);
		questDescription.setBackground(null);
		questDescription.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		top.add(new JScrollPane(questDescription), BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);

		JPanel main = new JPanel(new GridLayout(2, 1));
		JPanel mainTop = new JPanel(new GridLayout(1, 3));
		JPanel debugPanel = new JPanel(new BorderLayout());
		debugOutput = new JTextArea();
		debugOutput.setEditable(false);
		debugOutput.setLineWrap(false);
		debugOutput.setWrapStyleWord(false);
		debugOutput.setFont(QuestGuiConstants.QuestDescriptionFont);
		debugPanel.add(new JScrollPane(debugOutput));
		debugPanel.setBorder(BorderFactory.createTitledBorder("Debug Output"));
		mainTop.add(debugPanel);

		questStepView = new QuestStepInteractiveView(QuestTesterFrame.this);
		questStepView.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ev) {
				QuestStep selected = questStepView.getSelectedStep();
				updateStepDetails(selected);
			}
		});
		mainTop.add(questStepView);
		stepDetails = new JTextArea();
		stepDetails.setEditable(false);
		stepDetails.setLineWrap(true);
		stepDetails.setWrapStyleWord(true);
		stepDetails.setFont(QuestGuiConstants.QuestDescriptionFont);
		stepDetails.setBackground(null);
		stepDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainTop.add(new JScrollPane(stepDetails));
		main.add(mainTop);
		main.add(buildCharacterPanel());
		add(main, BorderLayout.CENTER);
	}

	private JPanel buildCharacterPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 4));
		panel.add(buildCharacterStatsPanel());
		panel.add(buildCharacterInventoryPanel());
		panel.add(buildCharacterHirelingPanel());
		panel.add(buildCharacterClearingPanel());
		return panel;
	}

	private JPanel buildCharacterStatsPanel() {
		JPanel superPanel = new JPanel(new BorderLayout());
		JPanel topPanel = new JPanel(new GridLayout(2, 1));
		JPanel questPanel = new JPanel(new GridLayout(2, 3));
		JButton retestQuestButton = new JButton("Check Quest");
		retestQuestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				retestQuest();
			}
		});
		questPanel.add(retestQuestButton);
		JButton fulfillRequirements = new JButton("Fulfill step");
		fulfillRequirements.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				QuestStep questStep = questStepView.getSelectedStep();
				if (questStep!=null) {
					questStepView.fulfillRequirementsForQuestStep(quest, questStep, character);
					if (quest.getState() == QuestState.Complete) {
						showQuestCompleted();
					}
					retestQuest();
				}
			}
		});
		questPanel.add(fulfillRequirements);
		JButton failRequirements = new JButton("Fail step");
		failRequirements.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				QuestStep questStep = questStepView.getSelectedStep();
				if (questStep!=null) {
					QuestStepInteractiveView.failRequirementsForQuestStep(quest, questStep, character);
					if (quest.getState() == QuestState.Complete) {
						showQuestCompleted();
					}
					retestQuest();
				}
			}
		});
		questPanel.add(failRequirements);
		JButton ready = new JButton("Ready step");
		ready.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				QuestStep questStep = questStepView.getSelectedStep();
				if (questStep!=null) {
					QuestStepInteractiveView.readyQuestStep(quest, questStep, character);
					if (quest.getState() == QuestState.Complete) {
						showQuestCompleted();
					}
					retestQuest();
				}
			}
		});
		questPanel.add(ready);
		JButton pend = new JButton("Pend step");
		pend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				QuestStep questStep = questStepView.getSelectedStep();
				if (questStep!=null) {
					QuestStepInteractiveView.pendQuestStep(quest, questStep, character);
					if (quest.getState() == QuestState.Complete) {
						showQuestCompleted();
					}
					retestQuest();
				}
			}
		});
		questPanel.add(pend);
		topPanel.add(questPanel);
		JPanel phaseOptions = new JPanel(new GridLayout(1, 5));
		ButtonGroup timeGroup = new ButtonGroup();
		unspecifiedTime = new ForceTextToggle("Any");
		unspecifiedTime.setSelected(true);
		unspecifiedTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				retestQuest();
			}
		});
		timeGroup.add(unspecifiedTime);
		phaseOptions.add(unspecifiedTime);
		birdsongTime = new ForceTextToggle("Birdsong");
		birdsongTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GameWrapper.findGame(gameData).setState(GameWrapper.GAME_STATE_RECORDING);
				retestQuest();
			}
		});
		timeGroup.add(birdsongTime);
		phaseOptions.add(birdsongTime);
		phaseTime = new ForceTextToggle("Phase");
		phaseTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GameWrapper.findGame(gameData).setState(GameWrapper.GAME_STATE_PLAYING);
				retestQuest();
			}
		});
		timeGroup.add(phaseTime);
		phaseOptions.add(phaseTime);
		turnTime = new ForceTextToggle("Turn");
		turnTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GameWrapper.findGame(gameData).setState(GameWrapper.GAME_STATE_RESOLVING);
				retestQuest();
			}
		});
		timeGroup.add(turnTime);
		phaseOptions.add(turnTime);
		eveningTime = new ForceTextToggle("Evening");
		eveningTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GameWrapper.findGame(gameData).setState(GameWrapper.GAME_STATE_DAYEND);
				retestQuest();
			}
		});
		timeGroup.add(eveningTime);
		phaseOptions.add(eveningTime);
		midnightTime = new ForceTextToggle("Midnight");
		midnightTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GameWrapper.findGame(gameData).setState(GameWrapper.GAME_STATE_DAYEND);
				retestQuest();
			}
		});
		timeGroup.add(midnightTime);
		phaseOptions.add(midnightTime);
		topPanel.add(phaseOptions);
		superPanel.add(topPanel, BorderLayout.NORTH);
		JPanel panel = new JPanel(new BorderLayout());
		Box box = Box.createVerticalBox();
		UniformLabelGroup group = new UniformLabelGroup();
		Box line;

		line = group.createLabelLine("Name");
		charName = new JLabel();
		line.add(charName);
		line.add(Box.createHorizontalGlue());
		JToggleButton toggleHidden = new JToggleButton("Hidden");
		toggleHidden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				toggleHidden();
			}
		});
		line.add(toggleHidden);
		JToggleButton toggleFlying = new JToggleButton("Flying");
		toggleFlying.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				toggleFlying();
			}
		});
		line.add(toggleFlying);
		box.add(line);
		
		line = group.createLabelLine("Current Location");
		currentLocation = new JLabel();
		line.add(currentLocation);
		line.add(Box.createHorizontalGlue());
		box.add(line);
		line = group.createLine();
		line.add(Box.createHorizontalGlue());
		JButton runAway = new JButton("Run Away");
		runAway.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				runAway();
			}
		});
		line.add(runAway);
		JButton changeLocation = new JButton("Change");
		changeLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				chooseNewLocation();
			}
		});
		line.add(changeLocation);
		box.add(line);

		line = group.createLabelLine("Current Day");
		currentDay = new JLabel();
		line.add(currentDay);
		line.add(Box.createHorizontalGlue());
		JButton changeDay = new JButton("Increment");
		changeDay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GameWrapper game = GameWrapper.findGame(gameData);
				game.addDay(1);
				character.setCurrentMonth(game.getMonth());
				character.setCurrentDay(game.getDay());
				character.startNewDay(RealmCalendar.getCalendar(gameData), HostPrefWrapper.findHostPrefs(gameData));
				CombatWrapper.clearAllCombatInfo(character.getGameObject());
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(changeDay);
		box.add(line);
		
		line = group.createLabelLine("Current Weather");
		currentWeather = new JLabel();
		line.add(currentWeather);
		line.add(Box.createHorizontalGlue());
		JButton changeWeather = new JButton("Change");
		changeWeather.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String weather = chooseWeather();
				if (weather != null) {
					RealmCalendar realmCalender = RealmCalendar.getCalendar(character.getGameData());
					realmCalender.setWeatherResult(RealmCalendar.getWeatherInt(weather));
					updateCharacterPanel();
					retestQuest();
				}
			}
		});
		line.add(changeWeather);
		box.add(line);

		line = group.createLabelLine("Great Treasures");
		gtAmount = new JLabel();
		line.add(gtAmount);
		line.add(Box.createHorizontalGlue());
		box.add(line);

		line = group.createLabelLine("Recorded Fame");
		fameAmount = new JLabel();
		line.add(fameAmount);
		line.add(Box.createHorizontalGlue());
		JButton subFame = new JButton("-");
		subFame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.addFame(-5);
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(subFame);
		JButton addFame = new JButton("+");
		addFame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.addFame(5);
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(addFame);
		box.add(line);

		line = group.createLabelLine("Recorded Notoriety");
		notorietyAmount = new JLabel();
		line.add(notorietyAmount);
		line.add(Box.createHorizontalGlue());
		JButton subNot = new JButton("-");
		subNot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.addNotoriety(-5);
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(subNot);
		JButton addNot = new JButton("+");
		addNot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.addNotoriety(5);
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(addNot);
		box.add(line);

		line = group.createLabelLine("Recorded Gold");
		goldAmount = new JLabel();
		line.add(goldAmount);
		line.add(Box.createHorizontalGlue());
		JButton subGold = new JButton("-");
		subGold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.addGold(-5);
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(subGold);
		JButton addGold = new JButton("+");
		addGold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.addGold(5);
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(addGold);
		box.add(line);
		
		line = group.createLabelLine("Recorded Fatique");
		fatigue = new JLabel();
		line.add(fatigue);
		line.add(Box.createHorizontalGlue());
		JButton subfatigue = new JButton("-");
		subfatigue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {		
				character.setWeatherFatigue(character.getWeatherFatigue()-1);
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(subfatigue);
		JButton addfatigue = new JButton("+");
		addfatigue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.setWeatherFatigue(character.getWeatherFatigue()+1);
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(addfatigue);
		box.add(line);
		
		line = group.createLabelLine("Recorded Wounds");
		wounds = new JLabel();
		line.add(wounds);
		line.add(Box.createHorizontalGlue());
		JButton subwounds = new JButton("-");
		subwounds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {		
				character.setExtraWounds(character.getExtraWounds()-1);
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(subwounds);
		JButton addwounds = new JButton("+");
		addwounds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.setExtraWounds(character.getExtraWounds()+1);
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(addwounds);
		box.add(line);
		
		line = group.createLabelLine("Relationship");
		relationship = new JLabel();
		line.add(relationship);
		line.add(Box.createHorizontalGlue());
		JButton relationship = new JButton("Set");
		relationship.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> list = chooseOther("native","rank=HQ","visitor");
				if (list == null)
					return;
				if (list.size() != 1) {
					JOptionPane.showMessageDialog(QuestTesterFrame.this, "Pick 1");
					return;
				}
				int targetRel = chooseRelationshipLevel();
				ArrayList<GameObject> representativeNativesToChange = QuestRequirementRelationship.getRepresentativeNatives(character);
				for(GameObject denizen:representativeNativesToChange) {
					int current = character.getRelationship(denizen);
					int diff = targetRel - current;
					character.changeRelationship(denizen,diff);
				}
				
				retestQuest();
			}
		});
		line.add(relationship);
		box.add(line);
		
		line = group.createLabelLine("Guild");
		guild = new JLabel();
		line.add(guild);
		line.add(Box.createHorizontalGlue());
		JButton guild = new JButton("Set");
		guild.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> guildName = chooseOther("Guilds", "guild");
				if (guildName == null)
					return;
				if (guildName.size() != 1) {
					JOptionPane.showMessageDialog(QuestTesterFrame.this, "Pick 1");
					return;
				}
				GuildLevel level = chooseGuildLevel();
				int guildLevel = GuildLevelType.getIntFor(level);
				
				character.setCurrentGuild(guildName.get(0).getName());
				character.setCurrentGuildLevel(guildLevel);
				
				updateCharacterPanel();
				retestQuest();
			}
		});
		line.add(guild);
		box.add(line);

		box.add(Box.createVerticalGlue());
		panel.add(box, BorderLayout.NORTH);
		panel.setBorder(BorderFactory.createTitledBorder("Character Stats"));
		superPanel.add(panel, BorderLayout.CENTER);
		return superPanel;
	}

	private JPanel buildCharacterInventoryPanel() {
		JPanel superPanel = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new GridLayout(2, 1));

		activeInventory = new JList<>();
		activeInventory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		activeInventory.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (inventorySelectionLock)
					return;
				inventorySelectionLock = true;
				inactiveInventory.clearSelection();
				inventorySelectionLock = false;
			}
		});
		activeInventory.setCellRenderer(new QuestListRenderer());
		panel.add(makeTitledScrollPane("Active Inventory", activeInventory));

		inactiveInventory = new JList<>();
		inactiveInventory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		inactiveInventory.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (inventorySelectionLock)
					return;
				inventorySelectionLock = true;
				activeInventory.clearSelection();
				inventorySelectionLock = false;
			}
		});
		inactiveInventory.setCellRenderer(new QuestListRenderer());
		panel.add(makeTitledScrollPane("Inactive Inventory", inactiveInventory));

		superPanel.add(panel, BorderLayout.CENTER);
		JPanel controls = new JPanel(new GridLayout(2, 4));
				
		JButton addNew = new JButton("Item");
		addNew.setToolTipText("Gain an item (treasure/weapon/armor)");
		addNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> things = chooseItem();
				if (things == null)
					return;
				for (GameObject thing : things) {
					Loot.addItemToCharacter(QuestTesterFrame.this, null, character, thing, HostPrefWrapper.findHostPrefs(gameData));
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(addNew);
		JButton toggleActive = new JButton("Toggle");
		toggleActive.setToolTipText("Activate/Deactivate the selected item above.");
		toggleActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (activeInventory.getSelectedIndex() != -1) {
					GameObject thing = activeInventory.getSelectedValue();
					if (TreasureUtility.doDeactivate(QuestTesterFrame.this, character, thing)) {
						updateCharacterPanel();
						retestQuest();
						inactiveInventory.setSelectedValue(thing, true);
					}
				}
				else if (inactiveInventory.getSelectedIndex() != -1) {
					GameObject thing = inactiveInventory.getSelectedValue();
					if (TreasureUtility.doActivate(QuestTesterFrame.this, character, thing, null, false)) {
						updateCharacterPanel();
						retestQuest();
						activeInventory.setSelectedValue(thing, true);
					}
				}
			}
		});
		controls.add(toggleActive);
		JButton remove = new JButton("Remove");
		remove.setToolTipText("Remove the selected item.  Note that it is NOT dropped in the clearing.");
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (activeInventory.getSelectedIndex() != -1) {
					GameObject thing = activeInventory.getSelectedValue();
					if (TreasureUtility.doDeactivate(QuestTesterFrame.this, character, thing)) {
						thing.detach();
						updateCharacterPanel();
						retestQuest();
					}
				}
				else if (inactiveInventory.getSelectedIndex() != -1) {
					GameObject thing = inactiveInventory.getSelectedValue();
					thing.detach();
					updateCharacterPanel();
					retestQuest();
				}
			}
		});
		controls.add(remove);
		JButton drop = new JButton("Drop");
		drop.setToolTipText("Drop the selected item in the clearing.");
		drop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				TileLocation tl = character.getCurrentLocation();
				if (activeInventory.getSelectedIndex() != -1) {
					GameObject thing = activeInventory.getSelectedValue();
					if (TreasureUtility.doDeactivate(QuestTesterFrame.this, character, thing)) {
						tl.clearing.add(thing, character);
						updateCharacterPanel();
						retestQuest();
					}
				}
				else if (inactiveInventory.getSelectedIndex() != -1) {
					GameObject thing = inactiveInventory.getSelectedValue();
					tl.clearing.add(thing, character);
					updateCharacterPanel();
					retestQuest();
				}
			}
		});
		controls.add(drop);
		JButton buy = new JButton("Buy");
		buy.setToolTipText("Buy an Item from a Native.");
		buy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> list = chooseOther("Seller", "visitor", "native,rank=HQ");
				if (list == null)
					return;
				if (list.size() != 1) {
					JOptionPane.showMessageDialog(QuestTesterFrame.this, "Pick 1");
					return;
				}
				GameObject seller = list.get(0);		
				QuestRequirementParams params = new QuestRequirementParams();
				params.actionType = CharacterActionType.Trading;
				params.actionName = TradeType.Buy.toString();
				params.objectList = new ArrayList<>();
				params.targetOfSearch = seller;			
				ArrayList<GameObject> items = chooseItem();
				if (items == null)
					return;
				for (GameObject item : items) {
					RealmComponent itemRc = RealmComponent.getRealmComponent(item);
					int price = TreasureUtility.getBasePrice(null, itemRc);
					character.setGold(character.getGold()-price);
					Loot.addItemToCharacter(QuestTesterFrame.this, null, character, item, HostPrefWrapper.findHostPrefs(gameData));
					params.objectList.add(item);
				}
				character.testQuestRequirements(QuestTesterFrame.this, params);
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(buy);
		JButton sell = new JButton("Sell");
		sell.setToolTipText("Sell the selected item to a Native.");
		sell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (activeInventory.getSelectedIndex() == -1 && inactiveInventory.getSelectedIndex() == -1)
					return;
				ArrayList<GameObject> list = chooseOther("Buyer", "visitor", "native,rank=HQ");
				if (list == null)
					return;
				if (list.size() != 1) {
					JOptionPane.showMessageDialog(QuestTesterFrame.this, "Pick 1");
					return;
				}

				QuestRequirementParams params = new QuestRequirementParams();
				params.actionType = CharacterActionType.Trading;
				params.actionName = TradeType.Sell.toString();
				params.objectList = new ArrayList<>();
				params.targetOfSearch = list.get(0);

				if (activeInventory.getSelectedIndex() != -1) {
					GameObject item = activeInventory.getSelectedValue();
					if (TreasureUtility.doDeactivate(QuestTesterFrame.this, character, item)) {
						item.detach();
						params.objectList.add(item);
						character.testQuestRequirements(QuestTesterFrame.this, params);
						updateCharacterPanel();
						retestQuest();
					}
				}
				else if (inactiveInventory.getSelectedIndex() != -1) {
					GameObject item = inactiveInventory.getSelectedValue();
					item.detach();
					params.objectList.add(item);
					character.testQuestRequirements(QuestTesterFrame.this, params);
					updateCharacterPanel();
					retestQuest();
				}
			}
		});
		controls.add(sell);
		JButton addMc = new JButton("MC");
		addMc.setToolTipText("Add a minor character");
		addMc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {

				String mcName = JOptionPane.showInputDialog("Minor Character Name"); // mcName??!?  Robble robble robble.
				if (mcName == null)
					return;

				QuestMinorCharacter minorCharacter = quest.createMinorCharacter();
				minorCharacter.setName(mcName);
				minorCharacter.getGameObject().setThisAttribute(Constants.ACTIVATED);
				//minorCharacter.setupAbilities();
				character.getGameObject().add(minorCharacter.getGameObject());

				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(addMc);
		JButton castSpell = new JButton("CastSpell");
		castSpell.setToolTipText("Casts a spell");
		castSpell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> spells = chooseOther("Spell", "spell");
				if (spells == null)
					return;
				if (spells.size() != 1) {
					JOptionPane.showMessageDialog(QuestTesterFrame.this, "Pick 1");
					return;
				}
				updateCharacterPanel();
								
				GameObject castedSpell = spells.get(0);
				character.addCastedSpell(castedSpell);
				String spellName = (castedSpell.getName().replaceAll("(\\s)\\[([0-9]+)\\]",""));
				castedSpell.setName(spellName);
				QuestRequirementParams reqParams = new QuestRequirementParams();
				reqParams.actionType = CharacterActionType.CastSpell;
				reqParams.objectList.add(castedSpell);
				retestQuest(reqParams);
			}
		});
		controls.add(castSpell);
		
		superPanel.add(controls, BorderLayout.SOUTH);
		return superPanel;
	}

	private JPanel buildCharacterHirelingPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1));
		JPanel hirelingsPanel = new JPanel(new BorderLayout());	
		hirelings = new JList<>();
		hirelings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hirelings.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateHirelingsButtons();
			}
		});
		JPanel hirelingButtons = new JPanel(new GridLayout(1, 4));
		hirelings.setCellRenderer(new HirelingListRenderer());
		hirelingsPanel.add(hirelings);
		hirelingAdd = new JButton("Add");
		hirelingAdd.setToolTipText("Hire new hirelings");
		hirelingAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> things = chooseOther("Hireling", "native,!treasure,!dwelling,!horse,!boon");
				if (things == null)
					return;
				for (GameObject thing : things) {
					thing.setThisAttribute("seen");
					thing.removeThisAttribute(Constants.DEAD);
					character.getCurrentLocation().clearing.add(thing, null);
					character.addHireling(thing);
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		hirelingButtons.add(hirelingAdd);
		hirelingUnhire = new JButton("Unhire");
		hirelingUnhire.setToolTipText("Unhire hireling");
		hirelingUnhire.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent hireling = hirelings.getSelectedValue();
				if (hireling != null) {
					character.removeHireling(hireling.getGameObject());
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		hirelingButtons.add(hirelingUnhire);
		hirelingKill = new JButton("Kill");
		hirelingKill.setToolTipText("Kill hireling");
		hirelingKill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent hireling = hirelings.getSelectedValue();
				killDenizen(hireling);
				
				CombatWrapper combat = new CombatWrapper(hireling.getGameObject());
				combat.setBetrayedBy(character.getGameObject());
				CombatWrapper combatCharacter = new CombatWrapper(character.getGameObject());
				combatCharacter.setBetrayed(hireling.getGameObject());
				character.addTreachery(hireling.getGameObject());
				
				retestQuest();
			}
		});
		hirelingButtons.add(hirelingKill);
		hirelingToggleFollow = new JButton("Follow");
		hirelingToggleFollow.setToolTipText("Toggle hireling to follow character");
		hirelingToggleFollow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent hireling = hirelings.getSelectedValue();
				if (hireling != null) {
					if (hireling.getHeldBy().getGameObject() != character.getGameObject()) {
						character.getGameObject().add(hireling.getGameObject());
					}
					else {
						if (character.getCurrentLocation() != null && character.getCurrentLocation().clearing != null) {
							character.getCurrentLocation().clearing.add(hireling.getGameObject(), null);
						}					
					}
				}
				hirelings.updateUI();
				retestQuest();
			}
		});
		hirelingButtons.add(hirelingToggleFollow);
		hirelingsPanel.add(hirelingButtons, BorderLayout.SOUTH);
		panel.add(makeTitledScrollPane("Hirelings", hirelingsPanel));

		journalList = new JList<>();
		journalList.setCellRenderer(new JournalEntryListRenderer());
		panel.add(makeTitledScrollPane("Journal", journalList));
		return panel;
	}
	
	private void updateHirelingsButtons() {
		RealmComponent rc = hirelings.getSelectedValue();
		hirelingUnhire.setEnabled(rc != null);
		hirelingKill.setEnabled(rc != null);
		hirelingToggleFollow.setEnabled(rc != null);
	}

	private JPanel buildCharacterClearingPanel() {
		JPanel locationPanel = new JPanel(new BorderLayout());
		JPanel locationButtonsWithTitlePanel = new JPanel(new GridLayout(2,1));
		JPanel locationButtonsPanel = new JPanel(new GridLayout(2,2));
		JPanel clearingChitsPanel = new JPanel(new BorderLayout());
				
		clearingTitle = new JLabel();
		locationButtonsWithTitlePanel.add(clearingTitle, BorderLayout.NORTH);

		searchClearingButton = new JButton("Search");
		searchClearingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent rc = clearingComponents.getSelectedValue();
				if (rc == null) {
					rc = character.getCurrentTile();
				}
				doSearchOn(rc);
			}
		});
		locationButtonsPanel.add(searchClearingButton);
		openLocationButton = new JButton("Open Location");
		openLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent rc = clearingComponents.getSelectedValue();
				ArrayList<GameObject> objectsToOpen = new ArrayList<>();
				objectsToOpen.add(rc.getGameObject());
				TreasureUtility.openOneObject(QuestTesterFrame.this, character, objectsToOpen, null, true);
				updateCharacterPanel();
				retestQuest();
			}
		});
		locationButtonsPanel.add(openLocationButton);
		enchantLocationButton = new JToggleButton("Enchant Location");
		enchantLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				TileComponent tile = character.getCurrentLocation().tile;
				if (tile.isLightSideUp()) {
					tile.setDarkSideUp();
				}
				else {
					tile.setLightSideUp();
				}
				
				QuestRequirementParams params = new QuestRequirementParams();
				params.actionType = CharacterActionType.Enchant;
				params.actionName = "tile";
				params.objectList.add(tile.getGameObject());
				character.testQuestRequirements(QuestTesterFrame.this, params);
				
				updateCharacterPanel();
				retestQuest();
			}
		});
		locationButtonsPanel.add(enchantLocationButton);
		magicForClearingButton = new JButton("Magic color");
		magicForClearingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				int colorId = chooseMagicColorId();
				if (colorId == -1) return;
				ClearingDetail clearing = character.getCurrentLocation().clearing;
				if (clearing.getMagic(colorId) == false) {
					clearing.setMagic(colorId, true);
				}
				else {
					clearing.setMagic(colorId, false);
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		locationButtonsPanel.add(magicForClearingButton);
		locationButtonsWithTitlePanel.add(locationButtonsPanel, BorderLayout.NORTH);
		clearingChitsPanel.add(locationButtonsWithTitlePanel, BorderLayout.NORTH);
		
		clearingComponents = new JList<>();
		clearingComponents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clearingComponents.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateClearingButtons();
			}
		});
		clearingComponents.setCellRenderer(new QuestListRenderer());
		clearingChitsPanel.add(new JScrollPane(clearingComponents), BorderLayout.CENTER);
		clearingChitsPanel.setBorder(BorderFactory.createTitledBorder("Current Clearing"));
		locationPanel.add(clearingChitsPanel, BorderLayout.CENTER);

		JPanel controls = new JPanel(new GridLayout(3, 4));
		JButton addChit = new JButton("Chit");
		addChit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> things = chooseOther("Chit", "chit,!treasure_location");
				if (things == null)
					return;
				for (GameObject thing : things) {
					thing.setThisAttribute("seen");
					character.getCurrentLocation().clearing.add(thing, null);
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(addChit);
		JButton addLocation = new JButton("Location");
		addLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> things = chooseOther("Location", "chit,treasure_location");
				if (things == null)
					return;
				for (GameObject thing : things) {
					thing.setThisAttribute("seen");
					character.getCurrentLocation().clearing.add(thing, null);
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(addLocation);
		JButton addItem = new JButton("Item");
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> things = chooseItem();
				if (things == null)
					return;
				for (GameObject thing : things) {
					thing.setThisAttribute("seen");
					character.getCurrentLocation().clearing.add(thing, null);
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(addItem);
		JButton addDwelling = new JButton("Dwelling");
		addDwelling.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> things = chooseOther("Dwelling", "dwelling,!native");
				if (things == null)
					return;
				for (GameObject thing : things) {
					thing.setThisAttribute("seen");
					character.getCurrentLocation().clearing.add(thing, null);
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(addDwelling);
		JButton addMonster = new JButton("Monster");
		addMonster.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> things = chooseOther("Monster", "monster,!part");
				if (things == null)
					return;
				for (GameObject thing : things) {
					thing.setThisAttribute("seen");
					thing.removeThisAttribute(Constants.DEAD);
					character.getCurrentLocation().clearing.add(thing, null);
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(addMonster);
		JButton addNative = new JButton("Native");
		addNative.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> things = chooseOther("Native", "native,!treasure,!dwelling,!horse,!boon");
				if (things == null)
					return;
				for (GameObject thing : things) {
					thing.setThisAttribute("seen");
					thing.removeThisAttribute(Constants.DEAD);
					character.getCurrentLocation().clearing.add(thing, null);
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(addNative);
		JButton addVisitor = new JButton("Visitor");
		addVisitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> things = chooseOther("Visitor", Constants.VISITOR);
				if (things == null)
					return;
				for (GameObject thing : things) {
					thing.setThisAttribute("seen");
					character.getCurrentLocation().clearing.add(thing, null);
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(addVisitor);
		JButton addMission = new JButton("Mission");
		addMission.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<GameObject> things = chooseOther("Mission", "gold_special,!visitor");
				if (things == null)
					return;
				for (GameObject thing : things) {
					thing.setThisAttribute("seen");
					character.getCurrentLocation().clearing.add(thing, null);
				}
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(addMission);

		pickupFromClearingButton = new JButton("Pick Up");
		pickupFromClearingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent rc = clearingComponents.getSelectedValue();
				if (rc == null)
					return;
				if (rc.isItem()) {
					Loot.addItemToCharacter(QuestTesterFrame.this, null, character, rc.getGameObject());
					updateCharacterPanel();
					retestQuest();
				}
				else if (rc.isGoldSpecial() && !rc.isVisitor()) {
					Loot.addItemToCharacter(QuestTesterFrame.this, null, character, rc.getGameObject());
					QuestRequirementParams qp = new QuestRequirementParams();
					qp.actionName = rc.getGameObject().getName();
					qp.actionType = CharacterActionType.PickUpMissionCampaign;
					qp.targetOfSearch = rc.getGameObject();
					updateCharacterPanel();
					retestQuest(qp);
				}
			}
		});
		controls.add(pickupFromClearingButton);
		removeFromClearingButton = new JButton("Remove");
		removeFromClearingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent rc = clearingComponents.getSelectedValue();
				if (rc == null)
					return;
				if (rc.ownedBy(RealmComponent.getRealmComponent(character.getGameObject()))) {
					character.removeHireling(rc.getGameObject());
				}
				rc.getGameObject().detach();
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(removeFromClearingButton);
		discoverButton = new JButton("Discover");
		discoverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent rc = clearingComponents.getSelectedValue();
				Search.discoverChit(QuestTesterFrame.this, character, character.getCurrentLocation().clearing, rc, new QuestRequirementParams(), null);
				updateCharacterPanel();
				retestQuest();
			}
		});
		controls.add(discoverButton);
		killDenizenButton = new JButton("Kill");
		killDenizenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent victim = clearingComponents.getSelectedValue();
				if (victim == null)
					return;
				int index = clearingComponents.getSelectedIndex();
				killDenizen(victim);
				int listLength = clearingComponents.getModel().getSize();
				if (listLength > 0) {
					if (index >= listLength)
						index = listLength - 1;
					clearingComponents.setSelectedIndex(index);
				}
				retestQuest();
			}
		});
		controls.add(killDenizenButton);
		locationPanel.add(controls, BorderLayout.SOUTH);

		return locationPanel;
	}

	private void killDenizen(RealmComponent victim) {
		String dayKey = character.getCurrentDayKey();
		ArrayList<GameObject> kills = character.getKills(dayKey);
		int killCount = kills == null ? 0 : kills.size();
		GameObject victimGameObject = victim.getGameObject();
		victimGameObject.setThisAttribute(Constants.DEAD);
		Spoils spoils = new Spoils();
		spoils.addFame(victimGameObject.getThisInt("fame"));
		spoils.addNotoriety(victimGameObject.getThisInt("notoriety"));
		spoils.setUseMultiplier(true);
		spoils.setMultiplier(killCount + 1);
		character.addKill(victimGameObject, spoils);
		character.addFame(spoils.getFame());
		character.addNotoriety(spoils.getNotoriety());
		if (victimGameObject.hasThisAttribute("native")) {
			character.addGold(Integer.parseInt(victimGameObject.getThisAttribute("base_price")));
		}
		character.removeHireling(victimGameObject);
		victimGameObject.detach();
		updateCharacterPanel();
	}
	
	private ArrayList<GameObject> chooseItem() {
		GamePool pool = new GamePool(gameData.getGameObjects());
		Hashtable<String, GameObject> hash = new Hashtable<>();
		ArrayList<String> weaponList = new ArrayList<>();
		ArrayList<String> armorList = new ArrayList<>();
		ArrayList<String> steedList = new ArrayList<>();
		ArrayList<String> treasureList = new ArrayList<>();

		ArrayList<GameObject> all = pool.find("item");
		all.addAll(pool.find("treasure_within_treasure"));
		for (GameObject item : all) {
			String itemKey = getKey(item);
			GameObject held = item.getHeldBy();
			if (held != null && (held == character.getGameObject() || (held.hasThisAttribute("tile") && held.hasThisAttribute("clearing"))))
				continue;

			if (item.hasAllKeyVals("horse,!native"))
				steedList.add(itemKey);
			else if (item.hasAllKeyVals("weapon,!character"))
				weaponList.add(itemKey);
			else if (item.hasAllKeyVals("armor,!treasure,!character"))
				armorList.add(itemKey);
			else
				treasureList.add(itemKey);

			hash.put(itemKey, item);
		}
		ArrayList<String> list = new ArrayList<>();
		list.addAll(weaponList);
		list.addAll(armorList);
		list.addAll(steedList);
		list.addAll(treasureList);
		Collections.sort(list);
		ListChooser chooser = new ListChooser(this, "Select item:", list);
		chooser.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		chooser.setDoubleClickEnabled(true);
		chooser.setLocationRelativeTo(this);
		chooser.setVisible(true);
		Vector v = chooser.getSelectedItems();
		if (v != null && !v.isEmpty()) {
			ArrayList<GameObject> ret = new ArrayList<>();
			for (int i = 0; i < v.size(); i++) {
				ret.add(hash.get(v.get(i)));
			}
			return ret;
		}
		return null;
	}

	private ArrayList<GameObject> chooseOther(String name, String... keyVals) {
		GamePool pool = new GamePool(gameData.getGameObjects());
		Hashtable<String, GameObject> hash = new Hashtable<>();
		ArrayList<String> chitList = new ArrayList<>();
		for (String kv : keyVals) {
			for (GameObject thing : pool.find(kv)) {
				String itemKey = getKey(thing);
				if (hash.containsKey(itemKey)) continue;
				chitList.add(itemKey);
				hash.put(itemKey, thing);
			}
		}
		Collections.sort(chitList);
		ListChooser chooser = new ListChooser(this, "Select " + name + ":", chitList);
		chooser.setDoubleClickEnabled(true);
		chooser.setLocationRelativeTo(this);
		chooser.setVisible(true);
		Vector v = chooser.getSelectedItems();
		if (v != null && !v.isEmpty()) {
			ArrayList<GameObject> ret = new ArrayList<>();
			for (int i = 0; i < v.size(); i++) {
				ret.add(hash.get(v.get(i)));
			}
			return ret;
		}
		return null;
	}
	
	private String chooseWeather() {
		ListChooser chooser = new ListChooser(this, "Choose weather", new String[] {RealmCalendar.WEATHER_CLEAR, RealmCalendar.WEATHER_SHOWERS, RealmCalendar.WEATHER_STORM, RealmCalendar.WEATHER_SPECIAL} );
		chooser.setDoubleClickEnabled(true);
		chooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chooser.setLocationRelativeTo(this);
		chooser.setVisible(true);
		String selected = (String) chooser.getSelectedItem();
		return selected;
	}
	
	private int chooseMagicColorId() {
		ListChooser chooser = new ListChooser(this, "Select magic color:", Constants.MAGIC_COLORS);
		chooser.setDoubleClickEnabled(true);
		chooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chooser.setLocationRelativeTo(this);
		chooser.setVisible(true);
		Object object = chooser.getSelectedItem();
		return Arrays.asList(Constants.MAGIC_COLORS).indexOf(object);
	}
	
	private int chooseRelationshipLevel() {
		ListChooser chooser = new ListChooser(this, "Choose releationship", RelationshipType.RelationshipNames);
		chooser.setDoubleClickEnabled(true);
		chooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chooser.setLocationRelativeTo(this);
		chooser.setVisible(true);
		Object object = chooser.getSelectedItem();
		return RelationshipType.getIntFor(object.toString());
	}
	
	private GuildLevel chooseGuildLevel() {
		ListChooser chooser = new ListChooser(this, "Choose guild level", GuildLevel.values() );
		chooser.setDoubleClickEnabled(true);
		chooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chooser.setLocationRelativeTo(this);
		chooser.setVisible(true);
		GuildLevel selected = (GuildLevel) chooser.getSelectedItem();
		return selected;
	}
	
	private String getEnchanted() {
		if (character != null && character.getCurrentLocation() != null && character.getCurrentLocation().tile.isEnchanted()) {
			return " (enchanted) ";
		}
		return "";
	}
	
	private String getMagicColors() {
		StringBuffer colors = new StringBuffer();
		if (character != null && character.getCurrentLocation() != null) {		
			for (String color : Constants.MAGIC_COLORS) {
				int colorId = Arrays.asList(Constants.MAGIC_COLORS).indexOf(color);
				if (character.getCurrentLocation().clearing.getMagic(colorId)) {
					colors.append(color+" ");
				}
			}
		}
		return colors.toString();
	}	

	private void updateStepDetails(QuestStep step) {
		StringBuffer sb = new StringBuffer();
		sb.append(step.getName());
		sb.append("\n\n");
		sb.append(step.getDescription() == null ? "" : step.getDescription());
		sb.append("\n");
		if (step.getRequirements().size() > 0) {
			sb.append("\nREQUIREMENTS(");
			sb.append(step.getReqType());
			sb.append("):\n");
		}
		for (QuestRequirement req : step.getRequirements()) {
			sb.append("[");
			sb.append(req.getRequirementType());
			sb.append("]: ");
			if (req.isNot()) {
				sb.append("NOT ");
			}
			sb.append(req.toString());
			sb.append("\n");
		}
		if (step.getRewards().size() > 0) {
			sb.append("\nREWARDS:\n");
		}
		for (QuestReward reward : step.getRewards()) {
			sb.append("[");
			sb.append(reward.getRewardType());
			sb.append("]: ");
			sb.append(reward.toString());
			sb.append("\n");
		}
		stepDetails.setText(sb.toString());
		stepDetails.setCaretPosition(0);
	}

	private boolean initQuestTest(String characterName) {
		RealmUtility.resetGame();
		RealmLoader loader = new RealmLoader();
		gameData = loader.getData();
		GameWrapper game = GameWrapper.findGame(gameData);
		game.setMonth(1);
		game.setDay(1);
		HostPrefWrapper hostPrefs = HostPrefWrapper.createDefaultHostPrefs(gameData);
		quest = questToTest.copyQuestToGameData(gameData);

		// TODO Choose gameplay options based on what quest uses (or default to single board and original if none)

		//hostPrefs.setBoardAutoSetup(false);
		//RealmSpeakInit init = new RealmSpeakInit(null);
		//		singleBoard.setSelected(quest.getBoolean(QuestConstants.SINGLE_BOARD));
		//		doubleBoard.setSelected(quest.getBoolean(QuestConstants.DOUBLE_BOARD));
		//		tripleBoard.setSelected(quest.getBoolean(QuestConstants.TRIPLE_BOARD));

		ArrayList<GameVariant> variantChoices = new ArrayList<>();
		if (quest.getBoolean(QuestConstants.VARIANT_ORIGINAL))
			variantChoices.add(GameVariant.ORIGINAL_GAME_VARIANT);
		if (quest.getBoolean(QuestConstants.VARIANT_PRUITTS))
			variantChoices.add(GameVariant.PRUITTS_GAME_VARIANT);
		if (quest.getBoolean(QuestConstants.VARIANT_EXP1))
			variantChoices.add(GameVariant.EXP1_GAME_VARIANT);
		if (quest.getBoolean(QuestConstants.VARIANT_SUPER_REALM))
			variantChoices.add(GameVariant.SUPER_REALM);
		if (variantChoices.isEmpty()) {
			variantChoices.add(GameVariant.ORIGINAL_GAME_VARIANT);
			variantChoices.add(GameVariant.PRUITTS_GAME_VARIANT);
			variantChoices.add(GameVariant.EXP1_GAME_VARIANT);
			variantChoices.add(GameVariant.SUPER_REALM);
		}

		GameVariant useVariant;
		if (variantChoices.size() == 1) {
			useVariant = variantChoices.get(0);
		}
		else {
			ButtonOptionDialog variantChooser = new ButtonOptionDialog(this, null, "Which variant are you testing?", "Choose game variant", false);
			variantChooser.addSelectionObjects(variantChoices);
			variantChooser.setVisible(true);
			useVariant = (GameVariant) variantChooser.getSelectedObject();
		}

		hostPrefs.setGameKeyVals(useVariant.getKeyVals());
		loader.cleanupData(hostPrefs.getGameKeyVals());

		// Choose a character
		GameObject chosen;
		if (characterName == null) {
			GamePool pool = new GamePool(gameData.getGameObjects());
			ArrayList<GameObject> characters = pool.find("character,!" + CharacterWrapper.NAME_KEY);
			characters.addAll(CustomCharacterLibrary.getSingleton().getCharacterTemplateList());
			Collections.sort(characters, new Comparator<GameObject>() {
				public int compare(GameObject go1, GameObject go2) {
					return go1.getName().compareTo(go2.getName());
				}
			});

			CharacterChooser chooser = new CharacterChooser(this, characters, hostPrefs);
			chooser.setVisible(true);
			chosen = chooser.getChosenCharacter();
			if (chosen == null) {
				setVisible(false);
				dispose();
				return false;
			}
		}
		else {
			chosen = gameData.getGameObjectByName(characterName);
		}

		character = new CharacterWrapper(chosen);
		int level = 4;
		character.setStartingLevel(level);
		character.setCharacterLevel(level); // only supports single
											// digit level numbers
											// (for now)
		// Set starting stage based on level and bonus chits
		character.setStartingStage((level * 3));
		character.setCharacterStage((level * 3));
		character.setCharacterExtraChitMarkers((level * 3));
		character.initChits();
		character.fetchStartingInventory(this, gameData, false);
		character.setGold(character.getStartingGold());
		character.setCurrentMonth(game.getMonth());
		character.setCurrentDay(game.getDay());
		TileComponent tile = (TileComponent) RealmComponent.getRealmComponent(gameData.getGameObjectByName("Awful Valley"));
		GamePool pool = new GamePool(gameData.getGameObjects());
		for (GameObject go : pool.find("tile")) {
			go.setAttribute("mapGrid","mapPosition",0);
		}
		ClearingDetail clearing = tile.getClearing(1);
		clearing.add(character.getGameObject(), character);
		character.startNewDay(RealmCalendar.getCalendar(gameData), hostPrefs);

		quest.reset();
		character.addQuest(this, quest);
		quest.setState(QuestState.Assigned, character.getCurrentDayKey(), character);
		quest.testRequirements(this, character, new QuestRequirementParams());

		// If quest lacks any "Activate" requirements, then activate by default
		if (!quest.isActivateable()) {
			quest.setState(QuestState.Active, character.getCurrentDayKey(), character);
			retestQuest();
		}

		questName.setIcon(RealmComponent.getRealmComponent(quest.getGameObject()).getFaceUpIcon());
		questDescription.setText(quest.getDescription());
		questDescription.setCaretPosition(0);
		questStepView.updateSteps(quest.getSteps());
		activateButton.setEnabled(quest.getState() == QuestState.Assigned);
		updateCharacterPanel();
		return true;
	}

	private void retestQuest() {
		retestQuest(new QuestRequirementParams());
	}

	private void retestQuest(QuestRequirementParams params) {
		params.timeOfCall = getTimeOfCallFromOptions();
		debugOutput.setText("");
		if (quest.testRequirements(this, character, params)) {
			character.testQuestRequirements(this); // Make sure that all quests get updated (auto-journal)
			questStepView.repaint();
			questName.setIcon(RealmComponent.getRealmComponent(quest.getGameObject()).getFaceUpIcon());

			if (quest.getState() == QuestState.Complete) {
				showQuestCompleted();
			}
		}
		questStepView.repaint();
		
		character.distributeMonsterControlInCurrentClearing(false);
		
		updateCharacterPanel();
		debugOutput.setCaretPosition(0); // why doesn't this work?
	}

	private GamePhaseType getTimeOfCallFromOptions() {
		if (birdsongTime.isSelected())
			return GamePhaseType.Birdsong;
		if (phaseTime.isSelected())
			return GamePhaseType.EndOfPhase;
		if (turnTime.isSelected())
			return GamePhaseType.EndOfTurn;
		if (eveningTime.isSelected())
			return GamePhaseType.StartOfEvening;
		if (midnightTime.isSelected())
			return GamePhaseType.Midnight;
		return GamePhaseType.Unspecified;
	}
	
	private void showQuestCompleted() {
		JOptionPane.showMessageDialog(this, "Quest is Complete!", "Quest Complete", JOptionPane.INFORMATION_MESSAGE, RealmComponent.getRealmComponent(quest.getGameObject()).getFaceUpIcon());
	}

	private void updateCharacterPanel() {
		charName.setText(character.getCharacterName());
		charName.setIcon(RealmComponent.getRealmComponent(character.getGameObject()).getSmallIcon());
		currentLocation.setText(character.getCurrentLocation().toString());
		currentDay.setText(character.getCurrentDayKey());
		currentWeather.setText(RealmCalendar.getCalendar(character.getGameData()).getWeatherTypeName(character.getCurrentMonth()));
		gtAmount.setText(String.valueOf(character.getGreatTreasureScore().getOwnedPoints()));
		fameAmount.setText(String.valueOf((int) character.getFame()));
		notorietyAmount.setText(String.valueOf((int) character.getNotoriety()));
		goldAmount.setText(String.valueOf((int) character.getGold()));
		fatigue.setText(String.valueOf(character.getWeatherFatigue()));
		wounds.setText(String.valueOf(character.getExtraWounds()));
		guild.setText(character.getCurrentGuildLevelName());

		activeInventory.setListData(new Vector<>(character.getActiveInventory()));
		inactiveInventory.setListData(new Vector<>(character.getInactiveInventory()));
		hirelings.setListData(new Vector<>(character.getAllHirelings()));
		journalList.setListData(new Vector<>(quest.getJournalEntries()));

		clearingTitle.setText(character.getCurrentLocation().toString()+getEnchanted()+" "+getMagicColors());
		
		Vector<RealmComponent> rcs = new Vector<>();
		for (RealmComponent rc : character.getCurrentLocation().clearing.getClearingComponents(true)) {
			if (rc.isCharacter())
				continue;
			rcs.add(rc);
		}
		clearingComponents.setListData(rcs);
		updateClearingButtons();
		updateHirelingsButtons();
	}
	
	private void updateClearingButtons() {
		RealmComponent rc = clearingComponents.getSelectedValue();
		pickupFromClearingButton.setEnabled(rc != null);
		removeFromClearingButton.setEnabled(rc != null);
		discoverButton.setEnabled(rc != null);
		openLocationButton.setEnabled(rc != null && rc.isTreasureLocation());
		killDenizenButton.setEnabled(rc != null && (rc.isMonster() || rc.isNative()));
		searchClearingButton.setEnabled(true); // always on?
	}

	private void toggleHidden() {
		if (character.isHidden()) {
			character.setHidden(false);
		}
		else {
			character.setHidden(true);
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Hide;
			character.testQuestRequirements(QuestTesterFrame.this,params);
		}
		retestQuest();
	}
	
	private void toggleFlying() {
		if (character.getCurrentLocation().isFlying()) {
			character.getCurrentLocation().setFlying(false);
			character.getGameObject().removeThisAttribute("isflying");
		}
		else {
			character.getCurrentLocation().setFlying(true);
			character.getGameObject().setThisAttribute("isflying");
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Fly;
			character.testQuestRequirements(QuestTesterFrame.this,params);
		}
		retestQuest();
	}
	
	private void runAway() {
		ClearingDetail clearing = chooseNewLocationDialog(true);
		if (clearing == null) {
			return;
		}
		
		// All following hirelings need to remain behind
		TileLocation oldLocation = character.getCurrentLocation();
		if (oldLocation != null) {
			for (RealmComponent hireling : character.getFollowingHirelings()) {
				oldLocation.clearing.add(hireling.getGameObject(),null);
				if (hireling.getGameObject().hasThisAttribute(Constants.CAPTURE)) {
					character.removeHireling(hireling.getGameObject());
				}
			}
		}
		
		ClearingUtility.moveToLocation(character.getGameObject(),clearing.getTileLocation(),true);
		character.addMoveHistory(character.getCurrentLocation());
		updateCharacterPanel();
		
		QuestRequirementParams params = new QuestRequirementParams();
		params.actionType = CharacterActionType.Move;
		character.testQuestRequirements(QuestTesterFrame.this, params);
		
		retestQuest();
	}
		
	private void chooseNewLocation() {
		ClearingDetail clearing = chooseNewLocationDialog(false);
		if (clearing != null) {
			character.moveToLocation(this, clearing.getTileLocation());
			updateCharacterPanel();
			
			QuestRequirementParams params = new QuestRequirementParams();
			params.actionType = CharacterActionType.Move;
			character.testQuestRequirements(QuestTesterFrame.this, params);
			
			retestQuest();
		}
	}
		
	private ClearingDetail chooseNewLocationDialog(boolean runAway) {
		GamePool pool = new GamePool(gameData.getGameObjects());
		Hashtable<String, ClearingDetail> hash = new Hashtable<String, ClearingDetail>();
		Vector<String> locationNames = new Vector<String>();
		for (GameObject go : pool.find("tile")) {
			TileComponent tile = (TileComponent) RealmComponent.getRealmComponent(go);
			for (ClearingDetail clearing : tile.getClearings()) {
				String key = clearing.getTileLocation().toString();
				locationNames.add(key);
				hash.put(key, clearing);
			}
		}
		Collections.sort(locationNames);
		
		String headline = "Change Location";
		String text = "Select new Location:";
		if (runAway) {
			headline = "Run Away";
			text = "Run towards which clearing?";
		}
		
		ButtonOptionDialog dialog = new ButtonOptionDialog(this, null, text, headline, true, 6);
		dialog.addSelectionObjects(locationNames);
		dialog.setVisible(true);

		String val = (String) dialog.getSelectedObject();
		if (val==null) {
			return null;
		}
		return hash.get(val);
	}

	private void exitApp() {
		// Should check all GameData windows, and verify that changes have been
		// saved
		setVisible(false);
		dispose();
		System.exit(0);
	}

	static String SEARCH_RESULT_NOTHING = "Nothing";
	static String SEARCH_RESULT_SOMETHING = "Something";
	static String SEARCH_RESULT_TREASURE = "Specific Treasure";
	static String SEARCH_RESULT_SPELL = "Specific Spell";

	private void doSearchOn(RealmComponent rc) {
		ButtonOptionDialog dialog = new ButtonOptionDialog(this, rc.getFaceUpIcon(), "Choose a search table:", "Search Table");
		dialog.addSelectionObjectArray(SearchTableType.values());
		dialog.setVisible(true);
		SearchTableType table = (SearchTableType) dialog.getSelectedObject();
		if (table == null)
			return;
		dialog = new ButtonOptionDialog(this, rc.getFaceUpIcon(), "Choose a search result:", "Search Result");
		dialog.addSelectionObjectArray(SearchResultType.getSearchResultTypes(table));
		dialog.setVisible(true);
		SearchResultType result = (SearchResultType) dialog.getSelectedObject();
		if (result == null)
			return;

		dialog = new ButtonOptionDialog(this, rc.getFaceUpIcon(), "What kind of gain?", "Search Gain", false);
		dialog.addSelectionObject(SEARCH_RESULT_NOTHING);
		dialog.addSelectionObject(SEARCH_RESULT_SOMETHING);
		if (result.canGetTreasure()) {
			dialog.addSelectionObject(SEARCH_RESULT_TREASURE);
		}
		if (result.canGetSpell()) {
			dialog.addSelectionObject(SEARCH_RESULT_SPELL);
		}
		dialog.setVisible(true);
		String gain = (String) dialog.getSelectedObject();

		QuestRequirementParams params = new QuestRequirementParams();
		params.actionType = CharacterActionType.SearchTable;
		params.searchType = result;
		params.targetOfSearch = rc.getGameObject();
		params.actionName = table.toString();
		if (SEARCH_RESULT_SOMETHING.equals(gain)) {
			params.searchHadAnEffect = true;
		}
		else if (SEARCH_RESULT_TREASURE.equals(gain)) {
			params.searchHadAnEffect = true;
			ArrayList<GameObject> stuff = chooseItem();
			if (stuff == null || stuff.size() == 0)
				return;
			params.objectList = stuff;
			for (GameObject thing : stuff) {
				Loot loot = new Loot(this, character, rc.getGameObject(), null);
				loot.handleSpecial(character, thing, true);
				//Loot.addItemToCharacter(this,null,character,thing);
			}
		}
		else if (SEARCH_RESULT_SPELL.equals(gain)) {
			params.searchHadAnEffect = true;
			ArrayList<GameObject> stuff = chooseOther("Spells", "spell,learnable");
			if (stuff == null || stuff.size() == 0)
				return;
			params.objectList = stuff;
			for (GameObject spell : stuff) {
				character.recordNewSpell(this, spell, true); // force learn?
			}
		}
		retestQuest(params);
	}
	
	private void updateTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				debugOutput.append(text);
			}
		});
	}

	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		//System.setErr(new PrintStream(out, true));
	}

	private static JScrollPane makeTitledScrollPane(String title, JComponent c) {
		JScrollPane sp = new JScrollPane(c);
		sp.setBorder(BorderFactory.createTitledBorder(title));
		return sp;
	}

	private static String getKey(GameObject go) {
		return go.getName() + " [" + go.getId() + "]";
	}

	private class QuestListRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			GameObject go = null;
			if (value instanceof RealmComponent) {
				go = ((RealmComponent) value).getGameObject();
			}
			else if (value instanceof GameObject) {
				go = (GameObject) value;
			}
			if (go != null) {
				StringBuffer sb = new StringBuffer();
				sb.append(getKey(go));
				if (character.hasTreasureLocationDiscovery(go.getName())) {
					sb.append(" - Discovered");
				}
				setText(sb.toString());
			}
			return this;
		}
	}

	private class HirelingListRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			GameObject go = null;
			if (value instanceof RealmComponent) {
				go = ((RealmComponent) value).getGameObject();
			}
			else if (value instanceof GameObject) {
				go = (GameObject) value;
			}
			if (go != null) {
				StringBuffer sb = new StringBuffer();
				sb.append(getKey(go));
				int days = go.getInt(RealmComponent.REALMCOMPONENT_BLOCK, RealmComponent.OWNER_TERM_OF_HIRE);
				if (days < 1000) {
					sb.append(" for ");
					sb.append(days);
					sb.append(" day");
					sb.append(days == 1 ? "." : "s");
				}
				else {
					sb.append(" (permanent)");
				}
				if (go.getHeldBy() == character.getGameObject()) {
					sb.append(" (following)");
				}
				
				setText(sb.toString());
			}
			return this;
		}
	}

	private class JournalEntryListRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			QuestJournalEntry entry = (QuestJournalEntry) value;
			setText(entry.getText());
			return this;
		}
	}

	private class ForceTextToggle extends JToggleButton {

		Color disabledTextColor = UIManager.getColor("Button.disabledText");

		private String text;

		public ForceTextToggle(String text) {
			super(""); // give NO text to the button itself!
			this.text = text;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Dimension size = getSize();
			g.setColor(isEnabled() ? getForeground() : disabledTextColor); // this isn't tested!!  7/5/2005
			GraphicsUtil.drawCenteredString(g, 0, 0, size.width, size.height, text);
		}
	}

	public static void main(String[] args) {
		ComponentTools.setSystemLookAndFeel();
		RealmUtility.setupTextType();

		GameData data = new GameData();
		data.ignoreRandomSeed = true;
		File file = (args.length > 0 && args[0].trim().length() > 0) ? new File(args[0]) : null;
		if (file != null && data.zipFromFile(file)) {
			Quest aQuest = new Quest(data.getGameObjects().iterator().next());
			aQuest.autoRepair(); // Just in case

			final QuestTesterFrame frame = new QuestTesterFrame(aQuest, "Berserker");
			if (!frame.ready)
				return;
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent ev) {
					frame.exitApp();
				}
			});
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
		else {
			if (args.length == 0) {
				System.out.println("No quest file provided!");
			}
			else {
				System.out.println("Unable to open quest: " + args[0]);
			}
		}
	}
}