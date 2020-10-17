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
package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

/*
 * Rewards are given to a character once the quest step requirements have been completed.  Rewards can be clustered into groups, in case there is a choice of rewards to give.
 */
public abstract class QuestReward extends AbstractQuestObject {
	
	public static final String REWARD_GROUP = "_rwg";
	public static final String ALL_REWARD_GROUP = "---";
	
	public enum RewardType {
		ActivateQuest,
		AlterBlock,
		AlterHide,
		Attribute,
		Blessing,
		ChooseNextStep,
		Companion,
		//CustomTreasure, // Pick a treasure, clone, and rename it (change attributes?  could be cool... new value, new fame/not, diff weight... - pick from ALL treasures including expansion)
						// No reason I couldn't make a special dialog for this one...
		Counter,
		Damage,
		Guild,
		Heal,
		Hireling,
		Information,
		Item,
		Journal,
		KillDenizen,
		LostInventoryToDefault,
		LostInventoryToLocation,
		MarkDenizen,
		MinorCharacter,
		PathsPassages,
		QuestComplete,
		QuestFailed,
		RegenerateDenizen,
		RelationshipChange,
		RelationshipSet,
		ResetQuest,
		ScareMonsters, 
		//Skill, // special ability (adv/disadv)  The more I think about this, this could be accomplished by providing a treasure, or minor character
		SpellEffect,
		SpellFromSite,
		StripInventory,
		SummonGuardian,
		SummonMonster,
		SummonRoll,
		Teleport,
		TreasureFromHq,
		TreasureFromSite, // Select Random/Top/Bottom/Choice from a specific TL/Scholar/Dwelling (NOT minor TLs or TWTs though)
		Visitor,
		;
		public boolean isShown() {
			switch(this) {
				case Journal:
				case LostInventoryToDefault:
				case LostInventoryToLocation:
				case MarkDenizen:
					return false;
			}
			return true;
		}

		public String getDescription() {
			switch(this) {
				case ActivateQuest:				return "Activates the quest if not already active.  Primarily used for \"Questing the Realm\" gameplay option.";	
				case AlterBlock:				return "Change questing character's blocked status (from blocked to unblocked, or the other way around).";	
				case AlterHide:					return "Change questing character's hide status (from hidden to unhidden, or the other way around).";
				case Attribute:					return "Modify Fame, Notoriety, or Gold.  Can either add or subtract points/gold.";
				case Blessing:					return "Grants the character a wish.";
				case ChooseNextStep:			return "Player chooses the next step to process from those steps that follow this step, and fullfill requirements.";
				case Companion:					return "Add or remove a monster ally.";
				//case CustomTreasure:			return "Create a new treasure by taking an existing treasure, renaming it, and giving it new  base attributes.";
				case Counter:					return "Modify count value of a counter";
				case Damage:					return "Character receives fatigue or wounds.";
				case Guild	:					return "Sets the characters guild and guild level.";
				case Heal:						return "Heals action chits of the character.";
				case Hireling:					return "Add or remove a hireling.";
				case Information:				return "Displays a dialog with information in it.  This is a good way to inform the player what is happening.";
				case Item:						return "Add an item to the character inventory, or take one away (placed to location defined by 'LostInventoryToLocation/Default').  Allows for choosing items from a group.";
				case Journal:					return "Add or update a journal entry for this quest.";
				case KillDenizen:				return "Kills a denizen.";
				case LostInventoryToDefault:	return "All future lost inventory from this quest will go to wherever they started the game, including treasures.  This is the default setting.";
				case LostInventoryToLocation:	return "All future lost inventory from this quest will go to a specified location.";
				case MarkDenizen:				return "Mark a particular denizen for later reference.  This is useful if you want to make sure a character kills (for example) a particular monster.";
				case MinorCharacter:			return "Add or remove a Minor Character.  Must create Minor Characters BEFORE creating this reward.";
				case PathsPassages:				return "Discover Paths and/or Passages in the current clearing or tile.";
				case QuestComplete:				return "Tells RealmSpeak that the character has completed this quest.";
				case QuestFailed:				return "Tells RealmSpeak that the character has failed this quest.";
				case RegenerateDenizen:			return "Regenerates denizen back to the chart of appearance.";
				case RelationshipChange:		return "Modify the relationship of the character with a particular native group, or all natives in the clearing.";
				case RelationshipSet:			return "Set the relationship of the character with a particular native group, or all natives in the clearing.";
				case ResetQuest:				return "Completely resets the quest, unmarking all quest steps and journal entries.";
				case ScareMonsters:				return "Randomly move all monsters in current clearing to other clearings either in the same tile or other tiles, as defined.";
				case SpellEffect:				return "Cast a spell effect on the character and/or other targets.";
				case SpellFromSite:				return "Learn a spell from a specific site, book, artifact, or Shaman.";
				case StripInventory:			return "Removes ALL inventory and (optionally) gold from the character (placed to location defined by 'LostInventoryToLocation/Default').";
				case SummonGuardian:			return "For a specific quest location, summon the treasure site guardian (if any)";
				case SummonMonster:				return "Summon a specific Monster to the characters clearing.";
				case SummonRoll:				return "Force a monster summoning roll with a specific number.";
				case Teleport:					return "Teleport the character to a new location.  Must create a QuestLocation BEFORE creating this reward.";
				case TreasureFromSite:			return "Gain a treasure from a specific site, dwelling, or Scholar.";
				case TreasureFromHq:			return "Gain a treasure from a specific HQ.";
				case Visitor:					return "Add or remove a visitor.";
			}
			return "(No Description)";
		}
		public boolean requiresLocations() {
			return this==Teleport || this==SummonGuardian || this==LostInventoryToLocation;
		}
	}
	
	public QuestReward(GameObject go) {
		super(go);
	}
	public void init() {
		setName("Reward");
		getGameObject().setThisAttribute(Quest.QUEST_REWARD,getRewardType().toString());
	}
	public String toString() {
		return getDescription();
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
	
	public void setRewardGroup(String val) {
		setString(REWARD_GROUP,val);
	}
	
	public String getRewardGroup() {
		return getString(REWARD_GROUP);
	}
		
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		// override if IDs need to be updated!
	}

	public void lostItem(GameObject toRemove) {
		Quest quest = getParentQuest();
		RewardType lostInventoryRule = quest.getLostInventoryRule();
		if (lostInventoryRule==RewardType.LostInventoryToDefault) {
			lostItemToDefault(toRemove);
		}
		else if (lostInventoryRule==RewardType.LostInventoryToLocation) {
			QuestLocation location = quest.getLostInventoryLocation();
			lostItemToLocation(toRemove,location);
		}
	}
	public void lostItemToDefault(GameObject go) {
		GameObject originalSetupOwner = go.getGameObjectFromAttribute("this",Constants.SETUP);
		if (originalSetupOwner!=null) {
			originalSetupOwner.add(go);
		}
		else {
			go.detach(); // Might happen in the quest tester
			RealmLogging.logMessage(QuestConstants.QUEST_ERROR,"Unable to identify setup start for "+go.getName()+" when removing item for quest \""+getParentQuest().getName()+"\".");
		}
	}
	
	private void lostItemToLocation(GameObject go,QuestLocation location) {
		ArrayList<TileLocation> validLocations = new ArrayList<TileLocation>();
		validLocations = location.fetchAllLocations(getGameData());
		if(validLocations.isEmpty()) {
			RealmLogging.logMessage(QuestConstants.QUEST_ERROR,"Item "+go.getName()+" didn't get moved to QuestLocation "+location.getName()+" for some reason...");
			return;
		}
		int random = RandomNumber.getRandom(validLocations.size());
		TileLocation tileLocation = validLocations.get(random);
		
		ArrayList<RealmComponent> clearingComponents = tileLocation.clearing.getClearingComponents();
		if (!clearingComponents.isEmpty()) {
			for (RealmComponent rc : clearingComponents) {
				if (rc.isTreasureLocation() || rc.isDwelling() || rc.isVisitor()) {
					rc.getGameObject().add(go);
					return;
				}
			}
		}
		ClearingUtility.moveToLocation(go,tileLocation);
	}

	public abstract void processReward(JFrame frame,CharacterWrapper character);
	public abstract RewardType getRewardType();
	public abstract String getDescription();
	
	/**
	 * Override this to supply an icon.
	 */
	public ImageIcon getIcon() {
		return null;
	}
	
	public static QuestReward getReward(RewardType type,GameObject go) {
		QuestReward reward = null;
		switch(type) {
			case ActivateQuest:
				reward = new QuestRewardActivateQuest(go);
				break;
			case AlterBlock:
				reward = new QuestRewardAlterBlock(go);
				break;
			case AlterHide:
				reward = new QuestRewardAlterHide(go);
				break;
			case Attribute:
				reward = new QuestRewardAttribute(go);
				break;
			case Blessing:
				reward = new QuestRewardBlessing(go);
				break;
			case ChooseNextStep:
				reward = new QuestRewardChooseNextStep(go);
				break;
			case Companion:
				reward = new QuestRewardCompanion(go);
				break;
			case Counter:
				reward = new QuestRewardCounter(go);
				break;
			case Damage:
				reward = new QuestRewardDamage(go);
				break;
			case Guild:
				reward = new QuestRewardGuild(go);
				break;
			case Heal:
				reward = new QuestRewardHeal(go);
				break;
			case Hireling:
				reward = new QuestRewardHireling(go);
				break;
			case Information:
				reward = new QuestRewardInformation(go);
				break;
			case Item:
				reward = new QuestRewardItem(go);
				break;
			case Journal:
				reward = new QuestRewardJournal(go);
				break;
			case KillDenizen:
				reward = new QuestRewardKillDenizen(go);
				break;	
			case LostInventoryToDefault:
				reward = new QuestRewardLostInventoryToDefault(go);
				break;
			case LostInventoryToLocation:
				reward = new QuestRewardLostInventoryToLocation(go);
				break;
			case MarkDenizen:
				reward = new QuestRewardMarkDenizen(go);
				break;
			case MinorCharacter:
				reward = new QuestRewardMinorCharacter(go);
				break;
			case PathsPassages:
				reward = new QuestRewardPathsPassages(go);
				break;
			case QuestComplete:
				reward = new QuestRewardComplete(go);
				break;
			case QuestFailed:
				reward = new QuestRewardFailed(go);
				break;
			case RegenerateDenizen:
				reward = new QuestRewardRegenerateDenizen(go);
				break;
			case RelationshipChange:
				reward = new QuestRewardRelationshipChange(go);
				break;
			case RelationshipSet:
				reward = new QuestRewardRelationshipSet(go);
				break;
			case ResetQuest:
				reward = new QuestRewardResetQuest(go);
				break;
			case ScareMonsters:
				reward = new QuestRewardScareMonsters(go);
				break;
			case SpellEffect:
				reward = new QuestRewardSpellEffect(go);
				break;
			case SpellFromSite:
				reward = new QuestRewardSpellFromSite(go);
				break;
			case StripInventory:
				reward = new QuestRewardStripInventory(go);
				break;
			case SummonGuardian:
				reward = new QuestRewardSummonGuardian(go);
				break;
			case SummonMonster:
				reward = new QuestRewardSummonMonster(go);
				break;
			case SummonRoll:
				reward = new QuestRewardSummonRoll(go);
				break;
			case Teleport:
				reward = new QuestRewardTeleport(go);
				break;
			case TreasureFromHq:
				reward = new QuestRewardTreasureFromHq(go);
				break;
			case TreasureFromSite:
				reward = new QuestRewardTreasureFromSite(go);
				break;
			case Visitor:
				reward = new QuestRewardVisitor(go);
				break;
			default:
				throw new IllegalArgumentException("Unsupported RewardType: "+type.toString());
		}
		return reward;
	}
	public static void main(String[] args) {
		for(RewardType rt:RewardType.values()) {
			StringBuilder sb = new StringBuilder();
			sb.append("<tr><th valign=\"top\">");
			sb.append(rt.toString());
			sb.append("</th><td>");
			sb.append(rt.getDescription());
			sb.append("</td></tr><br>");
			System.out.println(sb.toString());
		}
	}
	public int getDieRoll(DieRollType dieRoll) {
		switch (dieRoll) {
			case One:
				return 1;
			case Two:
				return 2;
			case Three:
				return 3;
			case Four:
				return 4;
			case Five:
				return 5;
			case Six:
				return 6;
			default:
				return RandomNumber.getDieRoll(6);
		}
	}
}