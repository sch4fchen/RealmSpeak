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
package com.robin.magic_realm.components.quest.requirement;

import java.util.Hashtable;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

/**
 * Quest requirements determine what must happen before the step can be
 * completed, and rewards (if any) are given.
 */
public abstract class QuestRequirement extends AbstractQuestObject {

	public enum RequirementType {
		Action,
		Active, // active or inactive
		Attribute,
		CastMultipleSpells,
		CastSpell,
		CharacterClass,
		ColorMagic,
		Counter,
		Discovery, // must have a specific discovery
		Enchant,
		Fighter,
		Fly,
		FoundHiddenEnemies,
		GamePhase, // end of phase, end of day, start of evening, midnight, birdsong
		Gender,
		Guild,
		Hidden,
		HideResult,
		Hirelings,
		Inventory, // a requirement that tests what you have in inventory
		Kill, 
		LearnAwaken,
		Loot, // (optional location designation)
		NextPhase,
		NoDenizens,
		MagicUser,
		MinorCharacter,
		MissionCampaign,
		Open,
		OccupyLocation,
		Path, // follows a specific path
		Probability,
		Relationship,
		SearchResult, // (optional location designation) Clues, Paths, Passages, Hidden Enemies, Discover Chit(s), Learn and Awaken, Curse!, Awaken, Counters, Treasure Cards,Perceive Spell
		TimePassed,
		Trade,
		;
		public String getDescription() {
			switch (this) {
				case Action:
					return "Tests for the character to execute a specific action.";
				case Active:
					return "Tests whether the quest is active or not (needed for Quest Cards).";
				case Attribute:
					return "Tests whether a specific attribute (fame, notoriety, gold) has reached a target number.";
				case CastMultipleSpells:
					return "Tests whether a certain number of (unique) spells have been cast.";
				case CastSpell:
					return "Tests whether a spell has just been cast.";
				case CharacterClass:
					return "Tests for the characters class.";
				case ColorMagic:
					return "Tests whether the character is in the presence of a specific color of magic (either permanent or burning a chit).";
				case Counter:
					return "Tests whether a specific counter has reached a certain value.";
				case Discovery:
					return "Tests for a specific recorded discovery.";
				case Enchant:
					return "Tests for enchant action.";
				case Fighter:
					return "Tests if the character is a fighter.";
				case Fly:
					return "Tests if the character is currently flying or has the ability to fly.";
				case FoundHiddenEnemies:
					return "Tests if the character has found hidden enemies.";		
				case GamePhase:
					return "Tests for a specific game phase (Birdsong, End-of-phase, End-of-turn, Evening).";
				case Gender:
					return "Tests for the characters gender.";
				case Guild:
					return "Tests if the character has a certain guild level.";
				case Hidden:
					return "Tests if the character is hidden.";
				case HideResult:
					return "Tests if the character succeeds a hide roll (below or equal to a specfic value).";
				case Hirelings:
					return "Tests if character owns certain hirelings.";
				case Inventory:
					return "Tests the contents of inventory.";
				case Kill:
					return "Tests for a specific kill or kills.";
				case LearnAwaken:
					return "Tests whether a spell has just been awakened and/or learned.";
				case Loot:
					return "Tests for a specific item result of looting.";
				case NextPhase:
					return "Tests for a specific length of time (in phases) passed after first time testing this requirement.";
				case NoDenizens:
					return "Tests for the absence of monster/natives in the clearing or tile.";
				case MagicUser:
					return "Tests if the character is a magic user.";
				case MinorCharacter:
					return "Tests for the presence of a minor character in the character inventory.";
				case MissionCampaign:
					return "Tests for start/stop of Mission/Campaign chits.";
				case OccupyLocation:
					return "Tests whether the character is in a specific location.";
				case Open:
					return "Tests whether the character (or anyone) opens specific location.";
				case Path:
					return "Tests whether the character has followed a specific path."; 
				case Probability:
					return "Requirement is met with a certain probability.";
				case Relationship:
					return "Tests whether the character has certain relationship with specific natives."; 
				case SearchResult:
					return "Tests for a specific search result.";
				case TimePassed:
					return "Tests for a specific length of time (in days) passed.";
				case Trade:
					return "Tests for a specific TRADE occurrence.";
			}
			return "(No Description)";
		}
	}
	
	public String toString() {
		return buildDescription();
	}
	
	public static final String AUTO_JOURNAL = "_aj";
	public static final String NOT = "_not__";

	protected abstract boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams);
	public abstract RequirementType getRequirementType();
	protected abstract String buildDescription();

	public QuestRequirement(GameObject go) {
		super(go);
	}

	public void init() {
		setName("Requirement");
		getGameObject().setThisAttribute(Quest.QUEST_REQUIREMENT, getRequirementType().toString());
	}

	/**
	 * Called when quest is first activated to allow setting up requirement at
	 * quest start.
	 */
	public void activate(CharacterWrapper character) {
		// This implementation does nothing.  Override to add this functionality.
	}

	/**
	 * Override this method if location is relevant, and handle appropriately.
	 */
	public boolean usesLocationTag(String tag) {
		return false;
	}
	
	/**
	 * Override this method if minor character is relevant, and handle appropriately.
	 */
	public boolean usesMinorCharacter(QuestMinorCharacter mc) {
		return false;
	}

	/**
	 * Override this method if counter is relevant, and handle appropriately.
	 */
	public boolean usesCounterTag(String tag) {
		return false;
	}
	
	/**
	 * Override this method if auto journal is relevant, and handle appropriately.
	 */
	public boolean usesAutoJournal() {
		return false;
	}

	public void updateIds(Hashtable<Long, GameObject> lookup) {
		// override if IDs need to be updated!
	}

	public boolean isAutoJournal() {
		return getBoolean(AUTO_JOURNAL);
	}

	public boolean isNot() {
		return getBoolean(NOT);
	}

	public String getDescription() {
		return (isNot() ? "NOT " : "") + buildDescription();
	}

	public boolean fulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		boolean result = testFulfillsRequirement(frame, character, reqParams);
		return isNot() ? !result : result;
	}

	public static QuestRequirement getRequirement(RequirementType type, GameObject go) {
		QuestRequirement requirement = null;
		switch (type) {
			case Action:
				requirement = new QuestRequirementAction(go);
				break;
			case Active:
				requirement = new QuestRequirementActive(go);
				break;
			case Attribute:
				requirement = new QuestRequirementAttribute(go);
				break;
			case CastMultipleSpells:
				requirement = new QuestRequirementCastMultipleSpells(go);
				break;
			case CastSpell:
				requirement = new QuestRequirementCastSpell(go);
				break;
			case CharacterClass:
				requirement = new QuestRequirementCharacterClass(go);
				break;
			case ColorMagic:
				requirement = new QuestRequirementColorMagic(go);
				break;
			case Counter:
				requirement = new QuestRequirementCounter(go);
				break;
			case Discovery:
				requirement = new QuestRequirementDiscovery(go);
				break;
			case Enchant:
				requirement = new QuestRequirementEnchant(go);
				break;
			case Fighter:
				requirement = new QuestRequirementFighter(go);
				break;
			case Fly:
				requirement = new QuestRequirementFly(go);
				break;
			case FoundHiddenEnemies:
				requirement = new QuestRequirementFoundHiddenEnemies(go);
				break;
			case GamePhase:
				requirement = new QuestRequirementGamePhase(go);
				break;
			case Gender:
				requirement = new QuestRequirementGender(go);
				break;
			case Guild:
				requirement = new QuestRequirementGuild(go);
				break;
			case Hidden:
				requirement = new QuestRequirementHidden(go);
				break;
			case HideResult:
				requirement = new QuestRequirementHideResult(go);
				break;
			case Hirelings:
				requirement = new QuestRequirementHirelings(go);
				break;
			case Inventory:
				requirement = new QuestRequirementInventory(go);
				break;
			case Kill:
				requirement = new QuestRequirementKill(go);
				break;
			case LearnAwaken:
				requirement = new QuestRequirementLearnAwaken(go);
				break;
			case Loot:
				requirement = new QuestRequirementLoot(go);
				break;
			case NextPhase:
				requirement = new QuestRequirementNextPhase(go);
				break;
			case NoDenizens:
				requirement = new QuestRequirementNoDenizens(go);
				break;
			case MagicUser:
				requirement = new QuestRequirementMagicUser(go);
				break;
			case MinorCharacter:
				requirement = new QuestRequirementMinorCharacter(go);
				break;
			case MissionCampaign:
				requirement = new QuestRequirementMissionCampaign(go);
				break;
			case OccupyLocation:
				requirement = new QuestRequirementLocation(go);
				break;
			case Open:
				requirement = new QuestRequirementOpen(go);
				break;
			case Path:
				requirement = new QuestRequirementPath(go);
				break;
			case Probability:
				requirement = new QuestRequirementProbability(go);
				break;
			case Relationship:
				requirement = new QuestRequirementRelationship(go);
				break;
			case SearchResult:
				requirement = new QuestRequirementSearchResult(go);
				break;
			case TimePassed:
				requirement = new QuestRequirementTimePassed(go);
				break;
			case Trade:
				requirement = new QuestRequirementTrade(go);
				break;
			default:
				throw new IllegalArgumentException("Unsupported RequirementType: " + type.toString());
		}
		return requirement;
	}
}