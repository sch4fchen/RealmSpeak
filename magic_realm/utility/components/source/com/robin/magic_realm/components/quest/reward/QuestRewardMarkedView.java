package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMarkedView extends QuestReward {	
	public static final String OPTION = "_option";
	public static final String ENABLE = "Enable";
	public static final String DISABLE = "Disable";
	public static final String NO_SECRETS = "_no_secrets";
	public static final String TITLE = "_title";

	public QuestRewardMarkedView(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		if (enabled()) {
			getParentQuest().getGameObject().setThisAttribute(QuestConstants.MAKRED_VIEW);
			if (noSecrets()) {
				getParentQuest().getGameObject().setThisAttribute(QuestConstants.MAKRED_VIEW_NO_SECRETS);
			} else {
				getParentQuest().getGameObject().removeThisAttribute(QuestConstants.MAKRED_VIEW_NO_SECRETS);
			}
			if (title()!=null && !title().isEmpty()) {
				getParentQuest().getGameObject().setThisAttribute(QuestConstants.MAKRED_VIEW_TITLE,title());
			} else {
				getParentQuest().getGameObject().removeThisAttribute(QuestConstants.MAKRED_VIEW_TITLE);
			}
			return;
		}
		getParentQuest().getGameObject().removeThisAttribute(QuestConstants.MAKRED_VIEW);
		getParentQuest().getGameObject().removeThisAttribute(QuestConstants.MAKRED_VIEW_NO_SECRETS);
		getParentQuest().getGameObject().removeThisAttribute(QuestConstants.MAKRED_VIEW_TITLE);
	}

	public RewardType getRewardType() {
		return RewardType.MarkedView;
	}

	public String getDescription() {
		if (enabled()) {
			return "Enable the Marked Things view";
		}
		return "Disable the Marked Things view";
	}
	
	private boolean enabled() {
		return getString(OPTION).matches(ENABLE);
	}
	
	private boolean noSecrets() {
		return getBoolean(NO_SECRETS);
	}
	
	private String title() {
		return getString(TITLE);
	}
}