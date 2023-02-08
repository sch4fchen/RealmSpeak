package com.robin.magic_realm.components.quest.requirement;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementGuild extends QuestRequirement {

	public static final String GUILD = "_gld";
	public static final String GUILD_LEVEL = "_gld_lvl";
	public static final String EXCEED_LEVEL = "_excd_lvl";
	public static final String SUBCEED_LEVEL = "_sucd_lvl";
	
	public QuestRequirementGuild(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (character.getCurrentGuild() != null && (getGuildName().isEmpty() || Pattern.compile(getGuildName()).matcher(character.getCurrentGuild()).find())) {
			if (exceedAllowed()) {
				return character.getCurrentGuildLevel() >= getGuildLevel();
			}
			if (subceedAllowed()) {
				return character.getCurrentGuildLevel() <= getGuildLevel();
			}
			return character.getCurrentGuildLevel() == getGuildLevel();
			
		}
		return false;
	}

	protected String buildDescription() {
		if (getGuildName().isEmpty()) {
			return "Character must be level "+getGuildLevel()+" member in any guild.";
		}
		return "Character must be level "+getGuildLevel()+" member in the "+getGuildName()+".";
	}
	public RequirementType getRequirementType() {
		return RequirementType.Guild;
	}
	private int getGuildLevel() {
		return getInt(GUILD_LEVEL);
	}
	private String getGuildName() {
		return getString(GUILD);
	}
	private boolean exceedAllowed() {
		return getBoolean(EXCEED_LEVEL);
	}
	private boolean subceedAllowed() {
		return getBoolean(SUBCEED_LEVEL);
	}
}