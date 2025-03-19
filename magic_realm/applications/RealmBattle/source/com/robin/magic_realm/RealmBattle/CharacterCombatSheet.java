package com.robin.magic_realm.RealmBattle;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

import javax.swing.*;

import com.robin.game.objects.GameObject;
import com.robin.game.server.GameClient;
import com.robin.general.graphics.GraphicsUtil;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.general.swing.ImageCache;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.SpellSet;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.TreasureUtility;
import com.robin.magic_realm.components.utility.TreasureUtility.ArmorType;
import com.robin.magic_realm.components.wrapper.*;

public class CharacterCombatSheet extends CombatSheet {

	private static final int POS_OWNER				= 0;
	
	private static final int POS_TARGET				= 1; // non-positioned targets
	private static final int POS_TARGET_BOX1		= 2; // charge and thrust 1,1
	private static final int POS_TARGET_BOX2		= 3; // dodge and swing 2,2
	private static final int POS_TARGET_BOX3		= 4; // duck and smash 3,3
	
	private static final int POS_MOVE_BOX1			= 5;
	private static final int POS_MOVE_BOX2			= 6;
	private static final int POS_MOVE_BOX3			= 7;
	
	private static final int POS_ATTACK				= 8; // non-positioned attacks
	private static final int POS_ATTACK_BOX1		= 9;
	private static final int POS_ATTACK_BOX2		=10;
	private static final int POS_ATTACK_BOX3		=11;
	
	private static final int POS_ATTACK_WEAPON1		=12;
	private static final int POS_ATTACK_WEAPON2		=13;
	private static final int POS_ATTACK_WEAPON3		=14;
	
	private static final int POS_SHIELD1			=15; // blocks 1
	private static final int POS_SHIELD2			=16; // blocks 2
	private static final int POS_SHIELD3			=17; // blocks 3
	private static final int POS_BREASTPLATE		=18; // blocks 1 and 2
	private static final int POS_HELMET				=19; // blocks 3
	private static final int POS_SUITOFARMOR		=20; // blocks 1-3
	
	private static final int POS_USEDCHITS			=21;
	private static final int POS_CHARGECHITS		=22;
	
	private static final int POS_DEADBOX			=23;
	
	private static final int POS_PARRY1				=24;
	private static final int POS_PARRY2				=25;
	private static final int POS_PARRY3				=26;
	
	private static final int POS_TARGET_CHARGE_SMASH	= 27; // attack (x-axis): 3 defense (y-axis): 1
	private static final int POS_TARGET_CHARGE_SWING	= 28; // attack (x-axis): 2 defense (y-axis): 1
	private static final int POS_TARGET_DODGE_SMASH		= 29; // attack (x-axis): 3 defense (y-axis): 2
	private static final int POS_TARGET_DODGE_THRUST	= 30; // attack (x-axis): 1 defense (y-axis): 2
	private static final int POS_TARGET_DUCK_SWING		= 31; // attack (x-axis): 2 defense (y-axis): 3
	private static final int POS_TARGET_DUCK_THRUST		= 32; // attack (x-axis): 1 defense (y-axis): 3
	
	private static final int CHAR_ROW1 = 74;
	private static final int CHAR_ROW2 = 171;
	private static final int CHAR_ROW3 = 268;
	
	private static final int CHAR_COL1 = 92;
	private static final int CHAR_COL2 = 208;
	private static final int CHAR_COL3 = 322;
	
	private static final Point[] CHARACTER_SHEET = {
			new Point(483,663),
			
			// Targets
			new Point(303,CHAR_ROW1),
			new Point(CHAR_COL1,CHAR_ROW1),
			new Point(CHAR_COL2,CHAR_ROW2),
			new Point(CHAR_COL3,CHAR_ROW3),
			
			// Move
			new Point(CHAR_COL1,690),
			new Point(CHAR_COL2,690),
			new Point(CHAR_COL3,690),
			
			// Attacks
			new Point(530,25),
			new Point(429,CHAR_ROW1),
			new Point(429,CHAR_ROW2),
			new Point(429,CHAR_ROW3),
			
			// Attacks - Weapon
			new Point(525,CHAR_ROW1),
			new Point(525,CHAR_ROW2),
			new Point(525,CHAR_ROW3),
			
			// Defenses
			new Point(CHAR_COL1,402), //shield
			new Point(CHAR_COL2,402), //shield
			new Point(CHAR_COL3,402), //shield
			new Point(150,507), //breastplate
			new Point(321,507), //helmet
			new Point(206,603), //suit of armor
			
			new Point(494,458), // Used Chits
			new Point(400,700), // Charge Chits
			
			new Point(CHAR_COL1,CHAR_ROW3), // Dead Box
			
			// Parry for Super Realm
			new Point(CHAR_COL1,390),
			new Point(CHAR_COL2,390),
			new Point(CHAR_COL3,390),
			
			// Additional targets for Super Realm
			new Point(CHAR_COL3,CHAR_ROW1),
			new Point(CHAR_COL2,CHAR_ROW1),
			new Point(CHAR_COL3,CHAR_ROW2),
			new Point(CHAR_COL1,CHAR_ROW2),
			new Point(CHAR_COL2,CHAR_ROW3),
			new Point(CHAR_COL1,CHAR_ROW3),
	};
	
	private RealmComponent sheetOwnerShield;
	
	private ArrayList<Rectangle> spellRegions;
	private Hashtable<Rectangle,SpellCardComponent> spellRegionHash;
	
	HostPrefWrapper hostPrefs;
	
	/**
	 * Testing constructor ONLY!!!
	 */
	private CharacterCombatSheet() {
		super();
	}
	public CharacterCombatSheet(CombatFrame frame,BattleModel model,RealmComponent participant,boolean interactiveFrame, HostPrefWrapper hostPrefs) {
		super(frame,model,participant,interactiveFrame);
		this.hostPrefs = hostPrefs;
		spellRegions = new ArrayList<>();
		spellRegionHash = new Hashtable<>();
		updateLayout();
	}
	
	protected int getDeadBoxIndex() {
		return POS_DEADBOX;
	}
	
	protected int getBoxIndexFromCombatBoxes(int boxA, int boxD) {
		if (boxA == 0 || boxD==0) return POS_TARGET;
		if (boxA == 1 && boxD==1) return POS_TARGET_BOX1;
		if (boxA == 2 && boxD==2) return POS_TARGET_BOX2;
		if (boxA == 3 && boxD==3) return POS_TARGET_BOX3;
		if (boxA == 3 && boxD==1) return POS_TARGET_CHARGE_SMASH;
		if (boxA == 2 && boxD==1) return POS_TARGET_CHARGE_SWING;
		if (boxA == 3 && boxD==2) return POS_TARGET_DODGE_SMASH;
		if (boxA == 1 && boxD==2) return POS_TARGET_DODGE_THRUST;
		if (boxA == 2 && boxD==3) return POS_TARGET_DUCK_SWING;
		if (boxA == 1 && boxD==3) return POS_TARGET_DUCK_THRUST;
		return -1;
	}
	protected int getBoxIndexFromCombatBoxesForDefender(int boxA, int boxD) {
		return getBoxIndexFromCombatBoxes(boxA,boxD);
	}
	
	protected ImageIcon getImageIcon() {
		return ImageCache.getIcon("combat/char_melee2");
	}
	
	protected int getHotSpotSize() {
		return 98;
	}
	
	protected Point[] getPositions() {
		return CHARACTER_SHEET;
	}
	
	protected String[] splitHotSpot(int index) {
		if (combatFrame==null) return horseRiderSplit; // for testing
		if (combatFrame.getActionState()==Constants.COMBAT_POSITIONING) {
			switch(index) {
				case POS_TARGET:
					if (containsHorse(layoutHash.getList(POS_TARGET))) {
						if (getAllBoxListFromLayout(POS_TARGET_BOX1).isEmpty()
								&& getAllFromSingleBoxListFromLayout(POS_TARGET_CHARGE_SMASH).isEmpty()
								&& getAllFromSingleBoxListFromLayout(POS_TARGET_CHARGE_SWING).isEmpty()
								&& getAllFromSingleBoxListFromLayout(POS_TARGET_DODGE_SMASH).isEmpty()
								&& getAllFromSingleBoxListFromLayout(POS_TARGET_DODGE_THRUST).isEmpty()
								&& getAllFromSingleBoxListFromLayout(POS_TARGET_DUCK_SWING).isEmpty()
								&& getAllFromSingleBoxListFromLayout(POS_TARGET_DUCK_THRUST).isEmpty()) {
							return horseRiderSplit;
						}
					}
					break;
				case POS_TARGET_BOX1:
				case POS_TARGET_BOX2:
				case POS_TARGET_BOX3:
				case POS_TARGET_CHARGE_SMASH:
				case POS_TARGET_CHARGE_SWING:
				case POS_TARGET_DODGE_SMASH:
				case POS_TARGET_DODGE_THRUST:
				case POS_TARGET_DUCK_SWING:
				case POS_TARGET_DUCK_THRUST:
					if (containsHorse(layoutHash.getList(POS_TARGET))) return horseRiderSplit;
					break;
			}
		}
		return null;
	}

	/**
	 * Based on the current action, hotspots are initialized
	 */
	protected void updateHotSpots() {
		hotspotHash.clear();
		CombatWrapper tile = new CombatWrapper(getBattleLocation().tile.getGameObject());
		if (tile.isPeaceClearing(getBattleLocation().clearing.getNum()) || tile.isSleepClearing(getBattleLocation().clearing.getNum()) || !interactiveFrame) {
			// No activities allowed!
			return;
		}
		CombatWrapper combat = new CombatWrapper(combatFrame.getActiveParticipant().getGameObject());
		GameObject go = combat.getCastSpell();
		SpellWrapper spell = go==null?null:new SpellWrapper(go);
		boolean battleMage = false;
		if (combatFrame.getActiveParticipant().isCharacter()) {
			GameObject chararacterGo = combatFrame.getActiveParticipant().getGameObject();
			CharacterWrapper activeCharacter = new CharacterWrapper(chararacterGo);
			if (activeCharacter.affectedByKey(Constants.BATTLE_MAGE) || hostPrefs.hasPref(Constants.SR_ADV_STEEL_AGAINST_MAGIC)) {
				if (activeCharacter.hasOnlyStaffAsActivatedWeapon() && !activeCharacter.hasActiveArmorChits()) {
					battleMage = true;
				}
			}
		}
		ArrayList<RealmComponent> attackers = model.getAttackersFor(combatFrame.getActiveParticipant());
		switch(combatFrame.getActionState()) {
			case Constants.COMBAT_LURE:
				if (!sheetOwner.isMistLike()) {
					if (sheetOwner.equals(combatFrame.getActiveParticipant())) {
						if (combatFrame.areDenizensToLure()) {
							hotspotHash.put(Integer.valueOf(POS_TARGET),"Lure");
						}
					}
				}
				break;
			case Constants.COMBAT_ASSIGN:
				ArrayList<WeaponChitComponent> weapons = (new CharacterWrapper(combatFrame.getActiveParticipant().getGameObject())).getActiveWeapons();
				if ((spell==null || battleMage)
						&& (combatFrame.getActiveParticipant().getTarget()==null
						|| (combatFrame.getActiveParticipant().get2ndTarget()==null && combatFrame.getActiveParticipant().isCharacter() && weapons!=null && weapons.size() > 1))) {
					if (containsEnemy(combatFrame.getActiveParticipant(),layoutHash.getList(Integer.valueOf(POS_TARGET)))) {
						hotspotHash.put(Integer.valueOf(POS_TARGET),combatFrame.getActiveParticipant().getGameObject().getName()+" Target");
					}
					
					// Allow character to be targeted by another character
					if (combatFrame.getActiveCharacterIsHere()
							&& !sheetOwner.equals(combatFrame.getActiveParticipant())
							&& combatFrame.canBeSeen(sheetOwner,false)) {
						hotspotHash.put(Integer.valueOf(POS_OWNER),combatFrame.getActiveParticipant().getGameObject().getName()+" Target");
					}
				}
				break;
			case Constants.COMBAT_POSITIONING:
				if (sheetOwner.equals(combatFrame.getActiveParticipant())) {
					hotspotHash.put(Integer.valueOf(POS_MOVE_BOX1),"Maneuver");
					hotspotHash.put(Integer.valueOf(POS_MOVE_BOX2),"Maneuver");
					hotspotHash.put(Integer.valueOf(POS_MOVE_BOX3),"Maneuver");
					
					boolean s1 = hasArmor(POS_SHIELD1);
					boolean s2 = hasArmor(POS_SHIELD2);
					boolean s3 = hasArmor(POS_SHIELD3);
					boolean canParryLikeShield = false;
					if (combatFrame.getActiveParticipant().isCharacter()) {
						GameObject chararacterGo = combatFrame.getActiveParticipant().getGameObject();
						CharacterWrapper activeCharacter = new CharacterWrapper(chararacterGo);
						canParryLikeShield = hostPrefs.hasPref(Constants.OPT_PARRY_LIKE_SHIELD) || activeCharacter.affectedByKey(Constants.PARRY_LIKE_SHIELD) || activeCharacter.affectedByKey(Constants.BLOCK_NO_WEAPON);
					}
					
					if (s2 || s3 || canParryLikeShield) {
						hotspotHash.put(Integer.valueOf(POS_SHIELD1),"Position Shield");
					}
					if (s1 || s3 || canParryLikeShield) {
						hotspotHash.put(Integer.valueOf(POS_SHIELD2),"Position Shield");
					}
					if (s1 || s2 || canParryLikeShield) {
						hotspotHash.put(Integer.valueOf(POS_SHIELD3),"Position Shield");
					}
					
					if (layoutHash.get(Integer.valueOf(POS_TARGET_BOX1))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_BOX2))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_BOX3))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_CHARGE_SMASH))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_CHARGE_SWING))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_DODGE_SMASH))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_DODGE_THRUST))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_DUCK_SWING))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_DUCK_THRUST))!=null) {
						hotspotHash.put(Integer.valueOf(POS_TARGET),"Reset");
					}
					else if (layoutHash.get(Integer.valueOf(POS_TARGET))!=null && !hostPrefs.hasPref(Constants.SR_COMBAT)) {
						hotspotHash.put(Integer.valueOf(POS_TARGET),"Auto-Position");
					}
					
					if (layoutHash.get(Integer.valueOf(POS_TARGET))!=null && !hostPrefs.hasPref(Constants.SR_COMBAT)) {
						hotspotHash.put(Integer.valueOf(POS_TARGET_BOX1),"Position Target");
						hotspotHash.put(Integer.valueOf(POS_TARGET_BOX2),"Position Target");
						hotspotHash.put(Integer.valueOf(POS_TARGET_BOX3),"Position Target");
						if (hostPrefs.hasPref(Constants.SR_COMBAT)) {
							hotspotHash.put(Integer.valueOf(POS_TARGET_CHARGE_SMASH),"Position Target");
							hotspotHash.put(Integer.valueOf(POS_TARGET_CHARGE_SWING),"Position Target");
							hotspotHash.put(Integer.valueOf(POS_TARGET_DODGE_SMASH),"Position Target");
							hotspotHash.put(Integer.valueOf(POS_TARGET_DODGE_THRUST),"Position Target");
							hotspotHash.put(Integer.valueOf(POS_TARGET_DUCK_SWING),"Position Target");
							hotspotHash.put(Integer.valueOf(POS_TARGET_DUCK_THRUST),"Position Target");
						}
					}
				}
				// Have to have a target to attack!  (Not really:  see rule 22.4/2a)
				ArrayList<RealmComponent> allSheetParticipants = new ArrayList<>(sheetParticipants);
				allSheetParticipants.add(sheetOwner);
				RealmComponent target = combatFrame.getActiveParticipant().getTarget();
				RealmComponent target2 = combatFrame.getActiveParticipant().get2ndTarget();
				boolean sheetHasTarget = (target==null && target2==null && (spell==null || battleMage) && sheetOwner.equals(combatFrame.getActiveParticipant()))
									|| (target!=null && allSheetParticipants.contains(target))
									|| (target2!=null && allSheetParticipants.contains(target2));
				boolean sheetHasSpellTarget = spell!=null && spell.isAttackSpell() && (spell.targetsRealmComponents(allSheetParticipants) || spell.noTargeting());
				if (sheetHasTarget || sheetHasSpellTarget) {
					int boxReq = spell==null?0:spell.getGameObject().getThisInt("box_req"); // most spells will be zero
					if ((spell==null || battleMage) || boxReq==0 || boxReq==1) {
						hotspotHash.put(Integer.valueOf(POS_ATTACK_WEAPON1),"Attack");
					}
					if ((spell==null || battleMage) || boxReq==0 || boxReq==2) {
						hotspotHash.put(Integer.valueOf(POS_ATTACK_WEAPON2),"Attack");
					}
					if ((spell==null || battleMage) || boxReq==0 || boxReq==3) {
						hotspotHash.put(Integer.valueOf(POS_ATTACK_WEAPON3),"Attack");
					}
				}
				// Attacking hirelings
				if (containsFriendOrDenizen(
						combatFrame.getActiveParticipant(),
						getAllBoxListFromLayout(POS_ATTACK_BOX1))) {
					hotspotHash.put(Integer.valueOf(POS_ATTACK_BOX1),"Position");
					hotspotHash.put(Integer.valueOf(POS_ATTACK_BOX2),"Position");
					hotspotHash.put(Integer.valueOf(POS_ATTACK_BOX3),"Position");
				}
				break;
			case Constants.COMBAT_TACTICS:
				CharacterWrapper character = combatFrame.getActiveCharacter();
				
				// Check conditions for REPLACE_MOVE (Elusive Cloak)
				if (sheetOwner.equals(combatFrame.getActiveParticipant())) {
					if (character.canReplaceMove(attackers)) {
						// can replace move
						hotspotHash.put(Integer.valueOf(POS_MOVE_BOX1),"Replace Move");
						hotspotHash.put(Integer.valueOf(POS_MOVE_BOX2),"Replace Move");
						hotspotHash.put(Integer.valueOf(POS_MOVE_BOX3),"Replace Move");
					}
				}
				
				// Check conditions for REPLACE_FIGHT (Battle Bracelets)
				RealmComponent aTarget1 = combatFrame.getActiveParticipant().getTarget();
				RealmComponent aTarget2 = combatFrame.getActiveParticipant().get2ndTarget();
				boolean canReplaceFightForTarget1 = combatFrame.getActiveParticipant().getTarget()!=null && (sheetParticipants.contains(aTarget1) || sheetOwner.equals(aTarget1));
				boolean canReplaceFightForTarget2 = combatFrame.getActiveParticipant().get2ndTarget()!=null && (sheetParticipants.contains(aTarget2) || sheetOwner.equals(aTarget2));
				if ((canReplaceFightForTarget1 && character.canReplaceFight(aTarget1)) || (canReplaceFightForTarget2 && character.canReplaceFight(aTarget2))) {
					// can replace fight
					hotspotHash.put(Integer.valueOf(POS_ATTACK_BOX1),"Replace Fight");
					hotspotHash.put(Integer.valueOf(POS_ATTACK_BOX2),"Replace Fight");
					hotspotHash.put(Integer.valueOf(POS_ATTACK_BOX3),"Replace Fight");
				}
				else {
					if (hostPrefs.hasPref(Constants.SR_ADV_SURVIVAL_TACTICS)) {
						if ((canReplaceFightForTarget1 && character.canReplaceParryThrustAttacks(aTarget1)) || (canReplaceFightForTarget2 && character.canReplaceParryThrustAttacks(aTarget2))
								|| (canReplaceFightForTarget1 && character.canReplaceAlertedParryInBox(aTarget1,1)) || (canReplaceFightForTarget2 && character.canReplaceAlertedParryInBox(aTarget2,1))) {
							hotspotHash.put(Integer.valueOf(POS_ATTACK_BOX1),"Replace Fight");
						}
						if ((canReplaceFightForTarget1 && character.canReplaceParrySwingAttacks(aTarget1)) || (canReplaceFightForTarget2 && character.canReplaceParrySwingAttacks(aTarget2))
								|| (canReplaceFightForTarget1 && character.canReplaceAlertedParryInBox(aTarget1,2)) || (canReplaceFightForTarget2 && character.canReplaceAlertedParryInBox(aTarget2,2))) {
							hotspotHash.put(Integer.valueOf(POS_ATTACK_BOX2),"Replace Fight");
						}
						if ((canReplaceFightForTarget1 && character.canReplaceParrySmashAttacks(aTarget1)) || (canReplaceFightForTarget2 && character.canReplaceParrySmashAttacks(aTarget2))
								|| (canReplaceFightForTarget1 && character.canReplaceAlertedParryInBox(aTarget1,3)) || (canReplaceFightForTarget2 && character.canReplaceAlertedParryInBox(aTarget2,3))) {
							hotspotHash.put(Integer.valueOf(POS_ATTACK_BOX3),"Replace Fight");
						}
					}
				}				
				break;
		}
	}
	protected boolean hasArmor(int index) {
		ArrayList<RealmComponent> list = layoutHash.getList(Integer.valueOf(index));
		if (list!=null) {
			for (RealmComponent rc : list) {
				ArmorType armorType = TreasureUtility.getArmorType(rc.getGameObject());
				if (armorType!=ArmorType.None) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * This method will find all the relevant info from the participant, and layout chits accordingly
	 */
	protected void updateLayout() {
		// First, determine if secrecy is needed (attacked by other character)
		boolean needsSecrecy = alwaysSecret;
		if (combatFrame.getActionState()<Constants.COMBAT_TACTICS
				&& !combatFrame.getActiveParticipant().equals(sheetOwner)
				&& isAttackedByCharacter()) {
			needsSecrecy = true;
		}
		
		battleChitsWithRolls.clear();
		layoutHash.clear();
		layoutHash.put(Integer.valueOf(POS_OWNER),sheetOwner);
		
		/*
		 * Cycle through all monsters and natives (not characters) that are in the model, that
		 * are targeting the sheetOwner.  (go into target boxes)
		 */
		sheetParticipants = new ArrayList<>();
		ArrayList<RealmComponent> exclude = new ArrayList<>();
		ArrayList<RealmComponent> all = model.getAllBattleParticipants(true);
		for (RealmComponent rc : all) {
			CombatWrapper rcCombat = new CombatWrapper(rc.getGameObject());
			RealmComponent target = rc.getTarget();
			RealmComponent target2 = rc.get2ndTarget();
			
			// If targeting the sheetOwner, put them on the sheet in the target boxes
			if ((target!=null && target.equals(sheetOwner)) || (target2!=null && target2.equals(sheetOwner))) {
				if (!rc.isCharacter()) {
					exclude.add(rc);
					if (!addedToDead(rc)) {
						updateBattleChitsWithRolls(rcCombat);
						CombatWrapper combat = new CombatWrapper(rc.getGameObject());
						int boxA = combat.getCombatBoxAttack();
						int boxD = combat.getCombatBoxDefense();
						layoutHash.put(getBoxIndexFromCombatBoxes(boxA,boxD),rc);
						sheetParticipants.add(rc);
						if (rc.isMonster()) {
							MonsterChitComponent monster = (MonsterChitComponent)rc;
							RealmComponent weapon = monster.getWeapon();
							if (weapon!=null) {
								updateBattleChitsWithRolls(new CombatWrapper(weapon.getGameObject()));
								combat = new CombatWrapper(weapon.getGameObject());
								boxA = combat.getCombatBoxAttack();
								boxD = combat.getCombatBoxDefense();
								if (boxA>0) {
									// only add monster weapon to layout if in a combat box!
									layoutHash.put(getBoxIndexFromCombatBoxes(boxA,boxD),weapon);
								}
							}
							RealmComponent horse = (RealmComponent)rc.getHorse();
							if (horse!=null) {
								combat = new CombatWrapper(horse.getGameObject());
								boxA = combat.getCombatBoxAttack();
								boxD = combat.getCombatBoxDefense();
								if (boxD>0) {
									// only add horse to layout if in a combat box!
									layoutHash.put(getBoxIndexFromCombatBoxes(boxA,boxD),horse);
								}
							}
						}
						else if (rc.isNative()) {
							RealmComponent horse = (RealmComponent)rc.getHorse();
							if (horse!=null) {
								combat = new CombatWrapper(horse.getGameObject());
								boxA = combat.getCombatBoxAttack();
								boxD = combat.getCombatBoxDefense();
								if (boxD>0) {
									// only add horse to layout if in a combat box!
									layoutHash.put(getBoxIndexFromCombatBoxes(boxA,boxD),horse);
								}
							}
						}
					}
				}
			}
			
			// If character, place move chits and/or armor (if any)
			if (rc.equals(sheetOwner) && rc.isCharacter()) {
				CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
				CharacterChitComponent characterChit = (CharacterChitComponent)rc;
				
				if (!needsSecrecy) { // don't show moves if being attacked by another character
					RealmComponent maneuverChit = characterChit.getManeuverChit(false);
					if (maneuverChit!=null) {
						CombatWrapper combat = new CombatWrapper(maneuverChit.getGameObject());
						if (combat.getPlacedAsMove()) {
							int box = combat.getCombatBoxDefense();
							if (maneuverChit.isCharacter()) { // This implies the character is transmorphed (normally, a character move chit is a chit)
								maneuverChit = characterChit.getTransmorphedComponent().getMoveChit();
							}
							layoutHash.put(Integer.valueOf(POS_MOVE_BOX1+box-1),maneuverChit);
						}
					}
					
					if (!character.isTransmorphed()) {
						for (RealmComponent chit : character.getActiveFightChits()) {
							CombatWrapper combat = new CombatWrapper(chit.getGameObject());
							int box = combat.getCombatBoxDefense();
							if (box>0 && this.sheetOwner.getGameObject().getStringId().equals(combat.getSheetOwnerId())) {
								if (combat.getPlacedAsParry()) {
									layoutHash.put(Integer.valueOf(POS_MOVE_BOX1+box-1),chit);
								} else if (combat.getPlacedAsParryShield()) {
									layoutHash.put(Integer.valueOf(POS_SHIELD1+box-1),chit);
								}
							}
						}
						ArrayList<WeaponChitComponent> weapons = character.getActiveWeapons();
						if (weapons!=null) {
							for (WeaponChitComponent weapon : weapons) {
								CombatWrapper combat = new CombatWrapper(weapon.getGameObject());
								int box = combat.getCombatBoxDefense();
								if (box>0 && this.sheetOwner.getGameObject().getStringId().equals(combat.getSheetOwnerId())) {
									if (combat.getPlacedAsParry()) {
										layoutHash.put(Integer.valueOf(POS_MOVE_BOX1+box-1),weapon);
									} else if (combat.getPlacedAsParryShield()) {
										layoutHash.put(Integer.valueOf(POS_SHIELD1+box-1),weapon);
									}
								}
							}
						}
					}
				}
				
				for (GameObject go : character.getActiveInventory()) {
					ArmorType armorType = TreasureUtility.getArmorType(go);
					RealmComponent item = RealmComponent.getRealmComponent(go);
					if (armorType!=ArmorType.None && armorType!=ArmorType.Special) {
						if (armorType==ArmorType.Shield) {
							CombatWrapper combat = new CombatWrapper(item.getGameObject());
							int box = combat.getCombatBoxDefense();
							if (box==0) { // default to box 1
								box = 1;
								combat.setCombatBoxDefense(box);
							}
							sheetOwnerShield = item;
							if (needsSecrecy) {
								box = 2; // default position for secrecy
							}
							layoutHash.put(Integer.valueOf(POS_SHIELD1+box-1),item);
						}
						else if (armorType==ArmorType.Helmet) {
							layoutHash.put(Integer.valueOf(POS_HELMET),item);
						}
						else if (armorType==ArmorType.Breastplate) {
							layoutHash.put(Integer.valueOf(POS_BREASTPLATE),item);
						}
						else if (armorType==ArmorType.Armor) {
							layoutHash.put(Integer.valueOf(POS_SUITOFARMOR),item);
						}
					}
					else if (item.isTreasure() && item.getGameObject().hasThisAttribute("armor_box")) {
						/*
						 * "armor_box" describes which helmet box
						 * "vulnerability" describes how tough
						 */
						int box = item.getGameObject().getThisInt("armor_box");
						layoutHash.put(Integer.valueOf(POS_SHIELD1+box-1),item);
					}
					else if (item.isTreasure() && item.getGameObject().hasThisAttribute("armor_row")) {
						int row = item.getGameObject().getThisInt("armor_row");
						if (row==3) { // This covers the Ointment of Steel
							layoutHash.put(Integer.valueOf(POS_SUITOFARMOR),item);
						}
					}
					else if (!needsSecrecy) {
						// Anything with a combat box!
						CombatWrapper combat = new CombatWrapper(item.getGameObject());
						if (combat.getPlacedAsMove()) {
							int box = combat.getCombatBoxDefense();
							if (box>0) {
								layoutHash.put(Integer.valueOf(POS_MOVE_BOX1+box-1),item);
							}
						}
					}
				}
			}
		}
		
		placeAllAttacks(POS_ATTACK_BOX1,POS_ATTACK_WEAPON1,exclude);
		
		if (sheetOwner.isCharacter()) {
			CombatWrapper combat = new CombatWrapper(sheetOwner.getGameObject());
			
			// Add all charge chits to attackers
			Collection<GameObject> chargeChits = combat.getChargeChits();
			for (GameObject i : chargeChits) {
				RealmComponent rc = RealmComponent.getRealmComponent(i);
				layoutHash.put(Integer.valueOf(POS_CHARGECHITS),rc);
			}
			
			// Add all used chits to used box
			Collection<GameObject> usedChits = combat.getUsedChits();
			for (GameObject i : usedChits) {
				RealmComponent rc = RealmComponent.getRealmComponent(i);
				if (!rc.isMonster() && !rc.isNative()) {
					layoutHash.put(Integer.valueOf(POS_USEDCHITS),rc);
				}
			}
		}
		updateHotSpots();
		
		if (combatFrame.getRollerResults()!=null) {
			updateRollerResults();
		}
	}
	protected void handleClick(int index,int swingConstant) {
		if (hotspotHash.get(Integer.valueOf(index))==null) {
			// Don't handle clicks unless there is a hotspot
			return;
		}
		GameObject chararacterGo = combatFrame.getActiveParticipant().getGameObject();
		CharacterWrapper activeCharacter = new CharacterWrapper(chararacterGo);
		
		switch(index) {
			case POS_OWNER:
				if (combatFrame.getActionState()==Constants.COMBAT_ASSIGN) {
					ArrayList<RealmComponent> list = new ArrayList<>();
					list.add(sheetOwner);
					combatFrame.assignTarget(list);
				}
				break;
			case POS_TARGET:
				if (combatFrame.getActionState()==Constants.COMBAT_LURE) {
					// Luring
					combatFrame.lureDenizens(sheetOwner,0,0,true);
				}
				else if (combatFrame.getActionState()==Constants.COMBAT_ASSIGN) {
					// Assign Targets
					combatFrame.assignTarget();
				}
				else if (combatFrame.getActionState()==Constants.COMBAT_POSITIONING) {
					// Auto-position targets or reset
					if (layoutHash.get(Integer.valueOf(POS_TARGET_BOX1))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_BOX2))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_BOX3))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_CHARGE_SMASH))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_CHARGE_SWING))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_DODGE_SMASH))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_DODGE_THRUST))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_DUCK_SWING))!=null
							|| layoutHash.get(Integer.valueOf(POS_TARGET_DUCK_THRUST))!=null) {
						// reset ALL when targets have already been placed, and the target hotspot is clicked
						ArrayList<RealmComponent> toReset = new ArrayList<>();
						for (int i=0;i<3;i++) {
							ArrayList<RealmComponent> list = layoutHash.getList(Integer.valueOf(POS_TARGET_BOX1+i));
							if (list!=null) {
								toReset.addAll(list);
							}
						}
						
						ArrayList<RealmComponent> listToAdd = new ArrayList<>();
						listToAdd=layoutHash.getList(Integer.valueOf(POS_TARGET_CHARGE_SMASH));
						if (listToAdd!=null) {
							toReset.addAll(listToAdd);
						}
						listToAdd=layoutHash.getList(Integer.valueOf(POS_TARGET_CHARGE_SWING));
						if (listToAdd!=null) {
							toReset.addAll(listToAdd);
						}
						listToAdd=layoutHash.getList(Integer.valueOf(POS_TARGET_DODGE_SMASH));
						if (listToAdd!=null) {
							toReset.addAll(listToAdd);
						}
						listToAdd=layoutHash.getList(Integer.valueOf(POS_TARGET_DODGE_THRUST));
						if (listToAdd!=null) {
							toReset.addAll(listToAdd);
						}
						listToAdd=layoutHash.getList(Integer.valueOf(POS_TARGET_DUCK_SWING));
						if (listToAdd!=null) {
							toReset.addAll(listToAdd);
						}
						listToAdd=layoutHash.getList(Integer.valueOf(POS_TARGET_DUCK_THRUST));
						if (listToAdd!=null) {
							toReset.addAll(listToAdd);
						}
						
						for (RealmComponent rc : toReset) {
							CombatWrapper combat = new CombatWrapper(rc.getGameObject());
							combat.setCombatBoxAttack(0);
							combat.setCombatBoxDefense(0);
						}
						updateLayout();
					}
					else {
						// auto-position
						ArrayList<RealmComponent> list = new ArrayList<>(layoutHash.getList(Integer.valueOf(POS_TARGET)));
						Collections.sort(list);
						int n=0;
						int m=0;
						if (hostPrefs.hasPref(Constants.SR_COMBAT)) {
							n = RandomNumber.getRandom(3);
							m = RandomNumber.getRandom(3);
						}
						while(list.size()>0) {
							RealmComponent rc = list.remove(0); // pop
							if (rc.isMonster()) {
								MonsterChitComponent monster = (MonsterChitComponent)rc;
								RealmComponent weapon = monster.getWeapon();
								if (weapon!=null) {
									list.add(0,weapon); // push
								}
								RealmComponent horse = (RealmComponent)rc.getHorse();
								if (horse!=null) {
									if (swingConstant==SwingConstants.LEFT) {
										CombatWrapper combat = new CombatWrapper(horse.getGameObject());
										combat.setCombatBoxDefense(n+1);
									}
									else {
										list.add(0,horse); // push
									}
								}
							}
							else if (rc.isNative()) {
								RealmComponent horse = (RealmComponent)rc.getHorse();
								if (horse!=null) {
									if (swingConstant==SwingConstants.LEFT) {
										CombatWrapper combat = new CombatWrapper(horse.getGameObject());
										combat.setCombatBoxDefense(n+1);
									}
									else {
										list.add(0,horse); // push
									}
								}
							}
							CombatWrapper combat = new CombatWrapper(rc.getGameObject());
							combat.setCombatBoxDefense(n+1);
							combat.setCombatBoxAttack(m+1);
							if (hostPrefs.hasPref(Constants.SR_COMBAT)) {
								n = RandomNumber.getRandom(3);
								m = RandomNumber.getRandom(3);
							} else {
								n = (n+1)%3;
								m = (m+1)%3;
							}
						}
						updateLayout();
					}
					combatFrame.updateControls();
				}
				break;
			case POS_TARGET_BOX1:
			case POS_TARGET_BOX2:
			case POS_TARGET_BOX3:
				// Position targets
				ArrayList<RealmComponent> list = layoutHash.getList(Integer.valueOf(POS_TARGET));
				int box = index-POS_TARGET_BOX1+1;
				combatFrame.positionTarget(box,box,list,false,swingConstant==SwingConstants.LEFT);
				break;
			case POS_TARGET_CHARGE_SMASH:
			case POS_TARGET_CHARGE_SWING:
			case POS_TARGET_DODGE_SMASH:
			case POS_TARGET_DODGE_THRUST:
			case POS_TARGET_DUCK_SWING:
			case POS_TARGET_DUCK_THRUST:
				ArrayList<RealmComponent> listTargets = layoutHash.getList(Integer.valueOf(POS_TARGET));
				switch(index) {
				case POS_TARGET_CHARGE_SMASH:
					combatFrame.positionTarget(3,1,listTargets,false,swingConstant==SwingConstants.LEFT);
					break;
				case POS_TARGET_CHARGE_SWING:
					combatFrame.positionTarget(2,1,listTargets,false,swingConstant==SwingConstants.LEFT);
					break;
				case POS_TARGET_DODGE_SMASH:
					combatFrame.positionTarget(3,2,listTargets,false,swingConstant==SwingConstants.LEFT);
					break;
				case POS_TARGET_DODGE_THRUST:
					combatFrame.positionTarget(1,2,listTargets,false,swingConstant==SwingConstants.LEFT);
					break;
				case POS_TARGET_DUCK_SWING:
					combatFrame.positionTarget(2,3,listTargets,false,swingConstant==SwingConstants.LEFT);
					break;
				case POS_TARGET_DUCK_THRUST:
					combatFrame.positionTarget(1,3,listTargets,false,swingConstant==SwingConstants.LEFT);
					break;
				}
				break;
			case POS_ATTACK_BOX1:
			case POS_ATTACK_BOX2:
			case POS_ATTACK_BOX3:
				if (combatFrame.getActionState()==Constants.COMBAT_TACTICS) {
					// Move attack
					if (hostPrefs.hasPref(Constants.SR_ADV_SURVIVAL_TACTICS)){
						combatFrame.replaceAttackOrParry(index-POS_ATTACK_BOX1+1,sheetOwner);
					} else {
						combatFrame.replaceAttack(index-POS_ATTACK_BOX1+1);
					}
				}
				else {
					combatFrame.positionAttacker(getAllBoxListFromLayout(POS_ATTACK_BOX1),index-POS_ATTACK_BOX1+1,index-POS_ATTACK_BOX1+1,false,swingConstant==SwingConstants.LEFT);
					updateLayout();
					repaint();
				}
				break;
			case POS_ATTACK_WEAPON1:
			case POS_ATTACK_WEAPON2:
			case POS_ATTACK_WEAPON3:
				if (combatFrame.getActionState()==Constants.COMBAT_POSITIONING) {
					// Play attack
					combatFrame.playAttack(index-POS_ATTACK_WEAPON1+1,this.getSheetOwner());
				}
				break;
			case POS_MOVE_BOX1:
			case POS_MOVE_BOX2:
			case POS_MOVE_BOX3:
				if (combatFrame.getActionState()==Constants.COMBAT_TACTICS) {
					// Move maneuver
					combatFrame.replaceManeuver(index-POS_MOVE_BOX1+1);
				}
				else {
					if (this.getSheetOwner() == RealmComponent.getRealmComponent(chararacterGo) && !activeCharacter.isTransmorphed() && (hostPrefs.hasPref(Constants.OPT_PARRY) || activeCharacter.affectedByKey(Constants.PARRY))){
						combatFrame.playManeuverOrParry(index-POS_MOVE_BOX1+1);
					} else {
						// Play maneuver
						combatFrame.playManeuver(index-POS_MOVE_BOX1+1);
					}
				}
				break;
			case POS_SHIELD1:
			case POS_SHIELD2:
			case POS_SHIELD3:
				if (combatFrame.getActionState()!=Constants.COMBAT_TACTICS) {
					if (this.getSheetOwner() == RealmComponent.getRealmComponent(chararacterGo) && !activeCharacter.isTransmorphed() && (hostPrefs.hasPref(Constants.OPT_PARRY_LIKE_SHIELD) || activeCharacter.affectedByKey(Constants.PARRY_LIKE_SHIELD) || activeCharacter.affectedByKey(Constants.BLOCK_NO_WEAPON))){
						combatFrame.playParryLikeShield(index-POS_SHIELD1+1);
					}
					else {
						// Position shield
						if (sheetOwnerShield!=null) {
							CombatWrapper combat = new CombatWrapper(sheetOwnerShield.getGameObject());
							combat.setCombatBoxDefense(index-POS_SHIELD1+1);
							combatFrame.updateSelection();
						}
					}
				}
				break;
		}
		updateHotSpots();
		repaint();
	}
	public boolean hasUnpositionedDenizens() {
		return layoutHash.get(Integer.valueOf(POS_TARGET))!=null;
	}
	public boolean usesMaxCombatBoxes() {
		return usesMaxCombatBoxes(POS_TARGET_BOX1);
	}
	public boolean usesCombatBoxesEqually() {
		return usesCombatBoxesEqually(POS_TARGET_BOX1);
	}
	public boolean needsTargetAssignment() {
		return false;
	}
	protected void drawRollers(Graphics g) {
		if (redGroup!=null) drawRollerGroup(g,redGroup,POS_TARGET,POS_TARGET_BOX1);
		if (circleGroup!=null) drawRollerGroup(g,circleGroup,POS_ATTACK,POS_ATTACK_BOX1);
	}
	private static Rectangle FORT_RECT = new Rectangle(0,330,400,35);  // Fort Rect?  Ewww.
	protected void drawOther(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		if (sheetOwner!=null && sheetOwner.isCharacter()) {
			spellRegions.clear();
			spellRegionHash.clear();
			CharacterWrapper character = new CharacterWrapper(sheetOwner.getGameObject());
			
			if (character.isFortified() || character.isFortDamaged()) {
				g.setColor(Color.blue);
				g.fill(FORT_RECT);
				g.setColor(Color.white);
				g.setFont(Constants.FORTRESS_FONT);
				String fort = character.getVulnerability().getChar() + (character.isFortDamaged()?" (Damaged)":"");
				GraphicsUtil.drawCenteredString(g,FORT_RECT,fort);
				ImageIcon f = ImageCache.getIcon("actions/fortify");
				g.drawImage(f.getImage(),5,333,null);
				g.drawImage(f.getImage(),400-5-f.getIconWidth(),333,null);
				
				if (!character.isFortified()) {
					// Destroyed fortification
					g.setColor(Color.red);
					Stroke old = g.getStroke();
					g.setStroke(Constants.THICK_STROKE);
					g.drawLine(FORT_RECT.x,FORT_RECT.y,FORT_RECT.x+FORT_RECT.width,FORT_RECT.y+FORT_RECT.height);
					g.drawLine(FORT_RECT.x+FORT_RECT.width,FORT_RECT.y,FORT_RECT.x,FORT_RECT.y+FORT_RECT.height);
					g.setStroke(old);
				}
			}
			
			TextType tt = new TextType(sheetOwner.getGameObject().getName(),300,"STAT_BLACK");
			tt.draw(g, 333, 710, Alignment.Center);
			tt = new TextType("Vulnerability = "+character.getVulnerability().toString(),300,"STAT_BLACK");
			tt.draw(g, 333, 730, Alignment.Center);
			
			// If transformed, show monster values
			if (character.isTransmorphed()) {
				GameObject go = character.getTransmorph();
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isMonster()) {
					MonsterChitComponent monster = (MonsterChitComponent)rc;
					Point p = CHARACTER_SHEET[POS_OWNER];
					g.drawImage(monster.getFightChit().getImage(),p.x-60,p.y-110,null);
					g.drawImage(monster.getMoveChit().getImage(),p.x+10,p.y-110,null);
					MonsterPartChitComponent weapon = monster.getWeapon();
					if (weapon!=null) {
						g.drawImage(weapon.getFlipSideImage(),p.x+40,p.y-30,null);
						g.drawImage(weapon.getImage(),p.x+40,p.y-50,null);
					}
				}
			}
			
			boolean isMe = GameClient.GetMostRecentClient()==null || character.getPlayerName().equals(GameClient.GetMostRecentClient().getClientName());
			if (isMe && !alwaysSecret) {
				ArrayList<SpellSet> spellSets = character.getCastableSpellSets();
				if (!spellSets.isEmpty()) {
					ArrayList<SpellCardComponent> spells = new ArrayList<>();
					for (SpellSet ss:spellSets) {
						SpellCardComponent spell = (SpellCardComponent)RealmComponent.getRealmComponent(ss.getSpell());
						if (!spells.contains(spell)) {
							spells.add(spell);
						}
					}
					int maxWidth = 200;
					int cardWidth = CardComponent.getMediumCardImageWidth();
					int totalWidth = (cardWidth*spells.size())+(5*(spells.size()-1));
					if (totalWidth>maxWidth) {
						totalWidth = maxWidth;
						cardWidth = ((maxWidth+5)/(spells.size()+1))-5;
					}
					int center = CHARACTER_SHEET[POS_OWNER].x;
					int x = center-(totalWidth>>1);
					int y = 550;
					for (SpellCardComponent spell:spells) {
						g.drawImage(spell.getMediumImage(),x,y,null);
						Rectangle r = new Rectangle(x,y,CardComponent.getMediumCardImageWidth(),CardComponent.getMediumCardImageHeight());
						spellRegions.add(0,r); // push the result, so the list is in reverse order
						spellRegionHash.put(r,spell);
						x += (cardWidth+5);
					}
					tt = new TextType("Castable Spells",300,"BOLD_BLUE");
					tt.draw(g, 333, 530, Alignment.Center);
				}
			}
			
			if (spellPoint!=null && spellCard!=null) {
				g.drawImage(spellCard.getImage(),spellPoint.x,spellPoint.y,null);
			}
			if (tallyPoint!=null && tallyView!=null) {
				tallyView.draw(g,tallyPoint);
			}
		}
	}
	private Point spellPoint = null;
	private SpellCardComponent spellCard;
	
	private Point tallyPoint = null;
	private CombatTallyView tallyView = null;
	public void updateMouseHover(Point p,boolean isShiftDown) {
		super.updateMouseHover(p,isShiftDown);
		
		Point oldPoint = spellPoint;
		spellPoint = null;
		if (p!=null) {
			for (Rectangle r:spellRegions) {
				if (r.contains(p)) {
					spellPoint = new Point(p.x-CardComponent.CARD_WIDTH,p.y-CardComponent.CARD_HEIGHT);
					spellCard = spellRegionHash.get(r);
					repaint();
					break;
				}
			}
		}
		
		if (spellPoint==null?oldPoint!=null:oldPoint==null) {
			repaint();
		}
		oldPoint = tallyPoint;
		tallyPoint = null;
		if (p!=null) {
			if (mouseHoverIndex!=null && mouseHoverIndex==POS_OWNER) {
				tallyView = new CombatTallyView(getSheetOwner());
				if (tallyView.isValid()) {
					tallyPoint = new Point(p.x - tallyView.getWidth(),p.y - tallyView.getHeight());
					repaint();
				}
				else {
					tallyView = null;
				}
			}
		}
		if (tallyPoint==null?oldPoint!=null:oldPoint==null) {
			repaint();
		}
	}
	/**
	 * Testing only
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		CharacterCombatSheet sheet = new CharacterCombatSheet();
		sheet.redGroup = new CombatSheet.RollerGroup();
		sheet.redGroup.repositionRoller = CombatSheet.makeRoller("2:4");
		sheet.redGroup.changeTacticsRoller1 = CombatSheet.makeRoller("2:4");
		sheet.redGroup.changeTacticsRoller2 = CombatSheet.makeRoller("2:4");
		sheet.redGroup.changeTacticsRoller3 = CombatSheet.makeRoller("2:4");
		sheet.circleGroup = new CombatSheet.RollerGroup();
		sheet.circleGroup.repositionRoller = CombatSheet.makeRoller("2:4");
		sheet.circleGroup.changeTacticsRoller1 = CombatSheet.makeRoller("2:4");
		sheet.circleGroup.changeTacticsRoller2 = CombatSheet.makeRoller("2:4");
		sheet.circleGroup.changeTacticsRoller3 = CombatSheet.makeRoller("2:4");
		for (int i=0;i<24;i++) {
			sheet.hotspotHash.put(Integer.valueOf(i),"test");
		}
		frame.getContentPane().add(sheet,"Center");
		frame.setSize(800,600);
		frame.pack();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}
}