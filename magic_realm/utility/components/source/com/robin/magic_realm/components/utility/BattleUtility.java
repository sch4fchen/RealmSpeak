package com.robin.magic_realm.components.utility;

import java.util.*;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.wrapper.*;

public class BattleUtility {
	private static CharacterActionChitComponent attentionMarker = null;

	public static Speed getFightSpeed(RealmComponent rc) {
		Speed speed = null;
		if (rc.isTreasure() && rc.getGameObject().hasThisAttribute("attack_speed")) {
			// This wont work for weapons, but that's what I want here!  Weapons are in ADDITION to fight speed.
			int mod = 0;
			if (rc.getGameObject().getHeldBy()!=null && new CombatWrapper(rc.getGameObject().getHeldBy()).isFreezed()) {
				mod = 1;
			}
			speed = new Speed(rc.getGameObject().getThisAttribute("attack_speed"),mod);
		}
		if (rc.isBattleChit()) { // this handles character action chits, natives, and horses
			BattleChit bc = (BattleChit)rc;
			speed = bc.getAttackSpeed();
		}
		return speed;
	}

	public static Speed getMagicSpeed(RealmComponent rc) {
		Speed speed = null;
		if (rc.isActionChit()) {
			CharacterActionChitComponent chit = (CharacterActionChitComponent)rc;
			if (chit.isMagic()) {
				speed = chit.getMagicSpeed();
			}
		}
		else {
			speed = new Speed(0);
		}
		return speed;
	}

	public static ArrayList<CharacterActionChitComponent> getPlayedChits(CharacterWrapper character) {
		ArrayList<CharacterActionChitComponent> list = new ArrayList<>();
		CombatWrapper combat = new CombatWrapper(character.getGameObject());
		for (GameObject go:combat.getUsedChits()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isActionChit()) { // might be a gloves or boots card...
				CharacterActionChitComponent chit = (CharacterActionChitComponent)rc;
				list.add(chit);
			}
		}
		
		for (CharacterActionChitComponent chit:character.getActiveAndAlertChits()) {
			CombatWrapper combatChit = new CombatWrapper(chit.getGameObject());
			if (combatChit.getCombatBox()>0) {
				// chit is in play
				list.add(chit);
			}
		}
		return list;
	}
	
	public static Effort getEffortUsed(CharacterWrapper character) {
		return getEffortUsed(character,false);
	}
	public static Effort getEffortUsed(CharacterWrapper character, boolean ignoreBerserk) {
		// Determine how many effort *s have already been played
		Effort totalEffort = new Effort();
		for (CharacterActionChitComponent chit:getPlayedChits(character)) {
			if (ignoreBerserk && chit.isFightAlert()) continue;
			totalEffort.addEffort(chit);
		}
		
		CombatWrapper combat = new CombatWrapper(character.getGameObject());
		GameObject spell = combat.getCastSpell();
		if (spell!=null) {
			SpellWrapper sp = new SpellWrapper(spell);
			GameObject incObj = sp.getIncantationObject();
			if (incObj!=null) {
				RealmComponent rc = RealmComponent.getRealmComponent(incObj);
				if (rc.isActionChit()) {
					CombatWrapper chit = new CombatWrapper(rc.getGameObject());
					// Only add effort if combat box is zero because if combat box is GREATER than
					// zero, it already got added by the previous loop above.
					if (chit.getCombatBox()==0) {
						totalEffort.addEffort((CharacterActionChitComponent)rc);
					}
				}
			}
		}
		
		return totalEffort;
	}

	public static CharacterActionChitComponent getAttentionMarker(CharacterWrapper character) {
		if (attentionMarker==null) {
			attentionMarker = new CharacterActionChitComponent(new GameObject(null));
			attentionMarker.setFaceDown();
		}
		String iconType = character.getGameObject().getThisAttribute("icon_type");
		String iconFolder = character.getGameObject().getThisAttribute(Constants.ICON_FOLDER);
		
		attentionMarker.getGameObject().setThisAttribute("character_chit");
		attentionMarker.getGameObject().setThisAttribute("icon_type",iconType);
		attentionMarker.getGameObject().setThisAttribute(Constants.ICON_FOLDER,iconFolder);
		return attentionMarker;
	}

	public static ArrayList<RealmComponent> findFightComponentsWithCombatBox(Collection<RealmComponent> list) {
		ArrayList<RealmComponent> fightChits = new ArrayList<>();
		for (RealmComponent rc : list) {
			CombatWrapper combat = new CombatWrapper(rc.getGameObject());
			if (combat.getCombatBox()>0 && (!rc.isActionChit() || combat.getPlacedAsFight() || combat.getPlacedAsParry())) {
				fightChits.add(rc);
			}
		}
		return fightChits;
	}

	public static RealmComponent findMoveComponentWithCombatBox(Collection<RealmComponent> list,boolean includeHorse) {
		RealmComponent ret = null;
		for (RealmComponent rc : list) {
			CombatWrapper combat = new CombatWrapper(rc.getGameObject());
			if (combat.getCombatBox()>0 && (!rc.isActionChit() || combat.getPlacedAsMove())) {
				if (!rc.isHorse() || includeHorse) {
					// found it!
					ret = rc;
					if (ret.isHorse()) {
						// If we found a horse, then this is the desirable thing to return, otherwise we'll keep searching
						break;
					}
				}
			}
		}
		return ret;
	}

	public static Speed getMoveSpeed(RealmComponent rc) {
		Speed speed = null;
		int mod = 0;
		if (rc.getGameObject().getHeldBy()!=null && new CombatWrapper(rc.getGameObject().getHeldBy()).isFreezed()) {
			mod = 1;
		}
		
		if (rc.isTreasure() && rc.getGameObject().hasThisAttribute("fly_speed")) {
			speed = new Speed(rc.getGameObject().getThisAttribute("fly_speed"),mod);
		}
		else if (rc.isTreasure() && rc.getGameObject().hasThisAttribute("move_speed")) {
			speed = new Speed(rc.getGameObject().getThisAttribute("move_speed"),mod);
		}
		else if (rc.isBattleChit()) { // this handles character action chits, natives, and horses
			BattleChit bc = (BattleChit)rc;
			speed = bc.getMoveSpeed();
		}
		else if (rc.isFlyChit()) {
			FlyChitComponent flyChit = (FlyChitComponent)rc;
			speed = new Speed(flyChit.getSpeed().getNum(),mod);
		}
		return speed;
	}

	public static void handleSpoilsOfWar(RealmComponent attacker,RealmComponent victim) {
		if (victim.getGameObject().hasThisAttribute(Constants.SPOILS_DONE)) {
			// Don't do this more than once per victim
			return;
		}
		RealmComponent attackerOwner = attacker.getOwner();
		RealmComponent victimOwner = victim.getOwner();
		
		// REWARDS
		if (attacker.isCharacter() || attackerOwner!=null) { // Attacker is Character or Hireling
			// Victim's inventory
			Collection<GameObject> c = RealmUtility.findInventory(victim);
			if (!c.isEmpty() && victim.isPlayerControlledLeader()) {
				if (attacker.isPlayerControlledLeader()) {
					// Victim's stuff is taken by the attacker
					victim.getGameObject().setThisAttribute(Constants.SPOILS_INVENTORY_TAKEN);
					RealmLogging.logMessage(attacker.getGameObject().getName(),
									"Gets inventory from the "
									+victim.getGameObject().getName()
									+":");
					for (GameObject thing : c) {
						RealmLogging.logMessage(attacker.getGameObject().getName(),"--> "+thing.getName());
					}
				}
				else {
					// Victim's stuff is abandoned in clearing
					victim.getGameObject().setThisAttribute(Constants.SPOILS_INVENTORY_DROP);
					RealmLogging.logMessage(RealmLogging.BATTLE,"The "+victim.getGameObject().getName()+" drops inventory in the clearing.");
				}
			}
		}
		else { // Attacker is unhired native or monster
			if (victim.isPlayerControlledLeader()) { // Victim is character or hired leader
				if (attacker.isMonster() || attacker.isMonsterPart() || attacker.isTreasure()) { // Monster or Toadstool Circle
					// Victim's stuff is abandoned in clearing
					victim.getGameObject().setThisAttribute(Constants.SPOILS_INVENTORY_DROP);
					RealmLogging.logMessage(RealmLogging.BATTLE,"The "+victim.getGameObject().getName()+" drops inventory in the clearing.");
				}
				else {
					// Victim's stuff is put onto setup card
					victim.getGameObject().setThisAttribute(Constants.SPOILS_INVENTORY_SETUP);
					RealmLogging.logMessage(RealmLogging.BATTLE,"The "+victim.getGameObject().getName()+"'s stuff is taken by the "+attacker.getGameObject().getThisAttribute("native"));
				}
			}
		}
		
		// PENALTIES
		if (victimOwner!=null && !victim.isCharacter()) { // Victim is a Hireling
			CharacterWrapper owningCharacter = new CharacterWrapper(victimOwner.getGameObject());
			
			// Owning character loses notoriety for loss of his/her hirelings
			int not = victim.getGameObject().getThisInt("notoriety");
			owningCharacter.addNotoriety(-not);
			RealmLogging.logMessage(owningCharacter.getGameObject().getName(),"Loses "
							+not+" notoriety for the loss of the "
							+victim.getGameObject().getName()+" hireling.");
		}
		if (victimOwner==null && (victim.isNativeLeader() || victim.isTransformedNativeLeader())) { // Unhired leader
			// Group's stuff is abandoned in the clearing
			victim.getGameObject().setThisAttribute(Constants.SPOILS_GROUP_INV_DROP);
			RealmLogging.logMessage(RealmLogging.BATTLE,"The native leader for the "+victim.getGameObject().getThisAttribute("native")+" drops the group's store in the clearing.");
		}
		victim.getGameObject().setThisAttribute(Constants.SPOILS_DONE);
	}
	public static boolean treacheryFlag = false;
	public static void processTreachery(CharacterWrapper activeCharacter,RealmComponent theTarget) {
		treacheryFlag = true;
		for (RealmComponent rc:activeCharacter.getAllHirelingsFromSame(theTarget)) {
			moveToNewSheet(rc,false,false);
			
			CombatWrapper combat = new CombatWrapper(rc.getGameObject());
			combat.setBetrayedBy(activeCharacter.getGameObject());
			CombatWrapper combatCharacter = new CombatWrapper(activeCharacter.getGameObject());
			combatCharacter.setBetrayed(rc.getGameObject());
			activeCharacter.addTreachery(theTarget.getGameObject());
			
			activeCharacter.removeHireling(rc.getGameObject());
			if (!activeCharacter.isBattling(rc.getGameObject())) {
				activeCharacter.addBattlingNative(rc.getGameObject());
			}
			int bounty = rc.getGameObject().getThisInt("notoriety");
			activeCharacter.addFame(-bounty);
			RealmLogging.logMessage(activeCharacter.getGameObject().getName(),"Loses "+bounty+" fame for treachery against "+rc.getGameObject().getName());
		}
	}
	/**
	 * This method moves the specified target to its own sheet, and deals with the old sheet, if necessary.
	 */
	public static void moveToNewSheet(RealmComponent toMove,boolean keepTarget,boolean lastAttackerFollows) {
		// Move the target to its own sheet (if necessary), and handle old sheet if needed
		CombatWrapper combat = new CombatWrapper(toMove.getGameObject());
		RealmComponent deployTargetsTarget = toMove.getTarget();
		RealmComponent deployTargetsTarget2 = toMove.get2ndTarget();
		if (!combat.isSheetOwner()) {
			combat.setSheetOwner(true); // well it is NOW!
			if (deployTargetsTarget!=null && !deployTargetsTarget.isCharacter()) {
				CombatWrapper dttc = new CombatWrapper(deployTargetsTarget.getGameObject());
				if (dttc.getAttackerCount()==1) {
					if (lastAttackerFollows) {
						// Move off the sheet...
						dttc.setSheetOwner(false);
						
						// ... and onto the new one as the last attacker
						deployTargetsTarget.setTarget(toMove);
					}
					else {
						deployTargetsTarget.clearTarget();
					}
				}
			}
			if (deployTargetsTarget2!=null && !deployTargetsTarget2.isCharacter()) {
				CombatWrapper dttc2 = new CombatWrapper(deployTargetsTarget2.getGameObject());
				if (dttc2.getAttackerCount()==1) {
					if (lastAttackerFollows) {
						// Move off the sheet...
						dttc2.setSheetOwner(false);
						
						// ... and onto the new one as the last attacker
						deployTargetsTarget2.setTarget(toMove);
					}
					else {
						deployTargetsTarget2.clear2ndTarget();
					}
				}
			}
		}
		if (keepTarget) {
			// I think the ONLY time this can happen, is when 2 T monsters hit the native... Can THAT happen?
			if (deployTargetsTarget!=null && !deployTargetsTarget.isCharacter()) {
				// If the deployTarget is keeping his target (RED-side monster), then the deployTargetsTarget should
				// move off the sheet anyway, dragging its attackers with it
				CombatWrapper dttc = new CombatWrapper(deployTargetsTarget.getGameObject());
				dttc.setSheetOwner(false);
			}
		}
		else {
			// Clear the target
			toMove.clearTargets();
		}
	}
}