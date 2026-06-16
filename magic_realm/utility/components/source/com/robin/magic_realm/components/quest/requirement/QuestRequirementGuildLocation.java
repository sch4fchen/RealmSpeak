package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementGuildLocation extends QuestRequirement {
	public static final String GUILD = "_guild";
	public static final String CURRENT_GUILD = "_current_guild";

	public QuestRequirementGuildLocation(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		TileLocation loc = character.getCurrentLocation();
		if (loc == null || loc.clearing == null || loc.clearing.getGuild() == null) return false;
		if (currentGuildLocation() && character.getCurrentGuild()!=null && loc.clearing.getGuild().getGameObject().getName().matches(character.getCurrentGuild())) {
			return true;
		}
		if (!currentGuildLocation() && loc.clearing.getGuild().getGameObject().getName().matches(getGuildName())) {
			return true;
		}
		
		return false;
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

	@Override
	public RequirementType getRequirementType() {
		return RequirementType.GuildLocation;
	}
}