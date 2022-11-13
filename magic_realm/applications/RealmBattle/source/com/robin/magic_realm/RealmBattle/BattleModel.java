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

import java.util.*;
import java.util.logging.Logger;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.general.util.HashLists;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.table.Curse;
import com.robin.magic_realm.components.table.Fear;
import com.robin.magic_realm.components.table.PowerOfThePit;
import com.robin.magic_realm.components.table.RaiseDead;
import com.robin.magic_realm.components.table.WallOfForce;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.*;

public class BattleModel {
	
	public static final int NO_ATTACK = -1;
	public static final int MISS = 0;
	public static final int INTERCEPT = 1;
	public static final int UNDERCUT = 2;
	public static final int INTERCEPT_PARRY = 3;
	public static final int UNDERCUT_PARRY = 4;
	public static final int ATTACK_CANCELLED = -2;
	public static final int PARRY_CANCELLED = -3;
	public static final int CANNOT_PARRY = -4;
	public static boolean SKIP_REPOSITIONING = false;
	public static boolean FORCE_MONSTER_FLIP = false;
	
	private static Logger logger = Logger.getLogger(BattleModel.class.getName());
	
	private GameData gameData;
	private HostPrefWrapper hostPrefs;
	private GameWrapper theGame;
	private TileLocation battleLocation;

	private BattleGroup denizenBattleGroup; // only one allowed
	private ArrayList<BattleGroup> characterBattleGroups;

	// The killedTallyHash is a hash of dead:killers - used to determine how many ways the points are divided
	private HashLists<GameObject,GameObject> killedTallyHash;
	
	// The killTallyHash is a hash of attacker:kills - used to add points in the proper order after battle resolution
	private HashLists<GameObject,GameObject> killTallyHash;
	
	// The killerOrder is the order that killers killed stuff, so that characters that kill characters get credit for notoriety earned in the same turn.
	private ArrayList<GameObject> killerOrder;
	
	private int totalHits;
	private boolean spellCasting;
	
	public BattleModel(GameData data,TileLocation battleLocation) {
		this.gameData = data;
		this.battleLocation = battleLocation;
		denizenBattleGroup = null;
		characterBattleGroups = new ArrayList<>();
		hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
		theGame = GameWrapper.findGame(gameData);
	}
	public GameData getGameData() {
		return gameData;
	}
	
	public int getBattleRound(int actionState) {
		TileLocation current = getBattleLocation();
		CombatWrapper combat = new CombatWrapper(current.tile.getGameObject());
		int round = combat.getHitResultCount();
		if (actionState<Constants.COMBAT_RESOLVING) {
			round++;
		}
		return round;
	}
	
	private static void logBattleInfo(String info) {
		CombatFrame.broadcastMessage(RealmLogging.BATTLE,info);
	}
	
	public boolean arePinningMonsters() {
		for (RealmComponent rc : getAllBattleParticipants(true)) {
			if (rc.isMonster()) {
				MonsterChitComponent monster = (MonsterChitComponent)rc;
				if (monster.isPinningOpponent()) {
					return true;
				}
			}
			else if (rc.isCharacter()) {
				CharacterChitComponent character = (CharacterChitComponent)rc;
				MonsterChitComponent monster = character.getTransmorphedComponent();
				if (monster!=null && monster.isPinningOpponent()) {
					return true;
				}
			}
		}
		return false;
	}
	private boolean isPacifiedByAllCharacters(RealmComponent denizen) {
		ArrayList<CharacterChitComponent> characters = getAllParticipatingCharacters();
		boolean pacified = characters.size()>0; // must be at least one character present for ANY pacification to happen
		for (CharacterChitComponent cc:characters) {
			CharacterWrapper character = new CharacterWrapper(cc.getGameObject());
			if (!denizen.isPacifiedBy(character)) {
				pacified = false;
				break;
			}
		}
		return pacified;
	}
	/**
	 * This should return true only if there is someone they can attack (ie., unhidden!)
	 */
	public boolean denizensAreBattling() {
		if (denizenBattleGroup!=null) {
			for (RealmComponent denizen:denizenBattleGroup.getBattleParticipants()) {
				CombatWrapper combat = new CombatWrapper(denizen.getGameObject());
				
				if (denizen.isMistLike() || combat.isPeaceful() || isPacifiedByAllCharacters(denizen)) continue;
				
				for (BattleGroup group:getAllBattleGroups(false)) {
					if (!group.canBeAttackedBy(denizen)) continue;
					if (denizen.isNative()) {
						String groupName = denizen.getGameObject().getThisAttribute("native").toLowerCase();
						CharacterWrapper character = new CharacterWrapper(group.getOwningCharacter().getGameObject());
						if (character.isBattling(groupName)) {
							return true;
						}
					}
					else {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean areUnhiredNatives() {
		if (denizenBattleGroup != null) {
			for (RealmComponent denizen : denizenBattleGroup.getBattleParticipants()) {
				if (denizen.isNative()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean areUnassignedDenizens() {
		if (denizenBattleGroup != null) {
			for (RealmComponent denizen : denizenBattleGroup.getBattleParticipants()) {
				if (denizen.getTarget()==null && denizen.get2ndTarget()==null) {
					return true;
				}
			}
		}
		return false;
	}

	public void addBattleGroup(BattleGroup group) {
		if (group.isDenizen()) {
			if (denizenBattleGroup == null) {
				denizenBattleGroup = group;
			}
			else {
				throw new IllegalStateException("Cannot have more than one denizen battle group!");
			}
		}
		else {
			characterBattleGroups.add(group);
			Collections.sort(characterBattleGroups);
		}
		group.setModel(this);
	}
	public BattleGroup getBattleGroup(RealmComponent owner) {
		for (BattleGroup group:characterBattleGroups) {
			if (group.getOwningCharacter().equals(owner)) {
				return group;
			}
		}
		return null;
	}
	public BattleGroup getParticipantsBattleGroup(RealmComponent participant) {
		for (BattleGroup group : getAllBattleGroups(true)) {
			if (group.contains(participant)) {
				return group;
			}
		}
		
		// participant might be an owning character that is not in the clearing,
		// so try another approach at this point.
		return getBattleGroup(participant);
	}

	/**
	 * <li>Denizen horses to walking side
	 * <li>Determine first character for clearing
	 */
	public void doPreparation(int round) {
		if (round==1) { // only report the header on round 1
			logBattleInfo("=======================");
			logBattleInfo("Evening of month "+theGame.getMonth()+", day "+theGame.getDay()+", in clearing "+battleLocation);
		}
		logBattleInfo("-----------------------");
		logBattleInfo("--  Combat Round "+round);
		logBattleInfo("-----------------------");
		
		// Denizen horses to walking side
		if (denizenBattleGroup != null) {
			denizenBattleGroup.allHorsesWalk();
			
			// denizens reset to not having a sheet
			for (RealmComponent rc : denizenBattleGroup.getBattleParticipants()) {
				CombatWrapper combat = new CombatWrapper(rc.getGameObject());
				if (combat.isSheetOwner()) {
					if(!rc.hasTarget() || (rc.isMonster() && ((MonsterChitComponent)rc).isTremendous() && rc.getTarget().isCharacter())) {
						combat.setSheetOwner(false);
					}
				}
			}
		}
		// First character is first BattleGroup in the list (already sorted), so nothing to do here
		
		// Setup combat sheet owners
		// Make sure all non-hired participants in clearing are sheet owners (to start)
		for (RealmComponent rc : getAllBattleParticipants(false)) {
			CombatWrapper combat = new CombatWrapper(rc.getGameObject());
			if (!combat.isSheetOwner()) {
				combat.setSheetOwner(true);
			}
		}
	}

	/**
	 * Assign remaining denizens (denizenBattleGroup) to characters
	 */
	public void doRandomAssignment() {
		if (denizenBattleGroup != null) {
			for (RealmComponent denizen:denizenBattleGroup.getBattleParticipants()) {
				CombatWrapper combat = new CombatWrapper(denizen.getGameObject());
				if (!denizen.isAssigned() && !combat.isPeaceful() && !denizen.isMistLike()) {
					ArrayList<BattleGroup> availableGroups = new ArrayList<>();
					// Find one possibility for each BattleGroup
					for (BattleGroup bg:characterBattleGroups) {
						if (bg.hasAvailableParticipant(denizen)) {
							availableGroups.add(bg);
						}
					}
					// It is possible that there are no random assignments, in which case do nothing
					if (availableGroups.size() > 0) {
						BattleGroup bg = rollOffForWorst(availableGroups);
						RealmComponent assignment = bg.getAvailableParticipant(denizen);
						if (assignment==null) {
							// This will happen when character is hidden or absent from clearing, and
							// there is more than one possible hireling to randomly assign.
							bg.getOwningCharacter().getGameObject().addThisAttributeListItem(Constants.RANDOM_ASSIGNMENT_WINNER,denizen.getGameObject().getStringId());
						}
						else {
							denizen.setTarget(assignment);
						}
					}
				}
			}
		}
	}

	private static BattleGroup rollOffForWorst(Collection<BattleGroup> groups) {
		return rollOff(groups, false);
	}

	private static BattleGroup rollOff(Collection<BattleGroup> groups, boolean best) {
		if (groups.size() > 0) {
			while (groups.size() > 1) { // As long as there are ties, the rolloff continues. Only one "winner" allowed.
				int markRoll = best ? 99 : -99;
				ArrayList<BattleGroup> markedRollers = new ArrayList<>();
				for (BattleGroup bg : groups) {
					DieRoller roller = bg.createDieRoller("Roll Off");
					int roll = roller.getHighDieResult();
					if (roll == markRoll) {
						// Tied
						markedRollers.add(bg);
					}
					else if ((!best && roll > markRoll) || (best && roll < markRoll)) {
						// New best/worst roll
						markedRollers.clear();
						markedRollers.add(bg);
						markRoll = roll;
					}
				}
				groups = markedRollers;
			}
			return groups.iterator().next();
		}
		return null;
	}

	public BattleGroup getDenizenBattleGroup() {
		return denizenBattleGroup;
	}
	
	public boolean areDenizens() {
		return denizenBattleGroup!=null;
	}

	public ArrayList<RealmComponent> getAllBattleParticipants(boolean includeDenizens) {
		return getParticipantsFromGroups(getAllBattleGroups(includeDenizens));
	}
	public ArrayList<RealmComponent> getAllOtherBattleParticipants(BattleGroup bg,boolean includeDenizens,boolean allowTreachery) {
		ArrayList<BattleGroup> list = findOtherBattleGroups(bg,includeDenizens);
		if (allowTreachery) {
			list.add(bg);
		}
		return getParticipantsFromGroups(list);
	}
	public int getGroupCount(boolean includeDenizens) {
		int count = characterBattleGroups.size();
		if (includeDenizens && denizenBattleGroup!=null) {
			count++;
		}
		return count;
	}
	/**
	 * @return			All characters involved in battle (includes characters that are absent, but have hirelings)
	 */
	public ArrayList<RealmComponent> getAllOwningCharacters() {
		ArrayList<RealmComponent> ret = new ArrayList<>();
		for (BattleGroup group:characterBattleGroups) {
			RealmComponent rc = group.getOwningCharacter();
			if (rc!=null) {
				ret.add(rc);
			}
		}
		return ret;
	}
	
	/**
	 * Returns a list of all native leaders, and controlled monsters in the clearing (not characters).
	 */
	public ArrayList<RealmComponent> getAllLeaders() {
		ArrayList<RealmComponent> ret = new ArrayList<>();
		ArrayList<RealmComponent> list  = getAllBattleParticipants(false);
		for (RealmComponent rc : list) {
			if (rc.isPlayerControlledLeader() && !rc.isCharacter()) {
				ret.add(rc);
			}
		}
		
		return ret;
	}
	
	/**
	 * @return			All characters in the battle (not owningCharacters!)
	 */
	public ArrayList<CharacterChitComponent> getAllParticipatingCharacters() {
		ArrayList<CharacterChitComponent> ret = new ArrayList<>();
		for (BattleGroup group:characterBattleGroups) {
			CharacterChitComponent rc = group.getCharacterInBattle();
			if (rc!=null) {
				ret.add(rc);
			}
		}
		return ret;
	}

	/**
	 * @return			All characters in the battle (not owningCharacters!)
	 */
	public ArrayList<RealmComponent> getAllParticipatingCharactersAsRc() {
		ArrayList<RealmComponent> ret = new ArrayList<>();
		for (BattleGroup group:characterBattleGroups) {
			RealmComponent rc = group.getCharacterInBattle();
			if (rc!=null) {
				ret.add(rc);
			}
		}
		return ret;
	}
	
	/**
	 * Returns a Collection of all the battle groups in this model.
	 */
	public ArrayList<BattleGroup> getAllBattleGroups(boolean includeDenizens) {
		ArrayList<BattleGroup> all = new ArrayList<>();
		all.addAll(characterBattleGroups);
		if (includeDenizens && denizenBattleGroup != null) {
			all.add(denizenBattleGroup);
		}
		return all;
	}

	/**
	 * Returns a collection of all battle groups that are not the specified battle group\ (denizens and characters are grouped together)
	 */
	public ArrayList<BattleGroup> findOtherBattleGroups(BattleGroup bg, boolean includeDenizens) {
		ArrayList<BattleGroup> otherBattleGroups = getAllBattleGroups(includeDenizens);
		otherBattleGroups.remove(bg);
		return otherBattleGroups;
	}

	public void doMeleeSetup() {
		if (denizenBattleGroup != null) {
			// Denizen's horses turn gallop side up
			denizenBattleGroup.allHorsesGallop();
			
			// Denizens select their target
			for (RealmComponent denizen : denizenBattleGroup.getBattleParticipants()) {
				if (!denizen.isMonster() || !((MonsterChitComponent)denizen).isPinningOpponent()) {
					ArrayList<RealmComponent> attackers = getAttackersFor(denizen);
					if (!attackers.isEmpty()) {
						Collections.sort(attackers,new TargetIndexComparator());
						// Target the last one, unless a character
						int n = attackers.size()-1;
						RealmComponent target = null;
						while(n>=0 && (target = attackers.get(n)).isCharacter()) {
							target = null;
							n--;
						}
						if (target!=null) {
							denizen.setTarget(target);
						}
					}
				}
			}
		}

		// Hireling horses turn over
		for (BattleGroup bg:characterBattleGroups) {
			bg.allHorsesFlip();
		}
		
		// Assign targets for hirelings where obvious
		ArrayList<RealmComponent> owners = new ArrayList<>();
		for (BattleGroup bg:characterBattleGroups) {
			owners.add(bg.getOwningCharacter());
			for (RealmComponent hireling:bg.getHirelings()) {
				if (hireling.getTarget()==null && hireling.get2ndTarget()==null) {
					ArrayList<RealmComponent> attackers = getAttackersFor(hireling,true,false);
					if (attackers.size()==1) {
						// its obvious, so do it here
						RealmComponent soleAttacker = attackers.iterator().next();
						hireling.setTarget(soleAttacker);
						CombatFrame.makeTarget(null,null,hireling,soleAttacker);
					}
				}
			}
		}
		
		// Assign the order for target selection (randomized every round)
		RealmLogging.logMessage(RealmLogging.BATTLE,"Assigning random target selection order.");
		ArrayList<RealmComponent> randomOrder = new ArrayList<>();
		while(!owners.isEmpty()) {
			int r = RandomNumber.getRandom(owners.size());
			randomOrder.add(owners.remove(r));
		}
		int n=1;
		for(RealmComponent rc:randomOrder) {
			CharacterWrapper owner = new CharacterWrapper(rc.getGameObject());
			owner.setMeleePlayOrder(n++);
		}
	}
	
	public void doRaiseDeads() {
		for (CharacterChitComponent rc : getAllParticipatingCharacters()) {
			CombatWrapper character = new CombatWrapper(rc.getGameObject());
			if (character.getRaiseTheDead()) {
				CharacterWrapper characterCasting = new CharacterWrapper(rc.getGameObject());
				RaiseDead raiseDead = new RaiseDead(CombatFrame.getSingleton());
				DieRoller deadRoller = DieRollBuilder.getDieRollBuilder(CombatFrame.getSingleton(),characterCasting).createRoller(raiseDead);
				String msg = raiseDead.apply(characterCasting,deadRoller);
				character.setRaisedDead();
				logBattleInfo(rc+": "+msg);
			}
		}
	}
	
	public void doEnergizeSpells() {
		// Find and hash all spells and casters cast this round by speed
		HashLists<Integer,SpellWrapper> spells = new HashLists<>();
		HashLists<Integer,MonsterChitComponent> monsterSpells = new HashLists<>();
		HashLists<Integer,CharacterChitComponent> casters = new HashLists<>();
		ArrayList<RealmComponent> spellCasters = new ArrayList<>();
		for (CharacterChitComponent rc : getAllParticipatingCharacters()) {
			CombatWrapper character = new CombatWrapper(rc.getGameObject());
			GameObject go = character.getCastSpell();
			if (go!=null) {
				if (!spellCasters.contains(rc)) {
					spellCasters.add(rc);
				}
				SpellWrapper spell = new SpellWrapper(go);
				if (spell.isAlive()) { // A spell might not be alive if it was already energized this round
					spells.put(Integer.valueOf(spell.getAttackSpeed().getNum()),spell);
					casters.put(Integer.valueOf(spell.getAttackSpeed().getNum()),rc);
				}
			}
		}
		if (hostPrefs.hasPref(Constants.OPT_POWER_OF_THE_PIT_DEMON)) {
			for (RealmComponent battleParticipant : getAllBattleParticipants(true)) {
				if (battleParticipant.isMonster() && !battleParticipant.isMonsterPart()) {
					MonsterChitComponent monster = (MonsterChitComponent)battleParticipant;
					if ("V".equals(monster.getMagicType())) {
						spellCasters.add(monster);
						monsterSpells.put(Integer.valueOf(monster.getAttackSpeed().getNum()),monster);
					}
				}
			}
		}
		
		if (spells.size()>0) {
			ArrayList<Integer> allSpeeds = new ArrayList<>(spells.keySet());
			allSpeeds.addAll(monsterSpells.keySet());
			Collections.sort(allSpeeds);
			
			// Determine which spells cancel which spellcasters
			for (Integer speed : allSpeeds) {
				ArrayList<SpellWrapper> spellsAtSpeed = spells.getList(speed);
				ArrayList<CharacterChitComponent> unaffectedCasters = casters.getList(speed);
				
				if (spellsAtSpeed != null) {
					for (SpellWrapper spell : spellsAtSpeed) {
						if (spell.isAlive() && !spell.targetsClearing()) { // might have already been cancelled!
							ArrayList<RealmComponent> targets = spell.getTargets();
							targets.retainAll(spellCasters);
							targets.removeAll(unaffectedCasters);
							
							if (targets.size()>0) {
								for (RealmComponent target : targets) {
									CombatWrapper combat = new CombatWrapper(target.getGameObject());
									if (hostPrefs.hasPref(Constants.OPT_POWER_OF_THE_PIT_DEMON)) {
										if (target.isMonster() && !target.isMonsterPart()) {
											MonsterChitComponent monster = (MonsterChitComponent)target;
											if ("V".equals(monster.getMagicType())) {
												if (monster.getAttackSpeed().getNum() <= speed) continue;
												combat.setCancelSpell();
												String message = "Attack spell, cast by the "
														+monster.getGameObject().getNameWithNumber()
														+" (speed "+monster.getAttackSpeed().getNum()+")"
														+",\n   was cancelled by "
														+spell.getGameObject().getName()
														+" (speed "+spell.getAttackSpeed().getNum()+")"
														+", cast by the "
														+spell.getCaster().getGameObject().getName()+".";
												logBattleInfo(message);
												continue;
											}
										}
									}
									
									GameObject spellToCancelGo = combat.getCastSpell();
									SpellWrapper spellToCancel = new SpellWrapper(spellToCancelGo);
									
									//attackSpeed cannot be fetched anymore, if spell already expired (e.g. dissolve spell didn't have a target)
									if (spellToCancel.getIncantationObject() == null) continue;
									if (spellToCancel.getAttackSpeed().getNum() <= speed) continue;
																	
									String targetingClearing = "";
									if (spellToCancel.targetsClearing()) {
										targetingClearing = ", targeting the clearing";
									}
									
									String message = spellToCancelGo.getName()
											+", cast by the "
											+target.getGameObject().getNameWithNumber()
											+targetingClearing
											+" (speed "+spellToCancel.getAttackSpeed().getNum()+")"
											+",\n   was cancelled by "
											+spell.getGameObject().getName()
											+" (speed "+spell.getAttackSpeed().getNum()+")"
											+", cast by the "
											+spell.getCaster().getGameObject().getName()+".";
									logBattleInfo(message);
									spellToCancel.cancelSpell();
								}
							}
						}
					}
				}
				
				if (hostPrefs.hasPref(Constants.OPT_POWER_OF_THE_PIT_DEMON)) {
					ArrayList<MonsterChitComponent> monstersAtSpeed = monsterSpells.getList(speed);
					if (monstersAtSpeed!=null) {
						for (MonsterChitComponent monster : monstersAtSpeed) {
							RealmComponent target = monster.getTarget();
							if (target == null || (unaffectedCasters != null && unaffectedCasters.contains(target))) continue;
							CombatWrapper combat = new CombatWrapper(target.getGameObject());
							GameObject spellToCancelGo = combat.getCastSpell();
							if (spellToCancelGo == null) continue;
							SpellWrapper spellToCancel = new SpellWrapper(spellToCancelGo);	
							//attackSpeed cannot be fetched anymore, if spell already expired (e.g. dissolve spell didn't have a target)
							if (spellToCancel.getIncantationObject() == null) continue;
							if (spellToCancel.getAttackSpeed().getNum() <= speed) continue;
							String message = spellToCancelGo.getName()
									+", cast by the "
									+target.getGameObject().getNameWithNumber()
									+" (speed "+spellToCancel.getAttackSpeed().getNum()+")"
									+",\n   was cancelled by "
									+monster.getGameObject().getName()+".";
							logBattleInfo(message);
							spellToCancel.cancelSpell();
						}
					}
				}
				
				// check for duplicate conflicting (transmorph) spells at same target
				HashMap<RealmComponent,ArrayList<SpellWrapper>> conflictingSpells = new HashMap<>();
				HashMap<RealmComponent,Integer> conflictingSpellsStrength = new HashMap<>();
				HashMap<RealmComponent,SpellWrapper> strongestConflictingSpells = new HashMap<>();
				if (spellsAtSpeed != null) {
					for (SpellWrapper spell : spellsAtSpeed) {
						if (!spell.isActive()) continue; // might have already been cancelled!
						ArrayList<RealmComponent> targets = spell.getTargets();
						for (RealmComponent target : targets) {
							if (spell.canConflict()) {
								ArrayList<SpellWrapper> targetedConflictingSpells = new ArrayList<>();
								targetedConflictingSpells.add(spell);
								int strongestSpellStrength = 0;
								if (conflictingSpells.containsKey(target)) {
									targetedConflictingSpells.addAll(conflictingSpells.get(target));
									strongestSpellStrength = conflictingSpellsStrength.get(target);
								}
								conflictingSpells.put(target, targetedConflictingSpells);
								if (spell.getConflictStrength() > strongestSpellStrength) {
									conflictingSpellsStrength.put(target, spell.getConflictStrength());
									strongestConflictingSpells.put(target, spell); 
								}
							}
						}
					}
				}
				for (RealmComponent target : conflictingSpells.keySet()) {
					ArrayList<SpellWrapper> conflictingSpellsAtTarget = conflictingSpells.get(target);
					if (conflictingSpellsAtTarget.size() <= 1) continue;
					int strongestSpellStrength = conflictingSpellsStrength.get(target);
					int numberOfStrongestSpells = 0;
					for (SpellWrapper spell : conflictingSpellsAtTarget) {
						if (spell.getConflictStrength() < strongestSpellStrength) {
							strongestConflictingSpells.get(target).addListItem(SpellWrapper.NULLIFIED_SPELLS, spell.getGameObject().getStringId());
							spell.nullifySpell(true);
							logBattleInfo(spell.getName() + " (cast by "+spell.getCaster().getName()+") was nullified as a stronger spell hit the " + target + " at the same speed of " + speed +".");
						}
						if (spell.getConflictStrength() == strongestSpellStrength) {
							numberOfStrongestSpells = numberOfStrongestSpells + 1;
						}
					}
					if (numberOfStrongestSpells >= 2) {
						for (SpellWrapper spell : conflictingSpellsAtTarget) {
							if (spell.getConflictStrength() == strongestSpellStrength) {
								if (spell.isTransform() || spell.isStoneGaze()) {
									spell.cancelSpell();
									logBattleInfo(spell.getName() + " (cast by "+spell.getCaster().getName()+") was cancelled as multiple Transform or Stone Gaze spells hit the " + target + " at the same speed of " + speed +".");
								}
								if (spell.isAbsorbEssence()) {
									spell.cancelSpell();
									logBattleInfo(spell.getName() + " (cast by "+spell.getCaster().getName()+") was cancelled as multiple Absorb Essence spells hit the " + target + " at the same speed of " + speed +".");								}
							}
						}
					}
				}
			}
					
			// Non-attack spells go into effect here
			CombatWrapper tile = new CombatWrapper(battleLocation.tile.getGameObject());
			for (Integer speed : allSpeeds) {
				ArrayList<SpellWrapper> spellsAtSpeed = spells.getList(speed);
				if (spellsAtSpeed == null) continue;
				for (SpellWrapper spell : spellsAtSpeed) {
					if (spell.isNullified() || !spell.isAlive()) continue;
					ArrayList<String> logs = new ArrayList<String>();
					if (spell.isInstantSpell()) {
						logs = spell.affectTargets(CombatFrame.getSingleton(),theGame,true,spellsAtSpeed);

					}
					else if (spell.isCombatSpell() || spell.isDaySpell() || spell.isPermanentSpell() || spell.isPhaseSpell() || spell.isMoveSpell()) {
						logs = spell.affectTargets(CombatFrame.getSingleton(),theGame,false,spellsAtSpeed);
					}
					if (logs != null && !logs.isEmpty()) {
						for (String log : logs) {
							logBattleInfo(log);
						}
					}
				}
				// Make sure PEACE didn't happen
				// FIXME The problem is, that the WISH for PEACE happens on a separate thread, so this is never called...
				if (tile.isPeaceClearing(battleLocation.clearing.getNum())) {
					// it did - no other spells go into effect, and all targets are cleared
					break;
				}
				
				if (tile.isSleepClearing(battleLocation.clearing.getNum())) {
					logBattleInfo("Clearing falls asleep. Combat ceases.");
					break;
				}
			}
		
			// Need to check battle model - if nobody is left in the clearing to fight, things should get reset
			if (!RealmBattle.testCombatInClearing(battleLocation,gameData)) {
				CombatFrame.closeCombatFrameIfNeeded();
			}
		}
	}
	
	/**
	 * This method is here to GUARANTEE that denizens being attacked by hidden attackers are moved to their own sheets.  This is
	 * supposed to happen when attacks are placed, but Watchful Natives messes this logic up.  Rather than constantly chase this
	 * problem around the code, I'm putting this here as a catch all!
	 */
	public void doFixSheetOwners() {
		BattleGroup battleGroup = getDenizenBattleGroup();
		if (battleGroup!=null) {
			for (RealmComponent rc : battleGroup.getBattleParticipants()) {
				CombatWrapper combat = new CombatWrapper(rc.getGameObject());
				if (rc.getTarget()==null && rc.get2ndTarget()==null && combat.getAttackerCount()>0 && !combat.isSheetOwner()) { // being attacked, but not already on their own sheet
					// As far as I know, there is NEVER a reason that the denizen shouldn't get their own sheet at this time
					combat.setSheetOwner(true);
					combat.setCombatBox(1);
				}
			}
		}
	}
	
	public void doRepositioningAndTactics() {
		/*
		 * Find all participants that have their own sheet, and start there.
		 * 
		 * Depending on sheet owner, there are a number of grouping possiblities
		 * that will require rolls:
		 * 
		 * Character:
		 * 		RED - Target/Attacker Group 1-n  (Red boxes)
		 * 		CIRCLE - Helping attackers
		 * 
		 * Hireling:
		 * 		SQUARE - Target = 1
		 * 		CIRCLE - Attackers = 0-n
		 * 
		 * Unhired Denizen:
		 * 		SQUARE - Target = 1
		 * 		CIRCLE - Attackers = 0-n
		 * 		RED - Self
		 */
		ArrayList<RealmComponent> all = getAllBattleParticipants(true);
		for (RealmComponent rc : all) {
			RealmComponent target = rc.getTarget();
			RealmComponent target2 = rc.get2ndTarget();
			ArrayList<RealmComponent> attackers = getAttackersFor(rc,false,true); // don't include character attackers here!
			CombatWrapper combat = new CombatWrapper(rc.getGameObject());
			if (combat.isSheetOwner()/* && (target!=null || attackers.size()>0)*/) {
				if (rc.isCharacter()) {
					// Character:
					
					// Red Box group - unhired natives/monsters targeting character directly
					if (attackers.size()>0) {
						repositionAndChangeTactics(CombatWrapper.GROUP_RED,combat,attackers);
					}
					
					// Circle group - hirelings or denizens targeting targets on character sheet
					ArrayList<RealmComponent> helpers = new ArrayList<>();
					for (RealmComponent test : all) {
						if (!test.isCharacter() && (attackers.contains(test.getTarget()) || attackers.contains(test.get2ndTarget())) ) {
							helpers.add(test);
						}
					}
					if (helpers.size()>0) {
						repositionAndChangeTactics(CombatWrapper.GROUP_CIRCLE,combat,helpers);
					}
				}
				else {
					// Setup some more lists
					ArrayList<RealmComponent> targets = new ArrayList<>(); // intentional misnomer - only ever one target
					ArrayList<RealmComponent> attackersMinusTarget = new ArrayList<>();
					if (attackers.size()>0) {
						attackersMinusTarget.addAll(attackers);
					}
					if (target!=null) {
						targets.add(target);
						attackersMinusTarget.remove(target);
						if (target.isMonster()) {
							MonsterChitComponent monster = (MonsterChitComponent)target;
							MonsterPartChitComponent weapon = monster.getWeapon();
							if (weapon!=null) {
								attackersMinusTarget.remove(weapon);
							}
						}
					}
					if (target2!=null) {
						targets.add(target2);
						attackersMinusTarget.remove(target2);
						if (target2.isMonster()) {
							MonsterChitComponent monster2 = (MonsterChitComponent)target2;
							MonsterPartChitComponent weapon2 = monster2.getWeapon();
							if (weapon2!=null) {
								attackersMinusTarget.remove(weapon2);
							}
						}
					}
					
					// Square group - this hireling/denizen's target
					repositionAndChangeTactics(CombatWrapper.GROUP_SQUARE,combat,targets);
					
					// Circle group - hirelings or denizens targeting the hireling/denizen
					repositionAndChangeTactics(CombatWrapper.GROUP_CIRCLE,combat,attackersMinusTarget);
					
					if (rc.getOwnerId()==null) {
						ArrayList<RealmComponent> self = new ArrayList<>();
						self.add(rc);
						repositionAndChangeTactics(CombatWrapper.GROUP_RED,combat,self);
					}
				}
			}
		}
	}
	
	/**
	 * @return The number of hits that occurred during this round
	 */
	public int doResolveAttacks(int round) {
		killedTallyHash = new HashLists<>();
		killTallyHash = new HashLists<>();
		killerOrder = new ArrayList<>();
		
		spellCasting = false;
		
		ArrayList<RealmComponent> all = new ArrayList<>(getAllBattleParticipants(true));
		
		// Since things might have been killed previously this round (PoP), be sure to start with them now
		populateKillLists(all);
		
		// Resolve Attacks
		/*
		 * Cycle through ALL participants, and compare targets and boxes.
		 * Sorted by speed (or length/speed if 1st round)
		 */
		
		// First, collect all the battle chits that have targets
		ArrayList<BattleChit> battleChits = collectBattleChits(all);
		
		// Include combat spells
		collectSpells(battleChits, getAllParticipatingCharacters());

		// Sort according to the round
		sortAccordingToRound(battleChits, round);
		
		// Group attackers that have same length and speed (simultaneous)
		ArrayList<String> attackBlockOrder = new ArrayList<>();
		HashLists<String,BattleChit> attackBlocks = new HashLists<>();

		handleSortTies(battleChits, attackBlockOrder, attackBlocks);
		
		// Process hits in order
		totalHits = 0;
		processHits(attackBlockOrder, attackBlocks, round);
		
		// Now, do all the appropriate scoring for characters that killed things
		scoreKills(round);
		
		// Build a battle summary here
		BattleSummaryWrapper bs = new BattleSummaryWrapper(theGame.getGameObject());
		bs.initFromBattleChits(battleChits);
		
		return totalHits;
	}

	private void populateKillLists(ArrayList<RealmComponent> allBattleChits) {
		for (RealmComponent realmComponent : allBattleChits) {
			BattleChit battleChit = (BattleChit)realmComponent;
			CombatWrapper combat = new CombatWrapper(battleChit.getGameObject());
			GameObject killedBy = combat.getKilledBy();
			if (killedBy!=null) {
				killedTallyHash.put(combat.getGameObject(),killedBy);
				killTallyHash.put(killedBy,combat.getGameObject());
				if (!killerOrder.contains(killedBy)) killerOrder.add(killedBy);
			}
		}
	}

	//collect all the battle chits that have targets, including weapons
	private static ArrayList<BattleChit>collectBattleChits(ArrayList<RealmComponent> allBattleChits) {
		ArrayList<BattleChit> battleChits = new ArrayList<>();
		for (RealmComponent realmComponent : allBattleChits) {
			BattleChit battleChit=(BattleChit)realmComponent;
			if (battleChit.getTarget()!=null) {
				// Only add battle chits that have targets
				if (battleChit.isMonster()) {
					battleChits.add(battleChit);
					MonsterChitComponent monster = (MonsterChitComponent)battleChit;
					RealmComponent weapon = monster.getWeapon();
					if (weapon!=null) {
						battleChits.add((BattleChit)weapon);
					}
				}
				else if (battleChit.isCharacter()) {
					CharacterChitComponent chit = (CharacterChitComponent)battleChit;
					MonsterChitComponent transmorph = chit.getTransmorphedComponent();
					if (transmorph!=null) {
						battleChits.add(battleChit);
						RealmComponent weapon = transmorph.getWeapon();
						if (weapon!=null) {
							battleChits.add((BattleChit)weapon);
						}
					}
					CharacterWrapper character = new CharacterWrapper(battleChit.getGameObject());
					for (RealmComponent fightChit : BattleUtility.findFightComponentsWithCombatBox(character.getFightSpeedOptions(new Speed(), true))) {
						CharacterChitComponent charChit = new CharacterChitComponent(chit.getGameObject());
						charChit.setAttackChit(fightChit);
						battleChits.add(charChit);
					}
				}
				else {
					battleChits.add(battleChit);
				}
			}
		}
		return battleChits;
	}

	private static void collectSpells(ArrayList<BattleChit>battleChits, ArrayList<CharacterChitComponent> participatingCharacters) {
		for (CharacterChitComponent characterChitComponent : participatingCharacters) {
			CharacterWrapper character = new CharacterWrapper(characterChitComponent.getGameObject());
			for (SpellWrapper spell : character.getAliveSpells()) {
				if (spell.isAttackSpell()) {
					battleChits.add(spell);
				}
			}
		}
	}
	
	private static void sortAccordingToRound(ArrayList<BattleChit>battleChits, int round) {
		RealmLogging.clearIndent();
		if (round==1) {
			// 1st round, sort by length first, then speed
			Collections.sort(battleChits,new BattleChitLengthComparator());
			logBattleInfo("Attacks are sorted by LENGTH first, then by SPEED.");
		}
		else {
			// All other rounds, sort by speed first, then length
			Collections.sort(battleChits,new BattleChitSpeedComparator());
			logBattleInfo("Attacks are sorted by SPEED first, then by LENGTH.");
		}
	}

	private static void handleSortTies(ArrayList<BattleChit>battleChits, ArrayList<String> attackBlockOrder, HashLists<String, BattleChit> attackBlocks) {
		for (BattleChit battleChit : battleChits) {
			String key = null;
			key = battleChit.getLength()+":"+battleChit.getAttackSpeed().getNum();
			attackBlockOrder.add(key);
			attackBlocks.put(key,battleChit);
		}
	}

	private void processHits(ArrayList<String> attackBlockOrder, HashLists<String,BattleChit> attackBlocks, int round) {
		int attackOrderPos = 1;	// This is incremented for each group:
		// all members of group have SAME attackOrderPos (simultaneous)
		for (String key : attackBlockOrder) {
			// All the chits in this list are simultaneous attackers
			for (BattleChit attacker : attackBlocks.getList(key)) {
				if (attacker instanceof SpellWrapper) {
					SpellWrapper spell = (SpellWrapper)attacker;
					if (spell.noTargeting()) {
						CharacterWrapper character = spell.getCaster();
						RealmComponent characterRc = RealmComponent.getRealmComponent(character.getGameObject());
						CombatSheet sheet = CombatSheet.createCombatSheet(CombatFrame.getSingleton(),this,characterRc,false, hostPrefs);
						Collection<RealmComponent> c = sheet.getAllParticipantsOnSheet();
						int attackCombatBox = spell.getAttackCombatBox();
						for (RealmComponent target : c) {
							CombatWrapper combatTarget = new CombatWrapper(target.getGameObject());
							if (combatTarget.getCombatBox() == attackCombatBox) {
								spell.addTarget(hostPrefs, target.getGameObject());
							}
						}
						for (String key2 : attackBlockOrder) {
							for (BattleChit casterAttacker : attackBlocks.getList(key2)) {
								RealmComponent casterAttackerRc = RealmComponent.getRealmComponent(casterAttacker.getGameObject());
								if(casterAttackerRc.isCharacter() && (casterAttackerRc.getTarget() == characterRc || casterAttackerRc.get2ndTarget() == characterRc)) {
									if (casterAttacker.getAttackCombatBox() == attackCombatBox) {
										spell.addTarget(hostPrefs, casterAttackerRc.getGameObject());
									}
								}
							}
						}
					}
					for (RealmComponent rc : spell.getTargets()) {
						BattleChit target = (BattleChit)rc;
						doTargetAttack(attacker,target,round,attackOrderPos);
						RealmLogging.clearIndent();
					}
				}
				else {
					BattleChit target = null;
					if (attacker.isCharacter()) {
						CharacterChitComponent character = (CharacterChitComponent)attacker;
						target = (BattleChit) character.getTarget();
						// all but the first attack, aim at the second target
						if (character.get2ndTarget() != null && character.getTargetAttacked()) {
							target = (BattleChit) character.get2ndTarget();
						}
						character.setTargetAttacked();
					}
					else {
						target = (BattleChit)attacker.getTarget();
					}
					doTargetAttack(attacker,target,round,attackOrderPos);
				}
				RealmLogging.clearIndent();
			}
			attackOrderPos++;
		}
	}
	
	private void scoreKills(int round) {
		for (GameObject attacker : killerOrder) { // use killerOrder instead of killTallyHash.keySet to guarantee proper ordering of calculations (fixes BUG 1719)
			RealmComponent rc = RealmComponent.getRealmComponent(attacker);
			RealmComponent owner = rc.getOwner();
			if (owner!=null) { // only characters and hirelings can score points and gold
				CharacterWrapper character = new CharacterWrapper(owner.getGameObject());
				CombatWrapper attackerCombat = new CombatWrapper(attacker);
				ArrayList<GameObject> kills = killTallyHash.getList(attacker);
				// Need to sort from most to least notoriety (Rule 43.4)
				Collections.sort(kills,new Comparator<GameObject>() {
					public int compare(GameObject go1,GameObject go2) {
						int ret = 0;
						RealmComponent rc1 = RealmComponent.getRealmComponent(go1);
						RealmComponent rc2 = RealmComponent.getRealmComponent(go2);
						int not1 = go1.getThisInt("notoriety");
						if (rc1.isCharacter()) {
							CharacterWrapper cw1 = new CharacterWrapper(go1);
							not1 = cw1.getRoundedNotoriety();
						}
						int not2 = go2.getThisInt("notoriety");
						if (rc2.isCharacter()) {
							CharacterWrapper cw2 = new CharacterWrapper(go2);
							not2 = cw2.getRoundedNotoriety();
						}
						ret = not2 - not1; // This sorts the one with the most bounty to the first positions (lower multiplication values)
						return ret;
					}
				});
				// Cycle through kills
				for (GameObject kill : kills) {
					int divides = killedTallyHash.getList(kill).size(); // how many ways to split?

					RealmComponent rcKill = RealmComponent.getRealmComponent(kill);
					if (!rcKill.isHorse() && !rcKill.isNativeHorse()) {
						attackerCombat.addKillResult();
					}

					int multiplier = attackerCombat.getHitResultCount();

					Spoils spoils = Spoils.getSpoils(attacker,kill);
					spoils.setMultiplier(multiplier);
					spoils.setDivisor(divides);

					character.addKill(kill,spoils); // for recording purposes, and quests

					if (spoils.hasFameOrNotoriety()) {
						character.addFame(spoils.getFame());
						character.addNotoriety(spoils.getNotoriety());
						logBattleInfo("The "+character.getGameObject().getName()
								+" gets "
								+spoils.getFameNotorietyString()
								+" for the death of the "
								+kill.getNameWithNumber());
					}

					if (spoils.hasGold() && rc.isPlayerControlledLeader()) {
						CharacterWrapper record = new CharacterWrapper(attacker);
						record.addGold(spoils.getGoldBounty());
						record.addGold(spoils.getGoldRecord());
						logBattleInfo("The "+attacker.getNameWithNumber()
								+" gets "
								+spoils.getGoldString()
								+" from the "
								+kill.getNameWithNumber());
					}

					CombatWrapper ownerCombat = new CombatWrapper(owner.getGameObject());
					ownerCombat.addSpoilsInfo(round,kill,spoils);
				}
			}
		}
	}
	
	private void doTargetAttack(BattleChit attacker,BattleChit target,int round,int attackOrderPos) {
		CombatWrapper attackerCombat = new CombatWrapper(attacker.getGameObject());
		CombatWrapper targetCombat = target==null?null:(new CombatWrapper(target.getGameObject()));
		String attackerName = attacker.getGameObject().getNameWithNumber();
		GameObject killer = attackerCombat.getKilledBy();
		int killLength = attackerCombat.getKilledLength();
		int killSpeed = attackerCombat.getKilledSpeed();
		if (attacker instanceof SpellWrapper) {
			// In the case of a spell, the attack is cancelled when the caster is dead
			SpellWrapper spell = (SpellWrapper)attacker;
			CharacterWrapper character = spell.getCaster();
			if (character!=null) {
				CombatWrapper casterCombat = new CombatWrapper(character.getGameObject());
				killer = casterCombat.getKilledBy();
				killLength = casterCombat.getKilledLength();
				killSpeed = casterCombat.getKilledSpeed();
				attackerName = character.getGameObject().getName();
			}
		}
		if (target==null) {
			// this happens with the monster weapons
			RealmComponent monster = RealmComponent.getRealmComponent(attacker.getGameObject().getHeldBy());
			target = (BattleChit)monster.getTarget();
			targetCombat = target==null?null:(new CombatWrapper(target.getGameObject()));
			CombatWrapper weaponHolder = new CombatWrapper(monster.getGameObject());
			killer = weaponHolder.getKilledBy(); // if holder is killed, their weapon attack is cancelled too!
			killLength = weaponHolder.getKilledLength();
			killSpeed = weaponHolder.getKilledSpeed();
			if (killer == null && attackerCombat.getKilledBy() != null) {
				killer = attackerCombat.getKilledBy();
				killLength = attackerCombat.getKilledLength();
				killSpeed = attackerCombat.getKilledSpeed();
			}
			else if (killer != null && attackerCombat.getKilledBy() != null) {
				if (round == 1) {
					if (weaponHolder.getKilledLength() > attackerCombat.getKilledLength()) {
						killer = weaponHolder.getKilledBy();
						killLength = weaponHolder.getKilledLength();
						killSpeed = weaponHolder.getKilledSpeed();
					}
					else if (weaponHolder.getKilledLength() < attackerCombat.getKilledLength()) {
						killer = attackerCombat.getKilledBy();
						killLength = attackerCombat.getKilledLength();
						killSpeed = attackerCombat.getKilledSpeed();
					}
					else {
						if (weaponHolder.getKilledSpeed() <= attackerCombat.getKilledSpeed()) {
							killer = weaponHolder.getKilledBy();
							killLength = weaponHolder.getKilledLength();
							killSpeed = weaponHolder.getKilledSpeed();
						}
						else {
							killer = attackerCombat.getKilledBy();
							killLength = attackerCombat.getKilledLength();
							killSpeed = attackerCombat.getKilledSpeed();
						}
					}
				}
				else {
					if (weaponHolder.getKilledSpeed() < attackerCombat.getKilledSpeed()) {
						killer = weaponHolder.getKilledBy();
						killLength = weaponHolder.getKilledLength();
						killSpeed = weaponHolder.getKilledSpeed();
					}
					else if (weaponHolder.getKilledSpeed() > attackerCombat.getKilledSpeed()) {
						killer = attackerCombat.getKilledBy();
						killLength = attackerCombat.getKilledLength();
						killSpeed = attackerCombat.getKilledSpeed();
					}
					else {
						if (weaponHolder.getKilledLength() >= attackerCombat.getKilledSpeed()) {
							killer = weaponHolder.getKilledBy();
							killLength = weaponHolder.getKilledLength();
							killSpeed = weaponHolder.getKilledSpeed();
						}
						else {
							killer = attackerCombat.getKilledBy();
							killLength = attackerCombat.getKilledLength();
							killSpeed = attackerCombat.getKilledSpeed();
						}
					}
				}
			}
		}
		if (targetCombat == null) {
			logBattleInfo(attacker.getName() + " has no target anymore. Attack canelled.");
			return;
		}
		GameObject targetKiller = targetCombat.getKilledBy();

		String attackCancelled = null;
		
		boolean parry = false;
		if (attacker.isCharacter() && !(new CharacterWrapper(attacker.getGameObject()).isTransmorphed())) {
			CombatWrapper combatAttackChit = new CombatWrapper(((CharacterChitComponent) attacker).getAttackChit().getGameObject());
			if (combatAttackChit.getPlacedAsParry()) {
				parry = true;
			}
			// Attack must be placed on target's sheet or target must be on character's sheet (=target attacks the character) or both must be on same denizen sheet
			if (!parry && !combatAttackChit.getSheetOwnerId().equals(target.getGameObject().getStringId())
					&& !(combatAttackChit.getSheetOwnerId().equals(attacker.getGameObject().getStringId()) && attackerCombat.getAttackers().contains(target.getGameObject()))
					&& (!target.isCharacter() && !combatAttackChit.getSheetOwnerId().equals(RealmComponent.getRealmComponent(target.getGameObject()).getTarget().getGameObject().getStringId()))
					&& (!target.isCharacter() && !combatAttackChit.getSheetOwnerId().equals(RealmComponent.getRealmComponent(target.getGameObject()).get2ndTarget().getGameObject().getStringId()))) {
				attackCancelled = "Miss! ("+attacker+" placed the attack not on same sheet as target.)";
			}
		}
		
		// You can't kill a target that is already dead, unless the target's killer attacked with the same speed and length (simultaneous)
		if (targetKiller!=null) {
			int targetKillerLength = targetCombat.getKilledLength();
			int targetKillerSpeed = targetCombat.getKilledSpeed();
			boolean simultaneous = targetKillerSpeed == attacker.getAttackSpeed().getNum()
					&& targetKillerLength == attacker.getLength().intValue();
			/*
			BattleChit targetKillerChit = RealmComponent.getBattleChit(targetKiller);
			boolean simultaneous = targetKillerChit.getAttackSpeed().equals(attacker.getAttackSpeed())
										&& targetKillerChit.getLength().equals(attacker.getLength());
			*/
			if (!simultaneous) {
				// nope - attack is cancelled
				attackCancelled = target.getGameObject().getNameWithNumber()+" was already killed by "+targetKiller.getNameWithNumber()+".";
			}
		}
		
		// The attacker can't attack if he/she is dead, unless their attack is simultaneous with their killer.
		if (killer!=null) {
			// The attacker was killed, but was it simultaneous?
			boolean simultaneous = killSpeed == attacker.getAttackSpeed().getNum()
					&& killLength == attacker.getLength().intValue();
			/*
			BattleChit killerChit = RealmComponent.getBattleChit(killer);
			boolean simultaneous = killerChit.getAttackSpeed().equals(attacker.getAttackSpeed())
										&& killerChit.getLength().equals(attacker.getLength());
			*/
			if (!simultaneous) {
				// nope - attack is cancelled
				attackCancelled = attackerName+" was already killed by "+killer.getNameWithNumber()+".";
			}
		}
		
		if (attackCancelled == null && attacker instanceof MonsterChitComponent) {
			if (attackerCombat.getCancelSpell()) {
				attackCancelled = attacker.getGameObject().getNameWithNumber()+"'s spell was already canceled.";
			}
		}
		
		// Before anything else, check to see if character is immune to the attacker
		if (attackCancelled == null && target!=null && (attacker instanceof RealmComponent) && target.isImmuneTo((RealmComponent)attacker)) {
			attackCancelled = target.getGameObject().getNameWithNumber()+" is immune to "+attacker.getGameObject().getNameWithNumber()+".";
		}
		
		// Before anything else, check to see if character fears the target
		if (attackCancelled == null && !parry && (attacker instanceof RealmComponent) && (target instanceof RealmComponent) && ((RealmComponent)attacker).fears((RealmComponent)target)) {
			attackCancelled = attacker.getGameObject().getNameWithNumber()+" fears "+target.getGameObject().getNameWithNumber()+" and cannot attack it.";
		}
		
		if (attackCancelled == null && attackerCombat.wasParried() && attackerCombat.getParriedBy().contains(target.getGameObject().getStringId())) {
			attackCancelled = attacker.getGameObject().getNameWithNumber()+" was already parried and cannot attack "+target.getGameObject().getNameWithNumber()+".";
		}
		
		logBattleInfo(">");
		RealmLogging.incrementIndent();
		String attackOrParry = "Attack";
		if (parry) {
			attackOrParry = "Parry";
		}
		if (round==1) {
			logBattleInfo(attackOrParry+" Length="+attacker.getLength()+", Speed="+attacker.getAttackSpeed().getNum());
		}
		else {
			logBattleInfo(attackOrParry+" Speed="+attacker.getAttackSpeed().getNum()+", Length="+attacker.getLength());
		}
		RealmLogging.incrementIndent();
		if (!parry) {
			logBattleInfo(getCombatantInformation(attacker,true)+" vs. "+getCombatantInformation(target,false));
		}
		if (attackCancelled!=null) {
			if (!parry) {
				logBattleInfo("Attack Cancelled:  "+attackCancelled);
				attackerCombat.addHitType(ATTACK_CANCELLED,target.getGameObject());
				return;
			}
			logBattleInfo("Parry Cancelled:  "+attackCancelled);
			attackerCombat.addHitType(PARRY_CANCELLED,target.getGameObject());
			return;
		}
		if (!parry) {
			int hitType = NO_ATTACK;
			if (attacker.hasAnAttack()) {
				hitType = MISS;
				boolean undercuttingAllowed = !attacker.getGameObject().hasThisAttribute(Constants.NO_UNDERCUT);
				if (attacker.getAttackCombatBox()==target.getManeuverCombatBox() || target.getManeuverCombatBox()==0) {
					// Intercepted!
					hitType = INTERCEPT;
					attackerCombat.setHitResult("Intercepted");
					setWeaponHitForCharacter(attacker);
					logBattleInfo("Intercepted! (box "+attacker.getAttackCombatBox()+" matches box "+target.getManeuverCombatBox()+")");
				}
				else if (undercuttingAllowed && attacker.getAttackSpeed().fasterThan(target.getMoveSpeed())) {
					// Undercut!
					boolean stopsUndercut = ((RealmComponent)target).affectedByKey(Constants.STOP_UNDERCUT);
					if (stopsUndercut) {
						logBattleInfo("Miss! ("+attacker.getAttackSpeed()+" is faster than "+target.getMoveSpeed()+", but "+target.getGameObject().getNameWithNumber()+" cannot be undercut!)");
					}
					else {
						hitType = UNDERCUT;
						attackerCombat.setHitResult("Undercut");
						setWeaponHitForCharacter(attacker);
						logBattleInfo("Undercut! ("+attacker.getAttackSpeed()+" is faster than "+target.getMoveSpeed()+")");
					}
				}
				else if (undercuttingAllowed && attacker.getAttackSpeed().equalTo(target.getMoveSpeed()) && attacker.hitsOnTie()) {
					// Check for the special case where a character has a HIT_TIE treasure alerted
					hitType = UNDERCUT;
					attackerCombat.setHitResult("Undercut");
					setWeaponHitForCharacter(attacker);
					logBattleInfo("Undercut (hits on tie)! ("+attacker.getAttackSpeed()+" is equal to "+target.getMoveSpeed()+")");
				}
				if (!undercuttingAllowed && hitType==MISS) {
					logBattleInfo(attacker.getGameObject().getNameWithNumber()+" cannot be used to undercut the "+target.getGameObject().getNameWithNumber()+", and as such has missed.");
				}
			}
			attackerCombat.addHitType(hitType,target.getGameObject());
			
			if (hitType>MISS) {
				int fumbleModifier = 0;
				if (hostPrefs.hasPref(Constants.OPT_FUMBLE)) {
					fumbleModifier = calculateBaseFumbleModifier(attacker, target, hitType, hostPrefs);
					/*
					 * Possibilities:
					 * 		No OPT_SEPARATE_RIDER
					 * 			Targeting non-horseback rider - DONE
					 * 			Targeting horseback rider - DONE
					 * 		OPT_SEPARATE_RIDER
					 * 			Targeting non-horseback rider - DONE
					 * 			Targeting horseback rider
					 * 			Targeting rider's horse - DONE
					 */
					if (hostPrefs.hasPref(Constants.OPT_RIDING_HORSES)) {
						// Determine if we need to do rider maneuver adjustment
						RealmComponent targetRc = (RealmComponent)target;
						if (targetRc.getHorse()!=null && targetCombat.isTargetingRider(attacker.getGameObject())) {
							// This is the new situation that we need to handle
							//		- Get the rider's maneuver (if any) separate from the horse
							//		- Need the box, and the speed...
							int mBox = 0;
							Speed mSpeed = null;
							if (targetRc instanceof Horsebackable) {
								Horsebackable hb = (Horsebackable)targetRc;
								mBox = hb.getManeuverCombatBox(false);
								mSpeed = hb.getMoveSpeed(false);
								if (mBox>0 && mSpeed!=null) {
									logBattleInfo("Applying special horse/rider maneuver rules:");
									
									// Apply the extra fumbleModifier here
									fumbleModifier += (attacker.getAttackSpeed().getNum() - mSpeed.getNum());
									logBattleInfo("fumble + "+attacker.getAttackSpeed().getNum()+" - "+mSpeed.getNum()+" = "+fumbleModifier+" (base attack speed versus rider)");
									
									if (attacker.getAttackCombatBox()==mBox) {
										logBattleInfo("Intercepted Rider! (box "+attacker.getAttackCombatBox()+" matches box "+mBox+")");
									}
									else {
										logBattleInfo("Did not Intercept Rider! (box "+attacker.getAttackCombatBox()+" does not match box "+mBox+")");
										fumbleModifier += 4;
										logBattleInfo("fumble + 4 = "+fumbleModifier+" (for failing to intercept rider)");
									}
								}
							}
						}
					}
				}
				int currentNewWounds = 0;
				boolean hitCausedHarm = false;
				String magicType = attacker.getMagicType();
				if (magicType!=null && magicType.trim().length()>0) {
					if ((attacker.isDenizen() &&  "V".equals(magicType)) || Constants.POWER_OF_THE_PIT.matches(magicType)) {
						if (attacker instanceof SpellWrapper) {
							// Spells belong to characters
							SpellWrapper spell = (SpellWrapper)attacker;
							attacker = new CharacterChitComponent(spell.getCaster().getGameObject());
						}
						// Demon's Power of the Pit
						logBattleInfo(target.getGameObject().getNameWithNumber()+" was hit with Power of the Pit along box "+attacker.getAttackCombatBox());
						PowerOfThePit pop = PowerOfThePit.doNow(SpellWrapper.dummyFrame,attacker.getGameObject(),target.getGameObject(),false,0,attacker.getAttackSpeed());
						ArrayList<GameObject> kills = new ArrayList<>(pop.getKills());
						kills.remove(targetCombat.getGameObject()); // Because targetCombat will be handled normally
						
						for (GameObject kill:kills) {
							logBattleInfo(kill.getNameWithNumber()+" was killed!");
							killedTallyHash.put(kill,attacker.getGameObject());
							killTallyHash.put(attacker.getGameObject(),kill);
							if (!killerOrder.contains(attacker.getGameObject())) killerOrder.add(attacker.getGameObject());
							BattleUtility.handleSpoilsOfWar((RealmComponent)attacker,RealmComponent.getRealmComponent(kill));
						}
						hitCausedHarm = pop.harmWasApplied();
						spellCasting = true;
					}
					else if (attacker.isDenizen() && "VIII".equals(magicType)) {
						// Imp's Curse
						logBattleInfo(target.getGameObject().getNameWithNumber()+" was hit with a Curse along box "+attacker.getAttackCombatBox());
						Curse curse = Curse.doNow(SpellWrapper.dummyFrame,attacker.getGameObject(),target.getGameObject());
						hitCausedHarm = curse.harmWasApplied();
						spellCasting = true;
					}
					else if (attacker instanceof SpellWrapper && Constants.WALL_OF_FORCE.matches(magicType)) {
						hitCausedHarm = WallOfForce.apply(new SpellWrapper(attacker.getGameObject()),target.getGameObject());
						logBattleInfo(target.getGameObject().getNameWithNumber()+" was hit with by "+attacker.getName()+" along box "+attacker.getAttackCombatBox());
						spellCasting = true;
					}
					else if (attacker instanceof SpellWrapper && Constants.FEAR.matches(magicType)) {
						hitCausedHarm = Fear.apply(new SpellWrapper(attacker.getGameObject()),target.getGameObject(),battleLocation);
						logBattleInfo(target.getGameObject().getNameWithNumber()+" was hit with by "+attacker.getName()+" along box "+attacker.getAttackCombatBox());
						spellCasting = true;
					}
				}
				else {
					// Get the adjusted Attacker Harm (fumble, missile applied)
					Harm attackerHarm = getAdjustedHarm(attacker,fumbleModifier,targetCombat.getGameObject().getStringId());
					
					if (attacker instanceof SpellWrapper) {
						// Spells should ref back to caster
						SpellWrapper spell = (SpellWrapper)attacker;
						CombatWrapper spellCaster = new CombatWrapper(spell.getCaster().getGameObject());
						spellCaster.addHarmApplied(attackerHarm,targetCombat.getGameObject());
						spellCasting = true;
					}
					else {
						attackerCombat.addHarmApplied(attackerHarm,targetCombat.getGameObject());
					}
							
					// Apply the hit
					targetCombat.addHitBy(attacker.getGameObject());
					currentNewWounds = targetCombat.getNewWounds();
					
					logBattleInfo(target.getGameObject().getNameWithNumber()+" is hit with "+attackerHarm+" harm along box "+attacker.getAttackCombatBox());
					hitCausedHarm = target.applyHit(theGame,hostPrefs,attacker,attacker.getAttackCombatBox(),attackerHarm,attackOrderPos);
				}
				
				if (hitCausedHarm) {
					// Only add to totalHits, when some sort of harm is caused
					totalHits++;
					
					// Determine wounds (if any)
					int woundsThisHit = targetCombat.getNewWounds() - currentNewWounds;
					if (woundsThisHit>0) {
						logBattleInfo(target.getGameObject().getNameWithNumber()+" takes "+woundsThisHit+" wound"+(woundsThisHit==1?"":"s"));
					}
				}
				
				// Check to see if the target was killed by this attacker,
				// and if so, give the killing character some points!
				if (targetCombat.getKilledBy()!=null && targetCombat.getKilledBy().equals(attacker.getGameObject())) {
					logBattleInfo(target.getGameObject().getNameWithNumber()+" was killed!");
					RealmComponent rc;
					if (attacker instanceof SpellWrapper) {
						// Spells belong to characters
						SpellWrapper spell = (SpellWrapper)attacker;
						GameObject caster = spell.getCaster().getGameObject();
						if (attacker.getGameObject().hasThisAttribute(Constants.DRAIN)) {
							String vul = target.getGameObject().getThisAttribute("vulnerability");
							if (vul!=null) {
								CombatWrapper casterCombat = new CombatWrapper(caster);
								int amt = Strength.valueOf(vul).getLevels();
								casterCombat.addHealing(amt);
								RealmLogging.logMessage(
										caster.getName(),
										"Life force of the "
											+target.getGameObject().getNameWithNumber()
											+" is worth "+amt+" asterisk"+(amt==1?"":"s"));
							}
						}
						rc = RealmComponent.getRealmComponent(caster);
						attacker = RealmComponent.getBattleChit(rc.getGameObject());
					}
					else if (attacker instanceof MonsterPartChitComponent) {
						// Might be a transformed character or controlled monster part
						rc = (RealmComponent)attacker;
						GameObject monster = attacker.getGameObject().getHeldBy();
						GameObject spellOrTile = monster.getHeldBy();
						RealmComponent test = RealmComponent.getRealmComponent(spellOrTile);
						if (test.isSpell()) {
							SpellWrapper spell = new SpellWrapper(spellOrTile);
							CharacterWrapper character = spell.getCaster();
							rc = RealmComponent.getRealmComponent(character.getGameObject());
							attacker = RealmComponent.getBattleChit(rc.getGameObject());
						}
					}
					else {
						rc = (RealmComponent)attacker;
					}
					RealmComponent targetRc = (RealmComponent)target;
					
					GameObject attackerGo = ((RealmComponent)attacker).isMonsterPart()?attacker.getGameObject().getHeldBy():attacker.getGameObject();
					
					killedTallyHash.put(target.getGameObject(),attackerGo);
					killTallyHash.put(attackerGo,target.getGameObject());
					if (!killerOrder.contains(attackerGo)) killerOrder.add(attackerGo);
					BattleUtility.handleSpoilsOfWar(rc,targetRc);
				}
				else {
					logBattleInfo(target.getGameObject().getNameWithNumber()+" was not killed.");
				}
			}
			else {
				if (hitType==MISS) {
					logBattleInfo("Missed! ("+attacker.getAttackSpeed()+" is not faster than "+target.getMoveSpeed()+")");
				}
				else {
					logBattleInfo(attacker.getGameObject().getNameWithNumber()+" didn't attack, and thus does not harm the target.");
				}
			}
			return;
		}
		if (parry) {
			if (!attacker.hasAnAttack()) {
				logBattleInfo(attacker.getGameObject().getNameWithNumber()+" didn't parry, and thus does not prevent an attack.");
				return;
			}
			
			ArrayList<BattleChit> targets = new ArrayList<>();
			if (!target.isCharacter()) {
				targets.add(target);
				if (target.isMonster()) {
					MonsterChitComponent monster = (MonsterChitComponent)target;
					RealmComponent weapon = monster.getWeapon();
					if (weapon!=null) {
						targets.add((BattleChit)weapon);
					}
				}
			}
			else {
				CharacterChitComponent chit = (CharacterChitComponent)target;
				MonsterChitComponent transmorph = chit.getTransmorphedComponent();
				if (transmorph!=null) {
					targets.add(chit);
					RealmComponent weapon = transmorph.getWeapon();
					if (weapon!=null) {
						targets.add((BattleChit)weapon);
					}
				}
				CharacterWrapper character = new CharacterWrapper(target.getGameObject());
				for (RealmComponent fightChit : BattleUtility.findFightComponentsWithCombatBox(character.getFightSpeedOptions(new Speed(), true))) {
					CharacterChitComponent charChit = new CharacterChitComponent(chit.getGameObject());
					charChit.setAttackChit(fightChit);
					targets.add(charChit);
				}
			}

			for (BattleChit targetBc : targets) {
				Speed targetAttackSpeed = targetBc.getAttackSpeed();
				int targetAttackBox = targetBc.getAttackCombatBox();
				RealmComponent targetRc = RealmComponent.getRealmComponent(targetBc.getGameObject());
				int hitType = MISS;
				boolean undercuttingAllowed = !attacker.getGameObject().hasThisAttribute(Constants.NO_UNDERCUT);
				logBattleInfo(attacker.getGameObject().getNameWithNumber()+" vs. target "+targetBc.getGameObject().getNameWithNumber()+" (Speed: "+targetBc.getAttackSpeed()+" Strength: "+targetBc.getHarm().getStrength()+")");
				
				if(targetRc.getTarget() != null && targetRc.getTarget().getGameObject() != attacker.getGameObject() && targetRc.get2ndTarget() != null && targetRc.get2ndTarget().getGameObject() != attacker.getGameObject()) {
					hitType = CANNOT_PARRY;
					logBattleInfo(attacker.getGameObject().getNameWithNumber()+" didn't parry, as target ("+targetBc.getGameObject().getNameWithNumber()+") didn't aim at "+attacker.getGameObject().getNameWithNumber()+".");
				}
				else if (targetBc.isMissile()) {
					hitType = CANNOT_PARRY;
					logBattleInfo(attacker.getGameObject().getNameWithNumber()+" cannot parry missile attacks.");
				}
				else if (targetBc instanceof SpellWrapper) {
					hitType = CANNOT_PARRY;
					logBattleInfo(attacker.getGameObject().getNameWithNumber()+" cannot parry spells.");
				}
				else if (targetBc.isMonster() && ((MonsterChitComponent)targetBc).isRedSideUp()) {
					hitType = CANNOT_PARRY;
					logBattleInfo(attacker.getGameObject().getNameWithNumber()+" cannot parry red side up tremendous monsters.");
				}
				else if (targetBc.getHarm().getStrength().equals(new Strength("X"))) {
					hitType = CANNOT_PARRY;
					logBattleInfo(attacker.getGameObject().getNameWithNumber()+" cannot parry attacks of maximum strength.");
				}
				else if (!attacker.getHarm().getStrength().strongerOrEqualTo(targetBc.getHarm().getStrength())) {
					hitType=CANNOT_PARRY;
					logBattleInfo("Cannot parry! ("+attacker.getGameObject().getNameWithNumber()+" cannot parry as strength of "+attacker.getHarm().getStrength()+" is not greater or equal as "+targetBc.getHarm().getStrength()+".)");
				}
				else if (attacker.getAttackCombatBox()==targetAttackBox) {
					// Intercepted!
					hitType = INTERCEPT_PARRY;
					attackerCombat.setHitResult("Intercepted");
					setWeaponHitForCharacter(attacker);
					logBattleInfo("Intercepted! (box "+attacker.getAttackCombatBox()+" matches box "+targetAttackBox+")");
				}
				else if (undercuttingAllowed && attacker.getAttackSpeed().fasterThan(targetAttackSpeed)) {
					// Undercut!
					boolean stopsUndercut = ((RealmComponent)targetBc).affectedByKey(Constants.STOP_UNDERCUT);
					if (stopsUndercut) {
						logBattleInfo("Miss! ("+attacker.getAttackSpeed()+" is faster than "+targetAttackSpeed+", but "+targetBc.getGameObject().getNameWithNumber()+" cannot be undercut!)");
					}
					else {
						hitType = UNDERCUT_PARRY;
						attackerCombat.setHitResult("Undercut");
						setWeaponHitForCharacter(attacker);
						logBattleInfo("Undercut! ("+attacker.getAttackSpeed()+" is faster than "+targetAttackSpeed+")");
					}
				}
				else if (undercuttingAllowed && attacker.getAttackSpeed().equalTo(targetAttackSpeed) && attacker.hitsOnTie()) {
					// Check for the special case where a character has a HIT_TIE treasure alerted
					hitType = UNDERCUT_PARRY;
					attackerCombat.setHitResult("Undercut");
					setWeaponHitForCharacter(attacker);
					logBattleInfo("Undercut (hits on tie)! ("+attacker.getAttackSpeed()+" is equal to "+targetAttackSpeed+")");
				}
				if (!undercuttingAllowed && hitType==MISS) {
					logBattleInfo(attacker.getGameObject().getNameWithNumber()+" cannot be used to undercut the "+targetBc.getGameObject().getNameWithNumber()+", and as such has missed.");
				}
				
				attackerCombat.addHitType(hitType,targetBc.getGameObject());
				
				if (hitType>MISS) {
					CombatWrapper targetCombatWrapper = new CombatWrapper(targetBc.getGameObject());
					targetCombatWrapper.setWasParried(attacker.getGameObject().getStringId());
					logBattleInfo("Parried! "+attacker.getGameObject().getNameWithNumber()+" parried "+targetBc.getGameObject().getNameWithNumber()+".");
				}
				else  {
					if (hitType==MISS) {
						logBattleInfo("Missed! ("+attacker.getAttackSpeed()+" is not faster than "+targetAttackSpeed+")");
					}
					else {
						logBattleInfo(attacker.getGameObject().getNameWithNumber()+" didn't parry, and thus does not prevent the attack.");
					}
				}
			}
		}
	}
	private static String getCombatantInformation(BattleChit chit,boolean attacker) {
		StringBuffer sb = new StringBuffer();
		sb.append(chit.getGameObject().getNameWithNumber());
		sb.append(" ");
		if (attacker) {
			Harm harm = chit.getHarm();
			sb.append(harm.getStrength());
			sb.append(chit.getAttackSpeed().getSpeedString());
			for (int i=0;i<harm.getSharpness();i++) {
				sb.append("*");
			}
		}
		else {
			sb.append(chit.getMoveSpeed());
		}
		sb.append(" ");
		return sb.toString();
	}
	private static int calculateBaseFumbleModifier(BattleChit attacker, BattleChit target, int hitType, HostPrefWrapper hostPrefs) {
		int fumbleModifier = attacker.getAttackSpeed().getNum() - target.getMoveSpeed().getNum();
		CombatWrapper attackerCombat = new CombatWrapper(attacker.getGameObject());
		if (hostPrefs.hasPref(Constants.OPT_TWO_HANDED_WEAPONS) && RealmComponent.getRealmComponent(attackerCombat.getGameObject()).isCharacter()) {
			CharacterWrapper attackerCharacter = new CharacterWrapper(attackerCombat.getGameObject());
			ArrayList<GameObject> activeInventory = attackerCharacter.getActiveInventory();
			boolean shield = false;
			boolean twoHandedWeapon = false;
			if (!attackerCharacter.affectedByKey(Constants.STRONG)) {
				for (GameObject item : activeInventory) {
					if (item.hasThisAttribute(Constants.SHIELD) && item.getThisAttribute("weight") != "L") shield = true;
					if (item.hasThisAttribute(Constants.TWO_HANDED)) twoHandedWeapon = true;
				}
			}
			if (twoHandedWeapon && shield) {
				fumbleModifier = fumbleModifier+2;
				logBattleInfo("fumble = "+attacker.getAttackSpeed().getNum()+" - "+target.getMoveSpeed().getNum()+" = "+fumbleModifier+" (base speed difference and two-handed weapon malus)");
			}
		}
		else {
			logBattleInfo("fumble = "+attacker.getAttackSpeed().getNum()+" - "+target.getMoveSpeed().getNum()+" = "+fumbleModifier+" (base speed difference)");
		}
		if (hitType==UNDERCUT) {
			fumbleModifier += 4;
			logBattleInfo("fumble + 4 = "+fumbleModifier+" (for undercut)");
		}
		return fumbleModifier;
	}
	private static void setWeaponHitForCharacter(BattleChit attacker) {
		if (attacker.isCharacter() && !(new CharacterWrapper(attacker.getGameObject()).isTransmorphed())) {
			RealmComponent attackChit = ((CharacterChitComponent)attacker).getAttackChit();
			if (attackChit!=null) {
				CombatWrapper combatAttack = new CombatWrapper(attackChit.getGameObject());
				if (combatAttack.getWeaponId()!=null) {
					CombatWrapper attackerCombat = new CombatWrapper(attacker.getGameObject());
					attackerCombat.setWeaponHit(combatAttack.getWeaponId());
				}
			}
		}
	}
	private Harm getAdjustedHarm(BattleChit attacker,int fumbleModifier,String targetId) {
		if (hostPrefs.hasPref(Constants.OPT_FUMBLE)) {
			logBattleInfo("fumbleModifier for attacker ("+attacker.getGameObject().getNameWithNumber()+") is "+fumbleModifier);
		}
		Harm totalHarm = attacker.getHarm();
		if (!totalHarm.isAdjustable()) {
			logBattleInfo("attacker harm of "+totalHarm.toString()+" is final (cannot be modified)");
			return totalHarm;
		}
		logBattleInfo("Initial harm: "+totalHarm.toString());
		
		String magType = attacker.getMagicType();
		boolean magicAttack = magType!=null && magType.length()>0;
		boolean redAttack = totalHarm.getStrength().isRed();
		
		CombatWrapper combat = new CombatWrapper(attacker.getGameObject());
		if (attacker instanceof SpellWrapper) {
			// Spells should ref back to caster
			SpellWrapper spell = (SpellWrapper)attacker;
			combat = new CombatWrapper(spell.getCaster().getGameObject());
		}

		if (!totalHarm.isNegligible()) { // only apply missile/fumble if totalHarm is not completely negligible (like light side spear goblins)
			if (attacker.isMissile()) {
				// Do the missile roll
				DieRoller roller;
				RealmComponent rc = RealmComponent.getRealmComponent(combat.getGameObject());
				if (rc.isCharacter()) {
					CharacterWrapper character = new CharacterWrapper(combat.getGameObject());
					if (combat.getCastSpell()==null) {
						String missileType = attacker.getMissileType();
						if (missileType.trim().length()==0) {
							missileType = "missile";
						}
						roller = DieRollBuilder.getDieRollBuilder(null,character).createRoller(missileType);
					}
					else {
						SpellWrapper spell = new SpellWrapper(combat.getCastSpell());
						roller = DieRollBuilder.getDieRollBuilder(null,character,spell.getRedDieLock()).createRoller("magicmissil"); // the 'e' is left off intentionally here, so that "indexOf" doesn't find the word "missile"
					}
				}
				else {
					// Monsters and Natives just get two dice, unless they are an archer
					String icon = combat.getGameObject().getThisAttribute(Constants.ICON_TYPE);
					int dice = 2;
					if (icon!=null && "archer".equalsIgnoreCase(icon)) {
						// If the native is an archer, they only roll one die
						dice = 1;
					}
					roller = createClearingRoller(dice,"Missile");
				}
				roller.addModifier(fumbleModifier);
				combat.addMissileRoll(roller.getStringResult());
				combat.addMissileRollTargetId(targetId);
				logBattleInfo("Missile roll: "+roller.getDescription());
				
				int result = roller.getHighDieResult(); // fumbleModifier will always be zero if this option is OFF
				if (hostPrefs.hasPref(Constants.OPT_MISSILE)) {
					// Optional missile table
					if (result<1) {
						result = 1;
					}
					if (result<10) {
						if (result<8) {
							totalHarm.changeLevels(4-result);
							combat.addMissileRollSubtitle(RealmUtility.getLevelChangeString(4-result));
							logBattleInfo("Missile table result (using optional): "+result+" = "+RealmUtility.getLevelChangeString(4-result));
						}
						else {
							totalHarm.setWound(true);
							combat.addMissileRollSubtitle("wound");
							logBattleInfo("Missile table result (using optional): "+result+" = wound");
						}
					}
				}
				else if (hostPrefs.hasPref(Constants.REV_MISSILE)) {
					// Revised Optional missile table
					int change;
					if (result<-1) {
						change = 3;
					}
					else if (result<0) {
						change = 2;
					}
					else if (result<2) {
						change = 1;
					}
					else if (result<5) {
						change = 0;
					}
					else if (result<7) {
						change = -1;
					}
					else if (result<8) {
						change = -2;
					}
					else {
						change = -3;
					}
					totalHarm.changeLevels(change);
					combat.addMissileRollSubtitle(RealmUtility.getLevelChangeString(change));
					logBattleInfo("Missile table result (using revised): "+result+" = "+RealmUtility.getLevelChangeString(change));
				}
				else {
					// Standard missile table - normalize value
					if (result>6) {
						result = 6;
					}
					if (result<1) {
						result = 1;
					}
					totalHarm.changeLevels(3-result);
					combat.addMissileRollSubtitle(RealmUtility.getLevelChangeString(3-result));
					logBattleInfo("Missile table result: "+result+" = "+RealmUtility.getLevelChangeString(3-result));
				}
			}
			else if (hostPrefs.hasPref(Constants.OPT_FUMBLE) && !magicAttack && !redAttack) {
				// Only roll on fumble table if option is enabled, the attack is NOT magic, and the harm is NOT red
				// Roll on FUMBLE table here
				DieRoller roller;
				RealmComponent rc = RealmComponent.getRealmComponent(attacker.getGameObject());
				if (rc.isCharacter()) {
					CharacterWrapper character = new CharacterWrapper(attacker.getGameObject());
					roller = DieRollBuilder.getDieRollBuilder(null,character).createRoller("fumble");
				}
				else {
					// Monsters and Natives just get two dice
					roller = createClearingRoller(2,"Fumble");
				}
				roller.addModifier(fumbleModifier);
				combat.addFumbleRoll(roller.getStringResult());
				combat.addFumbleRollTargetId(targetId);
				logBattleInfo("Fumble roll: "+roller.getDescription());
				
				int result = roller.getHighDieResult();
				int change;
				if (result<2) {
					change = 2;
				}
				else if (result<4) {
					change = 1;
				}
				else if (result<7) {
					change = 0;
				}
				else if (result<9) {
					change = -1;
				}
				else if (result<10) {
					change = -2;
				}
				else {
					change = -10000; // NEG
				}
				String changeString = change==-10000?"Negligible Harm":RealmUtility.getLevelChangeString(change);
				totalHarm.changeLevels(change);
				combat.addFumbleRollSubtitle(changeString);
				logBattleInfo("Fumble result: "+changeString);
			}
		}
		return totalHarm;
	}
	private void repositionAndChangeTactics(String prefix,CombatWrapper sheetOwner,ArrayList<RealmComponent> groupList) {
		if (groupList.size()>0) {
			// Hash by box
			HashLists<Integer, RealmComponent> boxHash = new HashLists<>();
			for (RealmComponent rc : groupList) {
				CombatWrapper combat = new CombatWrapper(rc.getGameObject());
				int box = combat.getCombatBox();
				if (box>0) {
					boxHash.put(Integer.valueOf(box),rc);
					if (rc.isMonster()) {
						// Make sure we get monster parts!
						MonsterChitComponent monster = (MonsterChitComponent)rc;
						RealmComponent weapon = monster.getWeapon();
						if (weapon!=null) {
							combat = new CombatWrapper(weapon.getGameObject());
							box = combat.getCombatBox();
							if (box==0) {
								throw new IllegalStateException("box is zero for "+weapon.getGameObject().getName()+" during reposition!!");
							}
							boxHash.put(Integer.valueOf(box),weapon);
						}
					}
					else if (rc.isNative()) {
						// Make sure we get native horses!
						NativeSteedChitComponent horse = (NativeSteedChitComponent)rc.getHorse();
						if (horse!=null) {
							combat = new CombatWrapper(horse.getGameObject());
							box = combat.getCombatBox();
							if (box==0) {
								throw new IllegalStateException("box is zero for "+horse.getGameObject().getName()+" during reposition!!");
							}
							boxHash.put(Integer.valueOf(box),horse);
						}
					}
				}// box might be zero if targeting an unassigned monster with a spell
			}
			reposition(prefix,sheetOwner,boxHash);
			if (!battleLocation.clearing.hasSpellEffect(Constants.BEWILDERED)) {
				changeTactics(prefix,sheetOwner,boxHash);
			}
			else {
				RealmLogging.logMessage(RealmLogging.BATTLE,"No Change Tactics rolls were made because of bewilder effect in clearing.");
			}
		}
	}
	private static void reposition(String prefix,CombatWrapper combatTarget,HashLists<Integer, RealmComponent> boxHash) {
		DieRoller roller = new DieRoller(); // Rule 22.5/2 specifies that modifiers do NOT affect this roll
		roller.addRedDie();
		roller.rollDice("Reposition");
		if (SKIP_REPOSITIONING || DebugUtility.isMonsterLock()) {
			// If this debug mode is enabled, don't allow monster repositioning to occur
			roller.setValue(0,4);
		}
		int result = roller.getValue(0);
		combatTarget.setRepositionResult(prefix,result); // Capture the result for purposes of displaying
		ArrayList<RealmComponent> box1 = null;
		ArrayList<RealmComponent> box2 = null;
		ArrayList<RealmComponent> box3 = null;
		// 1,2,3 means THAT box unchanged
		// 4 no change
		// 5 shift down/right
		// 6 shift up/left
		switch(result) {
			case 1:
				box1 = boxHash.getList(Integer.valueOf(1));
				box2 = boxHash.getList(Integer.valueOf(3));
				box3 = boxHash.getList(Integer.valueOf(2));
				break;
			case 2:
				box1 = boxHash.getList(Integer.valueOf(3));
				box2 = boxHash.getList(Integer.valueOf(2));
				box3 = boxHash.getList(Integer.valueOf(1));
				break;
			case 3:
				box1 = boxHash.getList(Integer.valueOf(2));
				box2 = boxHash.getList(Integer.valueOf(1));
				box3 = boxHash.getList(Integer.valueOf(3));
				break;
			case 4:
				box1 = boxHash.getList(Integer.valueOf(1));
				box2 = boxHash.getList(Integer.valueOf(2));
				box3 = boxHash.getList(Integer.valueOf(3));
				break;
			case 5:
				box1 = boxHash.getList(Integer.valueOf(3));
				box2 = boxHash.getList(Integer.valueOf(1));
				box3 = boxHash.getList(Integer.valueOf(2));
				break;
			case 6:
				box1 = boxHash.getList(Integer.valueOf(2));
				box2 = boxHash.getList(Integer.valueOf(3));
				box3 = boxHash.getList(Integer.valueOf(1));
				break;
		}
		boxHash.clear();
		if (box1!=null) {
			boxHash.putList(Integer.valueOf(1),box1);
			repositionToBox(box1,1);
		}
		if (box2!=null) {
			boxHash.putList(Integer.valueOf(2),box2);
			repositionToBox(box2,2);
		}
		if (box3!=null) {
			boxHash.putList(Integer.valueOf(3),box3);
			repositionToBox(box3,3);
		}
	}
	private static void repositionToBox(ArrayList<RealmComponent> list,int box) {
		for (RealmComponent rc : list) {
			CombatWrapper combat = new CombatWrapper(rc.getGameObject());
			combat.setCombatBox(box);
		}
	}
	private static void changeTactics(String prefix,CombatWrapper combatTarget,HashLists<Integer, RealmComponent> boxHash) {
		DieRoller roller = new DieRoller(); // Rule 22.5/3 specifies that modifiers do NOT affect this roll
		roller.addRedDie();
		roller.addWhiteDie();
		
		changeTactics(prefix,combatTarget,boxHash,roller,1);
		changeTactics(prefix,combatTarget,boxHash,roller,2);
		changeTactics(prefix,combatTarget,boxHash,roller,3);
	}
	private static void changeTactics(String prefix,CombatWrapper combatTarget,HashLists<Integer, RealmComponent> boxHash,DieRoller roller,int boxNumber) {
		roller.reset();
		roller.rollDice("Change Tactics");
		if (SKIP_REPOSITIONING || DebugUtility.isMonsterLock()) {
			roller.setValue(0,2);
			roller.setValue(1,2);
		}
		if (FORCE_MONSTER_FLIP || DebugUtility.isMonsterFlip()) {
			roller.setValue(0,6);
		}
		int result = roller.getHighDieResult();
		ArrayList<RealmComponent> list = boxHash.getList(Integer.valueOf(boxNumber));
		if (list!=null) {
			// Make sure there is at least ONE chit to flip
			boolean isOne = false;
			for (RealmComponent rc : list) {
				ChitComponent chit = (ChitComponent)rc; // they should ALL be chits
				if (canChangeTactics(chit)) {
					isOne = true;
					break;
				}
			}
			if (isOne) {
				combatTarget.setChangeTacticsResult(prefix,boxNumber,roller.getStringResult());
				if (result>=5) {
					changeTacticsOn(list,result,boxNumber);
				}
			}
		}
	}
	private static boolean canChangeTactics(ChitComponent chit) {
		boolean flip = true;
		if (chit.isMonster()) {
			MonsterChitComponent monster = (MonsterChitComponent)chit;
			if (monster.canPinOpponent()) {
				// Tremendous monsters don't change tactics
				flip = false;
			}
		}
		else if (chit.isNativeHorse()) {
			// Native horses never change tactics
			flip = false;
		}
		return flip;
	}
	private static void changeTacticsOn(ArrayList<RealmComponent> list,int result,int boxNumber) {
		if (list!=null) {
			boolean reportedChange = false;
			for (RealmComponent rc : list) {
				ChitComponent chit = (ChitComponent)rc; // they should ALL be chits
				if (canChangeTactics(chit)) {
					if (result==6 || chit.getGameObject().hasThisAttribute("sensitive_tactics")) {
						if (!reportedChange) {
							RealmLogging.logMessage(RealmLogging.BATTLE,"Change Tactics in Box "+boxNumber);
							reportedChange = true;
						}
						chit.flip();
						if (chit instanceof BattleChit) {
							RealmLogging.logMessage(chit.getGameObject().getNameWithNumber(),"Changes tactics:  "+getCombatantInformation((BattleChit)chit,true));
						}
					}
				}
			}
		}
	}
	/**
	 * This has to happen separate from disengagement, so that the fatigue/wound step isn't confusing.
	 */
	public void doExpireWishStrength() {
		ArrayList<RealmComponent> all = getAllBattleParticipants(true);
		for (RealmComponent rc:all) {
			// Determine if character has wishStrength, and hit with a physical attack
			if (rc.isCharacter()) {
				CombatWrapper combat = new CombatWrapper(rc.getGameObject());
				CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
				if (character.getWishStrength()!=null) {
					// Character has a "Wish for Strength" result applied
					Collection<RealmComponent> list = character.getActiveFightChitsAsRealmComponents();
					// Add any gloves cards
					for (GameObject go: character.getActiveInventory()) {
						RealmComponent item = RealmComponent.getRealmComponent(go);
						if (go.hasThisAttribute("gloves")) {
							list.add(item);
						}
					}
					if (combat.getHitResult()!=null && !BattleUtility.findFightComponentsWithCombatBox(list).isEmpty()) {
						// Character hit a target this round with a physical attack (chit or gloves)
						character.clearWishStrength();
					}
				}
			}
		}
	}
	/**
	 * Denizens that remain assigned
	 * <li>Red-side up tremendous monsters
	 * <li>Denizens assigned to characters
	 * All others are unassigned.
	 */
	public void doDisengagement() {
		logBattleInfo("---- DISENGAGEMENT ----");
		
		// Flip all native horses to light side
		for (BattleGroup bg : getAllBattleGroups(true)) {
			bg.allHorsesWalk();
		}
		
		// Need to get rid of dead participants here - wounds should have been handled already?  fatigued effort?
		ArrayList<SpellWrapper> attackSpellsToExpire = new ArrayList<>();
		ArrayList<RealmComponent> rcsToMakeDead = new ArrayList<>();
		ArrayList<RealmComponent> all = getAllBattleParticipants(true);
		for (RealmComponent rc:all) {
			boolean disengage=true;
			boolean disengage1 = true;
			boolean disengage2 = true;
			CombatWrapper combat = new CombatWrapper(rc.getGameObject());
			
			// Adjust weapon alertness
			BattleChit battle = (BattleChit)rc;
			battle.changeWeaponState();
			
			// Get rid of destroyed stuff and fatigue magic
			if (rc.isCharacter()) {
				CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
				
				for (CharacterActionChitComponent chit:character.getAllChits()) {
					if (chit.isMagicMove()) {
						chit.setMagicMove(false);
					}
				}
				
				if (character.isFortDamaged() && !character.isFortified()) { // destroyed fort
					// completely remove fort
					character.setFortified(false);
					character.setFortDamaged(false);
				}
				
				for (GameObject thing : character.getActiveInventory()) {
					CombatWrapper thingCombat = new CombatWrapper(thing);
					if (thingCombat.getKilledBy()!=null) { // A dead thing means destroyed
						rc.getGameObject().remove(thing);
						CombatWrapper.clearAllCombatInfo(thing);
						TreasureUtility.handleDestroyedItem(character,thing); // armor
					}
	
					if (thing.hasThisAttribute("oneshot") && thing.hasThisAttribute("potion")) {
						character.expirePotion(thing);
					}
					
					if (thingCombat.wasThrown() && battleLocation.isInClearing()) {
						thing.removeThisAttribute(Constants.ACTIVATED);
						RealmComponent thingRc = RealmComponent.getRealmComponent(thing);
						if (thingRc instanceof ChitComponent) {
							thingRc.setActivated(false);
							if (thingRc.isWeapon()) {
								((WeaponChitComponent)thingRc).setAlerted(false);
							}
						}
						CombatWrapper.clearAllCombatInfo(thing);
						battleLocation.clearing.add(thing, character);
					}
				}
				
				// Cue up attack spells to expire (don't expire until AFTER Grudges are determined)
				for (SpellWrapper spell : character.getAliveSpells()) {
					// Attack spells are over at the end of a round of combat
					if (spell.isAttackSpell()) {
						attackSpellsToExpire.add(spell);
					}
				}
				
				// Expire active phase chits
				character.endActivePhaseChits();
				
				// Check for COMBAT_HIDE
				if (character.affectedByKey(Constants.COMBAT_HIDE) && !character.isHidden() && combat.getAttackerCount()==0) {
					if (!character.hasCurse(Constants.SQUEAK)) {
						RealmCalendar cal = RealmCalendar.getCalendar(gameData);
						boolean canHide = !cal.isHideDisabled(character.getCurrentMonth());
						if (!canHide && hostPrefs.hasPref(Constants.HOUSE3_SNOW_HIDE_EXCLUDE_CAVES) && battleLocation.isInClearing() && battleLocation.clearing.isCave()) {
							canHide = true;
						}
						
						if (canHide) {
							DieRoller roller = DieRollBuilder.getDieRollBuilder(null,character).createHideRoller();
							CombatFrame.broadcastMessage(character.getGameObject().getName(),"World Fades: "+roller.getDescription());
							if (roller.getHighDieResult()<6) {
								character.setHidden(true);
								CombatFrame.broadcastMessage(character.getGameObject().getName(),"World Fades: Hides successfully!");
							}
						}
						else {
							CombatFrame.broadcastMessage(character.getGameObject().getName(),"World Fades: Cannot HIDE due to inclement weather.");
						}
					}
					else {
						CombatFrame.broadcastMessage(character.getGameObject().getName(),"World Fades: Cannot HIDE due to SQUEAK curse.");
					}
				}
			}
			
			// Test for deadness :)

			if (rc.isNative()) {
				// Handle dead native horses separately
				RealmComponent horse = (RealmComponent)rc.getHorse();
				if (horse!=null) {
					CombatWrapper horseCombat = new CombatWrapper(horse.getGameObject());
					if (horseCombat.getKilledBy()!=null) {
						horse.getGameObject().setThisAttribute(Constants.DEAD);
					}
				}
			}
			if (combat.getKilledBy()!=null) {
				// Dead - remove from wherever, and deal with whatever inventory
				logBattleInfo(rc+" is dead.  Killed by "+combat.getKilledBy().getNameWithNumber());
				
				// Test for Grudges/Gratitudes
				if (rc.isNative() && rc.getOwner()==null && hostPrefs.hasPref(Constants.OPT_GRUDGES)) {
					RealmComponent killer = RealmComponent.getRealmComponent(combat.getKilledBy());
					if (killer.isSpell()) {
						SpellWrapper spell = new SpellWrapper(killer.getGameObject());
						if (!"clearing".equals(spell.getGameObject().getThisAttribute("target"))) {
							CharacterWrapper caster = spell.getCaster();
							killer = RealmComponent.getRealmComponent(caster.getGameObject());
						}
						else {
							// If targeting the clearing, then grudges does NOT apply!  (the natives don't know what hit them)
							killer = null;
						}
					}
					if (killer.isMonsterPart()) { // we care about this, in case the character is an absorbed monster with a club/head/axe
						GameObject monster = killer.getGameObject().getHeldBy();
						if (monster!=null) { // should NEVER be null
							GameObject spellTest = monster.getHeldBy();
							if (spellTest!=null && spellTest.hasThisAttribute("spell")) { // Monster is "contained" by a spell, so MUST be absorbed
								SpellWrapper spell = new SpellWrapper(spellTest);
								CharacterWrapper caster = spell.getCaster();
								if (caster!=null) {
									// At long last, the REAL killer
									killer = RealmComponent.getRealmComponent(caster.getGameObject());
								}
							}
						}
					}
					if (killer!=null && (killer.isCharacter() || killer.getOwnerId()!=null)) {
						CharacterWrapper responsibleCharacter;
						if (killer.isCharacter()) {
							responsibleCharacter = new CharacterWrapper(killer.getGameObject());
						}
						else {
							responsibleCharacter = new CharacterWrapper(killer.getOwner().getGameObject());
						}
						int rel = responsibleCharacter.getRelationship(rc.getGameObject());
						if (rel==RelationshipType.FRIENDLY) {
							doGrudge(killer,responsibleCharacter,rc,2,"GRUDGES");
						}
						else if (rel>=RelationshipType.ALLY) {
							doGrudge(killer,responsibleCharacter,rc,4,"GRUDGES");
						}
						else if (hostPrefs.hasPref(Constants.TE_EXTENDED_GRUDGES)) {
							// but only once per character per native group per evening!   ...but how...
							if (!responsibleCharacter.hasChangedRelationshipToday(rc.getGameObject())) {
								doGrudge(killer,responsibleCharacter,rc,1,"EXTENDED GRUDGES");
							}
						}
					}
				}
				
				rcsToMakeDead.add(rc);
			}
			else {
				// Check first for Hurricane Winds
				String blownSpellId = rc.getGameObject().getThisAttribute(Constants.BLOWS_TARGET);
				if (blownSpellId!=null) {
					// Yep, getting blown away here
					
					// Flip to light side
					RealmUtility.normalizeParticipant(rc);
					
					// Move to target tile
					GameObject go = gameData.getGameObject(Long.valueOf(blownSpellId));
					SpellWrapper spell = new SpellWrapper(go);
					TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(spell.getSecondaryTarget());
					TileLocation tl = new TileLocation(tile,true);
					ClearingUtility.moveToLocation(rc.getGameObject(),tl);
					
					// Clear all attackers
					for (GameObject attacker:combat.getAttackers()) {
						RealmComponent arc = RealmComponent.getRealmComponent(attacker);
						arc.clearTargets();
						combat.removeAttacker(attacker);
					}
					
					// Expire the wind spell if rc is a player controlled critter
					if (rc.isAnyLeader()) {
						spell.expireSpell();
						rc.getGameObject().setThisAttribute(Constants.LAND_FIRST); // Forces a landing at the beginning of next turn
					}
					disengage = true;
					logBattleInfo(rc+" is blown away to "+tile.getGameObject().getNameWithNumber()+"!");
				}
				else {
					RealmComponent target1 = rc.getTarget();
					RealmComponent target2 = rc.get2ndTarget();
					CombatWrapper targetCombat1 = target1==null?null:new CombatWrapper(target1.getGameObject());
					CombatWrapper targetCombat2 = target2==null?null:new CombatWrapper(target2.getGameObject());
					
					// Determine disengagement rules
					if (rc.isMonster()) {
						if (!testRedDeadDisengage((MonsterChitComponent)rc,combat,targetCombat1)) {
							disengage = false;
						}
					}
					else if (rc.isCharacter()) {
						MonsterChitComponent transmorph = ((CharacterChitComponent)rc).getTransmorphedComponent();
						if (transmorph!=null) {
							if (!testRedDeadDisengage(transmorph,combat,targetCombat1)) {
								disengage = false;
							}
						}
					}
					if (target1!=null) {
						// If the target is a character, and the attacker is a denizen, then don't disengage
						if (target1.isCharacter() && rc.getOwner()==null) {
							logBattleInfo(rc+" won't disengage when fighting a character");
							disengage1 = false; // don't disengage when fighting a character
						}
						
						if (!rc.isCharacter() && target1.isMonster()) {
							MonsterChitComponent monster = (MonsterChitComponent)target1;
							if (monster.isPinningOpponent()) {
								RealmComponent monsterTarget = monster.getTarget();
								if (monsterTarget.equals(rc)) {
									logBattleInfo(rc+" can't disengage when held by a Red-side-up Tremendous monster");
									disengage1 = false;
								}
							}
						}
						
						// If the target is dead (regardless of who), always disengage
						if (targetCombat1.getKilledBy()!=null) { 
							logBattleInfo(rc+" disengages because target is dead");
							disengage1 = true; // always disengage if target is dead
						}
					}
					if (target2!=null) {
						// If the target is a character, and the attacker is a denizen, then don't disengage
						if (target2.isCharacter() && rc.getOwner()==null) {
							logBattleInfo(rc+" won't disengage when fighting a character");
							disengage2 = false; // don't disengage when fighting a character
						}
						
						if (!rc.isCharacter() && target2.isMonster()) {
							MonsterChitComponent monster = (MonsterChitComponent)target2;
							if (monster.isPinningOpponent()) {
								RealmComponent monsterTarget = monster.getTarget();
								if (monsterTarget.equals(rc)) {
									logBattleInfo(rc+" can't disengage when held by a Red-side-up Tremendous monster");
									disengage2 = false;
								}
							}
						}
						
						// If the target is dead (regardless of who), always disengage
						if (targetCombat2.getKilledBy()!=null) { 
							logBattleInfo(rc+" disengages because target is dead");
							disengage2 = true; // always disengage if target is dead
						}
					}
				}
			}
				
			if (disengage && disengage1) {
				rc.clearTarget();
			}
			if (disengage && disengage2) {
				rc.clear2ndTarget();
			}
		}
		
		// Now clean up
		for (RealmComponent rc:rcsToMakeDead) {
			RealmUtility.makeDead(rc);
		}
		for (SpellWrapper spell : attackSpellsToExpire) {
			spell.expireSpell();
		}
		for (RealmComponent rc:all) {
			CombatWrapper rcc = new CombatWrapper(rc.getGameObject());
			boolean dead = rcc.getKilledBy()!=null;
			
			// Expire "move" spells on all played chits (except fly chits - handled below)
			CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
			boolean expiredOne = false;
			for (CharacterActionChitComponent chit:BattleUtility.getPlayedChits(character)) {
				if (chit.expireMoveSpells()) {
					expiredOne = true;
				}
			}
			if (expiredOne) {
				character.updateChitEffects();
			}

			// Clear out all round combat info (include all held stuff)
			ArrayList<GameObject> removeList = new ArrayList<>();
			ArrayList<GameObject> hold = new ArrayList<>(rc.getGameObject().getHold()); // to prevent concurrent mod issues when flychits are expired
			for (GameObject held : hold) {
				CombatWrapper combat = new CombatWrapper(held);
				
				// If a "held" item is a horse, and its dead, then remove it
				RealmComponent goc = RealmComponent.getRealmComponent(held);
				if (goc.isHorse()) {
					if (combat.getKilledBy()!=null) {
						removeList.add(held);
					}
				}
				if (Fly.valid(goc) && combat.getPlacedAsMove()) {
					Fly fly = new Fly(goc);
					fly.useFly();
				}
				CombatWrapper.clearRoundCombatInfo(held);
				
				if (held.hasThisAttribute(Quest.QUEST_MINOR_CHARS)) {
					for (GameObject bonusChit : held.getHold()) {
						CombatWrapper.clearRoundCombatInfo(bonusChit);
					}
				}
			}
			
			if (rc.isCharacter()) {
				// Expire any spells on fly chits (special case) - is this really necessary?  It seems like this happens above with fly.useFly()...
				for (GameObject go : rcc.getUsedChits()) {
					RealmComponent goc = RealmComponent.getRealmComponent(go);
					if (goc.isFlyChit()) {
						FlyChitComponent flyChit = (FlyChitComponent)goc;
						flyChit.expireSourceSpell();
					}
				}
				
				// Don't forget to clear out any transmorphed monsters
				MonsterChitComponent transmorph = ((CharacterChitComponent)rc).getTransmorphedComponent();
				if (transmorph!=null) {
					CombatWrapper.clearRoundCombatInfo(transmorph.getGameObject());
					if (dead) {
						CombatWrapper.clearAllCombatInfo(transmorph.getGameObject());
					}
					
					// and their weapons (if any)
					MonsterPartChitComponent weapon = transmorph.getWeapon();
					if (weapon!=null) {
						CombatWrapper.clearRoundCombatInfo(weapon.getGameObject());
						if (dead) {
							CombatWrapper.clearAllCombatInfo(weapon.getGameObject());
						}
					}
				}
			}
			
			CombatWrapper.clearRoundCombatInfo(rc.getGameObject());
			
			// Remove any dead horses...
			for (GameObject held : removeList) {
				rc.getGameObject().remove(held);
				CombatWrapper.clearAllCombatInfo(held);
			}
			
			
			if (dead) {
				// Dead?  Then clear out everything!!
				CombatWrapper.clearAllCombatInfo(rc.getGameObject());
			}
		}
		// Reestablish attacker state for all attackers!
		for (RealmComponent rc:all) {
			RealmComponent target = rc.getTarget();
			if (target!=null) {
				CombatWrapper combat = new CombatWrapper(target.getGameObject());
				combat.addAttacker(rc.getGameObject());
			}
			RealmComponent target2 = rc.get2ndTarget();
			if (target2!=null) {
				CombatWrapper combat = new CombatWrapper(target2.getGameObject());
				combat.addAttacker(rc.getGameObject());
			}
		}
		// Reestablish attacker state for RED-side up transmorphed monsters
//		for (Iterator i=all.iterator();i.hasNext();) {
//			RealmComponent rc = (RealmComponent)i.next();
//			if (rc.isCharacter()) {
//				MonsterChitComponent monster = ((CharacterChitComponent)rc).getTransmorphedComponent();
//				if (monster!=null && monster.isRedSideUp()) {
//					RealmComponent sameTarget = monster.getTarget(); // sameTarget BETTER be not null!
//					// sameTarget WAS null when I absorbed a RED-side up T Monster!
//					CombatWrapper combat = new CombatWrapper(sameTarget.getGameObject());
//					combat.addAttacker(rc.getGameObject());
//				}
//			}
//		}
	}
	private static void doGrudge(RealmComponent killer,CharacterWrapper responsibleCharacter,RealmComponent rc,int penalty,String ruleName) {
		String currentRelString = RealmUtility.getRelationshipNameFor(responsibleCharacter,rc);
		responsibleCharacter.changeRelationship(rc.getGameObject(),-penalty);
		String newRelString = RealmUtility.getRelationshipNameFor(responsibleCharacter,rc);
		
		logBattleInfo(killer.getGameObject().getNameWithNumber()+" killed "+currentRelString+" "+rc.toString()+".");
		logBattleInfo(responsibleCharacter.getCharacterName()+" relationship is harmed by "+penalty+"! ("+ruleName+") --> "+newRelString);
	}
	/**
	 * @return		The appropriate DieRoller for this BattleGroup
	 */
	public DieRoller createClearingRoller(int dice,String reason) {
		// Create a roller for the clearing
		DieRoller roller = new DieRoller();
//		roller.setModifier(RealmUtility.getClearingDieMod(battleLocation)); // I'm taking this out, because only characters are affected by Cloven Hoof
		roller.addRedDie();
		if (dice==2) {
			roller.addWhiteDie();
		}
		roller.rollDice(reason);
		return roller;
	}
	public TileLocation getBattleLocation() {
		return battleLocation;
	}
	public void setBattleLocation(TileLocation battleLocation) {
		this.battleLocation = battleLocation;
	}
	public ArrayList<RealmComponent> getAttackersFor(RealmComponent rc) {
		return getAttackersFor(rc,true,true);
	}
	public ArrayList<RealmComponent> getAttackersFor(RealmComponent rc,boolean includeCharacters,boolean includeWeapons) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		for (RealmComponent bp : getAllBattleParticipants(true)) {
			if (includeCharacters || !bp.isCharacter()) {
				if ((bp.getTarget()!=null && rc.equals(bp.getTarget())) || (bp.get2ndTarget()!=null && rc.equals(bp.get2ndTarget()))) {
					list.add(bp);
					if (includeWeapons && bp instanceof MonsterChitComponent) {
						MonsterChitComponent monster = (MonsterChitComponent)bp;
						if (monster.getWeapon()!=null) {
							list.add(monster.getWeapon());
						}
					}
				}
			}
		}
		return list;
	}
	/**
	 * @param actionState		The actionState being tested (doesn't matter if WAIT is attached)
	 * 
	 * @return				true if combat can be ended at this point
	 */
	public boolean canSkipCombat(int actionState) {
		boolean ret = false;
		
		if (actionState>Constants.COMBAT_WAIT) {
			actionState -= Constants.COMBAT_WAIT;
		}
		
		if (actionState==Constants.COMBAT_LURE) {
			// Cannot skip combat if you are in the LURE phase, and there is at least one unhidden
			// participant!  (Being in the LURE phase already guarantees that there are unassigned
			// denizens)
			ret = !denizensAreBattling();
		}
		if (!ret && actionState<=Constants.COMBAT_ACTIONS) {
			// Check to see if denizens are battling anybody - if not, allow pressing skip
			ret = !denizensAreBattling() && !somebodyIsTargeted();
			logger.finest("Setting ret="+ret+" because !denizensAreBattling() && !somebodyIsTargeted()");
		}
		
		// If anyone is affected by Hurricane Winds, cannot skip combat
		if (somebodyIsBlowing() || somebodyIsCharging()) {
			ret = false;
		}
		
		logger.finer("Can"+(ret?"":"not")+" skip combat.");
		return ret;
	}
	private boolean somebodyIsCharging() {
		for (CharacterChitComponent chit:getAllParticipatingCharacters()) {
			CombatWrapper character = new CombatWrapper(chit.getGameObject());
			if (character.getHasCharged()) return true;
		}
		return false;
	}
	/**
	 * @return		true if at least one RealmComponent in the clearing is being affected by Hurricane Winds
	 */
	public boolean somebodyIsBlowing() {
		return !getAllBlowees().isEmpty();
	}
	/**
	 * @return		A list of all RealmComponents in the clearing that are currently being affected
	 * 				by Hurricane Winds
	 */
	public ArrayList<RealmComponent> getAllBlowees() {
		ArrayList<RealmComponent> list = new ArrayList<>();
		for (RealmComponent rc : getAllBattleParticipants(true)) {
			if (rc.getGameObject().hasThisAttribute(Constants.BLOWS_TARGET)) {
				list.add(rc);
			}
		}
		return list;
	}
	public boolean somebodyIsTargeted() {
		boolean ret = false;
		for (RealmComponent rc : getAllBattleParticipants(true)) {
			logger.finest(rc.getGameObject().getName()+"...");
			CombatWrapper rcCombat = new CombatWrapper(rc.getGameObject());
			if (rc.getTarget()!=null || rc.get2ndTarget()!=null) {
				ret = true;
				logger.finest("is targeting someone");
				break;
			}
			GameObject spell = rcCombat.getCastSpell();
			if (spell!=null) {
				SpellWrapper sw = new SpellWrapper(spell);
				if (sw.isAttackSpell()) {
					// Somebody HAS to be targeted, if there is an attack spell
					ret = true;
					logger.finest("is targeting someone with a spell");
					break;
				}
			}
			logger.finest("is NOT targeting someone");
		}
		return ret;
	}

	private boolean testRedDeadDisengage(MonsterChitComponent monster,CombatWrapper combat,CombatWrapper targetCombat) {
		if (monster.canPinOpponent()) {
			logBattleInfo(monster+" is a Tremendous monster");
			logBattleInfo(monster+" hit result: "+combat.getHitResult());
			if (monster.isPinningOpponent() && targetCombat!=null && targetCombat.getKilledBy()!=null) {
				// Red-side up monsters should flip back when their target is dead!
				monster.flip();
				logBattleInfo(monster+" flips back to light side.");
			}
			else if (!monster.isPinningOpponent() && combat.getHitResult()!=null && targetCombat.getKilledBy()==null) {
				/* Tremendous Monsters flip to red when they hit, but only if they:
				 * 		- aren't already red side up
				 * 		- hit their target somehow
				 * 		- didn't already KILL their target
				 */
				monster.flip();
				logBattleInfo(monster+" flips RED side up.");
			}
			
			// Also, test the monster weapon, if any
			MonsterPartChitComponent weapon = monster.getWeapon();
			if (weapon!=null) {
				logBattleInfo(monster+" has a "+weapon);
				CombatWrapper weaponCombat = new CombatWrapper(weapon.getGameObject());
				logBattleInfo(weapon+" hit result is "+weaponCombat.getHitResult());
				if (!monster.isPinningOpponent() && weaponCombat.getHitResult()!=null && targetCombat.getKilledBy()==null) {
					if (hostPrefs.hasPref(Constants.HOUSE2_MONSTER_WEAPON_NOFLIP)) {
						logBattleInfo(monster+" does NOT flip RED side up with weapon hit, because house rule is in play..");
					}
					else {
						// Target not dead, then monster goes RED (hey, that rhymes!)
						monster.flip();
						logBattleInfo(monster+" flips RED side up because of weapon hit.");
					}
				}
			}
			
			if (monster.isPinningOpponent()) {
				
				RealmComponent targetRc = RealmComponent.getRealmComponent(targetCombat.getGameObject());
				if (targetRc.isCharacter()) {
					CharacterWrapper character = new CharacterWrapper(targetCombat.getGameObject());
					if (character.hasActiveInventoryThisKey(Constants.NOPIN)) {
						// Don't allow RED when NOPIN is active
						monster.flip();
						logBattleInfo(character.getGameObject().getName()+" cannot be pinned!  Monster flips back to light side.");
						return true;
					}
				}
				
				logBattleInfo(monster+" doesn't disengage when red side up");
				return false; // never disengage when red side up
			}
		}
		return true;
	}
	/**
	 * Disengage ALL targets
	 */
	public void makePeace() {
		for (RealmComponent rc : getAllBattleParticipants(true)) {
			rc.clearTargets();
		}
	}
	
	public static ArrayList<RealmComponent> getParticipantsFromGroups(Collection<BattleGroup> battleGroups) {
		ArrayList<RealmComponent> participants = new ArrayList<>();
		for (BattleGroup bg : battleGroups) {
			participants.addAll(bg.getBattleParticipants());
		}
		return participants;
	}
	
	public boolean wasSpellCasting() {
		return spellCasting;
	}
}