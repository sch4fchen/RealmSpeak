/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
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
			step.clearStates();
			step.setState(QuestStepState.Pending, currentDay);
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
}