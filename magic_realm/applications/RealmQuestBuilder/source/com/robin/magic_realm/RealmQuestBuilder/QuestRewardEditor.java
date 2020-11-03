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

import javax.swing.JFrame;

import com.robin.game.objects.*;
import com.robin.magic_realm.RealmCharacterBuilder.EditPanel.CompanionEditPanel;
import com.robin.magic_realm.RealmQuestBuilder.QuestPropertyBlock.FieldType;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.quest.reward.*;

public class QuestRewardEditor extends QuestBlockEditor {

	private Quest quest;
	private QuestReward reward;

	// RewardGroups allow characters to choose a reward. This is also helpful if
	// there is a starting reward that you get when you start the quest.
	private static final String[] RewardGroups = { QuestReward.ALL_REWARD_GROUP, "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	public QuestRewardEditor(JFrame parent, GameData realmSpeakData, Quest quest, QuestReward reward) {
		super(parent, realmSpeakData, reward);
		this.quest = quest;
		this.reward = reward;
		setLocationRelativeTo(parent);
		read();
	}

	public String getEditorTitle() {
		return "Quest Reward: " + reward.getRewardType();
	}

	public boolean getCanceledEdit() {
		return canceledEdit;
	}

	protected ArrayList<QuestPropertyBlock> createPropertyBlocks() {
		ArrayList<QuestPropertyBlock> list = new ArrayList<QuestPropertyBlock>();
		list.add(new QuestPropertyBlock(QuestReward.REWARD_GROUP, "Reward group", FieldType.StringSelector, RewardGroups));
		switch (reward.getRewardType()) {
			case ActivateQuest:
				break;
			case AlterBlock:
				list.add(new QuestPropertyBlock(QuestRewardAlterBlock.GAIN_TYPE, "Block status", FieldType.StringSelector, GainType.values()));
				break;
			case AlterHide:
				list.add(new QuestPropertyBlock(QuestRewardAlterHide.GAIN_TYPE, "Hide status", FieldType.StringSelector, GainType.values()));
				break;
			case Attribute:
				list.add(new QuestPropertyBlock(QuestRewardAttribute.ATTRIBUTE_TYPE, "Affected Attribute", FieldType.StringSelector, new Object[] { AttributeType.Fame, AttributeType.Notoriety, AttributeType.Gold }));
				list.add(new QuestPropertyBlock(QuestRewardAttribute.GAIN_TYPE, "Gain or lose", FieldType.StringSelector, GainType.values()));
				list.add(new QuestPropertyBlock(QuestRewardAttribute.ATTRIBUTE_CHANGE, "Amount", FieldType.Number));
				break;
			case ChooseNextStep:
				list.add(new QuestPropertyBlock(QuestRewardChooseNextStep.TEXT, "Text", FieldType.TextLine));
				list.add(new QuestPropertyBlock(QuestRewardChooseNextStep.RANDOM, "Random choice", FieldType.Boolean));
				break;
			case Companion:
				list.add(new QuestPropertyBlock(QuestRewardCompanion.COMPANION_NAME, "Companion", FieldType.CompanionSelector, getAllCompanionKeyValues()));
				list.add(new QuestPropertyBlock(QuestRewardCompanion.GAIN_TYPE, "Gain or lose", FieldType.StringSelector, GainType.values()));
				list.add(new QuestPropertyBlock(QuestRewardCompanion.EXCLUDE_HORSE, "Exclude horse", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardCompanion.LOCATION_ONLY, "Apper in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardCompanion.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case Counter:
				list.add(new QuestPropertyBlock(QuestRewardCounter.COUNTER, "Quest Counter", FieldType.GameObjectWrapperSelector, quest.getCounters().toArray()));
				list.add(new QuestPropertyBlock(QuestRewardCounter.SET_COUNT, "Set current count ("+QuestConstants.ALL_VALUE+"=no change)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardCounter.INCREASE_COUNT, "Increase count", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardCounter.DECREASE_COUNT, "Decrease count", FieldType.NumberAll));
				break;
			case Curse:
				list.add(new QuestPropertyBlock(QuestRewardCurse.DIE_ROLL, "Die roll", FieldType.StringSelector, DieRollType.values()));
				list.add(new QuestPropertyBlock(QuestRewardCurse.REMOVE_CURSES, "Remove all curses", FieldType.Boolean));
				break;
			case CustomTreasure:
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.TREASURE_REGEX, "Treasure RegEx", FieldType.Regex, null, new String[] { "treasure" }));
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.TREASURE_NAME, "Name", FieldType.TextLine));
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.TREASURE_PRICE, "Price ("+QuestConstants.ALL_VALUE+"=no change)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.TREASURE_FAME, "Fame ("+QuestConstants.ALL_VALUE+"=no change)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.TREASURE_NOTORIETY, "Notoriety ("+QuestConstants.ALL_VALUE+"=no change)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.TREASURE_WEIGHT, "Weight", FieldType.StringSelector, new String[] {QuestRewardCustomTreasure.NO_CHANGE,"N","M","L","H","T"}));
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.TREASURE_SIZE, "Size", FieldType.StringSelector, new String[] {QuestRewardCustomTreasure.NO_CHANGE,QuestRewardCustomTreasure.SMALL,QuestRewardCustomTreasure.LARGE}));
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.TREASURE_GREAT, "Great?", FieldType.StringSelector, new String[] {QuestRewardCustomTreasure.NO_CHANGE,QuestRewardCustomTreasure.NOT_GREAT,QuestRewardCustomTreasure.GREAT}));
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.LOCATION_ONLY, "Appers in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case Damage:
				list.add(new QuestPropertyBlock(QuestRewardDamage.DAMAGE_TYPE, "Damage Type", FieldType.StringSelector, DamageType.values()));
				list.add(new QuestPropertyBlock(QuestRewardDamage.AMOUNT, "Amount", FieldType.Number));
				break;
			case DiscardQuest:
				break;
			case DiscoverTreasureSite:
				list.add(new QuestPropertyBlock(QuestRewardDiscoverTreasureSite.SITE_REGEX, "Treasure site", FieldType.Regex, null, new String[] { "treasure_location" }));
				break;
			case DrawQuests:
				break;
			case EnchantTile:
				list.add(new QuestPropertyBlock(QuestRewardEnchantTile.UNENCHANT, "Unenchant tiles", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardEnchantTile.CHARACTERS_TILE, "Affect characters tile", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardEnchantTile.AFFECT_LOCATION, "Affect location", FieldType.StringSelector, new String[] {QuestRewardEnchantTile.NONE,QuestRewardEnchantTile.RANDOM_TILE,QuestRewardEnchantTile.ALL_TILES} ));
				list.add(new QuestPropertyBlock(QuestRewardEnchantTile.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case FindHiddenEnemies:
				break;
			case Guild:
				list.add(new QuestPropertyBlock(QuestRewardGuild.GUILD, "Guild", FieldType.StringSelector, getGuildNames()));
				list.add(new QuestPropertyBlock(QuestRewardGuild.GUILD_LEVEL, "Guild level (1-3)", FieldType.Number));
				break;
			case Heal:
				list.add(new QuestPropertyBlock(QuestRewardHeal.HEAL, "Heal chits", FieldType.StringSelector, HealType.values()));
				break;
			case Hireling:
				list.add(new QuestPropertyBlock(QuestRewardHireling.HIRELING_REGEX, "Native RegEx", FieldType.Regex, null, new String[] { "native,rank" }));
				list.add(new QuestPropertyBlock(QuestRewardHireling.ACQUISITION_TYPE, "Method to acquire hireling", FieldType.StringSelector, ChitAcquisitionType.values()));
				list.add(new QuestPropertyBlock(QuestRewardHireling.TERM_OF_HIRE, "Term of hire", FieldType.StringSelector, TermOfHireType.values()));
				list.add(new QuestPropertyBlock(QuestRewardHireling.LOCATION_ONLY, "Apper in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardHireling.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case Information:
				list.add(new QuestPropertyBlock(QuestRewardInformation.INFORMATION_TEXT, "Information to provide", FieldType.TextArea));
				break;
			case Item:
				list.add(new QuestPropertyBlock(QuestRewardItem.GAIN_TYPE, "Gain or lose item", FieldType.StringSelector, ItemGainType.values()));
				list.add(new QuestPropertyBlock(QuestRewardItem.DAMAGED_ITEM, "Damaged item (if gained)", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardItem.ITEM_DESC, "Description", FieldType.TextLine));
				list.add(new QuestPropertyBlock(QuestRewardItem.ITEM_CHITTYPES, "Item Type Restriction", FieldType.ChitType));
				list.add(new QuestPropertyBlock(QuestRewardItem.ITEM_REGEX, "Item RegEx", FieldType.Regex));
				break;
			case Journal:
				list.add(new QuestPropertyBlock(QuestRewardJournal.JOURNAL_KEY, "Journal Key (no spaces)", FieldType.NoSpacesTextLine));
				list.add(new QuestPropertyBlock(QuestRewardJournal.ENTRY_TYPE, "Entry type", FieldType.StringSelector, new String[] { QuestStepState.Pending.toString(), QuestStepState.Finished.toString(), QuestStepState.Failed.toString() }));
				list.add(new QuestPropertyBlock(QuestRewardJournal.TEXT, "Text", FieldType.TextLine));
				break;
			case KillDenizen:
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.DENIZEN_REGEX, "Denizen", FieldType.Regex, null, new String[] { "denizen" }));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_HIRELINGS, "Kill hirelings", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_COMPANIONS, "Kill companions", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_SUMMONED, "Kill summoned monsters", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_LIMITED, "Kill ONLY those (see above)", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_IN_LOCATION, "Denizen must be in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case LostInventoryToDefault:
				break;
			case LostInventoryToLocation:
				list.add(new QuestPropertyBlock(QuestRewardLostInventoryToLocation.LOCATION, "Send inventory to", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case MarkDenizen:
				list.add(new QuestPropertyBlock(QuestRewardMarkDenizen.DENIZEN_REGEX, "Denizen name filter (regex)", FieldType.Regex, null, new String[] { "denizen" }));
				break;
			case MinorCharacter:
				list.add(new QuestPropertyBlock(QuestRewardMinorCharacter.MINOR_CHARACTER, "Minor character ", FieldType.SmartTextLine, quest.getMinorCharacters().toArray()));
				list.add(new QuestPropertyBlock(QuestRewardMinorCharacter.GAIN_TYPE, "Gain or lose", FieldType.StringSelector, GainType.values()));
				break;
			case MoveDenizen:
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.DENIZEN_REGEX, "Denizen", FieldType.Regex, null, new String[] { "denizen" }));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_TO_SAME_CLEARING, "Move all to same clearing", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_HIRELINGS, "Move hirelings", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_COMPANIONS, "Move companions", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_SUMMONED, "Move summoned monsters", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_LIMITED, "Move ONLY those (see above)", FieldType.Boolean));
				break;
			case PathsPassages:
				list.add(new QuestPropertyBlock(QuestRewardPathsPassages.DISCOVERY_TYPE, "Road type to discover", FieldType.StringSelector, RoadDiscoveryType.values()));
				list.add(new QuestPropertyBlock(QuestRewardPathsPassages.DISCOVERY_SCOPE, "Scope of discovery", FieldType.StringSelector, MapScopeType.values()));
				list.add(new QuestPropertyBlock(QuestRewardPathsPassages.LOCATION_ONLY, "Only in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardPathsPassages.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case PowerOfThePit:
				list.add(new QuestPropertyBlock(QuestRewardPowerOfThePit.DIE_ROLL, "Die roll", FieldType.StringSelector, DieRollType.values()));
				break;
			case RegenerateDenizen:
				list.add(new QuestPropertyBlock(QuestRewardRegenerateDenizen.DENIZEN_REGEX, "Denizen", FieldType.Regex, null, new String[] { "denizen" }));
				break;	
			case RelationshipChange:
				list.add(new QuestPropertyBlock(QuestRewardRelationshipChange.NATIVE_GROUP, "Native group", FieldType.StringSelector, getRelationshipNames()));
				list.add(new QuestPropertyBlock(QuestRewardRelationshipChange.GAIN_TYPE, "Gain or lose", FieldType.StringSelector, GainType.values()));
				list.add(new QuestPropertyBlock(QuestRewardRelationshipChange.RELATIONSHIP_CHANGE, "Levels of Friendliness", FieldType.Number));
				break;
			case RelationshipSet:
				list.add(new QuestPropertyBlock(QuestRewardRelationshipSet.NATIVE_GROUP, "Native group", FieldType.StringSelector, getRelationshipNames()));
				list.add(new QuestPropertyBlock(QuestRewardRelationshipSet.RELATIONSHIP_SET, "Relationship to set", FieldType.StringSelector, RelationshipType.RelationshipNames));
				break;
			case ResetQuest:
				list.add(new QuestPropertyBlock(QuestRewardResetQuest.NOT_RESET_FOR_LOCATIONS, "Don't reset locations", FieldType.Boolean));
				break;
			case ResetQuestLocations:
				break;
			case ResetQuestSteps:
				list.add(new QuestPropertyBlock(QuestRewardResetQuestSteps.RESET_METHOD, "Reset mode", FieldType.StringSelector, QuestRewardResetQuestSteps.ResetMethod.values()));
				list.add(new QuestPropertyBlock(QuestRewardResetQuestSteps.RESET_DEPENDENT_QUEST_STEPS, "Reset steps requiring resetted steps", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardResetQuestSteps.QUEST_STEPS_DEPTH, "Cascade 'depth'", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardResetQuestSteps.QUEST_STEP_NAME, "Quest Step", FieldType.StringSelector, quest.getSteps().toArray()));
				break;
			case ResetQuestToDeck:
				break;
			case ScareMonsters:
				break;
			case SpellEffect:
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.SPELL_REGEX, "Spell", FieldType.Regex, null, new String[] { "spell" }));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.AFFECT_CHARACTER, "Affect character", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.AFFECT_ALL_TARGETS_IN_CHARACTERS_CLEARING, "Affect all targets in characters clearing", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.TARGET_REGEX, "Affect targets", FieldType.Regex, null, new String[] { "vulnerability" }));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.EXPIRE_IMMEDIATELY, "Expire immediately", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.AFFECT_HIRELINGS, "Affect hirelings", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.AFFECT_COMPANIONS, "Affect companions", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.AFFECT_SUMMONED, "Affect summoned monsters", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.AFFECT_LIMITED, "Affect ONLY those (see above)", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.TARGET_IN_LOCATION, "Affect ONLY targets in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.ALL_TARGETS_IN_LOCATION, "Affect ALL targets in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.ALL_TARGETS_IN_RANDOM_LOCATION, "Affect targets in random location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffect.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case SpellFromSite:
				list.add(new QuestPropertyBlock(QuestRewardTreasureFromSite.SITE_REGEX, "Site RegEx", FieldType.Regex, null, new String[] { "spell_site","visitor,!name=Scholar","artifact","book,magic" }));
				list.add(new QuestPropertyBlock(QuestRewardTreasureFromSite.DRAW_TYPE, "Draw Type", FieldType.StringSelector, DrawType.values()));
				break;
			case StripInventory:
				list.add(new QuestPropertyBlock(QuestRewardStripInventory.STRIP_GOLD, "Strip Gold", FieldType.Boolean));
				break;
			case SummonGuardian:
				list.add(new QuestPropertyBlock(QuestRewardSummonGuardian.LOCATION, "Summon Guardian for ", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case SummonMonster:
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.MONSTER_NAME, "Monster", FieldType.CompanionSelector, getAllCompanionKeyValues()));
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.RANDOM_CLEARING, "Random clearing of characters tile", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.SUMMON_TO_LOCATION, "Summon to location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.RANDOM_LOCATION, "Random clearing of location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case SummonFromAppearance:
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearance.CHIT, "Chit to summon to", FieldType.Regex, null, new String[] { "warning", "sound", "treasure_location", "dwelling" }));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearance.DENIZEN, "Denizen (all if empty)", FieldType.Regex, null, new String[] { "vulnerability", "setup_start" }));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearance.SUMMON_LIVING_DENIZENS, "Summon living denizens", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearance.MAX_DENIZENS, "Max.# of summoned denizens", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearance.MAX_DENIZEN_HOLDERS, "Max.# of monster boxes", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearance.SUMMON_TO, "Chit location", FieldType.StringSelector, QuestRewardSummonFromAppearance.SummonTo.values()));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearance.LOCATION, "Quest Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case SummonRoll:
				list.add(new QuestPropertyBlock(QuestRewardSummonRoll.DIE_ROLL, "Die roll", FieldType.StringSelector, DieRollType.values()));
				break;
			case Teleport:
				list.add(new QuestPropertyBlock(QuestRewardTeleport.LOCATION, "Teleport to", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case TreasureFromHq:
				list.add(new QuestPropertyBlock(QuestRewardTreasureFromHq.HQ_REGEX, "HQ RegEx", FieldType.Regex, null, new String[] { "rank=HQ" }));
				list.add(new QuestPropertyBlock(QuestRewardTreasureFromHq.DRAW_TYPE, "Draw Type", FieldType.StringSelector, DrawType.values()));
				break;
			case TreasureFromSite:
				list.add(new QuestPropertyBlock(QuestRewardTreasureFromSite.SITE_REGEX, "Site RegEx", FieldType.Regex, null, new String[] { "treasure_location,!treasure_within_treasure,!cannot_move","visitor=scholar","dwelling,!native" }));
				list.add(new QuestPropertyBlock(QuestRewardTreasureFromSite.DRAW_TYPE, "Draw Type", FieldType.StringSelector, DrawType.values()));
				break;
			case QuestComplete:
				break;
			case QuestFailed:
				break;
			case Visitor:
				list.add(new QuestPropertyBlock(QuestRewardVisitor.VISITOR_REGEX, "Visitor RegEx", FieldType.Regex, null, new String[] { "visitor" }));
				list.add(new QuestPropertyBlock(QuestRewardVisitor.ACQUISITION_TYPE, "Method to acquire hireling", FieldType.StringSelector, ChitAcquisitionType.values()));
				break;
			case Wish:
				list.add(new QuestPropertyBlock(QuestRewardWish.DIE_ROLL, "Die roll", FieldType.StringSelector, DieRollType.values()));
				break;
		}
		return list;
	}

	private KeyValuePair[] getAllCompanionKeyValues() {
		ArrayList<KeyValuePair> companions = new ArrayList<KeyValuePair>();
		for (String[] section : CompanionEditPanel.COMPANIONS) {
			boolean first = true;
			for (String name : section) {
				// skip first every time
				if (first) {
					first = false;
					continue;
				}
				String[] ret = name.split(":");
				if (ret.length == 2) {
					companions.add(new KeyValuePair(ret[0], ret[1]));
				}
				else {
					companions.add(new KeyValuePair(ret[0], "Name=" + ret[0]));
				}
			}
		}
		return companions.toArray(new KeyValuePair[0]);
	}

	private String[] getRelationshipNames() {
		ArrayList<String> names = new ArrayList<String>();
		names.add("Clearing");
		GamePool pool = new GamePool(realmSpeakData.getGameObjects());
		for (GameObject go : pool.find("native,rank=HQ")) {
			names.add(go.getThisAttribute("native"));
		}
		for (GameObject go : pool.find("visitor")) {
			names.add(go.getThisAttribute("visitor"));
		}
		return names.toArray(new String[0]);
	}
	
	private String[] getGuildNames() {
		ArrayList<String> names = new ArrayList<String>();
		names.add(QuestConstants.CURRENT);
		names.add(QuestConstants.REMOVE);
		GamePool pool = new GamePool(realmSpeakData.getGameObjects());
		for (GameObject go : pool.find("guild")) {
			names.add(go.getName());
		}
		return names.toArray(new String[0]);
	}
}