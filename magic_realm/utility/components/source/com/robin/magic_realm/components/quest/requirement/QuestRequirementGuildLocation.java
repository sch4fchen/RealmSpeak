package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementGuildLocation extends QuestRequirement {
	public static final String GUILD = "_guild";
	public static final String CURRENT_GUILD = "_current_guild";
	public static final String REQ_MARK = "_req_mark";
	public static final String REQ_NO_MARK = "_req_no_mark";
	public static final String ADD_MARK = "_add_mark";
	public static final String REMOVE_MARK = "remove_mark";

	public QuestRequirementGuildLocation(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		TileLocation loc = character.getCurrentLocation();
		if (loc == null || loc.clearing == null || loc.clearing.getGuild() == null) return false;
		GameObject guild = loc.clearing.getGuild().getGameObject();
		if (requiresMark() && !Quest.GameObjectHasQuestMark(guild, getParentQuest().getGameObject().getStringId())) return false;
		if (requiresNoMark() && Quest.GameObjectHasQuestMark(guild, getParentQuest().getGameObject().getStringId())) return false;
		if (currentGuildLocation() && character.getCurrentGuild()!=null && guild.getName().matches(character.getCurrentGuild())) {
			updateGuildMark(guild);
			return true;
		}
		if (!currentGuildLocation() && guild.getName().matches(getGuildName())) {
			updateGuildMark(guild);
			return true;
		}
		
		return false;
	}
	
	private void updateGuildMark(GameObject guild) {
		if (addMark()) {
			Quest.GameObjectAddQuestMark(guild, getParentQuest().getGameObject().getStringId());
		}
		if (removeMark()) {
			Quest.GameObjectRemoveQuestMark(guild, getParentQuest().getGameObject().getStringId());
		}
	}
	
	protected String buildDescription() {
		if (currentGuildLocation()) {
			return "Character must be in the clearing with his current Guild.";
		}
		return "Character must be in the clearing with the "+getGuildName();
	}

	public String getGuildName() {
		return getString(GUILD);
	}
	
	public boolean currentGuildLocation() {
		return getBoolean(CURRENT_GUILD);
	}
	
	public boolean requiresMark() {
		return getBoolean(REQ_MARK);
	}
	public boolean requiresNoMark() {
		return getBoolean(REQ_NO_MARK);
	}
	public boolean addMark() {
		return getBoolean(ADD_MARK);
	}
	public boolean removeMark() {
		return getBoolean(REMOVE_MARK);
	}

	public RequirementType getRequirementType() {
		return RequirementType.GuildLocation;
	}
}