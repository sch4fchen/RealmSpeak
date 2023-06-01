package com.robin.magic_realm.RealmBattle;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.robin.game.objects.*;
import com.robin.general.io.PreferenceManager;
import com.robin.general.swing.IconFactory;
import com.robin.magic_realm.RealmCharacterBuilder.RealmCharacterBuilderModel;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.swing.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.*;

/**
 * A GUI for building battle situations
 */
public class BattleBuilder extends JFrame {
	private static final Font BIG_FONT = new Font("Dialog",Font.PLAIN,18);
	public static final String BATTLE_BUILDER_KEY = "_BATTLE_BUILDER_";
	public static final String BATTLE_CLEARING_KEY = "___BATTLE__Clearing_";
	public static final String CHARACTER_PRESENT = "_CHAR_PRES_";
	
	public static final String BATTLE_BUILDER_CLEARING = "bb_clearing";
	public static final String BATTLE_BUILDER_TILE = "bb_tile";
	public static final String BATTLE_BUILDER_TILE_IS_ENCHANTED = "bb_tile_is_enchanted";
	
	private static final String testPlayerName = "Player";
	private static final String defaultTileName = "Borderland";
	private static final int defaultClearingNumber = 1;
	
	private GameData gameData;
	private GamePool pool;
	private HostPrefWrapper hostPrefs;
	private PreferenceManager prefs;
	
	private JTabbedPane tabbedPane;
	
	private JButton changeClearingButton;
	private JLabel clearingTitle;
	private JCheckBox makeDuplicatesOption;
	private JCheckBox skipRepositioningOption;
	private JCheckBox forceMonsterFlipOption;
	private JButton addCharacterButton;
	private JButton castSpellButton;
	
	private RealmObjectPanel denizenPanel;
	private JButton addDenizensButton;
	private JButton removeDenizensButton;
	
	private ArrayList<CharacterBattleBuilderPanel> characterPanels;
	
	private JButton cancelButton;
	private JButton editOptionsButton;
	private JButton saveAndFinishButton;
	private JButton finishButton;
	
	// Info
	private ClearingDetail battleClearing;
//	private ClearingDetail alternateClearing; // for handling character "away"
	
	private boolean cancelled = false;
	
	public BattleBuilder() {
		characterPanels = new ArrayList<>();
		initComponents();
	}
	public boolean isCancelled() {
		return cancelled;
	}
	public GameData getGameData() {
		return gameData;
	}
	public GamePool getPool() {
		return pool;
	}
	private static boolean saidYes(String message) {
		int ret = JOptionPane.showConfirmDialog(null,message,"Realm Battle",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
		return ret==JOptionPane.YES_OPTION;
	}
	private void initComponents() {
		setSize(1000,800);
		setLocationRelativeTo(null);
		setTitle("RealmSpeak Battle Builder");
		setIconImage(IconFactory.findIcon("images/combat/combatsummary.gif").getImage());
		getContentPane().setLayout(new BorderLayout());
		
		Box box;
		JPanel panel;
		
		// Top Controls
		box = Box.createHorizontalBox();
		changeClearingButton = new JButton("Change Battle Clearing:");
		changeClearingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				changeClearing();
			}
		});
		box.add(changeClearingButton);
		box.add(Box.createHorizontalStrut(10));
		clearingTitle = new JLabel("");
		box.add(clearingTitle);
		box.add(Box.createHorizontalGlue());
		
		makeDuplicatesOption = new JCheckBox("Make Duplicates",false);
		box.add(makeDuplicatesOption);
		
		skipRepositioningOption = new JCheckBox("Skip repositioning",false);
		box.add(skipRepositioningOption);
		
		forceMonsterFlipOption = new JCheckBox("Force flipping monsters",false);
		box.add(forceMonsterFlipOption);
		
		box.add(Box.createHorizontalGlue());
		addCharacterButton = new JButton("Add Character Tab");
		addCharacterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				addCharacter();
			}
		});
		box.add(addCharacterButton);
		box.add(Box.createHorizontalGlue());
		
		castSpellButton = new JButton("Cast Permanent Spell");
		castSpellButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				castSpell();
			}
		});
		box.add(castSpellButton);
		box.add(Box.createHorizontalGlue());
		box.setBorder(BorderFactory.createEtchedBorder());
		getContentPane().add(box,"North");
		
		// Tabbed Pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(BIG_FONT);
		getContentPane().add(tabbedPane,"Center");
		
		// Denizen Panel
		panel = new JPanel(new BorderLayout());
		denizenPanel = new RealmObjectPanel(true,true);
		denizenPanel.addSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateControls();
			}
		});
		panel.add(new JScrollPane(denizenPanel),"Center");
		box = Box.createHorizontalBox();
		box.add(Box.createGlue());
		addDenizensButton = new JButton("Add Denizens");
		addDenizensButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				addDenizens();
			}
		});
		box.add(addDenizensButton);
		box.add(Box.createGlue());
		removeDenizensButton = new JButton("Remove Denizens");
		removeDenizensButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				removeDenizens();
			}
		});
		box.add(removeDenizensButton);
		box.add(Box.createGlue());
		panel.add(box,"North");
		tabbedPane.addTab("Denizens",panel);
		
		// Dialog controls
		box = Box.createHorizontalBox();
		cancelButton = new JButton("Cancel");
		cancelButton.setFont(BIG_FONT);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				cancelled = true;
				setVisible(false);
				CombatFrame.close();
			}
		});
		box.add(cancelButton);
		box.add(Box.createHorizontalGlue());
		editOptionsButton = new JButton("Game Options...");
		editOptionsButton.setFont(BIG_FONT);
		editOptionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				HostGameSetupDialog setup = new HostGameSetupDialog(new JFrame(),"Game Options for Battle",gameData);
				setup.loadPrefsFromData();
				setup.setVisible(true);
			}
		});
		box.add(editOptionsButton);
		box.add(Box.createHorizontalGlue());
		saveAndFinishButton = new JButton("Save and Play");
		saveAndFinishButton.setFont(BIG_FONT);
		saveAndFinishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doFinish();
				CombatFrame.saveBattle(new JFrame(),gameData);
				cancelled = false;
				setVisible(false);
				if (skipRepositioningOption.isSelected()) BattleModel.SKIP_REPOSITIONING = true;
				if (forceMonsterFlipOption.isSelected()) BattleModel.FORCE_MONSTER_FLIP = true;
				CombatFrame.startCombat(gameData);
			}
		});
		box.add(saveAndFinishButton);
		box.add(Box.createHorizontalStrut(10));
		finishButton = new JButton("Play (no save)");
		finishButton.setFont(BIG_FONT);
		finishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doFinish();
				cancelled = false;
				setVisible(false);
				if (skipRepositioningOption.isSelected()) BattleModel.SKIP_REPOSITIONING = true;
				if (forceMonsterFlipOption.isSelected()) BattleModel.FORCE_MONSTER_FLIP = true;
				CombatFrame.startCombat(gameData);
			}
		});
		box.add(finishButton);
		getContentPane().add(box,"South");
	}
	public boolean initialize(GameData data) {
		prefs = new PreferenceManager("BattleBuilder","BattleBuilder.cfg") {
			protected void createDefaultPreferences(Properties props) {
				props.put(BATTLE_BUILDER_CLEARING,defaultClearingNumber);
				props.put(BATTLE_BUILDER_TILE,defaultTileName);
				props.put(BATTLE_BUILDER_TILE_IS_ENCHANTED,true);
			}
		};
		prefs.loadPreferences();
		
		if (data==null) {
			// Building a new battle
			System.out.print("Loading data...");
			RealmLoader loader = new RealmLoader();
			System.out.println("Done.");
			gameData = loader.getData();
			
			int clearing = prefs.getInt(BATTLE_BUILDER_CLEARING);
			if (clearing == 0) clearing = defaultClearingNumber;
			String tileName = prefs.get(BATTLE_BUILDER_TILE);
			boolean tileIsEnchanted = prefs.getBoolean(BATTLE_BUILDER_TILE_IS_ENCHANTED);
			RealmUtility.prepMonsterNumbers(gameData);
			
			// Set starting clearing
			GameObject selectedTile = gameData.getGameObjectByName(tileName);
			if (selectedTile == null) selectedTile = gameData.getGameObjectByName(defaultTileName);
			TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(selectedTile);
			if (tileIsEnchanted) {
				tile.setLightSideUp();
			}
			else {
				tile.setDarkSideUp();
			}
			battleClearing = tile.getClearing(clearing);
			
			// Select game prefs...
			HostGameSetupDialog setup = new HostGameSetupDialog(new JFrame(),"Game Options for Battle",gameData);
			// Setup the local prefs (if any)
			setup.loadPrefsFromLocalConfiguration();
			setup.setVisible(true);
			
			if (setup.getDidStart()) {
				setup.savePrefsToLocalConfiguration();
				hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
				
				// load spells
				if (hostPrefs.getIncludeExpansionSpells()) {
					prepExpansionSpells("rw_expansion_1", gameData);
				}
				if(hostPrefs.getIncludeNewSpells()){
					prepExpansionSpells("new_spells_1", gameData);
				}
				if(hostPrefs.getIncludeNewSpells2()){
					prepExpansionSpells("new_spells_2", gameData);
				}
				if(hostPrefs.getIncludeSrSpells()){
					prepExpansionSpells("super_realm_spell", gameData);
				}
				if(hostPrefs.getSwitchDaySpells()){
					prepExpansionSpells("upg_day_spells", gameData);
					removeSpells("upg_swap_out", gameData);
				}
				if(hostPrefs.getIncludeExpansionTreasures()){
					prepExpansionTreasures("rw_expansion_1", gameData);
				}
				if(hostPrefs.getIncludeSrTreasures()){
					prepExpansionTreasures("super_realm_treasure", gameData);
				}
				if (hostPrefs.hasPref(Constants.OPT_POWER_OF_THE_PIT_ATTACK)) {
					GamePool pool = new GamePool(gameData.getGameObjects());
					ArrayList<GameObject> popSpells = pool.find("powerofthepit");
					for (GameObject go:popSpells) {
						go.setThisAttribute("duration","attack");
						go.setThisAttribute("strength","");
					}
				}
				
				updatePool();
				
				// Add a custom character keylist
				RealmCharacterBuilderModel.addCustomCharacters(hostPrefs,gameData);
						
				// Some items require a spell be cast (Flying Carpet)
				ArrayList<String> keyVals = new ArrayList<>();
				keyVals.add(hostPrefs.getGameKeyVals());
				keyVals.add(Constants.CAST_SPELL_ON_INIT);
				SpellMasterWrapper.getSpellMaster(gameData); // make sure SpellMaster is created
				Collection<GameObject> needsSpellInit = pool.find(keyVals);
				for (GameObject go : needsSpellInit) {
					for (GameObject sgo : go.getHold()) {
						if (sgo.hasThisAttribute("spell")) {
							SpellWrapper spell = new SpellWrapper(sgo);
							spell.castSpellNoEnhancedMagic(go);
							spell.addTarget(hostPrefs,go);
							spell.makeInert(); // starts off as inert
						}
					}
				}
				
				battleClearing.energizeItems();
				
				// Convert all traveler templates to travelers, so they can be tested here
				ArrayList<GameObject> travelerChits = pool.find("traveler");
				if (travelerChits.size()>0) {
					GameObject sample = travelerChits.get(0);
					for (GameObject go:pool.find("traveler_template")) {
						go.copyAttributeBlockFrom(sample,"this");
						go.setThisAttribute(Constants.TEMPLATE_ASSIGNED);
						go.removeThisAttribute(Constants.TRAVELER_TEMPLATE);
					}
					for (GameObject go:travelerChits) {
						go.removeThisAttribute("traveler"); // nullify these!
					}
				}
				
				updateControls();
				return true;
			}
			CombatFrame.close();
		}
		else {
			// Editing an existing battle
			gameData = data;
			hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
			pool = new GamePool(gameData.getGameObjects());
			pool = new GamePool(pool.find(hostPrefs.getGameKeyVals()));
			
			if (setupBuilderWithData()) {
				updateControls();
				return true;
			}
		}
		
		return false;
	}
	private void updatePool() {
		pool = new GamePool(gameData.getGameObjects());
		pool = new GamePool(pool.find(hostPrefs.getGameKeyVals()));
	}
	private void prepExpansionSpells(String spellKey, GameData data) {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> expansionSpells = pool.find("spell," + spellKey);
		for (GameObject go:expansionSpells) {
			go.setThisKeyVals(hostPrefs.getGameKeyVals());
		}
	}
	private void removeSpells(String spellKey, GameData data){
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> toRemove = pool.find("spell," + spellKey);
		for (GameObject go:toRemove) {
			go.stripThisKeyVals(hostPrefs.getGameKeyVals());
		}	
	}
	private void prepExpansionTreasures(String gameKey, GameData data) {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> expansionSpells = pool.find("!original_game,treasure,!treasure_within_treasure,!ts_section," + gameKey);
		for (GameObject go:expansionSpells) {
			go.setThisKeyVals(hostPrefs.getGameKeyVals());
		}
	}
	private boolean setupBuilderWithData() {
		// Fetch battleClearing
		GameObject bcObj = getBattleClearingReferenceObject(gameData);
		String v = bcObj.getThisAttribute("version");
		if (v==null || !v.equals(Constants.REALM_SPEAK_VERSION)) {
			JOptionPane.showMessageDialog(null,"Incompatible save file - wrong version","Invalid Save File",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		String val = bcObj.getThisAttribute("battleClearing");
		TileLocation tl = TileLocation.parseTileLocation(gameData,val);
		battleClearing = tl.clearing;
		
		// Load up the builder
		BattleModel model = RealmBattle.buildBattleModel(tl, gameData);
		
		// Denizens...
		BattleGroup denGroup = model.getDenizenBattleGroup();
		if (denGroup!=null) {
			ArrayList<GameObject> toAdd = new ArrayList<>();
			for (RealmComponent rc : denGroup.getBattleParticipants()) {
				GameObject go = rc.getGameObject();
				go.setThisAttribute(BATTLE_BUILDER_KEY);
				toAdd.add(go);
			}
			denizenPanel.addObjects(toAdd);
		}
		
		// Characters...
		for (BattleGroup group : model.getAllBattleGroups(false)) {
			RealmComponent rc = group.getOwningCharacter();
			
			// Add tab
			CharacterBattleBuilderPanel panel = new CharacterBattleBuilderPanel(this,hostPrefs,rc.getGameObject());
			characterPanels.add(panel);
			tabbedPane.addTab(rc.getGameObject().getName(),panel);
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
			
			if (group.isCharacterInBattle()) {
				rc.getGameObject().setThisAttribute(BATTLE_BUILDER_KEY);
			}
			
			// Tag everyone else
			for (RealmComponent bp : group.getBattleParticipants()) {
				if (bp!=rc) {
					bp.getGameObject().setThisAttribute(BATTLE_BUILDER_KEY);
				}
			}
			
		}
		return true;
	}
	private void updateControls() {
		clearingTitle.setText(battleClearing.fullString());
		removeDenizensButton.setEnabled(denizenPanel.getSelectedCount()>0);
		
		boolean isCombat = (denizenPanel.getComponentCount()>0 && characterPanels.size()>0) || characterPanels.size()>1;
		saveAndFinishButton.setEnabled(isCombat);
		finishButton.setEnabled(isCombat);
	}
	private void changeClearing() {
		// Choose a tile and clearing
		Collection<GameObject> tiles = pool.find("tile,"+hostPrefs.getGameKeyVals());
		Hashtable<String, GameObject> tileHash = new Hashtable<>();
		for (GameObject tile : tiles) {
			tileHash.put(tile.getName(),tile);
		}
		ArrayList<String> tileNames = new ArrayList<>(tileHash.keySet());
		Collections.sort(tileNames);
		String tileName = (String)JOptionPane.showInputDialog(
				null,
				"Select a tile where combat is occuring:",
				"Select Tile",
				JOptionPane.QUESTION_MESSAGE,
				null,
				tileNames.toArray(),
				tileNames.iterator().next());
		
		if (tileName==null) {
			return;
		}
		GameObject selectedTile = tileHash.get(tileName);
		
		TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(selectedTile);
		boolean enchanted = false;
		if (saidYes("Do you want to use the Enchanted side of the "+selectedTile.getName()+"?")) {
			tile.setDarkSideUp();
			enchanted = true;
		}
		else {
			tile.setLightSideUp();
		}
		
		ArrayList<String> clearingNames = new ArrayList<>();
		Hashtable<String, ClearingDetail> clearingHash = new Hashtable<>();
		for (int i=1;i<=6;i++) {
			ClearingDetail clearing = tile.getClearing(i);
			if (clearing!=null) {
				clearingHash.put(clearing.fullString(),clearing);
				clearingNames.add(clearing.fullString());
			}
		}
		String clearingName = (String)JOptionPane.showInputDialog(
				null,
				"Select a clearing where combat is occurring:",
				"Select Clearing",
				JOptionPane.QUESTION_MESSAGE,
				null,
				clearingNames.toArray(),
				clearingNames.iterator().next());
		if (clearingName==null) {
			return;
		}
		battleClearing = clearingHash.get(clearingName);
		
		prefs.set(BATTLE_BUILDER_CLEARING,clearingHash.get(clearingName).getNum());
		prefs.set(BATTLE_BUILDER_TILE,tileName);
		prefs.set(BATTLE_BUILDER_TILE_IS_ENCHANTED,enchanted);
		prefs.savePreferences();
		
		updateControls();
	}
	protected void checkHorses(Collection<GameObject> denizens) {
		ArrayList<GameObject> horses = new ArrayList<>();
		for (GameObject go : denizens) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			BattleHorse horse = rc.getHorse();
			if (horse!=null) {
				horses.add(horse.getGameObject());
			}
		}
		
		if (!horses.isEmpty()) {
			int ret = JOptionPane.showConfirmDialog(this,"Include horses?","",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
			boolean includeHorses = ret==JOptionPane.YES_OPTION;
			for (GameObject go : horses) {
				if (includeHorses) {
					go.removeThisAttribute(Constants.DEAD);
				}
				else {
					go.setThisAttribute(Constants.DEAD);
				}
			}
		}
	}
	private void addDenizens() {
		Collection<GameObject> monsters = pool.find("monster,!part,!treasure,"+hostPrefs.getGameKeyVals()+",!"+BATTLE_BUILDER_KEY);
		Collection<GameObject> natives = pool.find("native,!horse,!treasure,"+hostPrefs.getGameKeyVals()+",!"+BATTLE_BUILDER_KEY);
		RealmObjectChooser denizenChooser = new RealmObjectChooser("Choose Denizens to Add:",gameData,false);
		denizenChooser.addObjectsToChoose(monsters);
		denizenChooser.addObjectsToChoose(natives);
		denizenChooser.setVisible(true);
		if (denizenChooser.pressedOkay()) {
			Collection<GameObject> chosenDenizens = denizenChooser.getChosenObjects();
			if (chosenDenizens!=null && chosenDenizens.size()>0) {
				chosenDenizens = makeDuplicates(chosenDenizens); // only if the option is selected
				checkHorses(chosenDenizens);
				for (GameObject go : chosenDenizens) {
					go.setThisAttribute(BATTLE_BUILDER_KEY);
				}
				denizenPanel.clearSelected();
				denizenPanel.addObjects(chosenDenizens);
				updateControls();
			}
		}
	}
	public Collection<GameObject> makeDuplicates(Collection<GameObject> in) {
		if (makeDuplicatesOption.isSelected()) {
			ArrayList<GameObject> dups = new ArrayList<>();
			for (GameObject go :  in) {
				GameObject dup = gameData.createNewObject(go);
				Collection<GameObject> hold = go.getHold();
				if (!hold.isEmpty()) {
					// This recursive behavior will guarantee that the duplication goes deep
					dup.addAll(makeDuplicates(hold));
				}
				dups.add(dup);
			}
			// Refresh the pool so these new objects can be found
			pool = new GamePool(gameData.getGameObjects());
			return dups;
		}
		return in;
	}
	private void removeDenizens() {
		ArrayList all = new ArrayList<>(Arrays.asList(denizenPanel.getComponents()));
		Collection<RealmComponent> selDenizens = denizenPanel.getSelectedComponents();
		for (RealmComponent rc : selDenizens) {
			rc.getGameObject().removeThisAttribute(BATTLE_BUILDER_KEY);
			all.remove(rc);
			battleClearing.remove(rc.getGameObject());
		}
		denizenPanel.clearSelected();
		denizenPanel.removeAll();
		denizenPanel.addRealmComponents(all);
		updateControls();
	}
	private void addCharacter() {
		CharacterWrapper lastCharacter = null;
		if (characterPanels.size()>0) {
			CharacterBattleBuilderPanel panel = characterPanels.get(0);
			lastCharacter = panel.getCharacter();
		}
		
		ArrayList<GameObject> characters = pool.find("character,!"+CharacterWrapper.NAME_KEY+",!"+Constants.CUSTOM_CHARACTER);
		characters.addAll(CustomCharacterLibrary.getSingleton().getCharacterTemplateList());
		Collections.sort(characters,new Comparator<GameObject>() {
			public int compare(GameObject go1,GameObject go2) {
				return go1.getName().compareTo(go2.getName());
			}
		});
		
		CharacterChooser chooser = new CharacterChooser(this,characters,hostPrefs);
		chooser.setVisible(true);
		
		GameObject chosen = chooser.getChosenCharacter();
		if (chosen!=null) {
			if (chosen.hasThisAttribute(Constants.CUSTOM_CHARACTER)) {
				GameObject newChar = gameData.createNewObject();
				newChar.copyAttributesFrom(chosen);
				RealmComponent.clearOwner(newChar);
				newChar.setThisKeyVals(hostPrefs.getGameKeyVals());
				for (GameObject go : chosen.getHold()) {
					if (go.hasThisAttribute("character_chit")) {
						GameObject newChit = gameData.createNewObject();
						newChit.copyAttributesFrom(go);
						newChit.setThisKeyVals(hostPrefs.getGameKeyVals());
						newChar.add(newChit);
					}
				}
				chosen = newChar;
				updatePool();
			}
			
			CharacterWrapper character = new CharacterWrapper(chosen);
			battleClearing.add(character.getGameObject(),null);
			character.setPlayerName(testPlayerName);
			character.setPlayerPassword("");
			character.setPlayerEmail(""); // no e-mail for battle tests
			character.setCharacterLevel(4);
			character.updateLevelAttributes(hostPrefs);
			character.initChits();
			character.fetchStartingInventory(this,gameData,false);
			character.clearRelationships(hostPrefs);
			character.initRelationships(hostPrefs);
			character.setGold(50);
			chosen.setThisAttribute(BATTLE_BUILDER_KEY);
			if (lastCharacter!=null) {
				character.setEnemyCharacter(lastCharacter.getGameObject(),true);
			}
			
			// Choose Spells
			RealmUtility.fetchStartingSpells(this,character,gameData,false);
			
			// Add tab
			CharacterBattleBuilderPanel panel = new CharacterBattleBuilderPanel(this,hostPrefs,chosen);
			characterPanels.add(panel);
			tabbedPane.addTab(chosen.getName(),panel);
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
			updateControls();
		}
	}
	private void updateDenizenPanel() {
		denizenPanel.removeAll();
		ArrayList<GameObject> denizens = new ArrayList<>();
		denizens.addAll(pool.find("monster,!part,"+hostPrefs.getGameKeyVals()+","+BATTLE_BUILDER_KEY));
		denizens.addAll(pool.find("native,!horse,!treasure,"+hostPrefs.getGameKeyVals()+","+BATTLE_BUILDER_KEY));
		for(GameObject denizen:denizens) {
			RealmComponent rc = RealmComponent.getRealmComponent(denizen);
			if (rc.getCurrentLocation()!=null) {
				// absorbed!
				rc.getGameObject().removeThisAttribute(BATTLE_BUILDER_KEY);
				continue;
			}
			denizenPanel.add(rc);
		}
	}
	public void castSpell() {
		SpellWrapper spell = querySpell();
		if (spell==null) return;

		String spellType = spell.getGameObject().getThisAttribute("spell");
		GameObject caster = queryCaster(spellType);
		if (caster==null) return;
		
		GameObject incantation = queryIncantation(caster,spellType);
		if (incantation==null) return;
		
		// Ask for a target
		RealmComponent target = queryTarget(spell.getGameObject().getThisAttribute("target"));
		if (target==null) return;
		
		CharacterWrapper casterCharacter = new CharacterWrapper(caster);
		ClearingUtility.moveToLocation(caster,battleClearing.getTileLocation());
		spell = new SpellWrapper(casterCharacter.recordNewSpell(this,spell.getGameObject(),true));
		
		if (spell.getName().toLowerCase().startsWith("transform")) { // Handle special case
			int redDie = SpellUtility.chooseRedDie(this,"transform",casterCharacter);
			spell.setRedDieLock(redDie);
		}
		
		// Ask if spell is INERT or ALIVE
		int ret = JOptionPane.showConfirmDialog(
				this,
				"Do you want the spell to be ALIVE at the start of combat?",
				"Cast Spell",
				JOptionPane.YES_NO_OPTION);
		
		// Cast the spell
		spell.castSpellNoEnhancedMagic(incantation);
		spell.addTarget(hostPrefs,target.getGameObject());
		caster.addThisAttributeListItem("diemod","1d:all:all");
		spell.affectTargets(this,GameWrapper.findGame(gameData),false,null);
		caster.removeThisAttributeListItem("diemod","1d:all:all");
		if (ret==JOptionPane.NO_OPTION) {
			spell.unaffectTargets();
			spell.makeInert();
		}
		for(CharacterBattleBuilderPanel panel:characterPanels) {
			panel.refresh();
		}
		updateDenizenPanel();
		ClearingUtility.moveToLocation(caster,null);
		repaint();
	}
	private SpellWrapper querySpell() {
		ArrayList<GameObject> spells = pool.find("spell,duration=permanent");
		ArrayList<GameObject> toRemove = new ArrayList<>();
		for (GameObject spell:spells) {
			String spellType = spell.getThisAttribute("spell").trim();
			if (spellType.length()==0 || !"IIIVIII".contains(spellType)) {
				toRemove.add(spell);
				continue;
			}
			
			String target = spell.getThisAttribute("target");
			if (!"character,individual,monster".contains(target)) { // I'm only supporting these for the battle builder
				toRemove.add(spell);
				continue;
			}
		}
		spells.removeAll(toRemove);
		Collections.sort(spells,new Comparator<GameObject>() {
			public int compare(GameObject go1,GameObject go2) {
				return go1.getName().compareTo(go2.getName());
			}
		});
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(this,"Permanent Spell to Cast:",true);
		chooser.addGameObjects(spells,false);
		chooser.setMaxGroupSize(spells.size());
		chooser.setVisible(true);
		if (chooser.getSelectedText()!=null) {
			RealmComponent rc = chooser.getFirstSelectedComponent();
			return new SpellWrapper(rc.getGameObject());
		}
		return null;
	}
	private GameObject queryCaster(String spellType) {
		ArrayList<GameObject> characters = pool.find("character,"+BATTLE_BUILDER_KEY);
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(this,"Caster:",true);
		chooser.addOption("Other","Other");
		for (GameObject character:characters) {
			CharacterWrapper test = new CharacterWrapper(character);
			boolean hasChits = false;
			for(CharacterActionChitComponent chit:test.getActiveMagicChits()) {
				if (spellType.equals(chit.getMagicType())) {
					hasChits = true;
					break;
				}
			}
			chooser.addOption(character.getName(),character.getName() + (hasChits?"":("(No MAGIC "+spellType+" Chits)")));
		}
		chooser.setVisible(true);
		String key = chooser.getSelectedOptionKey();
		if (key!=null) {
			if ("Other".equals(key)) {
				GameObject other = pool.findFirst("Name=Inn"); // Yes, the Inn will be the "caster" in this case.  :-)
				other.setThisAttribute("character");
				return other;
			}
			return pool.findFirst("Name="+key);
		}
		return null;
	}
	private GameObject queryIncantation(GameObject caster,String spellType) {
		if ("Inn".equals(caster.getName())) {
			return caster;
		}
		CharacterWrapper test = new CharacterWrapper(caster);
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(this,"Incantation:",true);
		for(CharacterActionChitComponent chit:test.getActiveMagicChits()) {
			if (spellType.equals(chit.getMagicType())) {
				chooser.addRealmComponent(chit);
			}
		}
		if (!chooser.hasOptions()) return caster; 
		chooser.setVisible(true);
		if (chooser.getSelectedText()!=null) {
			return chooser.getFirstSelectedComponent().getGameObject();
		}
		return null;
	}
	private RealmComponent queryTarget(String targetType) {
		ArrayList<GameObject> choices = new ArrayList<>();
		boolean individual = "individual".equals(targetType);
		if (individual || "monster".equals(targetType)) {
			choices.addAll(pool.find("monster,"+BATTLE_BUILDER_KEY));
		}
		if (individual || "character".equals(targetType)) {
			choices.addAll(pool.find("character,"+BATTLE_BUILDER_KEY));
		}
		if (individual) {
			choices.addAll(pool.find("native,"+BATTLE_BUILDER_KEY));
		}
//		if ("artifact".equals(targetType)) {
//			choices.addAll(pool.find("artifact,"+BATTLE_BUILDER_KEY));
//			choices.addAll(pool.find("book,"+BATTLE_BUILDER_KEY));
//		}
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(this,"Target of Spell:",true);
		chooser.addGameObjects(choices,true);
		if (chooser.hasOptions()) {
			chooser.setVisible(true);
			String selText = chooser.getSelectedText();
			if (selText!=null) {
				return chooser.getFirstSelectedComponent();
			}
		}
		else {
			JOptionPane.showMessageDialog(this,"No valid targets!");
		}
		
		return null;
	}
	public void deleteTab(String name) {
		for (int i=0;i<tabbedPane.getTabCount();i++) {
			String title = tabbedPane.getTitleAt(i);
			if (title.equals(name)) {
				tabbedPane.removeTabAt(i);
				break;
			}
		}
		repaint();
	}
	private void doFinish() {
		Collection<GameObject> everything = pool.find(BATTLE_BUILDER_KEY);
		for (GameObject go : everything) {
			go.removeThisAttribute(BATTLE_BUILDER_KEY);
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isCharacter() || rc.isNative() || rc.isMonster() || rc.isTraveler()) {
				battleClearing.add(go,null);
			}
		}
		
		// Save the battleClearing for reference
		GameObject bcObj = getBattleClearingReferenceObject(gameData);
		TileLocation tl = new TileLocation(battleClearing);
		bcObj.setThisAttribute("version",Constants.REALM_SPEAK_VERSION);
		bcObj.setThisAttribute("battleClearing",tl.asKey());
	}
	
	public static GameObject getBattleClearingReferenceObject(GameData data) {
		GamePool thePool = new GamePool(data.getGameObjects());
		Collection<GameObject> bc = thePool.find(BATTLE_CLEARING_KEY);
		GameObject bcObj = null;
		if (bc.isEmpty()) {
			bcObj = data.createNewObject();
			bcObj.setThisAttribute(BATTLE_CLEARING_KEY);
		}
		else {
			bcObj = bc.iterator().next();
		}
		return bcObj;
	}
	
	public static void constructBattleSituation() {
		constructBattleSituation(null);
	}
	public static void constructBattleSituation(GameData data) {
		BattleBuilder builder = new BattleBuilder();
		if (builder.initialize(data)) {
			builder.setVisible(true);
		}
	}
	
//	public static void main(String[] args) {
//		RealmUtility.setupTextType();
//		RealmUtility.setupArgs(args);
//		LoggingHandler.initLogging();
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		}
//		catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		if (constructBattleSituation()!=null) {
//			System.out.println("Combat!");
//		}
//		else {
//			System.out.println("Cancelled");
//		}
//		System.exit(0);
//	}
}