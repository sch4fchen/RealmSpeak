package com.robin.magic_realm.RealmQuestBuilder;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;

import com.robin.game.objects.*;
import com.robin.magic_realm.RealmCharacterBuilder.EditPanel.CompanionEditPanel;
import com.robin.magic_realm.RealmQuestBuilder.QuestPropertyBlock.FieldType;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.quest.reward.*;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmCalendar;
import com.robin.magic_realm.components.utility.SpellUtility;

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
		ArrayList<QuestPropertyBlock> list = new ArrayList<>();
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
				list.add(new QuestPropertyBlock(QuestRewardCompanion.COMPANION_STAYS_INGAME, "Leave companion in game (if lost)", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardCompanion.EXCLUDE_HORSE, "Exclude horse", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardCompanion.COMPANION_RENAME, "Rename companion", FieldType.TextLine));
				list.add(new QuestPropertyBlock(QuestRewardCompanion.LOCATION_ONLY, "Appear in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardCompanion.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case Control:
				list.add(new QuestPropertyBlock(QuestRewardControl.DENIZEN_REGEX, "Denizen name filter (regex)", FieldType.Regex, null, new String[] { "denizen" }));
				list.add(new QuestPropertyBlock(QuestRewardControl.REMOVE_CONTROL, "Remove control", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardControl.AMOUNT, "Number of denizens (0: unlimited)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardControl.LOCATION_ONLY, "Control targets in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardControl.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case Counter:
				list.add(new QuestPropertyBlock(QuestRewardCounter.COUNTER, "Quest Counter", FieldType.GameObjectWrapperSelector, quest.getCounters().toArray()));
				list.add(new QuestPropertyBlock(QuestRewardCounter.SET_COUNT, "Set current count ("+QuestConstants.ALL_VALUE+"=no change)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardCounter.RANDOM, "Set current count to random number (1-100)", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardCounter.INCREASE_COUNT, "Increase count", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardCounter.DECREASE_COUNT, "Decrease count", FieldType.NumberAll));
				break;
			case Curse:
				list.add(new QuestPropertyBlock(QuestRewardCurse.DIE_ROLL, "Die roll", FieldType.StringSelector, DieRollType.values()));
				list.add(new QuestPropertyBlock(QuestRewardCurse.REMOVE_CURSES, "Remove all curses", FieldType.Boolean));
				break;
			case CustomTreasure:
				list.add(new QuestPropertyBlock(QuestRewardCustomTreasure.TREASURE_REGEX, "Treasure", FieldType.Regex, null, new String[] { "treasure" }));
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
				list.add(new QuestPropertyBlock(QuestRewardDamage.INCLUDE_FOLLOWERS, "Include followers", FieldType.Boolean));
				break;
			case DamageChit:
				list.add(new QuestPropertyBlock(QuestRewardDamageChit.DAMAGE_TYPE, "Damage Type", FieldType.StringSelector, DamageType.values()));
				list.add(new QuestPropertyBlock(QuestRewardDamageChit.TYPE, "Chit type", FieldType.StringSelector, QuestRewardDamageChit.ChitType.values()));
				list.add(new QuestPropertyBlock(QuestRewardDamageChit.STRENGTH, "Strength of the chit", FieldType.StringSelector, VulnerabilityType.values()));
				list.add(new QuestPropertyBlock(QuestRewardDamageChit.SPEED, "Speed of the chit (0=any)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardDamageChit.MAGIC_COLOR, "Magic color (only for magic chit)", FieldType.StringSelector, new Object[] { "Any",ColorMagic.White,ColorMagic.Grey,ColorMagic.Gold,ColorMagic.Purple,ColorMagic.Black }));
				list.add(new QuestPropertyBlock(QuestRewardDamageChit.MAGIC_TYPE, "Magic type (only for magic chit)", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRewardDamageChit.ONLY_ACTIVE, "Chit must be active", FieldType.Boolean));
				break;
			case DeactivateQuest:
				break;
			case DeductVps:
				list.add(new QuestPropertyBlock(QuestRewardDeductVps.AMOUNT, "Amount", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardDeductVps.ADD_VPS, "Add VPs", FieldType.Boolean));
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
			case Exorcise:
				list.add(new QuestPropertyBlock(QuestRewardExorcise.LOCATION_ONLY, "Cast exorcise in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardExorcise.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case FindHiddenEnemies:
				break;
			case Guild:
				list.add(new QuestPropertyBlock(QuestRewardGuild.GUILD, "Guild", FieldType.StringSelector, getGuildNames()));
				list.add(new QuestPropertyBlock(QuestRewardGuild.GUILD_CHANGE, "Change level", FieldType.StringSelector, QuestRewardGuild.GuildGainType.values()));
				list.add(new QuestPropertyBlock(QuestRewardGuild.GUILD_LEVEL, "Set guild level (1-3)", FieldType.Number));
				break;
			case Heal:
				list.add(new QuestPropertyBlock(QuestRewardHeal.HEAL, "Heal chits", FieldType.StringSelector, HealType.values()));
				break;
			case Hireling:
				list.add(new QuestPropertyBlock(QuestRewardHireling.HIRELING_REGEX, "Native RegEx", FieldType.Regex, null, new String[] { "native,rank" }));
				list.add(new QuestPropertyBlock(QuestRewardHireling.EXCLUDE_CLONED, "Exclude cloned hirelings in regex search", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardHireling.ACQUISITION_TYPE, "Method to acquire hireling", FieldType.StringSelector, ChitAcquisitionType.values()));
				list.add(new QuestPropertyBlock(QuestRewardHireling.TERM_OF_HIRE, "Term of hire", FieldType.StringSelector, TermOfHireType.values()));
				list.add(new QuestPropertyBlock(QuestRewardHireling.EXCLUDE_HORSE, "Exclude horse for cloned", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardHireling.HIRELING_RENAME, "Rename companion", FieldType.TextLine));
				list.add(new QuestPropertyBlock(QuestRewardHireling.LOCATION_ONLY, "Appear in location", FieldType.Boolean));
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
				list.add(new QuestPropertyBlock(QuestRewardItem.NATIVE_REGEX, "Native Hq RegEx", FieldType.RegexIgnoreChitTypes, null, new String[] { "native,rank=HQ" }));
				list.add(new QuestPropertyBlock(QuestRewardItem.RANDOM, "Select random item (no player choosing)", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardItem.FORCE_DEACTIVATION, "Force losing (e.g. cursed)", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardItem.MARK_ITEM, "Mark item", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardItem.REQ_MARK, "Requires mark", FieldType.Boolean));
				break;
			case Journal:
				list.add(new QuestPropertyBlock(QuestRewardJournal.JOURNAL_KEY, "Journal Key (no spaces)", FieldType.NoSpacesTextLine));
				list.add(new QuestPropertyBlock(QuestRewardJournal.ENTRY_TYPE, "Entry type", FieldType.StringSelector, new String[] { QuestStepState.Pending.toString(), QuestStepState.Finished.toString(), QuestStepState.Failed.toString() }));
				list.add(new QuestPropertyBlock(QuestRewardJournal.TEXT, "Text", FieldType.TextLine));
				break;
			case KillDenizen:
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.DENIZEN_REGEX, "Denizen", FieldType.Regex, null, new String[] { "denizen" }));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.REWARD_CHARACTER, "Reward character", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.AMOUNT, "Number of denizens (0: unlimited)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_MARKED, "Kill ONLY marked denizes", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_HIRELINGS, "Kill hirelings", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_COMPANIONS, "Kill companions", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_SUMMONED, "Kill summoned monsters", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_CLONED, "Kill cloned denizens", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_LIMITED, "Kill ONLY those (see above)", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_IN_CHAR_LOCATION, "Denizen must be in characters clearing", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_IN_CHAR_TILE, "Denizen must be in characters tile", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.KILL_IN_LOCATION, "Denizen must be in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardKillDenizen.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case LostInventoryToDefault:
				break;
			case LostInventoryToLocation:
				list.add(new QuestPropertyBlock(QuestRewardLostInventoryToLocation.LOCATION, "Send inventory to", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case MagicColor:
				list.add(new QuestPropertyBlock(QuestRewardMagicColor.COLOR, "Magic color", FieldType.StringSelector, Constants.MAGIC_COLORS));
				list.add(new QuestPropertyBlock(QuestRewardMagicColor.REMOVE, "Remove color", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMagicColor.AFFECT, "Target", FieldType.StringSelector, new String[] {QuestRewardMagicColor.CHARACTERS_CLEARING, QuestRewardMagicColor.CHARACTERS_TILE, QuestRewardMagicColor.LOC_RANDOM_CLEARING, QuestRewardMagicColor.LOC_RANDOM_TILE, QuestRewardMagicColor.LOC_ALL_TILES, QuestRewardMagicColor.ALL} ));
				list.add(new QuestPropertyBlock(QuestRewardMagicColor.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case MakeWhole:
				break;
			case MarkDenizen:
				list.add(new QuestPropertyBlock(QuestRewardMarkDenizen.DENIZEN_REGEX, "Denizen name filter (regex)", FieldType.Regex, null, new String[] { "denizen" }));
				list.add(new QuestPropertyBlock(QuestRewardMarkDenizen.DENIZEN_AMOUNT, "Number of denizens (0: unlimited)", FieldType.NumberAll));
				break;
			case MarkItem:
				list.add(new QuestPropertyBlock(QuestRewardMarkItem.ITEM_REGEX, "Item RegEx", FieldType.Regex));
				list.add(new QuestPropertyBlock(QuestRewardMarkItem.ITEM_CHITTYPES, "Item Type Restriction", FieldType.ChitType));
				list.add(new QuestPropertyBlock(QuestRewardMarkItem.ITEM_INVENTORY, "In character's inventory", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMarkItem.SINGLE_ITEM, "Character must choose single item", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMarkItem.REMOVE, "Remove mark", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMarkItem.ITEM_ACTIVE, "Must be activated?", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMarkItem.ITEM_DEACTIVE, "Must be deactivated?", FieldType.Boolean));
				break;
			case Mesmerize:
				list.add(new QuestPropertyBlock(QuestRewardMesmerize.DIE_ROLL, "Die roll", FieldType.StringSelector, DieRollType.values()));
				list.add(new QuestPropertyBlock(QuestRewardMesmerize.REMOVE_CURSES, "Remove all curses", FieldType.Boolean));
				break;
			case MinorCharacter:
				list.add(new QuestPropertyBlock(QuestRewardMinorCharacter.MINOR_CHARACTER, "Minor character ", FieldType.SmartTextLine, quest.getMinorCharacters().toArray()));
				list.add(new QuestPropertyBlock(QuestRewardMinorCharacter.GAIN_TYPE, "Gain or lose", FieldType.StringSelector, GainType.values()));
				break;
			case MoveDenizen:
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.DENIZEN_REGEX, "Denizen", FieldType.Regex, null, new String[] { "denizen" }));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_FROM_OPTION, "Move from", FieldType.StringSelector, QuestRewardMoveDenizen.MoveFromOption.values()));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_OPTION, "Move to", FieldType.StringSelector, QuestRewardMoveDenizen.MoveOption.values()));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.CLEARING, "Clearing", FieldType.StringSelector, QuestRewardMoveDenizen.ClearingSelection.values()));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.AMOUNT, "Number of denizens (0: unlimited)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_HIRELINGS, "Move hirelings", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_COMPANIONS, "Move companions", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_SUMMONED, "Move summoned monsters", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardMoveDenizen.MOVE_LIMITED, "Move ONLY those (see above)", FieldType.Boolean));
				break;
			case NoCombat:
				break;
			case NoProwling:
				break;
			case NoSummoning:
				break;
			case Note:
				list.add(new QuestPropertyBlock(QuestRewardNote.EVENT, "Event", FieldType.SmartTextLine));
				list.add(new QuestPropertyBlock(QuestRewardNote.NOTE, "Text", FieldType.SmartTextArea));
				break;
			case PathsPassages:
				list.add(new QuestPropertyBlock(QuestRewardPathsPassages.DISCOVERY_TYPE, "Road type to discover", FieldType.StringSelector, RoadDiscoveryType.values()));
				list.add(new QuestPropertyBlock(QuestRewardPathsPassages.DISCOVERY_SCOPE, "Scope of discovery", FieldType.StringSelector, MapScopeType.values()));
				list.add(new QuestPropertyBlock(QuestRewardPathsPassages.LOCATION_ONLY, "Only in location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardPathsPassages.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case Phantasm:
				list.add(new QuestPropertyBlock(QuestRewardPhantasm.REMOVE, "Remove phantasm", FieldType.Boolean));
				break;
			case PowerOfThePit:
				list.add(new QuestPropertyBlock(QuestRewardPowerOfThePit.DIE_ROLL, "Die roll", FieldType.StringSelector, DieRollType.values()));
				break;
			case RegenerateDenizen:
				list.add(new QuestPropertyBlock(QuestRewardRegenerateDenizen.DENIZEN_REGEX, "Denizen", FieldType.Regex, null, new String[] { "denizen" }));
				list.add(new QuestPropertyBlock(QuestRewardRegenerateDenizen.DENIZEN_AMOUNT, "Number of denizens (0: unlimited)", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardRegenerateDenizen.CHARACTERS_CLEARING, "In characters clearing only", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardRegenerateDenizen.CHARACTERS_TILE, "In characters tile only", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardRegenerateDenizen.REGENERATE_HIRELINGS, "Regenerate hirelings as well", FieldType.Boolean));
				break;	
			case RelationshipChange:
				list.add(new QuestPropertyBlock(QuestRewardRelationshipSet.NATIVE_GROUP, "Native group", FieldType.StringSelector, getRelationshipNames()));
				list.add(new QuestPropertyBlock(QuestRewardRelationshipChange.GAIN_TYPE, "Gain or lose", FieldType.StringSelector, GainType.values()));
				list.add(new QuestPropertyBlock(QuestRewardRelationshipChange.RELATIONSHIP_CHANGE, "Levels of Friendliness", FieldType.Number));
				break;
			case RelationshipSet:
				list.add(new QuestPropertyBlock(QuestRewardRelationshipSet.NATIVE_GROUP, "Native group", FieldType.StringSelector, getRelationshipNames()));
				list.add(new QuestPropertyBlock(QuestRewardRelationshipSet.RELATIONSHIP_SET, "Relationship to set", FieldType.StringSelector, RelationshipType.RelationshipNames));
				break;
			case Repair:
				list.add(new QuestPropertyBlock(QuestRewardRepair.ITEM, "Items (all if empty)", FieldType.Regex, null, new String[] { "armor,item" }));
				break;
			case ResetQuest:
				list.add(new QuestPropertyBlock(QuestRewardResetQuest.NOT_RESET_FOR_LOCATIONS, "Don't reset locations", FieldType.Boolean));
				break;
			case ResetQuestLocations:
				list.add(new QuestPropertyBlock(QuestRewardResetQuestLocations.RESET_ALL_LOCATIONS, "Reset all locations", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardResetQuestLocations.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case ResetQuestSteps:
				list.add(new QuestPropertyBlock(QuestRewardResetQuestSteps.RESET_METHOD, "Reset mode", FieldType.StringSelector, QuestRewardResetQuestSteps.ResetMethod.values()));
				list.add(new QuestPropertyBlock(QuestRewardResetQuestSteps.RESET_DEPENDENT_QUEST_STEPS, "Reset steps requiring resetted steps", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardResetQuestSteps.RESET_DEPENDENT_FAILED_QUEST_STEPS, "Reset steps requiring resetted steps as failed", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardResetQuestSteps.READY_RESETTED_STEPS, "Ready resetted steps", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardResetQuestSteps.QUEST_STEPS_DEPTH, "Cascade 'depth'", FieldType.NumberAll));
				list.add(new QuestPropertyBlock(QuestRewardResetQuestSteps.QUEST_STEP_NAME, "Quest Step", FieldType.StringSelector, quest.getSteps().toArray()));
				break;
			case ResetQuestToDeck:
				break;
			case Rest:
				list.add(new QuestPropertyBlock(QuestRewardRest.AMOUNT, "Number of asterisks", FieldType.Number));
				break;
			case ScareMonsters:
				break;
			case SpellEffectOnCharacter:
				list.add(new QuestPropertyBlock(QuestRewardSpellEffectOnCharacter.SPELL, "Spell", FieldType.StringSelector, QuestRewardSpellEffectOnCharacter.EffectOnCharacter.values()));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffectOnCharacter.REMOVE, "Remove spell", FieldType.Boolean));
				break;
			case SpellEffectOnTile:
				list.add(new QuestPropertyBlock(QuestRewardSpellEffectOnTile.SPELL, "Spell", FieldType.StringSelector, QuestRewardSpellEffectOnTile.EffectOnTile.values()));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffectOnTile.REMOVE, "Remove spell", FieldType.Boolean));
				break;
			case SpellEffectSummon:
				list.add(new QuestPropertyBlock(QuestRewardSpellEffectSummon.SUMMON_TYPE, "Spell", FieldType.StringSelector, SpellUtility.SummonType.values()));
				list.add(new QuestPropertyBlock(QuestRewardSpellEffectSummon.REMOVE, "Unsummon creatures", FieldType.Boolean));
				break;
			case SpellFromSite:
				list.add(new QuestPropertyBlock(QuestRewardTreasureFromSite.SITE_REGEX, "Site RegEx", FieldType.Regex, null, new String[] { "spell_site","visitor,!name=Scholar","artifact","book,magic" }));
				list.add(new QuestPropertyBlock(QuestRewardTreasureFromSite.DRAW_TYPE, "Draw Type", FieldType.StringSelector, DrawType.values()));
				break;
			case StripInventory:
				list.add(new QuestPropertyBlock(QuestRewardStripInventory.STRIP_GOLD, "Strip Gold", FieldType.Boolean));
				break;
			case SummonGeneratedMonster:
				list.add(new QuestPropertyBlock(QuestRewardSummonGeneratedMonster.MONSTER_TYPE, "Monster type", FieldType.StringSelector, QuestRewardSummonGeneratedMonster.MonsterType.values()));
				list.add(new QuestPropertyBlock(QuestRewardSummonGeneratedMonster.AMOUNT, "Amount", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRewardSummonGeneratedMonster.RANDOM_CLEARING, "Random clearing of characters tile", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonGeneratedMonster.SUMMON_TO_LOCATION, "Summon to location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonGeneratedMonster.RANDOM_LOCATION, "Random clearing of location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonGeneratedMonster.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case SummonGuardian:
				list.add(new QuestPropertyBlock(QuestRewardSummonGuardian.LOCATION, "Summon Guardian for ", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case SummonMonster:
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.MONSTER_NAME, "Monster", FieldType.CompanionSelector, getAllCompanionKeyValues()));
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.SUMMON_TYPE, "Summon type", FieldType.StringSelector, QuestRewardSummonMonster.SummonType.values()));
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.RANDOM_CLEARING, "Random clearing of characters tile", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.SUMMON_TO_LOCATION, "Summon to location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.RANDOM_LOCATION, "Random clearing of location", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonMonster.LOCATION, "Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case SummonFromAppearanceToChit:
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearanceToChit.CHIT, "Chit to summon to", FieldType.Regex, null, new String[] { "warning", "sound", "treasure_location", "dwelling" }));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearanceToChit.DENIZEN, "Denizen (all if empty)", FieldType.Regex, null, new String[] { "vulnerability", "setup_start" }));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearanceToChit.SUMMON_LIVING_DENIZENS, "Summon living denizens", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearanceToChit.MAX_DENIZENS, "Max.# of summoned denizens", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearanceToChit.MAX_DENIZEN_HOLDERS, "Max.# of monster boxes", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearanceToChit.SUMMON_TO, "Chit location", FieldType.StringSelector, QuestRewardSummonFromAppearanceToChit.SummonTo.values()));
				list.add(new QuestPropertyBlock(QuestRewardSummonFromAppearanceToChit.LOCATION, "Quest Location", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case SummonRoll:
				list.add(new QuestPropertyBlock(QuestRewardSummonRoll.DIE_ROLL, "Die roll", FieldType.StringSelector, DieRollType.values()));
				break;
			case TalkToWiseBird:
				break;
			case Teleport:
				list.add(new QuestPropertyBlock(QuestRewardTeleport.LOCATION, "Teleport to", FieldType.GameObjectWrapperSelector, quest.getLocations().toArray()));
				break;
			case TeleportChoose:
				list.add(new QuestPropertyBlock(QuestRewardTeleportChoose.TELEPORT_TYPE, "Tpye of choice", FieldType.StringSelector, SpellUtility.TeleportType.values()));
				list.add(new QuestPropertyBlock(QuestRewardTeleportChoose.REASON, "Reason in message", FieldType.TextLine));
				break;
			case Transmorph:
				list.add(new QuestPropertyBlock(QuestRewardTransmorph.TRANSMORPH_TYPE, "Transformation type", FieldType.StringSelector, QuestRewardTransmorph.TransmorphType.values()));
				list.add(new QuestPropertyBlock(QuestRewardTransmorph.DIE_ROLL, "Die roll (for Animal)", FieldType.StringSelector, DieRollType.values()));
				list.add(new QuestPropertyBlock(QuestRewardTransmorph.REVERT_TRANSFORMATION, "Revert transformation", FieldType.Boolean));
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
				list.add(new QuestPropertyBlock(QuestRewardComplete.WIN_BOQ, "Win Book of Quests game with this 'Event'", FieldType.Boolean));
				break;
			case QuestFailed:
				break;
			case QuestVps:
				list.add(new QuestPropertyBlock(QuestRewardQuestVps.AMOUNT, "Amount", FieldType.Number));
				list.add(new QuestPropertyBlock(QuestRewardQuestVps.SUBSTRACT, "Substract points?", FieldType.Boolean));
				list.add(new QuestPropertyBlock(QuestRewardQuestVps.BONUS_VP, "Set Bonus Quest Vps?", FieldType.Boolean));
				break;
			case Visitor:
				list.add(new QuestPropertyBlock(QuestRewardVisitor.VISITOR_REGEX, "Visitor RegEx", FieldType.Regex, null, new String[] { "visitor" }));
				list.add(new QuestPropertyBlock(QuestRewardVisitor.ACQUISITION_TYPE, "Method to acquire hireling", FieldType.StringSelector, ChitAcquisitionType.values()));
				break;
			case Weather:
				list.add(new QuestPropertyBlock(QuestRewardWeather.WEATHER, "Weather", FieldType.StringSelector,  new String[] {RealmCalendar.WEATHER_CLEAR, RealmCalendar.WEATHER_SHOWERS, RealmCalendar.WEATHER_STORM, RealmCalendar.WEATHER_SPECIAL}));
				break;
			case Wish:
				list.add(new QuestPropertyBlock(QuestRewardWish.DIE_ROLL, "Die roll", FieldType.StringSelector, DieRollType.values()));
				break;
		}
		return list;
	}

	private static KeyValuePair[] getAllCompanionKeyValues() {
		ArrayList<KeyValuePair> companions = new ArrayList<>();
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
		ArrayList<String> names = new ArrayList<>();
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
		ArrayList<String> names = new ArrayList<>();
		names.add(QuestConstants.CURRENT);
		names.add(QuestConstants.REMOVE);
		GamePool pool = new GamePool(realmSpeakData.getGameObjects());
		for (GameObject go : pool.find("guild")) {
			names.add(go.getName());
		}
		return names.toArray(new String[0]);
	}
}