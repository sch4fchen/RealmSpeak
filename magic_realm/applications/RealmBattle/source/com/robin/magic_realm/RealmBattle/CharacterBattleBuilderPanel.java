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
package com.robin.magic_realm.RealmBattle;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.swing.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class CharacterBattleBuilderPanel extends JPanel {
	private BattleBuilder builder;
	private HostPrefWrapper hostPrefs;
	private CharacterWrapper character;
	private ChangeListener dummyListener;
	private JFrame dummyFrame;
	
	private ChitBinPanel chitsPanel;
	
	private RealmObjectPanel activeInventoryPanel;
	private RealmObjectPanel inactiveInventoryPanel;
	private RealmObjectPanel hirelingPanel;
	private RealmObjectPanel spellPanel;
	
	private JLabel charToken;
	private JButton toggleHiddenButton;
	private JButton toggleFortifyButton;
	private JButton removeCharacterButton;
	private JCheckBox characterIsPresentCheckBox;
	private JCheckBox characterFoundHiddenEnemiesCheckBox;
	
	private JButton addInventoryButton;
	private JButton removeInventoryButton;
	private JButton activateInventoryButton;
	private JButton inactivateInventoryButton;
	
	private JButton addHirelingsButton;
	private JButton removeHirelingsButton;
	private JButton toggleHiddenHirelingsButton;
	private boolean nativesHidden = false;
	
	private boolean selectionLock = false;
	
	public CharacterBattleBuilderPanel(BattleBuilder builder,HostPrefWrapper hostPrefs,GameObject go) {
		this.builder = builder;
		this.hostPrefs = hostPrefs;
		this.character = new CharacterWrapper(go);
		this.character.setWantsCombat(true);
		dummyListener = new ChangeListener() {
			public void stateChanged(ChangeEvent ev) {
				// do nothing
			}
		};
		dummyFrame = new JFrame();
		initPanel();
	}
	public CharacterWrapper getCharacter() {
		return character;
	}
	private void initPanel() {
		setLayout(new BorderLayout());
		
		for (GameObject item : character.getInventory()) {
			item.setThisAttribute(BattleBuilder.BATTLE_BUILDER_KEY);
		}
		
		Box box;
		
		// West panel
		RealmComponent token = RealmComponent.getRealmComponent(character.getGameObject());
		JPanel left = new JPanel(new BorderLayout());
		box = Box.createVerticalBox();
		removeCharacterButton = new JButton("Remove Character");
		removeCharacterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.getGameObject().removeThisAttribute(BattleBuilder.BATTLE_BUILDER_KEY);
				if (character.getInventory()!=null) {
					for (GameObject item : character.getInventory()) {
						item.removeThisAttribute(Constants.ACTIVATED);
						item.removeThisAttribute(BattleBuilder.BATTLE_BUILDER_KEY);
						character.getGameObject().remove(item);
						if (item.hasThisAttribute("artifact") || item.hasThisAttribute("book")) {
							item.clearHold();
						}
						if (item.hasThisAttribute("potion")) {
							TreasureUtility.handleExpiredPotion(item);
						}
					}
				}
				if (hirelingPanel.getAllRealmComponents()!=null) {
					for (RealmComponent hireling : hirelingPanel.getAllRealmComponents()) {
						hireling.getGameObject().removeThisAttribute(BattleBuilder.BATTLE_BUILDER_KEY);
						hireling.setHidden(false);
						NativeSteedChitComponent horse = (NativeSteedChitComponent)hireling.getHorseIncludeDead();
						if (horse!=null) {
							horse.getGameObject().removeThisAttribute(Constants.DEAD);
						}
						character.removeHireling(hireling.getGameObject());
					}
				}
				character.clearPlayerAttributes(); // puts it back in the player pool again
				character.clearWishStrength();
				character.setHidden(false);
				for (CharacterActionChitComponent chit : character.getAllChits()) {
					chit.makeActive();
				}
				character.moveToLocation(dummyFrame, null);
				if (character.getGameObject().hasThisAttribute(Constants.CUSTOM_CHARACTER)) {
					character.getGameData().removeObject(character.getGameObject());
				}
				builder.deleteTab(character.getGameObject().getName());
			}
		});
		box.add(removeCharacterButton);
		charToken = new JLabel(character.getCharacterName(),token.getIcon(),JLabel.LEADING);
		charToken.setFont(new Font("Arial",Font.PLAIN,18));
		box.add(charToken);
		toggleHiddenButton = new JButton("Toggle Hidden");
		toggleHiddenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.setHidden(!character.isHidden());
				RealmComponent rc = RealmComponent.getRealmComponent(character.getGameObject());
				charToken.setIcon(rc.getIcon());
			}
		});
		box.add(toggleHiddenButton);
		toggleFortifyButton = new JButton("Toggle Fortify");
		toggleFortifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.setFortified(!character.isFortified());
				RealmComponent rc = RealmComponent.getRealmComponent(character.getGameObject());
				charToken.setIcon(rc.getIcon());
			}
		});
		box.add(toggleFortifyButton);
		characterIsPresentCheckBox = new JCheckBox("Character Present",character.getGameObject().hasThisAttribute(BattleBuilder.CHARACTER_PRESENT));
		characterIsPresentCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (characterIsPresentCheckBox.isSelected()) {
					character.getGameObject().setThisAttribute(BattleBuilder.BATTLE_BUILDER_KEY);
					character.getGameObject().setThisAttribute(BattleBuilder.CHARACTER_PRESENT);
				}
				else {
					character.getGameObject().removeThisAttribute(BattleBuilder.BATTLE_BUILDER_KEY);
					character.getGameObject().removeThisAttribute(BattleBuilder.CHARACTER_PRESENT);
				}
			}
		});
		box.add(characterIsPresentCheckBox);
		characterFoundHiddenEnemiesCheckBox = new JCheckBox("Found Hidden enemies",character.foundHiddenEnemies());
		characterFoundHiddenEnemiesCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				character.setFoundHiddenEnemies(characterFoundHiddenEnemiesCheckBox.isSelected());
			}
		});
		box.add(characterFoundHiddenEnemiesCheckBox);
		left.add(box,"North");
		JPanel chitMain = new JPanel(new BorderLayout());
		ChitBinLayout layout = new ChitBinLayout(character.getCompleteChitList());
		chitsPanel = new ChitBinPanel(layout) {
			public boolean canClickChit(ChitComponent aChit) {
				return true;
			}
			public void handleClick(Point p) {
				ChitComponent chit = chitsPanel.getClickedChit(p);
				if (chit!=null && chit.isActionChit()) {
					CharacterActionChitComponent achit = (CharacterActionChitComponent)chit;
					if (achit.isActive()) {
						if (achit.isEnchantable()) {
							achit.enchant();
						}
						else if (achit.isMagic()) {
							achit.makeAlerted();
						}
						else {
							achit.makeFatigued();
							if (!achit.isFatigued()) { // some chits cannot be fatigued
								achit.makeWounded();
							}
						}
					}
					else if (achit.isColor()) {
						achit.makeAlerted();
					}
					else if (achit.isAlerted()) {
						achit.makeFatigued();
						if (!achit.isFatigued()) { // some chits cannot be fatigued
							achit.makeWounded();
						}
					}
					else if (achit.isFatigued()) {
						achit.makeWounded();
					}
					else if (achit.isWounded()) {
						achit.makeActive();
					}
				}
				chitsPanel.repaint();
			}
		};
		chitsPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				chitsPanel.handleClick(ev.getPoint());
			}
		});
		chitMain.add(new JScrollPane(chitsPanel),"Center");
		JLabel chitsPanelLabel = new JLabel("Click chits to change state");
		chitsPanelLabel.setForeground(Color.red);
		chitMain.add(chitsPanelLabel,"North");
		left.add(chitMain,"Center");
		left.add(Box.createHorizontalStrut(300),"South");
		add(left,"West");
		
		// Center panel
		JPanel center = new JPanel(new GridLayout(3,1));
		JPanel inventoryMain = new JPanel(new BorderLayout());
		JLabel insLabel = new JLabel("SHIFT-click inventory to flip.");
		insLabel.setForeground(Color.red);
		inventoryMain.add(insLabel,"North");
		JPanel inventoryPanel = new JPanel(new GridLayout(1,2));
		activeInventoryPanel = new RealmObjectPanel(true,true);
		activeInventoryPanel.setSelectionMode(RealmObjectPanel.SINGLE_SELECTION);
		activeInventoryPanel.setOpaque(true);
		activeInventoryPanel.setBackground(new Color(255,255,204));
		activeInventoryPanel.addSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				if (!selectionLock) {
					selectionLock = true;
					inactiveInventoryPanel.clearSelected();
					selectionLock = false;
					updateInventoryControls();
				}
			}
		});
		activeInventoryPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount()==2) {
					GameObject go = activeInventoryPanel.getSelectedGameObject();
					showAwakenedSpells(go);
				}
			}
		});
		inventoryPanel.add(new JScrollPane(activeInventoryPanel));
		inactiveInventoryPanel = new RealmObjectPanel(true,true);
		inactiveInventoryPanel.setSelectionMode(RealmObjectPanel.SINGLE_SELECTION);
		inactiveInventoryPanel.addSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				if (!selectionLock) {
					selectionLock = true;
					activeInventoryPanel.clearSelected();
					selectionLock = false;
					updateInventoryControls();
				}
			}
		});
		inactiveInventoryPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount()==2) {
					GameObject go = inactiveInventoryPanel.getSelectedGameObject();
					showAwakenedSpells(go);
				}
			}
		});
		inventoryPanel.add(new JScrollPane(inactiveInventoryPanel));
		inventoryMain.add(inventoryPanel,"Center");
		box = Box.createHorizontalBox();
		box.add(Box.createGlue());
		addInventoryButton = new JButton("Add Inventory");
		addInventoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doAddInventory();
			}
		});
		box.add(addInventoryButton);
		box.add(Box.createGlue());
		removeInventoryButton = new JButton("Remove Inventory");
		removeInventoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				ArrayList<RealmComponent> componentsToDitch = new ArrayList<>();
				componentsToDitch.addAll(activeInventoryPanel.getSelectedComponents());
				componentsToDitch.addAll(inactiveInventoryPanel.getSelectedComponents());
				for (RealmComponent rc : componentsToDitch) {
					GameObject thing = rc.getGameObject();
					thing.removeThisAttribute(Constants.ACTIVATED);
					thing.removeThisAttribute(BattleBuilder.BATTLE_BUILDER_KEY);
					character.getGameObject().remove(thing);
					if (thing.hasThisAttribute("artifact") || thing.hasThisAttribute("book")) {
						thing.clearHold();
					}
					if (thing.hasThisAttribute("potion")) {
						TreasureUtility.handleExpiredPotion(thing);
					}
					
				}
				activeInventoryPanel.clearSelected();
				inactiveInventoryPanel.clearSelected();
				
				character.updateChitEffects();
				chitsPanel.repaint();
				updateInventoryPanels();
			}
		});
		box.add(removeInventoryButton);
		box.add(Box.createGlue());
		activateInventoryButton = new JButton("Activate");
		activateInventoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Collection<RealmComponent> c = inactiveInventoryPanel.getSelectedComponents();
				for (RealmComponent rc : c) {
					GameObject thing = rc.getGameObject();
					TreasureUtility.doActivate(dummyFrame,character,thing,dummyListener,false);
				}
				inactiveInventoryPanel.clearSelected();
				
				chitsPanel.repaint();
				updateInventoryPanels();
			}
		});
		box.add(activateInventoryButton);
		box.add(Box.createGlue());
		inactivateInventoryButton = new JButton("Inactivate");
		inactivateInventoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Collection<RealmComponent> c = activeInventoryPanel.getSelectedComponents();
				for (RealmComponent rc : c) {
					GameObject thing = rc.getGameObject();
					thing.removeThisAttribute(Constants.ACTIVATED);
					if (thing.hasThisAttribute("potion")) {
						TreasureUtility.handleExpiredPotion(thing);
					}
				}
				activeInventoryPanel.clearSelected();
				
				character.updateChitEffects();
				chitsPanel.repaint();
				updateInventoryPanels();
			}
		});
		box.add(inactivateInventoryButton);
		box.add(Box.createGlue());
		inventoryMain.add(box,"South");
		center.add(inventoryMain);
		
		JPanel hirelingMain = new JPanel(new BorderLayout());
		hirelingPanel = new RealmObjectPanel(true,true);
		hirelingPanel.addSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				updateHirelingControls();
			}
		});
		hirelingMain.add(new JScrollPane(hirelingPanel));
		box = Box.createHorizontalBox();
		box.add(Box.createGlue());
		addHirelingsButton = new JButton("Add Hirelings");
		addHirelingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String keyVals = hostPrefs.getGameKeyVals();
				Collection<GameObject> natives = builder.getPool().find(keyVals+",native,!horse,!treasure,!"+BattleBuilder.BATTLE_BUILDER_KEY);
				ArrayList<GameObject> travelers = builder.getPool().find(keyVals+",traveler,!"+BattleBuilder.BATTLE_BUILDER_KEY);
				for(GameObject go:travelers) {
					RealmComponent traveler = RealmComponent.getRealmComponent(go);
					if (traveler.isTraveler()) {
						TravelerChitComponent chit = (TravelerChitComponent)traveler;
						chit.setFaceUp();
					}
				}
				Collection<GameObject> monsters = builder.getPool().find(keyVals+",monster,name=Giant,!"+BattleBuilder.BATTLE_BUILDER_KEY);
				monsters.addAll(builder.getPool().find(keyVals+",monster,name=Ogre,!"+BattleBuilder.BATTLE_BUILDER_KEY));
				monsters.addAll(builder.getPool().find(keyVals+",monster,name=Spear Goblin,!"+BattleBuilder.BATTLE_BUILDER_KEY));
				monsters.addAll(builder.getPool().find(keyVals+",monster,name=Axe Goblin,!"+BattleBuilder.BATTLE_BUILDER_KEY));
				monsters.addAll(builder.getPool().find(keyVals+",monster,name=Sword Goblin,!"+BattleBuilder.BATTLE_BUILDER_KEY));
				monsters.addAll(builder.getPool().find(keyVals+",monster,name=T Flying Dragon,!"+BattleBuilder.BATTLE_BUILDER_KEY));
				monsters.addAll(builder.getPool().find(keyVals+",monster,name=T Dragon,!"+BattleBuilder.BATTLE_BUILDER_KEY));
				RealmObjectChooser hirelingChooser = new RealmObjectChooser("Choose Hired Natives for the "+character.getGameObject().getName()+":",builder.getGameData(),false);
				hirelingChooser.addObjectsToChoose(natives);
				hirelingChooser.addObjectsToChoose(monsters);
				hirelingChooser.addObjectsToChoose(travelers);
				hirelingChooser.setVisible(true);
				if (hirelingChooser.pressedOkay()) {
					Collection<GameObject> chosenNatives = hirelingChooser.getChosenObjects();
					if (chosenNatives!=null && chosenNatives.size()>0) {
						chosenNatives = builder.makeDuplicates(chosenNatives); // only if the option is selected
						builder.checkHorses(chosenNatives);
						
						for (GameObject go : chosenNatives) {
							character.addHireling(go);
							go.setThisAttribute(BattleBuilder.BATTLE_BUILDER_KEY);
							RealmComponent hireling = RealmComponent.getRealmComponent(go);
							hireling.setHidden(nativesHidden);
						}
						hirelingPanel.clearSelected();
						updateHirelingPanel();
					}
				}
			}
		});
		box.add(addHirelingsButton);
		box.add(Box.createGlue());
		removeHirelingsButton = new JButton("Remove Hirelings");
		removeHirelingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Collection<RealmComponent> sel = hirelingPanel.getSelectedComponents();
				for (RealmComponent rc : sel) {
					GameObject go = rc.getGameObject();
					character.removeHireling(go);
					go.removeThisAttribute(BattleBuilder.BATTLE_BUILDER_KEY);
					rc.setHidden(false);
					NativeSteedChitComponent horse = (NativeSteedChitComponent)rc.getHorseIncludeDead();
					if (horse!=null) {
						horse.getGameObject().removeThisAttribute(Constants.DEAD);
					}
				}
				hirelingPanel.clearSelected();
				updateHirelingPanel();
			}
		});
		box.add(removeHirelingsButton);
		box.add(Box.createGlue());
		toggleHiddenHirelingsButton = new JButton("Toggle Hidden");
		toggleHiddenHirelingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				nativesHidden = !nativesHidden;
				for (RealmComponent hireling : character.getAllHirelings()) {
					hireling.setHidden(nativesHidden);
				}
				hirelingPanel.repaint();
			}
		});
		box.add(toggleHiddenHirelingsButton);
		hirelingMain.add(box,"South");
		center.add(hirelingMain);
		
		spellPanel = new RealmObjectPanel();
		center.add(new JScrollPane(spellPanel));
		
		add(center,"Center");
		
		updatePanels();
	}
	private Collection<GameObject> setFaceUp(Collection<GameObject> in) {
		for (GameObject go : in) {
			go.setThisAttribute(Constants.FACING_KEY,CardComponent.FACE_UP);
		}
		return in;
	}
	private void doAddInventory() {
		activeInventoryPanel.clearSelected();
		inactiveInventoryPanel.clearSelected();
		String keyVals = hostPrefs.getGameKeyVals();
		RealmObjectChooser invChooser = new RealmObjectChooser("Select other inventory for the "+character.getGameObject().getName(),builder.getGameData(),false);
		invChooser.addObjectsToChoose(builder.getPool().find(keyVals+",armor,!character,!treasure,!"+BattleBuilder.BATTLE_BUILDER_KEY));
		invChooser.addObjectsToChoose(builder.getPool().find(keyVals+",weapon,!character,!"+BattleBuilder.BATTLE_BUILDER_KEY));
		ArrayList<GameObject> treasures = new ArrayList<>(setFaceUp(builder.getPool().find(keyVals+",treasure,!"+BattleBuilder.BATTLE_BUILDER_KEY)));
		Collections.sort(treasures,new Comparator<GameObject>() {
			public int compare(GameObject go1,GameObject go2) {
				int ret = 0;
				ret = go1.getName().compareTo(go2.getName());
				return ret;
			}
		});
		invChooser.addObjectsToChoose(treasures);
		invChooser.addObjectsToChoose(builder.getPool().find(keyVals+",horse,!native,!"+BattleBuilder.BATTLE_BUILDER_KEY));
		invChooser.setVisible(true);
		Collection<GameObject> otherInv = invChooser.getChosenObjects();
		if (otherInv!=null && otherInv.size()>0) {
			for (GameObject go : otherInv) {
				go.setThisAttribute(BattleBuilder.BATTLE_BUILDER_KEY);
				go.setThisAttribute(Constants.TREASURE_SEEN);
				character.getGameObject().add(go);
				
				if (go.hasThisAttribute("artifact")) {
					String magicType = go.getThisAttribute("magic");
					go.clearHold();
					loadSpells(go,"Choose a spell to be awakened on the "+go.getName(),magicType,1);
				}
				else if (go.hasThisAttribute("book")) {
					int mainCount = 4;
					String magicType = go.getThisAttribute("magic");
					String otherMagicType = go.getThisAttribute("magic_other");
					if (otherMagicType!=null) {
						mainCount = 2;
					}
					go.clearHold();
					loadSpells(go,"Choose "+mainCount+" type "+magicType+" spells to be awakened on the "+go.getName(),magicType,mainCount);
					if (otherMagicType!=null) {
						loadSpells(go,"Choose another "+mainCount+" type "+otherMagicType+" spells to be awakened on the "+go.getName(),otherMagicType,mainCount);
					}
				}
			}
			chitsPanel.repaint();
			activeInventoryPanel.clearSelected();
			inactiveInventoryPanel.clearSelected();
			updateInventoryPanels();
		}
	}
	private void loadSpells(GameObject go,String title,String magicType,int count) {
		ArrayList<GameObject> choices = builder.getPool().find("spell="+magicType+",!"+Constants.SPELL_INSTANCE);
		SpellSelector ss = new SpellSelector(dummyFrame,go.getGameData(),choices,count);
		ss.setTitle(title);
		ss.setVisible(true);
		Collection<GameObject> sel = ss.getSpellSelection();
		for (GameObject spell : sel) {
			spell.setThisAttribute(Constants.SPELL_AWAKENED);
			go.add(spell);
		}
	}
	public void refresh() {
		RealmComponent rc = RealmComponent.getRealmComponent(character.getGameObject());
		charToken.setIcon(rc.getIcon());
	}
	private void updatePanels() {
		chitsPanel.addChits(character.getCompleteChitList());
		spellPanel.addObjects(character.getAllSpells());
		updateInventoryPanels();
		updateHirelingPanel();
		
		updateInventoryControls();
	}
	private void updateInventoryPanels() {
		activeInventoryPanel.removeAll();
		inactiveInventoryPanel.removeAll();
		activeInventoryPanel.addObjects(character.getActiveInventory());
		inactiveInventoryPanel.addObjects(character.getInactiveInventory());
	}
	private void updateInventoryControls() {
		removeInventoryButton.setEnabled(activeInventoryPanel.getSelectedCount()>0 || inactiveInventoryPanel.getSelectedCount()>0);
		activateInventoryButton.setEnabled(inactiveInventoryPanel.getSelectedCount()>0);
		inactivateInventoryButton.setEnabled(activeInventoryPanel.getSelectedCount()>0);
	}
	private void updateHirelingPanel() {
		hirelingPanel.removeAll();
		hirelingPanel.addRealmComponents(character.getAllHirelings());
	}
	private void updateHirelingControls() {
		removeHirelingsButton.setEnabled(hirelingPanel.getSelectedCount()>0);
	}
	private void showAwakenedSpells(GameObject go) {
		if (go!=null && go.hasThisAttribute("treasure") && go.hasThisAttribute("magic")) {
			Collection<GameObject> c = SpellUtility.getSpells(go,Boolean.TRUE,false,true);
			if (c.size()>0) {
				RealmObjectPanel panel = new RealmObjectPanel();
				panel.addObjects(c);
				JOptionPane.showMessageDialog(dummyFrame,panel,go.getName()+" Awakened Spells",JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
}