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
package com.robin.magic_realm.RealmQuestBuilder;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;

import com.robin.game.objects.*;
import com.robin.magic_realm.RealmQuestBuilder.QuestPropertyBlock.FieldType;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.quest.requirement.*;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmCalendar;

public class QuestRequirementEditor extends QuestBlockEditor {

	private Quest quest;
	private QuestRequirement requirement;

	public QuestRequirementEditor(JFrame parent, GameData realmSpeakData, Quest quest, QuestRequirement requirement) {
		super(parent, realmSpeakData, requirement);
		this.quest = quest;
		this.requirement = requirement;
		setLocationRelativeTo(parent);
		read();
	}

	public String getEditorTitle() {
		return "Quest Requirement: " + requirement.getRequirementType();
	}

	public boolean getCanceledEdit() {
		return canceledEdit;
	}

	protected ArrayList<QuestPropertyBlock> createPropertyBlocks() {
		ArrayList<QuestPropertyBlock> list = new ArrayList<>();
		list.add(new QuestPropertyBlock(QuestRequirement.NOT,"NOT",FieldType.Boolean));
		switch (requirement.getRequirementType()) {
			case Action:
				list.add(new QuestPropertyBlock(QuestRequirementAction.ACTION, "Action", FieldType.StringSelector, CharacterActionType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementAction.ACTION_WITHOUT_NATIVES, "Hire and trade action without natives", FieldType.Boolean));
				break;
			case Active:
				break;
			case Attribute:
				list.add(new QuestPropertyBlock(QuestRequirementAttribute.ATTRIBUTE_TYPE, "Which attribute to measure", FieldType.StringSelector, AttributeType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementAttribute.TARGET_VALUE_TYPE, "Only count points gained during the", FieldType.StringSelector, TargetValueType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementAttribute.VALUE, "How much should it be", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRequirementAttribute.REGEX_FILTER, "Filter points to what things (regex)", FieldType.Regex, null, new String[] { "item", "spell", "denizen" }));
				list.add(new QuestPropertyBlock(QuestRequirementAttribute.AUTO_JOURNAL, "Auto Journal Entry", FieldType.Boolean));
				break;
			case CastMultipleSpells:
				list.add(new QuestPropertyBlock(QuestRequirementCastMultipleSpells.NUMBER_OF_SPELLS, "Number of spells", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRequirementCastMultipleSpells.UNIQUE, "Unique spells", FieldType.Boolean));
				break;
			case CastSpell:
				list.add(new QuestPropertyBlock(QuestRequirementCastSpell.REGEX_FILTER, "Spell to be casted (regex)", FieldType.Regex, null, new String[] {"spell"}));
				break;
			case CharacterClass:
				list.add(new QuestPropertyBlock(QuestRequirementCharacterClass.REGEX_FILTER, "Character(s)", FieldType.Regex, null, new String[] {"character"}));
				break;
			case Chit:
				list.add(new QuestPropertyBlock(QuestRequirementChit.TYPE, "Chit type", FieldType.StringSelector, QuestRequirementChit.ChitType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementChit.AMOUNT, "Number of chits", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRequirementChit.STRENGTH, "Strength of the chits", FieldType.StringSelector, VulnerabilityType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementChit.SPEED, "Speed of the chits (0=any)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRequirementChit.MAGIC_COLOR, "Magic color (only for magic chits)", FieldType.StringSelector, new Object[] { "Any",ColorMagic.White,ColorMagic.Grey,ColorMagic.Gold,ColorMagic.Purple,ColorMagic.Black }));
				list.add(new QuestPropertyBlock(QuestRequirementChit.MAGIC_TYPE, "Magic type (only for magic chits)", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRequirementChit.ONLY_ACTIVE, "Chits must be active", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementChit.NOT_FATIGUED, "Chits must not be fatigued", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementChit.NOT_WOUNDED, "Chits must not be wounded", FieldType.Boolean));
				break;
			case ColorMagic:
				list.add(new QuestPropertyBlock(QuestRequirementColorMagic.COLOR_KEY, "In the presence of color magic.", FieldType.StringSelector, Constants.MAGIC_COLORS));
				break;
			case Counter:
				list.add(new QuestPropertyBlock(QuestRequirementCounter.COUNTER, "Quest Counter", FieldType.GameObjectWrapperSelector, quest.getCounters().toArray()));
				list.add(new QuestPropertyBlock(QuestRequirementCounter.TARGET_VALUE, "Target count", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRequirementCounter.EXCEED_TARGET_VALUE, "Exceed target value allowed", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementCounter.SUBCEED_TARGET_VALUE, "Subceed target value allowed", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementCounter.AUTO_JOURNAL, "Auto Journal Entry", FieldType.Boolean));
				break;
			case Discovery:
				list.add(new QuestPropertyBlock(QuestRequirementDiscovery.DISCOVERY_KEY, "Discovery", FieldType.StringSelector, getDiscoveryStrings().toArray()));
				break;
			case Enchant:
				list.add(new QuestPropertyBlock(QuestRequirementEnchant.TYPE, "Target", FieldType.StringSelector, new String[] { "chit", "tile"} ));
				break;
			case Fighter:
				break;
			case Fly:
				list.add(new QuestPropertyBlock(QuestRequirementFly.FLY, "Needs to fly?", FieldType.StringSelector, new String[] { QuestRequirementFly.FLYING,QuestRequirementFly.ABILITY_TO_FLY }));
				break;
			case FoundHiddenEnemies:
				break;
			case GamePhase:
				list.add(new QuestPropertyBlock(QuestRequirementGamePhase.GAME_PHASE_TYPE, "Only at", FieldType.StringSelector, GamePhaseType.values()));
				break;
			case Gender:
				list.add(new QuestPropertyBlock(QuestRequirementGender.GENDER, "Gender", FieldType.StringSelector, new String[] { GenderType.Female.toString(), GenderType.Male.toString() }));
				break;
			case Guild:
				list.add(new QuestPropertyBlock(QuestRequirementGuild.GUILD, "Guild", FieldType.Regex, null, new String[] { "guild" }));
				list.add(new QuestPropertyBlock(QuestRequirementGuild.GUILD_LEVEL, "Guild level (1-3)", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRequirementGuild.EXCEED_LEVEL, "Exceed target level allowed", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementGuild.SUBCEED_LEVEL, "Subceed target level allowed", FieldType.Boolean));
				break;
			case Hidden:
				break;
			case HideResult:
				list.add(new QuestPropertyBlock(QuestRequirementHideResult.DIE_ROLL, "Die roll", FieldType.StringSelector, DieRollType.values()));
				break;
			case Hirelings:
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.HIRELING_REGEX, "Hireling RegEx", FieldType.Regex, null, new String[] { "denizen" }));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.AMOUNT, "Number of hirelings", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.MUST_FOLLOW, "Must be following", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.SAME_LOCATION, "Same location (incl. character riding it)", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.VULNERARBILITY, "Vulnerability", FieldType.StringSelector, VulnerabilityType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.ATTACK_STRENGTH, "Attack strength", FieldType.StringSelector, VulnerabilityType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.ATTACK_SPEED, "Attack speed", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.ATTACK_LENGTH, "Attack length", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.SHARPNESS, "Number of sharpness stars", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.MOVE_SPEED, "Move speed", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.FLY_SPEED, "Fly speed", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.MISSILE, "Must own missile attack", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementHirelings.ARMORED, "Must be armored", FieldType.Boolean));
				break;
			case Inventory:
				list.add(new QuestPropertyBlock(QuestRequirementInventory.TREASURE_TYPE, "Type of inventory", FieldType.StringSelector, TreasureType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementInventory.REGEX_FILTER, "Inventory name filter (regex)", FieldType.Regex, null, new String[] { "item","treasure_within_treasure" }));
				list.add(new QuestPropertyBlock(QuestRequirementInventory.NUMBER, "How many in inventory?", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRequirementInventory.ITEM_ACTIVE, "Require activated?", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementInventory.ITEM_DEACTIVE, "Require deactivated?", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementInventory.REQ_MARK, "Requires mark?", FieldType.Boolean));
				break;
			case Kill:
				list.add(new QuestPropertyBlock(QuestRequirementKill.REGEX_FILTER, "Denizen name filter (regex)", FieldType.Regex, null, new String[] { "denizen" }));
				list.add(new QuestPropertyBlock(QuestRequirementKill.VALUE, "How many (" + QuestConstants.ALL_VALUE + " means ALL)", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRequirementKill.VULNERABILITY, "Vulnerability", FieldType.StringSelector, VulnerabilityType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementKill.ARMORED, "Armor", FieldType.StringSelector, ArmoredType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementKill.TARGET_VALUE_TYPE, "Only count points gained during the", FieldType.StringSelector, TargetValueType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementKill.REQUIRE_MARK, "Mark is required", FieldType.Boolean));
				break;
			case LearnAwaken:
				list.add(new QuestPropertyBlock(QuestRequirementLearnAwaken.REGEX_FILTER, "Spell filter (regex)", FieldType.Regex, null, new String[] { "spell,learnable" }));
				list.add(new QuestPropertyBlock(QuestRequirementLearnAwaken.MUST_LEARN, "Must Learn Spell", FieldType.Boolean));
				break;
			case LocationExists:
				list.add(new QuestPropertyBlock(QuestRequirementLocationExists.LOCATION, "Quest Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;	
			case Loot:
				list.add(new QuestPropertyBlock(QuestRequirementLoot.TREASURE_TYPE, "Type of Loot to acquire", FieldType.StringSelector, TreasureType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementLoot.REGEX_FILTER, "Loot name filter (regex)", FieldType.Regex, null, new String[] { "item","treasure_within_treasure" }));
				list.add(new QuestPropertyBlock(QuestRequirementLoot.REQ_MARK, "Loot requires mark", FieldType.Boolean));
				break;
			case NextPhase:
				list.add(new QuestPropertyBlock(QuestRequirementNextPhase.PHASES_TO_SKIP, "Phases to pass (1 day has 4 phases)", FieldType.Number));
				break;
			case NoDenizens:
				list.add(new QuestPropertyBlock(QuestRequirementNoDenizens.NO_MONSTERS, "No Monsters", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementNoDenizens.NO_NATIVES, "No Natives", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementNoDenizens.TILE_WIDE, "All Tile Clearings", FieldType.Boolean));
				break;
			case MagicUser:
				break;
			case MinorCharacter:
				list.add(new QuestPropertyBlock(QuestRequirementMinorCharacter.MINOR_CHARACTER, "Minor character ", FieldType.SmartTextLine, quest.getMinorCharacters().toArray()));
				break;
			case MissionCampaign:
				list.add(new QuestPropertyBlock(QuestRequirementMissionCampaign.ACTION_TYPE, "Mission/Campaign action", FieldType.StringSelector, CharacterActionType.mcValues()));
				list.add(new QuestPropertyBlock(QuestRequirementMissionCampaign.DISABLE_ON_PICKUP, "Disable on Pickup", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementMissionCampaign.REGEX_FILTER, "Mission/Campaign filter (regex)", FieldType.Regex, null, new String[] { "mission","campaign" }));
				break;
			case OccupyLocation:
				list.add(new QuestPropertyBlock(QuestRequirementLocation.LOCATION, "Quest Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				list.add(new QuestPropertyBlock(QuestRequirementLocation.NON_CLEARING, "Non-clearing location allowed", FieldType.Boolean));
				break;
			case Open:
				list.add(new QuestPropertyBlock(QuestRequirementOpen.LOCATION_REGEX, "Location to open/search", FieldType.Regex, null, new String[] { "needs_open", "search" }));
				list.add(new QuestPropertyBlock(QuestRequirementOpen.OPEN_BY_ANYONE, "Open by anyone (chest or vault)", FieldType.Boolean));
				break;
			case Path:
				list.add(new QuestPropertyBlock(QuestRequirementPath.PATH, "Specific Path (like \"CV1 CV3 CV6 CV4 CV5\")", FieldType.SmartTextArea, getAllClearingCodes()));
				list.add(new QuestPropertyBlock(QuestRequirementPath.TIME_RESTRICTION, "Only count moves made during the", FieldType.StringSelector, TargetValueType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementPath.CHECK_REVERSE, "Either direction", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementPath.ALLOW_TRANSPORT, "Allow teleport", FieldType.Boolean));
				break;
			case Probability:
				list.add(new QuestPropertyBlock(QuestRequirementProbability.CHANCE, "Probability in % (1-100)", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRequirementProbability.MAX_NUMBER_OF_CHECKS, "Max. checks per day (0: unlimited)", FieldType.NumberAll));
				break;
			case Relationship:
				list.add(new QuestPropertyBlock(QuestRequirementRelationship.NATIVE_GROUP, "Native group", FieldType.Regex, null, new String[] {"native,rank=HQ", "visitor"}));
				list.add(new QuestPropertyBlock(QuestRequirementRelationship.RELATIONSHIP_LEVEL, "Relationship", FieldType.StringSelector, RelationshipType.RelationshipNames));
				list.add(new QuestPropertyBlock(QuestRequirementRelationship.EXCEED_LEVEL, "Exceed target level allowed", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementRelationship.SUBCEED_LEVEL, "Subceed target level allowed", FieldType.Boolean));
				break;
			case SearchResult:
				list.add(new QuestPropertyBlock(QuestRequirementSearchResult.REQ_TABLENAME, "Search table", FieldType.StringSelector, SearchTableType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementSearchResult.LOCATION, "Location of search", FieldType.GameObjectWrapperSelector, getOptionalQuestLocationArray().toArray()));
				list.add(new QuestPropertyBlock(QuestRequirementSearchResult.TARGET_LOC, "Target of search", FieldType.GameObjectWrapperSelector, getOptionalQuestLocationArray().toArray()));
				list.add(new QuestPropertyBlock(QuestRequirementSearchResult.TARGET_REGEX, "OR", FieldType.Regex, null, new String[] { "ts_section,!dwelling,!summon,!guild" }));
				list.add(new QuestPropertyBlock(QuestRequirementSearchResult.RESULT1, "Search result", FieldType.StringSelector, SearchResultType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementSearchResult.RESULT2, "OR", FieldType.StringSelector, SearchResultType.optionalValues()));
				list.add(new QuestPropertyBlock(QuestRequirementSearchResult.RESULT3, "OR", FieldType.StringSelector, SearchResultType.optionalValues()));
				list.add(new QuestPropertyBlock(QuestRequirementSearchResult.REQUIRES_GAIN, "Require search effect", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementSearchResult.REQUIRES_NO_GAIN, "Requires no search effect", FieldType.Boolean));
				break;
			case Season:
				list.add(new QuestPropertyBlock(QuestRequirementSeason.SEASON, "Season", FieldType.StringSelector, getSeasonStrings().toArray() ));
				break;
			case TimePassed:
				list.add(new QuestPropertyBlock(QuestRequirementTimePassed.VALUE, "How many days", FieldType.Number));
				break;
			case Trade:
				list.add(new QuestPropertyBlock(QuestRequirementTrade.TRADE_TYPE, "Buy or Sell", FieldType.StringSelector,  TradeType.values()));
				list.add(new QuestPropertyBlock(QuestRequirementTrade.TRADE_ITEM_REGEX, "Item Traded", FieldType.Regex, null, new String[] { "item" }));
				list.add(new QuestPropertyBlock(QuestRequirementTrade.TRADE_WITH_REGEX, "Trade With", FieldType.Regex, null, new String[] { "native,rank=HQ","visitor" }));
				break;
			case Weather:
				list.add(new QuestPropertyBlock(QuestRequirementWeather.WEATHER_ENABLED, "Weather must be enabled", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRequirementWeather.WEATHER, "Weather", FieldType.StringSelector, new String[] {"", RealmCalendar.WEATHER_CLEAR, RealmCalendar.WEATHER_SHOWERS, RealmCalendar.WEATHER_STORM, RealmCalendar.WEATHER_SPECIAL}));
				break;
		}
		return list;
	}

	private String[] getAllClearingCodes() {
		ArrayList<String> list = new ArrayList<>();
		GamePool pool = new GamePool(realmSpeakData.getGameObjects());
		for(GameObject go:pool.find("tile")) {
			TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(go);
			String code = tile.getTileCode();
			for (ClearingDetail clearing:tile.getClearings()) {
				list.add(code+clearing.getNumString());
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	private ArrayList<String> getDiscoveryStrings() {
		ArrayList<String> list = new ArrayList<>();
		
		// Build the discovery lists
		GamePool pool = new GamePool(realmSpeakData.getGameObjects());
		for(GameObject go:pool.find("treasure_location,discovery")) {
			list.add(go.getName());
		}
		for(GameObject go:pool.find("red_special")) {
			list.add(go.getName());
		}
		Collections.sort(list);
		
		ArrayList<String> sublist = new ArrayList<>();
		for(GameObject go:pool.find("tile")) {
			TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(go);
			for(PathDetail path:tile.getHiddenPaths()) {
				sublist.add(path.getFullPathKey());
			}
			for(PathDetail path:tile.getSecretPassages()) {
				sublist.add(path.getFullPathKey());
			}
		}
		Collections.sort(sublist);
		list.addAll(sublist);
		
		return list;
	}

	private ArrayList<QuestLocation> getOptionalQuestLocationArray() {
		ArrayList<QuestLocation> list = new ArrayList<>();
		ArrayList<QuestLocation> locations = quest.getLocations();
		if (locations != null) {
			list.addAll(locations);
		}
		list.add(0, null);
		return list;
	}
	
	private ArrayList<String> getSeasonStrings() {
		ArrayList<String> list = new ArrayList<>();
		GamePool pool = new GamePool(realmSpeakData.getGameObjects());
		for(GameObject go:pool.find("season")) {
			list.add(go.getName());
		}
		Collections.sort(list);		
		return list;
	}
}