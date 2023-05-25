package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestCounter;
import com.robin.magic_realm.components.quest.QuestState;
import com.robin.magic_realm.components.quest.reward.QuestReward.RewardType;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class QuestRewardClonedQuestsFailed extends QuestReward {

	public final static String WIN_BOQ = "_win_boq";
	
	public QuestRewardClonedQuestsFailed(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		GameObject questGo = getGameObject().getHeldBy();
		Quest quest = new Quest(questGo);
		for (GameObject clonedQuestGo : quest.findClones(getGameData().getGameObjects())) {
			Quest clonedQuest = new Quest(clonedQuestGo);
			if (clonedQuest.getOwner()==null) continue;
			clonedQuest.setState(QuestState.Failed,clonedQuest.getOwner().getCurrentDayKey(), clonedQuest.getOwner());
		}
	}
	
	public String getDescription() {
		return "Fail cloned quests.";
	}

	public RewardType getRewardType() {
		return RewardType.ClonedQuestsFailed;
	}
}