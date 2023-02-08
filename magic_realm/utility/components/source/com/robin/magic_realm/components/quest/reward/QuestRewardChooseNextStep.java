package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardChooseNextStep extends QuestReward {
	
	public static final String TEXT = "_tx";
	public static final String RANDOM = "_rnd";

	public QuestRewardChooseNextStep(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		QuestRequirementParams params = new QuestRequirementParams();
		params.timeOfCall = character.getCurrentGamePhase();
		
		ArrayList<QuestStep> dependentSteps = new ArrayList<QuestStep>();
		for(QuestStep step:Quest.currentQuest.getSteps()) {
			if (step.getState()!=QuestStepState.Pending) continue;
			if (step.requires(QuestStep.currentStep)) {
				dependentSteps.add(step);
			}
		}
		if (dependentSteps.isEmpty()) return;
		
		String dayKey = character.getCurrentDayKey();
		String stepName=null;
		
		ArrayList<String> availableSteps = new ArrayList<String>();
		for(QuestStep step:dependentSteps) {
			if (step.fulfillsRequirements(frame,character,params)) {
				availableSteps.add(step.getName());
			}
		}
		
		if (randomNextStep()) {
			int random = RandomNumber.getRandom(availableSteps.size());
			stepName = availableSteps.get(random);
		}
		else {
			RealmComponent rc = RealmComponent.getRealmComponent(Quest.currentQuest.getGameObject());
			ButtonOptionDialog dialog = new ButtonOptionDialog(frame,rc.getIcon(),getString(TEXT),"Choose",false);
			for(String availableStepName : availableSteps) {
				dialog.addSelectionObject(availableStepName);
			}
			if (dialog.getSelectionObjectCount()>0) {
				dialog.setVisible(true);
				stepName = (String)dialog.getSelectedObject();
			}
		}
		
		for(QuestStep step:dependentSteps) {
			if (!step.getName().equals(stepName)) {
				step.setState(QuestStepState.Failed,dayKey);
			}
		}
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.ChooseNextStep;
	}

	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Choose ");
		if (randomNextStep()) {
			sb.append("randomly ");
		}
		sb.append("a path from steps dependent on this step.");
		return sb.toString();
	}
	
	private boolean randomNextStep() {
		return getBoolean(RANDOM);
	}
}