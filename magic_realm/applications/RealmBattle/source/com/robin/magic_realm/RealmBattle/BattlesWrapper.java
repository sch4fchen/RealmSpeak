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

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GameObjectWrapper;
import com.robin.game.server.GameClient;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class BattlesWrapper extends GameObjectWrapper {
	
	private static final String CURRENT_BATTLE_LOCATION = "current_bl";
	private static final String BATTLE_LOCATION = "battle_locations";
	private static final int STARTING_WAIT_STATE = Constants.COMBAT_WAIT + Constants.COMBAT_PREBATTLE;
	
	public BattlesWrapper(GameObject go) {
		super(go);
	}
	public String getBlockName() {
		return "_BATTLES_";
	}
	/**
	 * Resets all battles - do this before initBattles
	 */
	public void clearBattles() {
		getGameObject().removeAttributeBlock(getBlockName());
	}
	/**
	 * Adds a battle location.  The order that locations are added is the order of battle.
	 */
	public void addBattleLocation(TileLocation tl,GameData data) {
		addListItem(BATTLE_LOCATION,tl.asKey());
		
		// Bump combat count for each owning character
		BattleModel model = RealmBattle.buildBattleModel(tl,data);
		for (RealmComponent chars : model.getAllOwningCharacters()) {
			CharacterWrapper character = new CharacterWrapper(chars.getGameObject());
			character.setCombatCount(character.getCombatCount()+1);
		}
	}
	/**
	 * Inits a clearing with battle setup:
	 * 	- All owning characters get a starting wait state
	 * 	- Fffft?
	 * 
	 * @return		true if a new clearing was initialized for battle
	 */
	public boolean initNextBattleLocation(GameData data) {
		// First, make sure current clearing is dealt with
		TileLocation current = getCurrentBattleLocation(data);
		if (current!=null) {
			clearBattleInfo(current,data);
		}
		
		ArrayList<String> list = new ArrayList<>(getList(BATTLE_LOCATION));
		if (!list.isEmpty()) {
			String tlKey = list.remove(0);
			setList(BATTLE_LOCATION,list); // make sure the list is updated
			setString(CURRENT_BATTLE_LOCATION,tlKey);
			TileLocation tl = TileLocation.parseTileLocation(data,tlKey);
			BattleModel model = RealmBattle.buildBattleModel(tl,data);
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(data);
			
			ArrayList<RealmComponent> combatants = tl.clearing.getClearingComponents();
			for (RealmComponent monster : combatants) {
				if (monster.isCharacter()) continue;
				ArrayList<RealmComponent> characterCanControl = new ArrayList<>();
				for (RealmComponent characterRc : combatants) {
					if (!characterRc.isCharacter()) continue;
						for (Object monsterType : characterRc.getControllableMonsters() ) {
							if (monster.toString().matches(monsterType.toString()+".*")) {
								if (!characterCanControl.contains(characterRc)) {
									characterCanControl.add(characterRc);
								}
							}
						}

				}
				if (characterCanControl.toArray().length == 1) { // only if exactly one character can control this monster
					CharacterWrapper characterWrapper = new CharacterWrapper(characterCanControl.get(0).getGameObject());
					int duration = characterCanControl.get(0).getControllableMonstersDuration();
					RealmComponent monsterOwner = monster.getOwner();
					
					if(monsterOwner!=null && monsterOwner.isCharacter() && monsterOwner.getGameObject() == characterWrapper.getGameObject()) {
						monster.setTermOfHire(duration);
					}
					else {
						if(monsterOwner!=null && monsterOwner.isCharacter()) {
							CharacterWrapper owner = new CharacterWrapper(monsterOwner.getGameObject());
							owner.removeHireling(monster.getGameObject());
						}
						characterWrapper.addHireling(monster.getGameObject(), duration);
					}
				}
			}
			
			if (GameClient.GetMostRecentClient()!=null) {
				GameClient.GetMostRecentClient().broadcast(RealmLogging.BATTLE,"Battle resolving at "+tl+":");
				int count = 1;
				for (BattleGroup group : model.getAllBattleGroups(true)) {
					GameClient.GetMostRecentClient().broadcast(RealmLogging.BATTLE,"GROUP "+(count++));
					RealmComponent owner = group.getOwningCharacter();
					for (RealmComponent rc : group.getBattleParticipants()) {
						String message = rc.getGameObject().getName();
						if (owner!=null && owner!=rc) {
							message = message+" ("+owner.getGameObject().getName()+")";
						}
						GameClient.GetMostRecentClient().broadcast(RealmLogging.BATTLE,"  "+message);
						
						// Make sure everyone is starting out light-side up, and with NO predefined targets!
						if (rc.isChit() && !rc.isCharacter()) {
							ChitComponent chit = (ChitComponent)rc;
							chit.setLightSideUp();

							if ((hostPrefs.hasPref(Constants.OPT_ALERTED_MONSTERS) || hostPrefs.hasPref(Constants.OPT_ALERTED_MONSTERS_VARIANT)) && !rc.isHiredOrControlled() && !rc.isCompanion()) {
								Strength vulnerability = null;
								if (rc.isMonster()) {
									vulnerability = ((MonsterChitComponent)rc).getVulnerability();
								}
								else if (rc.isNative()) {
									vulnerability = ((NativeChitComponent)rc).getVulnerability();
								}
								if (vulnerability != null && vulnerability.weakerOrEqualTo(Strength.valueOf("H"))) {
									if (hostPrefs.hasPref(Constants.OPT_ALERTED_MONSTERS) && hasUnhiddenCharactersOrControlledDenizen(combatants)) {
										chit.setDarkSideUp();
									}
									if(hostPrefs.hasPref(Constants.OPT_ALERTED_MONSTERS_VARIANT)) {
										int dieRoll1 = RandomNumber.getDieRoll(6);
										int dieRoll2 = RandomNumber.getDieRoll(6);
										if (dieRoll1 == 6 || dieRoll2 == 6) {
											chit.setDarkSideUp();
										}
									}
								}
							}
						}
						
						rc.clearTargets();
						
						// Flip monster weapons light side up
						if (rc.isMonster()) {
							MonsterChitComponent monster = (MonsterChitComponent)rc;
							MonsterPartChitComponent weapon = monster.getWeapon();
							if (weapon!=null && weapon.isDarkSideUp()) {
								weapon.setLightSideUp();
							}
							
							if ((hostPrefs.hasPref(Constants.OPT_ALERTED_MONSTERS) || hostPrefs.hasPref(Constants.OPT_ALERTED_MONSTERS_VARIANT))
									&& weapon!=null && monster.getVulnerability().weakerOrEqualTo(Strength.valueOf("H")) && !rc.isHiredOrControlled() && !rc.isCompanion()) {
								if (hostPrefs.hasPref(Constants.OPT_ALERTED_MONSTERS) && hasUnhiddenCharactersOrControlledDenizen(combatants)) {
									weapon.setDarkSideUp();
								}
								if(hostPrefs.hasPref(Constants.OPT_ALERTED_MONSTERS_VARIANT)) {
									int dieRoll1 = RandomNumber.getDieRoll(6);
									int dieRoll2 = RandomNumber.getDieRoll(6);
									if (dieRoll1 == 6 || dieRoll2 == 6) {
										weapon.setDarkSideUp();
									}
								}
							}
						}
					}
				}
			}
			
			for (RealmComponent owner : model.getAllOwningCharacters()) {
				// owner may or may not be present, but they still are involved, and must make battle decisions
				CharacterWrapper character = new CharacterWrapper(owner.getGameObject());
				character.setCombatStatus(STARTING_WAIT_STATE);
			}
			
			return true;
		}
		removeAttribute(CURRENT_BATTLE_LOCATION);
		return false;
	}
	public void clearBattleInfo(TileLocation tl,GameData data) {
		BattleModel model = RealmBattle.buildBattleModel(tl,data);
		for (RealmComponent owner : model.getAllOwningCharacters()) {
			CharacterWrapper character = new CharacterWrapper(owner.getGameObject());
			CombatWrapper.clearAllCombatInfo(character.getGameObject());
			character.clearCombat();
			character.decrementCombatCount();
		}
	}
	private static boolean hasUnhiddenCharactersOrControlledDenizen(ArrayList<RealmComponent> combatants) {
		for (RealmComponent combatant : combatants) {
			if(combatant.isCharacter() && combatant.isHidden() == false) {
				return true;
			}
			if((combatant.isHiredOrControlled() || combatant.isCompanion()) && combatant.isHidden() == false) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Returns the current battle location
	 */
	public TileLocation getCurrentBattleLocation(GameData data) {
		String tlKey = getString(CURRENT_BATTLE_LOCATION);
		if (tlKey!=null) {
			return TileLocation.parseTileLocation(data,tlKey);
		}
		return null;
	}
}