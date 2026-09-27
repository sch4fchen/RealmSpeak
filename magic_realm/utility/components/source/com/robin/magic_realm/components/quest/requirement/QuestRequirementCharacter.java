package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.GenderType;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementCharacter extends QuestRequirement {
	
	public static final String CHARACTER_REGEX = "_regex";
	public static final String SAME_TILE = "_tile";
	public static final String MARK = "_mark";
	public static final String NO_MARK = "_no_mark";
	public static final String ADD_MARK = "_add_mark";
	public static final String REMOVE_MARK = "_remove_mark";
	public static final String GUILD = "_guild";
	public static final String GENDER = "_guild";
	public static final String FIGHTER = "_guild";
	public static final String MAGIC_USER = "_guild";

	public QuestRequirementCharacter(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		TileLocation loc = character.getCurrentLocation();
		if (loc==null) return false;
		if (!sameTile() && !loc.isInClearing()) return false;
		Pattern pattern = Pattern.compile(getRegExFilter());
		String questId = getParentQuest().getGameObject().getStringId();
		ArrayList<RealmComponent> allCharactersFound = new ArrayList<>();
		ArrayList<RealmComponent> correctCharactersFound = new ArrayList<>();
		if (sameTile()) {
			for (ClearingDetail cl : loc.tile.getClearings()) {
				for (RealmComponent characterRc : cl.getClearingComponents()) {
					if (!characterRc.isCharacter()) continue;
					allCharactersFound.add(characterRc);
				}
			}
		} else {
			for (RealmComponent characterRc : loc.clearing.getClearingComponents()) {
				if (!characterRc.isTraveler()) continue;
				allCharactersFound.add(characterRc);
			}
		}
		if (!allCharactersFound.isEmpty()) {
			for (RealmComponent characterRc : allCharactersFound) {
				if (getRegExFilter().isEmpty() || pattern.matcher(characterRc.getGameObject().getName()).find()) {
					if (requiresMark() && !Quest.GameObjectHasQuestMark(characterRc.getGameObject(), questId)) continue;
					if (requiresNoMark() && Quest.GameObjectHasQuestMark(characterRc.getGameObject(), questId)) continue;
					CharacterWrapper characterWrapper = new CharacterWrapper(characterRc.getGameObject());
					if (!requiredGuild().matches(QuestConstants.ANY)) {
						if (requiredGuild().matches(QuestConstants.NONE)) {
							if (characterWrapper.getCurrentGuild()!=null) return false;
						}
						if (requiredGuild().matches(QuestConstants.MEMBER)) {
							if (characterWrapper.getCurrentGuild()==null) return false;
						}
						if (!requiredGuild().matches(QuestConstants.NONE) && !requiredGuild().matches(QuestConstants.MEMBER)) {
							if (characterWrapper.getCurrentGuild()==null || !characterWrapper.getCurrentGuild().matches(requiredGuild())) return false;
						}
					}
					if (!requiredGender().matches(QuestConstants.ANY)) {
						if (requiredGender().matches(GenderType.Female.toString()) && !characterWrapper.isFemale()) return false;
						if (requiredGender().matches(GenderType.Male.toString()) && !characterWrapper.isMale()) return false;
					}
					if (mustBeAFighter() && !characterWrapper.isFighter()) return false;
					if (mustBeAMagicUser() && !characterWrapper.isMagicUser()) return false;
					correctCharactersFound.add(characterRc);
				}
			}
			if (!correctCharactersFound.isEmpty()) {
				for (RealmComponent characterRc : correctCharactersFound) {
					if (addMark()) {
						Quest.GameObjectAddQuestMark(characterRc.getGameObject(), questId);
					}
					if (removeMark()) {
						Quest.GameObjectRemoveQuestMark(characterRc.getGameObject(), questId);
					}
				}
				return true;
			}
		}
		return false;
	}
	
	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must be in the same");
		if (sameTile()) {
			sb.append(" tile as");
		} else {
			sb.append(" clearing as");
		}
		if (requiresMark()) {
			sb.append(" a marked");
		}
		sb.append(" character");
		if (!getRegExFilter().isEmpty()) {
			sb.append("with the name: "+getRegExFilter());
		}
		else {
			sb.append(".");
		}
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Character;
	}
	
	private String getRegExFilter() {
		return getString(CHARACTER_REGEX).trim();
	}
	private boolean sameTile() {
		return getBoolean(SAME_TILE);
	}
	public boolean requiresMark() {
		return getBoolean(MARK);
	}
	public boolean requiresNoMark() {
		return getBoolean(NO_MARK);
	}
	private String requiredGuild() {
		return getString(GUILD);
	}
	private String requiredGender() {
		return getString(GENDER);
	}
	private boolean mustBeAFighter() {
		return getBoolean(FIGHTER);
	}
	private boolean mustBeAMagicUser() {
		return getBoolean(MAGIC_USER);
	}
	public boolean addMark() {
		return getBoolean(ADD_MARK);
	}
	public boolean removeMark() {
		return getBoolean(REMOVE_MARK);
	}
}