package com.robin.magic_realm.RealmCharacterBuilder.EditPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import com.robin.game.objects.*;
import com.robin.general.swing.ComponentTools;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.swing.RealmObjectChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class StartingInventoryEditPanel extends AdvantageEditPanel {
	
	private GameData magicRealmData;
	
	private JLabel iconLabel;
	private JLabel iconLabelFlipped;
	private JButton treasureInventoryButton;
	private JButton renameTreasureButton;
	private JButton horsesInventoryButton;
	private JButton weaponInventoryButton;
	
	private RealmComponent extraInventory;
	
	public StartingInventoryEditPanel(CharacterWrapper pChar, String levelKey,GameData magicRealmData) {
		super(pChar, levelKey);
		this.magicRealmData = magicRealmData;
		
		setLayout(new BorderLayout());
		
		GameObject go = GameObject.createEmptyGameObject();
		go.setName("Irrelevant Cup");
		go.setThisAttribute("treasure","small");
		go.setThisAttribute("text","Nothing special.");
		go.setThisAttribute(Constants.BONUS_INVENTORY,levelKey);
		
		extraInventory = new TreasureCardComponent(go);
		extraInventory.setFacing(CardComponent.FACE_UP);
		
		Box main = Box.createVerticalBox();
		main.add(Box.createVerticalGlue());
		Box line;
			line = Box.createHorizontalBox();
			line.add(Box.createHorizontalGlue());
			iconLabel = new JLabel(extraInventory.getIcon());
			line.add(iconLabel);
			iconLabelFlipped = new JLabel();
			line.add(iconLabelFlipped);
			line.add(Box.createHorizontalGlue());
		main.add(line);
			line = Box.createHorizontalBox();
			line.add(Box.createHorizontalGlue());
			renameTreasureButton = new JButton("Rename");
			renameTreasureButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					String newName = JOptionPane.showInputDialog(StartingInventoryEditPanel.this,"Treasure Name:",extraInventory.getGameObject().getName());
					if (newName!=null) {
						extraInventory.getGameObject().setName(newName);
					}
					updateIcon();
				}
			});
			ComponentTools.lockComponentSize(renameTreasureButton,80,25);
			line.add(renameTreasureButton);
			line.add(Box.createHorizontalGlue());
		main.add(line);
		main.add(Box.createVerticalStrut(25));
			line = Box.createHorizontalBox();
			line.add(Box.createHorizontalGlue());
			treasureInventoryButton = new JButton("Pick Treasure");
			treasureInventoryButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					pickTreasure();
					updateIcon();
				}
			});
			ComponentTools.lockComponentSize(treasureInventoryButton,100,40);
			line.add(treasureInventoryButton);
			line.add(Box.createHorizontalGlue());
		main.add(line);
		main.add(Box.createVerticalStrut(25));
			line = Box.createHorizontalBox();
			line.add(Box.createHorizontalGlue());
			horsesInventoryButton = new JButton("Pick Horse");
			horsesInventoryButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					pickHorse();
					updateIcon();
				}
			});
			ComponentTools.lockComponentSize(horsesInventoryButton,100,40);
			line.add(horsesInventoryButton);
			line.add(Box.createHorizontalGlue());
		main.add(line);
		main.add(Box.createVerticalStrut(25));
			line = Box.createHorizontalBox();
			line.add(Box.createHorizontalGlue());
			weaponInventoryButton = new JButton("Pick Weapon");
			weaponInventoryButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					pickWeapon();
					updateIcon();
				}
			});
			ComponentTools.lockComponentSize(weaponInventoryButton,100,40);
			line.add(weaponInventoryButton);
			line.add(Box.createHorizontalGlue());
		main.add(line);
		main.add(Box.createVerticalGlue());
		add(main,"Center");
		
		if (hasAttribute(Constants.BONUS_INVENTORY)) {
			GameObjectBlockManager man = new GameObjectBlockManager(character);
			go = man.extractGameObjectFromBlocks(getBaseBlockName(),true);
			extraInventory = RealmComponent.getRealmComponent(go);
			RealmComponent.reset();
			man.clearBlocks(getBaseBlockName());
			updateIcon();
		}
	}
	private void pickTreasure() {
		ArrayList<String> query = new ArrayList<>();
		query.add("treasure");
		query.add("!treasure_within_treasure");
		query.add("!book");
		query.add("!artifact");
		query.add("!cannot_move");
		query.add("!cursed");
		GameObject go = pickGameObject(query,"Choose a Treasure:");
		if (go!=null) {
			extraInventory = new TreasureCardComponent(go);
		}
	}
	private void pickHorse() {
		ArrayList<String> query = new ArrayList<>();
		query.add("horse");
		GameObject go = pickGameObject(query,"Choose a Horse:");
		if (go!=null) {
			extraInventory = new SteedChitComponent(go);
		}
	}
	private void pickWeapon() {
		ArrayList<String> query = new ArrayList<>();
		query.add("weapon");
		query.add("!treasure");
		GameObject go = pickGameObject(query,"Choose a Weapon:");
		if (go!=null) {
			extraInventory = new WeaponChitComponent(go);
		}
	}
	private GameObject pickGameObject(ArrayList<String> query,String title) {
		GamePool pool = new GamePool(magicRealmData.getGameObjects());
		ArrayList<GameObject> list = pool.find(query);
		ArrayList<GameObject> remove = new ArrayList<>();
		for(GameObject go:list) {
			if (go.getHoldCount()>0) {
				remove.add(go);
			}
			else {
				if (!go.hasThisAttribute("weapon")) {
					go.setThisAttribute(Constants.FACING_KEY,CardComponent.FACE_UP);
				}
			}
		}
		list.removeAll(remove);
		
		//filter duplicates
		ArrayList<GameObject> listFiltered = new ArrayList<>();
		ArrayList<String> names = new ArrayList<>();
		for (GameObject go:list) {
			if (!names.contains(go.getName())) {
				listFiltered.add(go);
				names.add(go.getName());
			}
		}
		
		Collections.sort(listFiltered,new Comparator<GameObject>() {
			public int compare(GameObject g1,GameObject g2) {
				return g1.getName().compareTo(g2.getName());
			}
		});
		RealmObjectChooser chooser = new RealmObjectChooser(title,magicRealmData,true,true);
		chooser.addObjectsToChoose(listFiltered);
		chooser.setVisible(true);
		RealmComponent.reset(); // Don't keep a cache!
		GameObject pick = chooser.getChosenObject();
		if (pick!=null) {
			GameObject go = GameObject.createEmptyGameObject();
			go.copyAttributesFrom(pick);
			go.removeThisAttribute("fame");
			go.removeThisAttribute("notoriety");
			go.removeThisAttribute("base_price");
			go.removeThisAttribute("activated");
			go.removeThisAttribute("native");
			go.setThisAttribute(Constants.LEVEL_KEY_TAG,getLevelKey());
			if (!go.hasThisAttribute("weapon")) {
				go.setThisAttribute(Constants.FACING_KEY,CardComponent.FACE_UP);
			}
			pick = go;
		}
		return pick;
	}
	
	private void updateIcon() {
		renameTreasureButton.setVisible(extraInventory.isTreasure());
		iconLabel.setIcon(extraInventory.getIcon());
		if (extraInventory.isHorse()) {
			SteedChitComponent horse = (SteedChitComponent)extraInventory;
			iconLabelFlipped.setIcon(horse.getFlipSideIcon());
		}
		else {
			iconLabelFlipped.setIcon(null);
		}
	}
	
	private String getBaseBlockName() {
		return Constants.BONUS_INVENTORY+getLevelKey();
	}

	protected void applyAdvantage() {
		setAttribute(Constants.BONUS_INVENTORY);
		GameObjectBlockManager man = new GameObjectBlockManager(character);
		man.storeGameObjectInBlocks(extraInventory.getGameObject(),getBaseBlockName());
	}
	
	public boolean isCurrent() {
		return hasAttribute(Constants.BONUS_INVENTORY);
	}

	public String toString() {
		return "Starting Inventory";
	}
	public String getSuggestedDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Starts with ");
		if (!extraInventory.isTreasure()) {
			sb.append("a ");
		}
		sb.append(extraInventory.getGameObject().getName());
		sb.append(" in inventory.");
		return sb.toString();
	}
}