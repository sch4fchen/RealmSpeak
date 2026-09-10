package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.GenderType;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.requirement.QuestRequirement;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMarkCharacter extends QuestReward {
	
	public static final String CHARACTER_REGEX = "_regex";
	public static final String CHARACTER_GENDER = "_gender";
	public static final String FIGHTER = "_fighter";
	public static final String MAGIC_USER = "_magic_user";
	public static final String CHARACTER_GUILD = "_guild";
	public static final String CHARACTERS_CLEARING = "_ch_cl";
	public static final String CHARACTERS_TILE = "_ch_tile";
	public static final String CHOOSE_CHARACTER = "_choose_character";
	public static final String RANDOM_CHARACTER = "_rnd_character";
	public static final String REMOVE = "_rmv_mrk";
	
	public QuestRewardMarkCharacter(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		ArrayList<GameObject> characters = new ArrayList<>();
		if (charactersClearingOnly()) {
			TileLocation current = character.getCurrentLocation();
			if (!current.isInClearing()) return;
			characters = new ArrayList<>();
			for (RealmComponent rc : current.clearing.getClearingComponents()) {
				if (rc.isCharacter()) {
					characters.add(rc.getGameObject());
				}
			}
		} else if (charactersTileOnly()) {
			TileLocation current = character.getCurrentLocation();
			characters = new ArrayList<>();
			for (ClearingDetail cl : current.tile.getClearings()) {
				for (RealmComponent rc : cl.getClearingComponents()) {
					if (rc.isCharacter()) {
						characters.add(rc.getGameObject());
					}
				}
			}
		} else {
			characters.addAll(RealmUtility.getLivingCharacters(character.getGameData()));
		}
		String regex = getCharacterRegEx().trim();
		Pattern pattern = regex.length()==0?null:Pattern.compile(regex);
		ArrayList<GameObject> allCharacters = new ArrayList<>();
		String questId = getParentQuest().getGameObject().getStringId();
		for (GameObject go:characters) {
			if (pattern==null || pattern.matcher(go.getName()).find()) {
				CharacterWrapper target = new CharacterWrapper(go);
				if (!gender().matches(QuestConstants.ANY)) {
					if (gender().matches(GenderType.Female.toString()) && !target.isFemale()) continue;
					if (gender().matches(GenderType.Male.toString()) && !target.isMale()) continue;
				}
				if (mustBeAFighter() && !target.isFighter()) continue;
				if (mustBeAMagicUser() && !target.isMagicUser()) continue;
				if (!guild().matches(QuestConstants.ANY)) {
					if (guild().matches(QuestConstants.NONE)) {
						if (target.getCurrentGuild()!=null) continue;
					}
					if (guild().matches(QuestConstants.MEMBER)) {
						if (target.getCurrentGuild()==null) continue;;
					}
					if (!guild().matches(QuestConstants.NONE) && !guild().matches(QuestConstants.MEMBER)) {
						if (target.getCurrentGuild()==null || !target.getCurrentGuild().matches(guild())) continue;
					}
				}
				
				if (randomCharacter() || chooseCharacter()) {
					if (removeMark() && !Quest.GameObjectHasQuestMark(go,questId)) continue;
					if (!removeMark() && Quest.GameObjectHasQuestMark(go,questId)) continue;
					allCharacters.add(go);
				} else {
					if (removeMark()) {
						Quest.GameObjectRemoveQuestMark(go,questId);
					} else {
						Quest.GameObjectAddQuestMark(go,questId);
					}
				}
			}
		}
		if (allCharacters.size() == 0) return;
		if (chooseCharacter()) {
			RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Choose one traveler to mark:",false);
			chooser.addGameObjects(allCharacters,false);
			chooser.setVisible(true);
			RealmComponent selectedCharacter = chooser.getFirstSelectedComponent();
			if (removeMark()) {
				Quest.GameObjectRemoveQuestMark(selectedCharacter.getGameObject(),questId);
			} else {
				Quest.GameObjectAddQuestMark(selectedCharacter.getGameObject(),questId);
			}
		}
		if (randomCharacter()) {
			if (removeMark()) {
				Quest.GameObjectRemoveQuestMark(allCharacters.get(RandomNumber.getRandom(allCharacters.size())),questId);
			} else {
				Quest.GameObjectAddQuestMark(allCharacters.get(RandomNumber.getRandom(allCharacters.size())),questId);
			}
		}
	}

	public String getDescription() {
		if (randomCharacter() || chooseCharacter()) {
			StringBuffer sb = new StringBuffer();
			if (randomCharacter()) {
				sb.append("Mark a random character");
			} else {
				sb.append("Mark a character");
			}
			if (charactersClearingOnly()) {
				sb.append(" in current clearing");
			}
			if (charactersTileOnly()) {
				sb.append(" in current tile");
			}
			sb.append(".");
			return sb.toString();
		}
		StringBuffer sb = new StringBuffer();
		sb.append("Mark all characters");
		if (mustBeAFighter()) {
			if (randomCharacter() || chooseCharacter()) {
				sb.append(" who is a fighter");
			} else {
				sb.append("who are fighters");
			}
		}
		if (mustBeAMagicUser()) {
			if (randomCharacter() || chooseCharacter()) {
				sb.append(" who is a magic user");
			} else {
				sb.append("who are magic users");
			}
		}
		if (charactersClearingOnly()) {
			sb.append(" in current clearing");
		}
		if (charactersClearingOnly()) {
			sb.append(" in current clearing");
		}
		if (getCharacterRegEx()!=null && !getCharacterRegEx().isEmpty()) {
			sb.append(" matching the name: "+getCharacterRegEx());
		} else {
			sb.append(".");
		}
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.MarkCharacter;
	}
	
	public String getCharacterRegEx() {
		return getString(CHARACTER_REGEX);
	}
	
	private Boolean charactersClearingOnly() {
		return getBoolean(CHARACTERS_CLEARING);
	}
	
	private Boolean charactersTileOnly() {
		return getBoolean(CHARACTERS_TILE);
	}
	
	private Boolean chooseCharacter() {
		return getBoolean(CHOOSE_CHARACTER);
	}
	
	private Boolean randomCharacter() {
		return getBoolean(RANDOM_CHARACTER);
	}
	
	private Boolean removeMark() {
		return getBoolean(REMOVE);
	}
	
	private String guild() {
		return getString(CHARACTER_GUILD);
	}
	private String gender() {
		return getString(CHARACTER_GENDER);
	}
	private boolean mustBeAFighter() {
		return getBoolean(FIGHTER);
	}
	private boolean mustBeAMagicUser() {
		return getBoolean(MAGIC_USER);
	}
}