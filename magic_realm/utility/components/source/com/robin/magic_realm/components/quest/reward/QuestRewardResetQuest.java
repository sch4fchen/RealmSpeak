package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestState;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardResetQuest extends QuestReward {

	public static final String NOT_RESET_FOR_LOCATIONS = "_no_reset_location";
	
	public QuestRewardResetQuest(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		Quest quest = getParentQuest();
		quest.reset();
		quest.initialize(frame,character,resetLocation());
		QuestRequirementParams params = new QuestRequirementParams();
		params.timeOfCall = character.getCurrentGamePhase();
		quest.testRequirements(frame,character,params);
		quest.setState(QuestState.Assigned,character.getCurrentDayKey(), character);
	}
	
	private boolean resetLocation() {
		return !getBoolean(NOT_RESET_FOR_LOCATIONS);
	}
	
	public String getDescription() {
		return "All quest steps and journal entries are reset.";
	}

	public RewardType getRewardType() {
		return RewardType.ResetQuest;
	}
}