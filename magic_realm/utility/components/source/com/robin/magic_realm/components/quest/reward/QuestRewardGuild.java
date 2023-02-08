package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardGuild extends QuestReward {
	public static final String GUILD = "_guild";
	public static final String GUILD_CHANGE = "_guild_change";
	public static final String GUILD_LEVEL = "_guild_lvl";
	
	public enum GuildGainType {
		Increase,
		Decrease,
		Set
	}
	
	public QuestRewardGuild(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		if (getGuildName().matches(QuestConstants.REMOVE)) {
			character.clearGuild();
			return;
		}
		if (!getGuildName().matches(QuestConstants.CURRENT)) {
			character.setCurrentGuild(getGuildName());
		}
		
		switch (getGuildChange()) {
		case Increase:
			character.setCurrentGuildLevel(character.getCurrentGuildLevel()+1);
			break;
		case Decrease:
			character.setCurrentGuildLevel(character.getCurrentGuildLevel()-1);
			break;
		case Set:
			character.setCurrentGuildLevel(getGuildLevel());
		}	
	}	
		
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		if (getGuildName().matches(QuestConstants.REMOVE)) {
			return "Removes the characters guild membership.";
		}
		switch (getGuildChange()) {
		case Increase:
			sb.append("Increase ");
			break;
		case Decrease:
			sb.append("Deacrease ");
			break;
		case Set:
			sb.append("Set ");
		}
		if (getGuildName().matches(QuestConstants.CURRENT)) {
			sb.append("characters current guild level");
		}
		else {
			sb.append("characters guild level of "+getGuildName());
		}
		if (getGuildChange() == GuildGainType.Set) {
			sb.append(" to "+getGuildLevel());
		}
		
		sb.append(".");
		return sb.toString();
	}
	public RewardType getRewardType() {
		return RewardType.Guild;
	}
	private GuildGainType getGuildChange() {
		return GuildGainType.valueOf(getString(GUILD_CHANGE));
	}
	private int getGuildLevel() {
		return getInt(GUILD_LEVEL);
	}
	private String getGuildName() {
		return getString(GUILD);
	}
}