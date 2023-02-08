package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardResetQuestSteps extends QuestReward {
	
	public static final String RESET_METHOD = "_reset_method";
	public static final String QUEST_STEPS_DEPTH = "_depth";
	public static final String QUEST_STEP_NAME = "_step_name";
	public static final String RESET_DEPENDENT_QUEST_STEPS = "_reset_dependent_steps";
	public static final String RESET_DEPENDENT_FAILED_QUEST_STEPS = "_reset_dependent_failed_steps";
	public static final String READY_RESETTED_STEPS = "_ready_resetted_steps";

	public enum ResetMethod {
		CascadedReset,
		SingleQuestStep
	}
	
	public QuestRewardResetQuestSteps(GameObject go) {
		super(go);
	}
	
	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		String currentDay = character.getCurrentDayKey();
		ArrayList<QuestStep> stepsToReset = new ArrayList<>();
		ArrayList<QuestStep> dependentSteps = new ArrayList<>();
		Quest quest = getParentQuest();
		QuestStep currentStep = getParentStep();
		
		switch (resetMode()) {
		case SingleQuestStep:
			for (QuestStep step:quest.getSteps()) {
				if (step.toString().matches(questStepToReset())) {
					stepsToReset.add(step);
				}
			}
			break;
		case CascadedReset:
		default:
			stepsToReset.add(currentStep);
			dependentSteps.add(currentStep);		
			int i=0;
			while (i < questStepsDepthToReset()) {
				for(QuestStep step:quest.getSteps()) {
					for(QuestStep dependentStep:dependentSteps) {
						if (dependentStep.requires(step) && !stepsToReset.contains(step)) {
							stepsToReset.add(step);
						}
					}
				}
				dependentSteps.addAll(stepsToReset);
				i++;
			}
			break;
		}
		
		if (resetRequiredSteps() || resetRequiredFailedSteps()) {
			ArrayList<QuestStep> moreStepsToReset = new ArrayList<>();
			boolean newStepsToAdd = true;
			while (newStepsToAdd) {
				newStepsToAdd = false;
				for(QuestStep step:quest.getSteps()) {
					for(QuestStep resettedStep:stepsToReset) {
						if (!moreStepsToReset.contains(step) && ((resetRequiredSteps() && step.requires(resettedStep)) || (resetRequiredFailedSteps() && step.requiresFail(resettedStep)))) {
							moreStepsToReset.add(step);
							newStepsToAdd = true;
						}
					}
				}
				stepsToReset.addAll(moreStepsToReset);
			}
		}
		
		for(QuestStep step:stepsToReset) {
			if (step.getState() == QuestStepState.Pending) continue;
			step.clearStates();
			if (readyResettedSteps()) {
				step.setState(QuestStepState.Ready, currentDay);
			}
			else {
				step.setState(QuestStepState.Pending, currentDay);
			}
		}
		quest.updateStepStates(currentDay);
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.ResetQuestSteps;
	}

	@Override
	public String getDescription() {
		switch (resetMode()) {
			case SingleQuestStep:
				return questStepToReset()+" is reset.";
			case CascadedReset:
			default:
				return "Resets all quest steps depending on current step with a depth of "+questStepsDepthToReset()+". ";
		}
	}
	
	private ResetMethod resetMode() {
		return ResetMethod.valueOf(getString(RESET_METHOD));
	}
	private int questStepsDepthToReset() {
		return getInt(QUEST_STEPS_DEPTH);
	}
	private String questStepToReset() {
		return getString(QUEST_STEP_NAME);
	}
	private boolean resetRequiredSteps() {
		return getBoolean(RESET_DEPENDENT_QUEST_STEPS);
	}
	private boolean resetRequiredFailedSteps() {
		return getBoolean(RESET_DEPENDENT_FAILED_QUEST_STEPS);
	}
	private boolean readyResettedSteps() {
		return getBoolean(READY_RESETTED_STEPS);
	}
}