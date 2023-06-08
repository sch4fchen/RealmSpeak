package com.robin.magic_realm.RealmBattle.targeting;

import java.util.*;

import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.*;

public class SpellTargetingClearing extends SpellTargetingSpecial {

	public SpellTargetingClearing(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}
	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		
		// Assume that activeParticipant IS character
		CharacterWrapper character = new CharacterWrapper(activeParticipant.getGameObject());
	
		ArrayList<String> clearingTargetType = spell.getGameObject().getThisAttributeList("target_clearing");
		if (clearingTargetType.contains("combatants")) {
			ArrayList<RealmComponent> allBattleParticipants = battleModel.getAllBattleParticipants(true); // clearing affects everything, including hidden!!!
			for (RealmComponent rc : allBattleParticipants) {
				gameObjects.add(rc.getGameObject());
			}
		}
		if (clearingTargetType.contains("characters")) {
			ArrayList<RealmComponent> allBattleParticipants = battleModel.getAllBattleParticipants(true); // clearing affects everything, including hidden!!!
			for (RealmComponent rc : allBattleParticipants) {
				if (rc.isCharacter() && !gameObjects.contains(rc.getGameObject())) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		if (clearingTargetType.contains("monsters")) {
			ArrayList<RealmComponent> allBattleParticipants = battleModel.getAllBattleParticipants(true); // clearing affects everything, including hidden!!!
			for (RealmComponent rc : allBattleParticipants) {
				if (rc.isMonster() && !gameObjects.contains(rc.getGameObject())) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		if (clearingTargetType.contains("demons")) {
			ArrayList<RealmComponent> allBattleParticipants = battleModel.getAllBattleParticipants(true); // clearing affects everything, including hidden!!!
			for (RealmComponent rc : allBattleParticipants) {
				if (rc.getGameObject().hasThisAttribute(Constants.DEMON) && !gameObjects.contains(rc.getGameObject())) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		if (clearingTargetType.contains("ghosts")) {
			ArrayList<RealmComponent> allBattleParticipants = battleModel.getAllBattleParticipants(true); // clearing affects everything, including hidden!!!
			for (RealmComponent rc : allBattleParticipants) {
				if (rc.getGameObject().hasThisAttribute(Constants.GHOST) && !gameObjects.contains(rc.getGameObject())) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		if (clearingTargetType.contains("skeletons")) {
			ArrayList<RealmComponent> allBattleParticipants = battleModel.getAllBattleParticipants(true); // clearing affects everything, including hidden!!!
			for (RealmComponent rc : allBattleParticipants) {
				if (rc.getGameObject().hasThisAttribute(Constants.SKELETON) && !gameObjects.contains(rc.getGameObject())) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		if (clearingTargetType.contains("wraiths")) {
			ArrayList<RealmComponent> allBattleParticipants = battleModel.getAllBattleParticipants(true); // clearing affects everything, including hidden!!!
			for (RealmComponent rc : allBattleParticipants) {
				if (rc.getGameObject().hasThisAttribute(Constants.WRAITH) && !gameObjects.contains(rc.getGameObject())) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		if (clearingTargetType.contains("ghouls")) {
			ArrayList<RealmComponent> allBattleParticipants = battleModel.getAllBattleParticipants(true); // clearing affects everything, including hidden!!!
			for (RealmComponent rc : allBattleParticipants) {
				if (rc.getGameObject().hasThisAttribute(Constants.GHOUL) && !gameObjects.contains(rc.getGameObject())) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		if (clearingTargetType.contains("undead")) {
			ArrayList<RealmComponent> allBattleParticipants = battleModel.getAllBattleParticipants(true); // clearing affects everything, including hidden!!!
			for (RealmComponent rc : allBattleParticipants) {
				if (rc.getGameObject().hasThisAttribute(Constants.UNDEAD) && !gameObjects.contains(rc.getGameObject())) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		if (clearingTargetType.contains("spells")) {
			SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(spell.getGameObject().getGameData());
			for (SpellWrapper sw : sm.getAllSpellsInClearing(battleModel.getBattleLocation(),true)) {
				gameObjects.add(sw.getGameObject());
			}
		}
		if (clearingTargetType.contains("curses")) {
			for (RealmComponent rc : battleModel.getAllParticipatingCharacters()) {
				CharacterWrapper thisCharacter = new CharacterWrapper(rc.getGameObject());
				Collection<String> curses = thisCharacter.getAllCurses();
				if (curses.size()>0) {
					gameObjects.add(rc.getGameObject());
				}
			}
		}
		if (clearingTargetType.contains("horses")) {
			ArrayList<RealmComponent> allBattleParticipants = battleModel.getAllBattleParticipants(true); // clearing affects everything, including hidden!!!
			for (RealmComponent rc : allBattleParticipants) {
				if ((rc.getGameObject().hasThisAttribute("horse") || rc.getGameObject().hasThisAttribute(RealmComponent.MONSTER_STEED)) && !gameObjects.contains(rc.getGameObject())) {
					gameObjects.add(rc.getGameObject());
				}
				for (GameObject go : rc.getHold()) {
					if ((go.hasThisAttribute("horse") || go.hasThisAttribute(RealmComponent.MONSTER_STEED)) && !gameObjects.contains(go)) {
						gameObjects.add(go);
					}
				}
			}
		}
		boolean ignorebattle = spell.getGameObject().hasThisAttribute("nobattle");
		for (GameObject theTarget : gameObjects) {
			spell.addTarget(combatFrame.getHostPrefs(),theTarget,ignorebattle);
			if (!ignorebattle) {
				combatFrame.makeWatchfulNatives(RealmComponent.getRealmComponent(theTarget),true);
			}
			CombatFrame.broadcastMessage(character.getGameObject().getName(),"Targets the "+theTarget.getNameWithNumber()+" with "+spell.getGameObject().getName());
		}
		if (!gameObjects.isEmpty()) {
			JOptionPane.showMessageDialog(combatFrame,"All valid targets are selected.",spell.getName(),JOptionPane.INFORMATION_MESSAGE);
		}
		if (clearingTargetType.contains("clearing")) {
			// Affects the clearing itself
			TileLocation loc = battleModel.getBattleLocation();
			
			spell.addTarget(combatFrame.getHostPrefs(),loc.tile.getGameObject(),true);
			spell.setExtraIdentifier(String.valueOf(loc.clearing.getNum()));
			
			JOptionPane.showMessageDialog(combatFrame,"The current clearing is selected.",spell.getName(),JOptionPane.INFORMATION_MESSAGE);
			CombatFrame.broadcastMessage(character.getGameObject().getName(),"Targets clearing "
					+loc.clearing.getNum()
					+" of the "+loc.tile.getGameObject().getName()+".");
		}
		return true;
	}
}