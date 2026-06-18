package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardDiscardOption extends QuestReward {

	public static final String OPTION = "_option";

	public QuestRewardDiscardOption(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		getParentQuest().getGameObject().removeThisAttribute(QuestConstants.DISCARD_ALWAYS);
		getParentQuest().getGameObject().removeThisAttribute(QuestConstants.DISCARD_NEVER);
		if (!getOption().matches(QuestConstants.DISCARD_NORMAL)) {
			getParentQuest().getGameObject().setThisAttribute(getOption());
		}
	}

	public String getOption() {
		return getString(OPTION);
	}

	public RewardType getRewardType() {
		return RewardType.DiscardOption;
	}

	public String getDescription() {
		String option = getOption();
		StringBuilder sb = new StringBuilder();
		sb.append("Character can");
		if (option.matches(QuestConstants.DISCARD_ALWAYS)) {
			sb.append(" always");
		} else if (option.matches(QuestConstants.DISCARD_NEVER)) {
			sb.append(" never");
		}
		sb.append(" discard the quest");
		if (option.matches(QuestConstants.DISCARD_NORMAL)) {
			sb.append(" normally");
		}
		sb.append(".");
		return sb.toString();
	}
}