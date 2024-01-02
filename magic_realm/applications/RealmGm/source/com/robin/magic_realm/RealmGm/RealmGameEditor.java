package com.robin.magic_realm.RealmGm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;

import com.robin.game.objects.*;
import com.robin.general.swing.ComponentTools;
import com.robin.general.swing.IconFactory;
import com.robin.general.swing.ListChooser;
import com.robin.general.swing.TableSorter;
import com.robin.magic_realm.RealmBattle.BattlesWrapper;
import com.robin.magic_realm.RealmBattle.RealmBattle;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestLoader;
import com.robin.magic_realm.components.swing.*;
import com.robin.magic_realm.components.table.Loot;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class RealmGameEditor extends JInternalFrame {
	
	private RealmGmFrame parent;
	private GameData gameData;
	private String title;
	private TileLocation battleLocation;
	
	private CenteredMapView map;
	
	private ArrayList<CharacterWrapper> characters;
	private ArrayList<CharacterEditRibbon> characterPage = new ArrayList<CharacterEditRibbon>();
	private JTabbedPane characterTabs;
	private JButton addCharacter;
	private JButton removeCharacter;
	private JButton killCharacter;
	private JButton reviveCharacter;
	private ArrayList<RealmComponent> thingsWithLocations;
	private ArrayList<RealmComponent> thingsWithLocationsFiltered;
	private Box filterToolbar;
	private JPanel locationEditToolbar;
	private ArrayList<TileComponent> tiles;
	private ArrayList<Quest> quests;
	
	private JTable locationTable;
	private JTable questTable;
	private JButton showChanges;
	private JButton revertChanges;
	
	// Actions
	private Action makeDeadAction;
	private Action toggleHiddenAction;
	private Action toggleBlockedAction;
	private Action hireAction;
	private Action unhireAction;
	private Action makePeaceAction;
	
	// Locations
	private Action setupCardAction;
	private Action toClearingAction;
	private Action toRoadAction;
	private Action toOffroadAction;
	private Action toTileAction;
	private Action toBetweenTileAction;
	private Action leaderAction;
	
	// Treasure
	private Action makeDropped;
	private Action makeAbandoned;
	private Action makeFaceDown;
	private Action makeFaceUp;
	private Action makeDamaged;
	private Action makeRepaired;
	private Action toggleAlerted;
	
	// Quests
	private Action addQuest;
	private Action removeQuest;
	private Action assignQuest;
	private Action unassignQuest;
	private Action resetQuest;
	
	// Character
	private final String level_9_advantage = "LEVEL 9 BONUS:  Gets bonus phase every day.";
	
	public RealmGameEditor(RealmGmFrame frame,String title,GameData gameData) {
		super(title,true,true,true,true);
		
		// before setting tracking changes here, make sure all character action chits are dark side up
		for(GameObject go:gameData.getGameObjects()) {
			if (go.hasThisAttribute("action")) {
				go.setThisAttribute("facing","dark");
			}
		}
		
		gameData.setTracksChanges(true);
		RealmUtility.resetGame();
		CenteredMapView.clearTileLayer();
		CenteredMapView.initSingleton(gameData);
		this.parent = frame;
		this.gameData = gameData;
		this.title = title;
		gameData.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				parent.updateControls();
				updateModified();
			}
		});
		readData();
		initComponents();
	}
	public GameData getGameData() {
		return gameData;
	}
	private void readData() {
		thingsWithLocations = new ArrayList<RealmComponent>();
		thingsWithLocationsFiltered = new ArrayList<RealmComponent>();
		characters = new ArrayList<CharacterWrapper>();
		tiles = new ArrayList<TileComponent>();
		for (GameObject go : gameData.getGameObjects()) {
			if (go.hasThisAttribute(RealmComponent.CHARACTER) && !go.hasAttribute(RealmComponent.REALMCOMPONENT_BLOCK,RealmComponent.OWNER_ID)) {
				continue;
			}
			if (go.hasThisAttribute("animal") || go.hasThisAttribute("statue")) {
				continue;
			}
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc!=null) {
				if (!rc.isTile()
						&& !rc.isMonsterPart()
						&& !rc.isActionChit()
						&& !rc.isSpell()
						&& !rc.isNativeHorse()
						) {
					TileLocation tl = ClearingUtility.getTileLocation(rc);
					if (tl!=null
							|| rc.isMonster()
							|| rc.isNative()
							|| rc.isItem()
							|| rc.isStateChit()
							|| (rc.isGoldSpecial() && go.getHeldBy()!=null)
							|| go.hasThisAttribute(Constants.DEAD)) {
						thingsWithLocations.add(rc);
					}
				}
				if (rc.isCharacter() && CharacterWrapper.hasPlayerBlock(go)) {
					CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
					characters.add(character);
				}
				if (rc.isTile() && go.hasAttribute("mapGrid","mapposition")) {
					tiles.add((TileComponent)rc);
				}
			}
		}
		Collections.sort(characters,new Comparator<CharacterWrapper>() {
			public int compare(CharacterWrapper c1,CharacterWrapper c2) {
				return c1.getName().compareTo(c2.getName());
			}
		});
		BattlesWrapper battles = RealmBattle.getBattles(gameData);
		if (battles!=null) {
			battleLocation = battles.getCurrentBattleLocation(gameData);
		}
		updateFilter(null);
	}
	private Box buildFilterToolbar() {
		ArrayList<String> uniqueTypes = new ArrayList<String>();
		for(RealmComponent rc:thingsWithLocations) {
			String type = rc.getName();
			if (uniqueTypes.contains(type)) continue;
			uniqueTypes.add(type);
		}
		Collections.sort(uniqueTypes);
		ButtonGroup group = new ButtonGroup();
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("Show:"));
		
		int cols = 6;
		int rows = (uniqueTypes.size()/cols)+1;
		
		JPanel grid = new JPanel(new GridLayout(rows,cols));
		
		JToggleButton toggle = new JToggleButton("ALL",true);
		toggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				updateFilter(null);
			}
		});
		group.add(toggle);
		grid.add(toggle);
		
		for(String uniqueType:uniqueTypes) {
			toggle = new JToggleButton(uniqueType);
			toggle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					JToggleButton me = (JToggleButton)ev.getSource();
					String filterType = me.getText();
					updateFilter(filterType);
				}
			});
			group.add(toggle);
			grid.add(toggle);
		}
		box.add(grid);
		box.add(Box.createHorizontalGlue());
		return box;
	}
	private void updateFilter(String filterType) {
		thingsWithLocationsFiltered.clear();
		for(RealmComponent rc:thingsWithLocations) {
			if (filterType==null || rc.getName().equals(filterType)) {
				thingsWithLocationsFiltered.add(rc);
			}
		}
		if (locationTable!=null) {
			locationTable.clearSelection();
			locationTable.revalidate();
			locationTable.repaint();
		}
	}
	public void reinitMap() {
		map.setReplot(true);
		map.updateTilesStyle();
		map.repaint();
	}
	public CenteredMapView getMap() {
		return map;
	}
	
	private void initComponents() {
		updateTitle();
		setSize(800,600);
		setLayout(new BorderLayout());
		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Characters",buildCharacterEditorTab());
		tabs.add("Game Pieces",buildLocationEditorTab());
		map = new CenteredMapView(gameData,true,true);
		map.setScale(0.5);
		map.centerMap();
		tabs.add("Map",map);
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
		if (hostPrefs.isUsingQuests()) {
			tabs.add("Quests",buildQuestTab(hostPrefs.getQuestMode()));
		}
		add(tabs,BorderLayout.CENTER);
		Box box = Box.createHorizontalBox();
		revertChanges = new JButton("Revert Changes");
		revertChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (!gameData.hasChanges()) return;
				int ret = JOptionPane.showConfirmDialog(parent,"Revert changes will undo anything you have done since the last save.  Are you sure?");
				if (ret!=JOptionPane.YES_OPTION) return;
				gameData.rollback();
				locationTable.revalidate();
				locationTable.repaint();
				for(CharacterEditRibbon ribbon:characterPage) {
					ribbon.refresh();
				}
			}
		});
		box.add(revertChanges);
		box.add(Box.createHorizontalGlue());
		showChanges = new JButton("Show Changes");
		showChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				StringBuilder sb = new StringBuilder();
				for(GameObjectChange change:gameData.getObjectChanges()) {
					if (change instanceof GameBumpVersionChange) continue; // no need to see these
					sb.append(change.toString());
					sb.append("\n");
				}
				JTextArea area = new JTextArea();
				area.setText(sb.toString());
				area.setEditable(false);
				JScrollPane sp = new JScrollPane(area);
				ComponentTools.lockComponentSize(sp,600,600);
				JOptionPane.showMessageDialog(parent,sp,"Object Changes",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		box.add(showChanges);
		add(box,BorderLayout.SOUTH);
		updateControls();
	}
	public void updateModified() {
		updateTitle();
		parent.updateControls();
	}
	public void setTitle(String title) {
		this.title = title;
		updateTitle();
	}
	private void updateTitle() {
		super.setTitle((gameData.isModified()?"* ":"")+title);
	}
	private JPanel buildCharacterEditorTab() {
		JPanel panel = new JPanel(new BorderLayout());
		characterTabs = new JTabbedPane(SwingConstants.LEFT);
		characterTabs.setFont(new Font("Dialog",Font.PLAIN,24));
		characterTabs.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	        	updateCharacterButtons();
	        }
	    });
		panel.add(characterTabs,BorderLayout.CENTER);
		Box box = Box.createHorizontalBox();
		addCharacter = new JButton("Add Character");
		addCharacter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(parent,"Add Character",true);
				GamePool pool = new GamePool(gameData.getGameObjects());
				for (GameObject go:pool.find("character")) {
					if (!CharacterWrapper.hasPlayerBlock(go)) {
						chooser.addRealmComponent(RealmComponent.getRealmComponent(go));
					}
				}
				chooser.setVisible(true);
				RealmComponent rc = chooser.getFirstSelectedComponent();
				if (rc==null) return;
				
				GameObject characterGo = rc.getGameObject();
				CharacterWrapper character = new CharacterWrapper(characterGo);
				String[] levels = character.getCharacterLevels();
				ListChooser levelChooser = new ListChooser(parent, "Select level for "+rc.toString(),  levels);
				levelChooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				levelChooser.setDoubleClickEnabled(true);
				levelChooser.setLocationRelativeTo(null);
				levelChooser.setVisible(true);
				Object selectedLevel = levelChooser.getSelectedItem();
				if (selectedLevel == null) return;
				
				String levelString = selectedLevel.toString().substring(0,2).trim();
				HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
				int level = Integer.valueOf(levelString.toString());
				if (level >= 9) {
					character.getGameObject().setThisAttribute("extra_phase");
					character.getGameObject().addAttributeListItem("level_4", "advantages", level_9_advantage);
				}
				if (characterGo.hasThisAttribute(Constants.CUSTOM_CHARACTER)) {
					GameObject newChar = gameData.createNewObject();
					newChar.copyAttributesFrom(characterGo);
					RealmComponent.clearOwner(newChar);
					newChar.setThisKeyVals(hostPrefs.getGameKeyVals());
					for (GameObject go : characterGo.getHold()) {
						if (go.hasThisAttribute("character_chit")) {
							GameObject newChit = gameData.createNewObject();
							newChit.copyAttributesFrom(go);
							newChit.setThisKeyVals(hostPrefs.getGameKeyVals());
							newChar.add(newChit);
						}
					}
					characterGo = newChar;
				}
				GameObject borderland = gameData.getGameObjectByName("Borderland");
				TileComponent borderlandTile = (TileComponent)RealmComponent.getRealmComponent(borderland);
				borderlandTile.getClearing(1).add(character.getGameObject(),null);
				character.setPlayerName("Player");
				character.setPlayerPassword("");
				character.setPlayerEmail("");
				character.setStartingLevel(level);
				character.setCharacterLevel(level);
				character.updateLevelAttributes(hostPrefs);
				character.initChits();
				character.fetchStartingInventory(parent,gameData,false);
				character.clearRelationships(hostPrefs);
				character.initRelationships(hostPrefs);
				character.setGold(character.getStartingGold());	
				character.setHidden(true);
				character.initializeVpsSetup(hostPrefs,level,RealmCalendar.getCalendar(gameData));
				RealmUtility.fetchStartingSpells(parent,character,gameData,false);
				character.tagUnplayableChits(); // this need only happen once, because it examines ALL the chits
				character.updateLevelAttributes(hostPrefs);
				character.applyMidnight();
				character.calculateStartingWorth();
				characters.add(character);
				Collections.sort(characters,new Comparator<CharacterWrapper>() {
					public int compare(CharacterWrapper c1,CharacterWrapper c2) {
						return c1.getName().compareTo(c2.getName());
					}
				});
				updateCharacterEditorTabs();
				readData();
				updateFilter(null);
				locationEditToolbar.remove(filterToolbar);
				filterToolbar = buildFilterToolbar();
				locationEditToolbar.add(filterToolbar,BorderLayout.CENTER);
			}
		});
		box.add(addCharacter);
		removeCharacter = new JButton("Remove Character");
		removeCharacter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = characterTabs.getSelectedIndex();
				if (selectedRow < 0) return;
				CharacterWrapper character = characters.get(selectedRow);
				character.getGameObject().removeThisAttribute(Constants.DEAD);
				
				if (character.getGameObject().hasAttributeListItem("level_4", "advantages", level_9_advantage)) {
					character.getGameObject().removeThisAttribute("extra_phase");
					character.getGameObject().removeAttributeListItem("level_4", "advantages", level_9_advantage);
				}
				
				character.moveToLocation(null,null);
				character.clearMoveHistory();
				character.clearPlayerAttributes();
				characters.remove(character);
				updateCharacterEditorTabs();
				readData();
				updateFilter(null);
			}
		});
		box.add(removeCharacter);
		killCharacter = new JButton("Kill Character");
		killCharacter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = characterTabs.getSelectedIndex();
				if (selectedRow < 0) return;
				CharacterWrapper character = characters.get(selectedRow);
				TileLocation tl = ClearingUtility.getTileLocation(character.getGameObject());
				if (tl!=null && tl.isInClearing()) {
					RealmUtility.makeDead(RealmComponent.getRealmComponent(character.getGameObject()));
				}
				updateCharacterEditorTabs();
				updateFilter(null);
			}
		});
		box.add(killCharacter);
		reviveCharacter = new JButton("Revive Character to Borderland");
		reviveCharacter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = characterTabs.getSelectedIndex();
				if (selectedRow < 0) return;
				CharacterWrapper character = characters.get(selectedRow);
				character.getGameObject().removeThisAttribute(Constants.DEAD);
				GameObject borderland = gameData.getGameObjectByName("Borderland");
				TileComponent borderlandTile = (TileComponent)RealmComponent.getRealmComponent(borderland);
				borderlandTile.getClearing(1).add(character.getGameObject(),null);
				updateCharacterEditorTabs();
				updateFilter(null);
			}
		});
		box.add(reviveCharacter);
		box.add(Box.createHorizontalGlue());
		panel.add(box,BorderLayout.SOUTH);
		updateCharacterEditorTabs();
		return panel;
	}
	private void updateCharacterButtons() {
		int selectedRow = characterTabs.getSelectedIndex();
		CharacterWrapper selectedCharacter = null;
		if (selectedRow >= 0) {
			selectedCharacter = characters.get(selectedRow);
		}
		addCharacter.setEnabled(true);
		removeCharacter.setEnabled(selectedCharacter!=null);
		killCharacter.setEnabled(selectedCharacter!=null && !selectedCharacter.isDead());
		reviveCharacter.setEnabled(selectedCharacter!=null && selectedCharacter.isDead());
	}
	private void updateCharacterEditorTabs() {
		characterPage.clear();
		characterTabs.removeAll();
		for(CharacterWrapper character:characters) {
			CharacterEditRibbon ribbon = new CharacterEditRibbon(parent,character);
			characterPage.add(ribbon);
			characterTabs.add(character.getName(),ribbon);
		}
		updateCharacterButtons();
	}
	private JPanel buildLocationEditorTab() {
		JPanel panel = new JPanel(new BorderLayout());
		locationTable = new JTable(new ChitLocationTableModel());
		locationTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateControls();
			}
		});
		TableSorter.makeSortable(locationTable);
		locationEditToolbar = new JPanel(new BorderLayout());
		JPanel controls = new JPanel(new GridLayout(3,1));
		controls.add(buildOtherToolbar());
		controls.add(buildLocationEditorToolbar());
		controls.add(buildTreasureLayToolbar());
		locationEditToolbar.add(controls,BorderLayout.NORTH);
		filterToolbar = buildFilterToolbar();
		locationEditToolbar.add(filterToolbar,BorderLayout.CENTER);
		JLabel sortMessage = new JLabel("Click any header to sort piece table:");
		sortMessage.setForeground(Color.red);
		locationEditToolbar.add(sortMessage,BorderLayout.SOUTH);
		panel.add(locationEditToolbar,BorderLayout.NORTH);
		panel.add(new JScrollPane(locationTable),BorderLayout.CENTER);
		return panel;
	}
	private ArrayList<RealmComponent> getSelectedComponents() {
		ArrayList<RealmComponent> selected = new ArrayList<RealmComponent>();
		TableSorter sorter = TableSorter.getSorter(locationTable);
		for (int viewRow:locationTable.getSelectedRows()) {
			int index = sorter.convertRowIndexToModel(viewRow);
			selected.add(thingsWithLocationsFiltered.get(index));
		}
		return selected;
	}
	private Box buildOtherToolbar() {
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("Conditions:"));
		makeDeadAction = new AbstractAction("Make Dead") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					if (rc.isMonster() || rc.isNative() || rc.isCharacter()) {
						TileLocation tl = ClearingUtility.getTileLocation(rc.getGameObject());
						if (tl!=null && tl.isInClearing()) {
							RealmUtility.makeDead(rc);
						}
					}
				}
				locationTable.revalidate();
				locationTable.repaint();
			}
		};
		box.add(new JButton(makeDeadAction));
		toggleHiddenAction = new AbstractAction("Toggle Hidden") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					if (rc.isMonster() || rc.isNative() || rc.isCharacter()) {
						rc.setHidden(!rc.isHidden());
					}
				}
				map.setReplot(true);
				locationTable.revalidate();
				locationTable.repaint();
			}
		};
		box.add(new JButton(toggleHiddenAction));
		toggleBlockedAction = new AbstractAction("Toggle Blocked") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					if (CharacterWrapper.hasPlayerBlock(rc.getGameObject())) { // hired natives too
						CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
						character.setBlocked(!character.isBlocked());
					}
					else if (rc.isMonster()) {
						MonsterChitComponent monster = (MonsterChitComponent)rc;
						monster.setBlocked(!monster.isBlocked());
					}
				}
				locationTable.revalidate();
				locationTable.repaint();
			}
		};
		box.add(new JButton(toggleBlockedAction));
		hireAction = new AbstractAction("Hire") {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent leader = chooseLeader("Hire selections to which character?",true);
				if (leader!=null) {
					CharacterWrapper character = new CharacterWrapper(leader.getGameObject());
					String val = JOptionPane.showInputDialog("Hire term (days)?",14);
					if (val!=null) {
						int term = Integer.valueOf(val);
						for (RealmComponent rc:getSelectedComponents()) {
							if (rc.isNative() || rc.isMonster()) {
								SetupCardUtility.resetDenizen(rc.getGameObject());							
								character.addHireling(rc.getGameObject(),term);
							}
						}
						locationTable.revalidate();
						locationTable.repaint();
					}
				}
			}
		};
		box.add(new JButton(hireAction));
		unhireAction = new AbstractAction("Clear Owner") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					RealmComponent owner = rc.getOwner();
					if (owner.isCharacter()) {
						(new CharacterWrapper(owner.getGameObject())).removeHireling(rc.getGameObject());
					}
				}
				locationTable.revalidate();
				locationTable.repaint();
			};
		};
		box.add(new JButton(unhireAction));
		makePeaceAction = new AbstractAction("Make Peace") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					if (isBattling(rc)) {
						CombatWrapper combat = new CombatWrapper(rc.getGameObject());
						combat.setPeace(true);
						rc.clearTargets();
					}
				}
				locationTable.revalidate();
				locationTable.repaint();
			}
		};
		box.add(new JButton(makePeaceAction));
		return box;
	}
	private Box buildLocationEditorToolbar() {
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("Locations:"));
		setupCardAction = new AbstractAction("To Setup Card") {
			public void actionPerformed(ActionEvent ev) {
				moveSelectionsToSetupCard();
			}
		};
		box.add(new JButton(setupCardAction));
		toClearingAction = new AbstractAction("To Clearing") {
			public void actionPerformed(ActionEvent ev) {
				CenteredMapView.getSingleton().markAllClearings(true);
				moveSelectionsToLocation(chooseTileLocation("Select a clearing"));
			}
		};
		box.add(new JButton(toClearingAction));
		toRoadAction = new AbstractAction("To Road") {
			public void actionPerformed(ActionEvent ev) {
				CenteredMapView.getSingleton().markAllClearings(true);
				TileLocation road = chooseTileLocation("Select start clearing");
				CenteredMapView.getSingleton().markClearingConnections(road.clearing,true);
				TileLocation end = chooseTileLocation("Select end clearing");
				road.setOther(end);
				moveSelectionsToLocation(road);
			}
		};
		box.add(new JButton(toRoadAction));
		toOffroadAction = new AbstractAction("To Offroad") {
			public void actionPerformed(ActionEvent ev) {
				CenteredMapView.getSingleton().markAllTiles(true);
				TileLocation tl = chooseTileLocation("Select an offroad tile");
				tl.setFlying(false);
				moveSelectionsToLocation(tl);
			}
		};
		box.add(new JButton(toOffroadAction));
		toTileAction = new AbstractAction("To Tile") {
			public void actionPerformed(ActionEvent ev) {
				CenteredMapView.getSingleton().markAllTiles(true);
				TileLocation tl = chooseTileLocation("Select a tile");
				tl.setFlying(true);
				moveSelectionsToLocation(tl);
			}
		};
		box.add(new JButton(toTileAction));
		toBetweenTileAction = new AbstractAction("To Tiles") {
			public void actionPerformed(ActionEvent ev) {
				CenteredMapView.getSingleton().markAllTiles(true);
				TileLocation between = chooseTileLocation("Select start tile");
				CenteredMapView.getSingleton().markAdjacentTiles(between.tile,true);
				TileLocation end = chooseTileLocation("Select end tile");
				between.setOther(end);
				between.setFlying(true);
				moveSelectionsToLocation(between);
			}
		};
		box.add(new JButton(toBetweenTileAction));
		
		leaderAction = new AbstractAction("To Leader/Character") {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent leader = chooseLeader("Move selections to which leader?",false);
				if (leader!=null) {
					moveSelectionsToLocation(leader.getCurrentLocation());
					moveSelectionsToLeader(leader);
				}
			}
		};
		box.add(new JButton(leaderAction));
		
		return box;
	}
	private Box buildTreasureLayToolbar() {
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("Treasures:"));
		makeDropped = new AbstractAction("Make Dropped") {
			public void actionPerformed(ActionEvent ev) {
				RealmComponent leader = chooseLeader("Which character dropped it?",true);
				if (leader!=null) {
					for (RealmComponent rc:getSelectedComponents()) {
						if (rc.isItem()) {
							rc.getGameObject().setThisAttribute(Constants.PLAIN_SIGHT);
							rc.getGameObject().setThisAttribute(Constants.DROPPED_BY,leader.getGameObject().getStringId());
						}
					}
				}
			}
		};
		box.add(new JButton(makeDropped));
		makeAbandoned = new AbstractAction("Make Abandoned") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					if (rc.isItem()) {
						rc.getGameObject().removeThisAttribute(Constants.PLAIN_SIGHT);
					}
				}
			}
		};
		box.add(new JButton(makeAbandoned));
		makeFaceDown = new AbstractAction("Make Face Down") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					if (rc.isTreasure()) {
						TreasureCardComponent treasure = (TreasureCardComponent)rc;
						if (treasure.isFaceUp()) {
							treasure.getCurrentLocation().tile.doRepaint();
							treasure.setFaceDown();
						}
					}
				}
				map.setReplot(true);
			}
		};
		box.add(new JButton(makeFaceDown));
		makeFaceUp = new AbstractAction("Make Face Up") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					if (rc.isTreasure()) {
						TreasureCardComponent treasure = (TreasureCardComponent)rc;
						if (!treasure.isFaceUp()) {
							treasure.getCurrentLocation().tile.doRepaint();
							treasure.setFaceUp();
						}
					}
				}
				map.setReplot(true);
			}
		};
		box.add(new JButton(makeFaceUp));
		makeDamaged = new AbstractAction("Make Damaged") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					if (rc.isArmor()) {
						ArmorChitComponent armor = (ArmorChitComponent)rc;
						if (!armor.isDamaged()) armor.setIntact(false);
					}
				}
				map.setReplot(true);
				locationTable.revalidate();
				locationTable.repaint();
			}
		};
		box.add(new JButton(makeDamaged));
		makeRepaired = new AbstractAction("Make Repaired") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					if (rc.isArmor()) {
						ArmorChitComponent armor = (ArmorChitComponent)rc;
						if (armor.isDamaged()) armor.setIntact(true);
					}
				}
				map.setReplot(true);
				locationTable.revalidate();
				locationTable.repaint();
			}
		};
		box.add(new JButton(makeRepaired));
		toggleAlerted = new AbstractAction("Toggle Alerted") {
			public void actionPerformed(ActionEvent ev) {
				for (RealmComponent rc:getSelectedComponents()) {
					if (rc.isWeapon()) {
						WeaponChitComponent weapon = (WeaponChitComponent)rc;
						weapon.setAlerted(!weapon.isAlerted());
					}
				}
				map.setReplot(true);
				locationTable.revalidate();
				locationTable.repaint();
			}
		};
		box.add(new JButton(toggleAlerted));
		return box;
	} 
	private RealmComponent chooseLeader(String title,boolean charactersOnly) {
		boolean found = false;
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(parent,title,true);
		for(GameObject go:RealmObjectMaster.getRealmObjectMaster(gameData).getPlayerCharacterObjects()) {
			if (CharacterWrapper.hasPlayerBlock(go)) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.getGameObject().hasThisAttribute(Constants.DEAD)) continue;
				if (!charactersOnly || rc.isCharacter()) {
					String option = chooser.generateOption();
					chooser.addGameObjectToOption(option,go);
					found = true;
				}
			}
		}
		if (found) {
			chooser.setVisible(true);
			return chooser.getFirstSelectedComponent();
		}
		JOptionPane.showMessageDialog(parent,"Apparently, there are no player characters in the game yet...","Whoa!",JOptionPane.WARNING_MESSAGE);
		return null;
	}
	private TileLocation chooseTileLocation(String title) {
		CenteredMapView cmap = CenteredMapView.getSingleton();
		cmap.setMapAttentionMessage(title);
		TileLocationChooser chooser = new TileLocationChooser(parent,cmap,null);
		chooser.setLocationRelativeTo(parent);
		chooser.setVisible(true);
		cmap.markAllClearings(false);
		cmap.markAllTiles(false);
		return chooser.getSelectedLocation();
	}
	private void moveSelectionsToSetupCard() {
		ArrayList<RealmComponent> denizens = new ArrayList<RealmComponent>();
		ArrayList<RealmComponent> treasure = new ArrayList<RealmComponent>();
		ArrayList<RealmComponent> other = new ArrayList<RealmComponent>();
		for (RealmComponent rc:getSelectedComponents()) {
			if (rc.isMonster() || rc.isNative()) {
				denizens.add(rc);
			}
			else if (rc.isItem()) {
				treasure.add(rc);
			}
			else if (rc.isTreasureLocation() || rc.isStateChit()) {
				other.add(rc);
			}
		}
		if (treasure.size()>0) {
			GamePool pool = new GamePool(gameData.getGameObjects());
			Hashtable<String,GameObject> lookup = new Hashtable<String,GameObject>();
			for (GameObject go:pool.find("ts_section,!treasure,!summon")) {
				lookup.put(go.getName(),go);
			}
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(parent,"Which setup card location?",true);
			chooser.addStrings(lookup.keySet());
			chooser.setVisible(true);
			String selString = chooser.getSelectedText();
			if (selString==null) return;
			GameObject target = lookup.get(selString);
			for (RealmComponent item:treasure) {
				item.getGameObject().removeThisAttribute(Constants.DEAD); // just in case
				target.add(item.getGameObject());
			}
		}
		for (RealmComponent denizen:denizens) {
			SetupCardUtility.resetDenizen(denizen.getGameObject());
		}
		for (RealmComponent rc:other) {
			ClearingUtility.moveToLocation(rc.getGameObject(),null);
		}
		map.setReplot(true);
		locationTable.revalidate();
		locationTable.repaint();
	}
	private void moveSelectionsToLocation(TileLocation tl) {
		for (RealmComponent rc:getSelectedComponents()) {
			CombatWrapper.clearAllCombatInfo(rc.getGameObject());
			rc.getGameObject().removeThisAttribute(Constants.DEAD); // just in case
			ClearingUtility.moveToLocation(rc.getGameObject(),tl);
			if (tl.isFlying() && rc.isStateChit()) {
				rc.getGameObject().removeThisAttribute("isflying");
			}
		}
		tl.tile.doRepaint();
		map.setReplot(true);
		locationTable.revalidate();
		locationTable.repaint();
	}
	private void moveSelectionsToLeader(RealmComponent leader) {
		boolean didOne = false;
		CharacterWrapper character = new CharacterWrapper(leader.getGameObject());
		for (RealmComponent rc:getSelectedComponents()) {
			if (rc.isAnyLeader()) continue;
			if (rc.isDenizen()) continue;
			if (rc.isStateChit()) continue;
			if (rc.isDwelling()) continue;
			if (rc.getGameObject().hasThisAttribute(Constants.DEAD)) continue;	// No dead things (ewww)
			Loot.addItemToCharacter(parent,null,character,rc.getGameObject());
			if (rc.isMonster() || rc.isNative()) {
				rc.getGameObject().removeThisAttribute(Constants.TREASURE_NEW);
			}
			didOne = true;
		}
		if (didOne) {
			map.setReplot(true);
			locationTable.revalidate();
			locationTable.repaint();
		}
	}
	private void updateControls() {
		int faunaCount = 0; // all monsters/natives/characters
		int itemCount = 0; // all items
		int treasureCount = 0;
		int totalCount = 0;
		int armorCount = 0;
		int weaponCount = 0;
		
		for (RealmComponent rc:getSelectedComponents()) {
			if (rc.isItem()) itemCount++;
			if (rc.isNative() || rc.isMonster() || rc.isCharacter()) faunaCount++;
			if (rc.isTreasure()) treasureCount++;
			if (rc.isArmor()) armorCount++;
			if (rc.isWeapon()) weaponCount++;
			totalCount++;
		}
		
		makeDeadAction.setEnabled(faunaCount>0);
		toggleHiddenAction.setEnabled(faunaCount>0);
		toggleBlockedAction.setEnabled(faunaCount>0);
		hireAction.setEnabled(faunaCount>0);
		unhireAction.setEnabled(faunaCount>0);
		
		setupCardAction.setEnabled(totalCount>0);
		toClearingAction.setEnabled(totalCount>0);
		toRoadAction.setEnabled(totalCount>0);
		toOffroadAction.setEnabled(totalCount>0);
		toTileAction.setEnabled(totalCount>0);
		toBetweenTileAction.setEnabled(totalCount>0);
		leaderAction.setEnabled(totalCount>0);
		
		makeDropped.setEnabled(itemCount>0);
		makeAbandoned.setEnabled(itemCount>0);
		makeFaceDown.setEnabled(treasureCount>0);
		makeFaceUp.setEnabled(treasureCount>0);
		makeDamaged.setEnabled(armorCount>0);
		makeRepaired.setEnabled(armorCount>0);
		toggleAlerted.setEnabled(weaponCount>0);
	}
	private boolean isBattling(RealmComponent rc) {
		if (battleLocation==null) return false;
		if (!rc.isDenizen()) return false;
		if (!battleLocation.equals(rc.getCurrentLocation())) return false;
		CombatWrapper combat = new CombatWrapper(rc.getGameObject());
		if (combat.isPeaceful() || combat.isPacified()) return false;
		if (rc.isMonster()) {
			// pacified or peaceful
			MonsterChitComponent monster = (MonsterChitComponent)rc;
			for(CharacterWrapper character:characters) {
				if (!monster.isPacifiedBy(character)) return true;
			}
		}
		else if (rc.isNative()) {
			for(CharacterWrapper character:characters) {
				if (character.isBattling(rc.getGameObject())) return true;
			}
		}
		return false;
	}
	
	private static String[] CHIT_LOCATION_HEADER = {
		"Name",
		"Type",
		"Owner",
		"Location",
		"Map Location",
		"Hidden",
		"Blocked",
	};
	
	private class ChitLocationTableModel extends AbstractTableModel {

		public int getColumnCount() {
			return CHIT_LOCATION_HEADER.length;
		}
		
		public String getColumnName(int index) {
			return CHIT_LOCATION_HEADER[index];
		}

		public int getRowCount() {
			return thingsWithLocationsFiltered.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex<getRowCount()) {
				RealmComponent chit = thingsWithLocationsFiltered.get(rowIndex);
				GameObject held = chit.getGameObject().getHeldBy();
				switch(columnIndex) {
					case 0:
						return getChitName(chit);
					case 1:
						return getChitType(chit);
					case 2:
						int term = chit.getTermOfHire();
						RealmComponent owner = chit.getOwner();
						String name = "";
						if (owner!=null) {
							name = owner.getGameObject().getName();
							if (term > 0) {
								name = name + " ("+term+" days)";
							}
						}
						return name;
					case 3:
						if (held!=null) {
							RealmComponent rc = RealmComponent.getRealmComponent(held);
							if (rc==null) {
								return "<Setup Card>";
							}
							return rc.isTile()?"<On the map>":held.getName();
						}
						return "<Dead>";
					case 4:
						if (held!=null) {
							TileLocation tl = ClearingUtility.getTileLocation(chit);
							return tl==null?"<Setup Card>":tl.toString();
						}
						return "<Dead>";
					case 5:
						return chit.isHidden()?"Hidden":"";
					case 6:
						boolean blocked = false;
						if (CharacterWrapper.hasPlayerBlock(chit.getGameObject())) {
							CharacterWrapper character = new CharacterWrapper(chit.getGameObject());
							blocked = character.isBlocked();
						}
						else if (chit.isMonster()) {
							MonsterChitComponent monster = (MonsterChitComponent)chit;
							blocked = monster.isBlocked();
						}
						return blocked?"Blocked":"";
				}
			}
			return null;
		}
		private String getChitName(RealmComponent chit) {
			StringBuilder sb = new StringBuilder();
			sb.append(chit.getGameObject().getName());
			if (chit.isHorse()) {
				sb.append(" (");
				sb.append(chit.getGameObject().getAttribute("trot","strength"));
				sb.append(chit.getGameObject().getAttribute("trot","move_speed"));
				sb.append("/");
				sb.append(chit.getGameObject().getAttribute("gallop","strength"));
				sb.append(chit.getGameObject().getAttribute("gallop","move_speed"));
				sb.append(")");
			}
			return sb.toString();
		}
		private String getChitType(RealmComponent chit) {
			if (chit.isArmor()) {
				ArmorChitComponent armor = (ArmorChitComponent)chit;
				if (armor.isDamaged()) return chit.getName()+" (damaged)";
			}
			else if (chit.isWeapon()) {
				WeaponChitComponent weapon = (WeaponChitComponent)chit;
				if (weapon.isAlerted()) return chit.getName()+" (alerted)";
			}
			else if (chit.isDenizen() && isBattling(chit)) {
				return chit.getName()+" (battling)";
			}
			return chit.getName();
		}
	}
	
	private JPanel buildQuestTab(Constants.QuestDeckMode mode) {
		ArrayList<Quest> quests = new ArrayList<Quest>();
		GamePool pool = new GamePool(gameData.getGameObjects());
    	for(GameObject go:pool.find("quest")) {
    		quests.add(new Quest(go));
    	}		
    	this.quests = quests;
		JPanel panel = new JPanel(new BorderLayout());
		questTable = new JTable(new DeckTableModel(mode));
		TableSorter.makeSortable(questTable);
		ComponentTools.lockColumnWidth(questTable,0,40);
		ComponentTools.lockColumnWidth(questTable,1,40);
		ComponentTools.lockColumnWidth(questTable,2,50);
		ComponentTools.lockColumnWidth(questTable,3,40);
		
		JPanel toolbar = new JPanel(new BorderLayout());
		JPanel controls = new JPanel(new GridLayout(2,1));
		controls.add(buildQuestToolbar());
		toolbar.add(controls);
		panel.add(toolbar,BorderLayout.NORTH);
		
		panel.add(new JScrollPane(questTable),BorderLayout.CENTER);
		return panel;		
	}
	
	private Box buildQuestToolbar() {
		Box box = Box.createHorizontalBox();
		box.add(new JLabel("Actions:"));
		addQuest = new AbstractAction("Add") {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<Quest> quests = QuestLoader.loadAllQuestsFromQuestFolder();
				Hashtable<String, Quest> hash = new Hashtable<String, Quest>();
				ArrayList<String> questList = new ArrayList<String>();
					for (Quest quest : quests) {
						questList.add(quest.getName());				
						hash.put(quest.getName(), quest);
					}
				ListChooser chooser = new ListChooser(new JFrame(), "Select a quest:", quests);
				chooser.setDoubleClickEnabled(true);
				chooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				chooser.setLocationRelativeTo(parent);
				chooser.setVisible(true);
				Vector<Quest> v = chooser.getSelectedItems();
				if (v == null || v.isEmpty()) return;
				Quest selectedQuest = v.get(0);
				gameData.createNewObject(selectedQuest.getGameObject());
				updateQuestTable();
			}
		};
		box.add(new JButton(addQuest));
		removeQuest = new AbstractAction("Remove") {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<Quest> quests = getSelectedQuest();
				for (Quest quest : quests) {
					quest.unassign();
					gameData.removeObject(quest.getGameObject());
				}
				updateQuestTable();
			}
		};
		box.add(new JButton(removeQuest));
		assignQuest = new AbstractAction("Assign") {
			public void actionPerformed(ActionEvent ev) {
				ListChooser chooser = new ListChooser(new JFrame(), "Select a character:", characters);
				chooser.setDoubleClickEnabled(true);
				chooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				chooser.setLocationRelativeTo(parent);
				chooser.setVisible(true);
				Vector<CharacterWrapper> v = chooser.getSelectedItems();
				if (v == null || v.isEmpty()) return;
				CharacterWrapper selectedCharacter = v.get(0);
				ArrayList<Quest> quests = getSelectedQuest();
				for (Quest quest : quests) {
					quest.setOwner(selectedCharacter);
				}
				updateQuestTable();
			}
		};
		box.add(new JButton(assignQuest));
		unassignQuest = new AbstractAction("Unassign") {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<Quest> quests = getSelectedQuest();
				for (Quest quest : quests) {
					quest.unassign();
				}
				updateQuestTable();
			}
		};
		box.add(new JButton(unassignQuest));
		resetQuest = new AbstractAction("Reset") {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<Quest> quests = getSelectedQuest();
				for (Quest quest : quests) {
					quest.reset();
				}
				updateQuestTable();
			}
		};
		box.add(new JButton(resetQuest));
		return box;
	}
	private void updateQuestTable() {
		ArrayList<Quest> quests = new ArrayList<Quest>();
		GamePool pool = new GamePool(gameData.getGameObjects());
    	for(GameObject go:pool.find("quest")) {
    		quests.add(new Quest(go));
    	}		
    	this.quests = quests;
    	questTable.clearSelection();
    	questTable.revalidate();
    	questTable.repaint();
	}
	private ArrayList<Quest> getSelectedQuest() {
		ArrayList<Quest> selected = new ArrayList<Quest>();
		TableSorter sorter = TableSorter.getSorter(questTable);
		for (int row:questTable.getSelectedRows()) {
			int index = sorter.convertRowIndexToModel(row);
			selected.add(quests.get(index));
		}
		return selected;
	}
	private class DeckTableModel extends AbstractTableModel {
		public DeckTableModel(Constants.QuestDeckMode mode) {
			this.mode = mode;
		}
		private Constants.QuestDeckMode mode;
		
		ImageIcon test = IconFactory.findIcon("icons/search.gif");
		ImageIcon cross = IconFactory.findIcon("icons/cross.gif");
		ImageIcon check = IconFactory.findIcon("icons/check.gif");
		ImageIcon plus = IconFactory.findIcon("icons/plus.gif");
		
		private String[] HEADER_QtR = {
			"TEST",
			"BAD",
			"ALL",
			"ACT",
			"Name",
			"Character",
			"State"
		};
		private String[] HEADER_BoQ = {
				"TEST",
				"BAD",
				"EVENT",
				"ACT",
				"Name",
				"Character",
				"State"
		};
		private Class[] CLASS = {
			ImageIcon.class,
			ImageIcon.class,
			ImageIcon.class,
			ImageIcon.class,
			String.class,
			String.class,
			String.class
		};
		public int getColumnCount() {
			switch (mode) {
				default:
				case QtR:
				case SR:
					return HEADER_QtR.length;
				case GQ:
				case BoQ:
					return HEADER_BoQ.length;
			}
		}	
		public Class getColumnClass(int col) {
			return CLASS[col];
		}
		public String getColumnName(int col) {
			switch (mode) {
				default:
				case QtR:
				case SR:
					return HEADER_QtR[col];
				case GQ:
				case BoQ:
					return HEADER_BoQ[col];
				}
		}
		public int getRowCount() {
			return quests.size();
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex<getRowCount()) {
				Quest quest = quests.get(rowIndex);
				switch(columnIndex) {
					case 0:		return quest.isTesting()?test:null;
					case 1:		return quest.isBroken()?cross:null;
					case 2:			
						switch (mode) {
							case QtR:
							case SR:
								return quest.isAllPlay()?check:null;
							case GQ:
							case BoQ:
								return quest.isEvent()?check:null;
						}
					case 3:		return quest.isActivateable()?plus:null;
					case 4:		return quest.getName();
					case 5:		return quest.getOwner()==null?null:quest.getOwner().getPlayerName()+" ("+quest.getOwner().getName()+")";
					case 6:		return quest.getState();
				}
			}
			return null;
		}
	}
}