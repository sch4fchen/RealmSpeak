package com.robin.magic_realm.components.quest.requirement;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.GenderType;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementStealing extends QuestRequirement {
	
	public enum VictimType {
		Any,
		Native,
		NativeNotControlled,
		Character,
		HiredLeader,
		CharacterOrHiredLeader
		;
	}
	
	public enum ItemType {
		Any,
		Gold,
		Mount,
		Armor,
		Weapon,
		Treasure,
		;
	}
	
	public static final String VICTIM = "_victim";
	public static final String VICTIM_REGEX = "_victim_regex";
	public static final String VICTIM_REQ_MARK = "_victim_req_mark";
	public static final String VICTIM_ADD_MARK = "_victim_add_mark";
	public static final String VICTIM_REMOVE_MARK = "_victim_remove_mark";
	public static final String VICTIM_GUILD = "_victim_guild";
	public static final String VICTIM_GENDER = "_victim_gender";
	public static final String VICTIM_FIGHTER = "_victim_fighter";
	public static final String VICTIM_MAGIC_USER = "_victim_magic_user";
	public static final String ITEM_TYPE = "_item_type";
	public static final String ITEM_REQ_MARK = "_item_req_mark";
	public static final String ITEM_ADD_MARK = "_item_add_mark";
	public static final String ITEM_REMOVE_MARK = "_item_remove_mark";
	
	public QuestRequirementStealing(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (reqParams.actionType!=CharacterActionType.Stealing) return false;
		GameObject victim = reqParams.targetOfSearch;
		RealmComponent victimRc = RealmComponent.getRealmComponent(victim);
		if (getVictim()==VictimType.Native && !victimRc.isNativeLeader()) return false;
		if (getVictim()==VictimType.NativeNotControlled && (!victimRc.isNativeLeader() || victimRc.isControlledNative())) return false;
		if (getVictim()==VictimType.Character && !victimRc.isCharacter()) return false;
		if (getVictim()==VictimType.HiredLeader && !victimRc.isHiredLeader()) return false;
		if (getVictim()==VictimType.CharacterOrHiredLeader && (!victimRc.isCharacter() || !victimRc.isHiredLeader())) return false;
		Pattern pattern = Pattern.compile(getVictimRegEx());
		if (!getVictimRegEx().isEmpty() && !pattern.matcher(victim.getName()).find()) return false;
		String questId = getParentQuest().getGameObject().getStringId();
		if (victimRequiresMark() && !Quest.GameObjectHasQuestMark(victim, questId));
		if (!victimGuild().matches(QuestRequirement.ANY) && victimRc.isCharacter()) {
			CharacterWrapper victimCharacter = new CharacterWrapper(victim);
			if (victimGuild().matches(QuestRequirement.NONE)) {
				if (victimCharacter.getCurrentGuild()!=null) return false;
			}
			if (victimGuild().matches(QuestRequirement.MEMBER)) {
				if (victimCharacter.getCurrentGuild()==null) return false;
			}
			if (!victimGuild().matches(QuestRequirement.NONE) && !victimGuild().matches(QuestRequirement.MEMBER)) {
				if (victimCharacter.getCurrentGuild()==null || !victimCharacter.getCurrentGuild().matches(victimGuild())) return false;
			}
		}
		if (!victimGender().matches(QuestRequirement.ANY) && victimRc.isCharacter()) {
			CharacterWrapper victimCharacter = new CharacterWrapper(victim);
			if (victimGender().matches(GenderType.Female.toString()) && !victimCharacter.isFemale()) return false;
			if (victimGender().matches(GenderType.Female.toString()) && !victimCharacter.isFemale()) return false;
		}
		if (victimMustBeAFighter() && victimRc.isCharacter() && !(new CharacterWrapper(victim).isFighter())) return false;
		if (victimMustBeAMagicUser() && victimRc.isCharacter() && !(new CharacterWrapper(victim).isFighter())) return false;
		
		GameObject stolenItem = null;
		if (reqParams.objectList !=null && !reqParams.objectList.isEmpty()) {
			stolenItem = reqParams.objectList.get(0);
		}
		if (itemType()!=ItemType.Any) {
			if (itemType()==ItemType.Gold) {
				if (stolenItem!=null || reqParams.searchHadAnEffect==false) return false;
			}
			if (stolenItem==null) return false;
			RealmComponent stolenItemRc = RealmComponent.getRealmComponent(stolenItem);
			if (itemType()==ItemType.Mount) {
				if (!stolenItemRc.isHorse()) return false;
			}
			if (itemType()==ItemType.Armor) {
				if (!stolenItemRc.isArmor()) return false;
			}
			if (itemType()==ItemType.Weapon) {
				if (!stolenItemRc.isWeapon() && (!stolenItemRc.isTreasure() || !stolenItem.hasThisAttribute("atttack") || stolenItem.hasThisAttribute(Constants.POTION))) return false;
			}
			if (itemType()==ItemType.Treasure) {
				if (!stolenItemRc.isTreasure()) return false;
			}
		}
		if (itemRequiresMark() && (stolenItem==null || !Quest.GameObjectHasQuestMark(stolenItem, questId))) return false;
		// check properties of item
		// check if gold stolen: (=searchHadAnEffect, but no item)
		
		if (itemAddMark() && stolenItem!=null) {
			Quest.GameObjectAddQuestMark(stolenItem, questId);
		}
		if (itemRemoveMark() && stolenItem!=null) {
			Quest.GameObjectAddQuestMark(stolenItem, questId);
		}
		
		if (victimAddMark()) {
			Quest.GameObjectAddQuestMark(victim, questId);
		}
		if (victimRemoveMark()) {
			Quest.GameObjectRemoveQuestMark(victim, questId);
		}
		
		return true;
	}

	
	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must steal.");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Stealing;
	}
	
	private VictimType getVictim() {
		return VictimType.valueOf(getString(VICTIM));
	}
	private String getVictimRegEx() {
		return getString(VICTIM_REGEX).trim();
	}
	private boolean victimRequiresMark() {
		return getBoolean(VICTIM_REQ_MARK);
	}
	private boolean victimAddMark() {
		return getBoolean(VICTIM_ADD_MARK);
	}
	private boolean victimRemoveMark() {
		return getBoolean(VICTIM_REMOVE_MARK);
	}
	private String victimGuild() {
		return getString(VICTIM_GUILD);
	}
	private String victimGender() {
		return getString(VICTIM_GENDER);
	}
	private boolean victimMustBeAFighter() {
		return getBoolean(VICTIM_FIGHTER);
	}
	private boolean victimMustBeAMagicUser() {
		return getBoolean(VICTIM_MAGIC_USER);
	}
	private ItemType itemType() {
		return ItemType.valueOf(getString(ITEM_TYPE));
	}
	private boolean itemRequiresMark() {
		return getBoolean(VICTIM_REQ_MARK);
	}
	private boolean itemAddMark() {
		return getBoolean(VICTIM_ADD_MARK);
	}
	private boolean itemRemoveMark() {
		return getBoolean(VICTIM_REMOVE_MARK);
	}
	
}