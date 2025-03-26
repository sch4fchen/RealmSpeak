package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.MonsterChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.swing.CenteredMapView;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmObjectMaster;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingAskDemon extends SpellTargetingSpecial {

	public SpellTargetingAskDemon(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}
	public boolean hasTargets() {
		return !gameObjects.isEmpty();  // always true
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		ArrayList<RealmComponent> allDenizens = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true);
		ArrayList<RealmComponent> allParticipantsSansDenizens = combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(false),true);
		allDenizens.removeAll(allParticipantsSansDenizens);
		for (RealmComponent rc : allDenizens) {
			if (rc.isMonster() && !rc.hasMagicProtection() && !rc.hasMagicColorImmunity(spell)) {
				if ((rc.getGameObject().hasThisAttribute(Constants.DEMON) || rc.getGameObject().hasThisAttribute(Constants.DEVIL)) && !rc.getGameObject().hasThisAttribute(Constants.IMP)) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		if (gameObjects.isEmpty()) {
			return true;
		}
		
		// Pick Demon
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(combatFrame,"Select a Target for "+spell.getName()+":",true);
		chooser.addGameObjects(gameObjects,false);
		chooser.setVisible(true);
		if (chooser.getSelectedText()==null) {
			return false;
		}
		MonsterChitComponent demon = (MonsterChitComponent)chooser.getFirstSelectedComponent();
		
		// Pick a Player
		GamePool pool = new GamePool(RealmObjectMaster.getRealmObjectMaster(battleModel.getGameData()).getPlayerCharacterObjects());
		ArrayList<String> playerNames = new ArrayList<>();
		ArrayList<GameObject> list = pool.find(CharacterWrapper.NAME_KEY);
		for (GameObject go:list) {
			CharacterWrapper character = new CharacterWrapper(go);
			String name = character.getPlayerName();
			if (!playerNames.contains(name)) {
				playerNames.add(name);
			}
		}
//		playerNames.remove((new CharacterWrapper(activeParticipant.getGameObject())).getPlayerName());
		if (!spell.getGameObject().hasThisAttribute(Constants.ASKDEMON_PEEK) && playerNames.isEmpty()) {
			gameObjects.clear();
			return true;
		}		
		
		String choice = Constants.ASK_DEMON_QUESTION;
		
		if (spell.getGameObject().hasThisAttribute(Constants.ASKDEMON_PEEK)) {
			ButtonOptionDialog optionChooser = new ButtonOptionDialog(combatFrame,demon.getIcon(),"Which information do you want?","Ask Demon",true);
			if (!playerNames.isEmpty()) {
				optionChooser.addSelectionObject(Constants.ASK_DEMON_QUESTION);
			}
			optionChooser.addSelectionObject(Constants.ASK_DEMON_PEEK_TILE);
			optionChooser.addSelectionObject(Constants.ASK_DEMON_PEEK_BOX);
			if (!playerNames.isEmpty()) {
				optionChooser.addSelectionObject(Constants.ASK_DEMON_PEEK_QUEST_CARDS);
			}
			optionChooser.setVisible(true);
			choice = (String)optionChooser.getSelectedObject();
			if (choice==null) {
				return false;
			}
		}
		
		if (choice==Constants.ASK_DEMON_QUESTION) {
			ButtonOptionDialog nameChooser = new ButtonOptionDialog(combatFrame,demon.getIcon(),"Which player do you want information from?","Ask Demon",true);
			nameChooser.addSelectionObjects(playerNames);
			nameChooser.setVisible(true);
			String playerName = (String)nameChooser.getSelectedObject();
			if (playerName==null) {
				return false;
			}
			
			// Select a Question
			String input = (String)JOptionPane.showInputDialog(
								combatFrame,
								"You must ask a question that can be answered with a\n"
								+"yes or a no, or a number.  You CANNOT ask about future\n"
								+"intents, only about past or present conquests.\n\nWhat do you ask?",
								"Ask Demon",
								JOptionPane.QUESTION_MESSAGE,demon.getIcon(),null,null);
			
			if (input==null) {
				return false;
			}
			
			// Finally, we can assign this info
			spell.addTarget(combatFrame.getHostPrefs(),demon.getGameObject());
			spell.setExtraIdentifier(playerName + Constants.DEMON_Q_DELIM + input);
			return true;
		}
		
		if (choice==Constants.ASK_DEMON_PEEK_TILE) {			
			CenteredMapView.getSingleton().markAllTiles(true);
			TileLocation tl = SpellUtility.chooseTileLocation(combatFrame,"Select a tile");
			spell.addTarget(combatFrame.getHostPrefs(),demon.getGameObject());
			spell.setExtraIdentifier(Constants.ASK_DEMON_PEEK_TILE);
			spell.setSecondaryTarget(tl.tile.getGameObject());
			return true;
		}
		
		if (choice==Constants.ASK_DEMON_PEEK_BOX) {
			GamePool boxPool = new GamePool(activeParticipant.getGameObject().getGameData().getGameObjects());
			RealmComponentOptionChooser boxChooser = new RealmComponentOptionChooser(combatFrame, "Select a box", true);
			for (GameObject box : boxPool.find(RealmComponent.VIRTUAL_DWELLING)) {
				boxChooser.addRealmComponent(RealmComponent.getRealmComponent(box), true);
			}
			for (GameObject box : boxPool.find(RealmComponent.TREASURE_LOCATION+",!"+RealmComponent.TREASURE_WITHIN_TREASURE)) {
				boxChooser.addRealmComponent(RealmComponent.getRealmComponent(box), true);
			}
			boxChooser.setVisible(true);
			if (boxChooser.getSelectedText() == null) {
				return false;
			}
			spell.addTarget(combatFrame.getHostPrefs(),demon.getGameObject());
			spell.setExtraIdentifier(Constants.ASK_DEMON_PEEK_BOX);
			spell.setSecondaryTarget(boxChooser.getFirstSelectedComponent().getGameObject());
			return true;
		}
		
		if (choice==Constants.ASK_DEMON_PEEK_QUEST_CARDS) {
			ArrayList<GameObject> players = pool.find(CharacterWrapper.NAME_KEY+",!"+Constants.DEAD);
			RealmComponentOptionChooser playerChooser = new RealmComponentOptionChooser(combatFrame, "Which player do you want to peek the Quest cards from?", true);
			for (GameObject player : players) {
				playerChooser.addRealmComponent(RealmComponent.getRealmComponent(player), true);
			}
			playerChooser.setVisible(true);
			if (playerChooser.getSelectedText() == null) {
				return false;
			}
			spell.addTarget(combatFrame.getHostPrefs(),demon.getGameObject());
			spell.setExtraIdentifier(Constants.ASK_DEMON_PEEK_QUEST_CARDS);
			spell.setSecondaryTarget(playerChooser.getFirstSelectedComponent().getGameObject());
			return true;
		}
		
		return false;
	}
}