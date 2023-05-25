package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestCounter;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.quest.QuestState;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class QuestRewardClonedQuestsComplete extends QuestReward {

	public final static String WIN_BOQ = "_win_boq";
	
	public QuestRewardClonedQuestsComplete(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {	
		GameObject questGo = getGameObject().getHeldBy();
		Quest quest = new Quest(questGo);
		for (GameObject clonedQuestGo : quest.findClones(getGameData().getGameObjects())) {
			Quest clonedQuest = new Quest(clonedQuestGo);
			completeClonedQuest(clonedQuest);
		}
	}
	
	private void completeClonedQuest(Quest clonedQuest) {
		CharacterWrapper character = clonedQuest.getOwner();
		if (character==null) return;
		clonedQuest.setState(QuestState.Complete,character.getCurrentDayKey(), character);
		if (winBookOfQuestGame()) {
			clonedQuest.setEvent(false);
		}
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameData());
		if (hostPrefs.hasPref(Constants.SR_DEDUCT_VPS)) {
			character.addDeductVPs(clonedQuest.getInt(QuestConstants.VP_REWARD));
		}
	}
	
	private boolean winBookOfQuestGame() {
		return getBoolean(WIN_BOQ);
	}
	
	public String getDescription() {
		return "Complete cloned quests.";
	}

	public RewardType getRewardType() {
		return RewardType.ClonedQuestsComplete;
	}
}