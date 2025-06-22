package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestState;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class QuestRewardComplete extends QuestReward {

	public final static String WIN_BOQ = "_win_boq";
	
	public QuestRewardComplete(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		getParentQuest().setState(QuestState.Complete,character.getCurrentDayKey(), character);
		if (winBookOfQuestGame()) {
			getParentQuest().setEvent(false);
		}
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameData());
		if (hostPrefs.hasPref(Constants.SR_DEDUCT_VPS) && !hostPrefs.hasPref(Constants.EXP_DEVELOPMENT_SR)) {
			character.addDeductVPs(getParentQuest().getInt(QuestConstants.VP_REWARD));
		}
	}
	
	private boolean winBookOfQuestGame() {
		return getBoolean(WIN_BOQ);
	}
	
	public String getDescription() {
		return "Quest Complete";
	}

	public RewardType getRewardType() {
		return RewardType.QuestComplete;
	}
}