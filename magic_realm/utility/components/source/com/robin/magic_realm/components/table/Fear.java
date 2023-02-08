package com.robin.magic_realm.components.table;

import java.util.*;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class Fear {
	public static boolean apply(SpellWrapper spell, GameObject target, TileLocation currentLocation) {
		if (currentLocation == null || currentLocation.clearing == null) return false;
				
		RealmComponent targetRc = RealmComponent.getRealmComponent(target);
		if (targetRc.isCharacter()) {
			CharacterWrapper character = new CharacterWrapper(target);
			RealmComponent discoverToLeave = ClearingUtility.findDiscoverToLeaveComponent(currentLocation,character);
			if (discoverToLeave != null) {
				RealmLogging.logMessage(RealmLogging.BATTLE, target.getName() +" is trapped and cannot flee.");
				return false;
			}
			
			ArrayList<ClearingDetail> possibleClearings = new ArrayList<>(character.findAvailableClearingMoves(true));
			if (possibleClearings.isEmpty()) {
				RealmLogging.logMessage(RealmLogging.BATTLE, target.getName() +" cannot run away, as way out exists.");
				return false;
			}
			ClearingDetail selectedClearing =  possibleClearings.get(RandomNumber.getRandom(possibleClearings.size()));
			TileLocation runToClearing = new TileLocation(selectedClearing);						
			ClearingUtility.moveToLocation(character.getGameObject(),runToClearing,true);
			character.addMoveHistory(character.getCurrentLocation());
			
			// All following hirelings need to remain behind
			for (RealmComponent hireling : character.getFollowingHirelings()) {
				currentLocation.clearing.add(hireling.getGameObject(),null);
				if (hireling.getGameObject().hasThisAttribute(Constants.CAPTURE)) {
					character.removeHireling(hireling.getGameObject());
					RealmLogging.logMessage(character.getGameObject().getName(),"The "+hireling.getGameObject().getName()+" escaped!");
				}
			}
		}
		else {
			ArrayList<ClearingDetail> possibleClearings = new ArrayList<>();
			for (ClearingDetail clearing : currentLocation.tile.getClearings()) {
				boolean addClearing = true;
				for (RealmComponent rc : clearing.getClearingComponents()) {
					if (rc.isCharacter() || rc.isHiredOrControlled()) {
						addClearing = false;
						break;
					}
				}
				if (addClearing) possibleClearings.add(clearing);
			}
			ClearingDetail selectedClearing =  possibleClearings.get(RandomNumber.getRandom(possibleClearings.size()));
			TileLocation runToClearing = new TileLocation(selectedClearing);
			
			if (possibleClearings.isEmpty()) {
				RealmLogging.logMessage(RealmLogging.BATTLE, target.getName() +" cannot run away, as no clearing without a character exists.");
				return false;
			}
			ClearingUtility.moveToLocation(target,runToClearing,false);
		}
		
		CombatWrapper.clearAllCombatInfo(target);
		targetRc.clearTargets();
		
		// Need to disengage any participants who are targeting the runner!
		CombatWrapper targetCombat = new CombatWrapper(target);
		for (RealmComponent attacker : targetCombat.getAttackersAsComponents()) {
			RealmComponent bpTarget = attacker.getTarget();
			if (bpTarget!=null && bpTarget.getGameObject() == target) {
				attacker.clearTarget();
			}
			RealmComponent bpTarget2 = attacker.get2ndTarget();
			if (bpTarget2!=null && bpTarget2.getGameObject() == target) {
				attacker.clear2ndTarget();
			}
		}
		
		RealmLogging.logMessage(RealmLogging.BATTLE, target.getName() +" ran away.");
		return true;
	}
}