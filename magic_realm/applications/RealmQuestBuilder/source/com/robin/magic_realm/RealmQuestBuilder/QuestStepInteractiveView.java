package com.robin.magic_realm.RealmQuestBuilder;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.quest.QuestStepState;
import com.robin.magic_realm.components.quest.QuestStepView;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestStepInteractiveView extends QuestStepView {
	JFrame frame;
	
	public QuestStepInteractiveView(JFrame frame){
		this.frame = frame;
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				QuestStep step = getStepAtPoint(ev.getPoint());
				if (step!=null) {
					setSelectedStep(step);
					fireStateChanged();
				}
			}
		});		
	}
	
	public void fulfillRequirementsForQuestStep(Quest quest, QuestStep questStep, CharacterWrapper character) {
		ArrayList<QuestStep> questSteps = quest.getSteps();
		String dayKey = character.getCurrentDayKey();
		questStep.preemptSteps(questSteps, dayKey);
		questStep.setState(QuestStepState.Finished, dayKey);
		Quest.currentQuest = quest;
		QuestStep.currentStep = questStep;
		questStep.doRewards(frame, character);
		Quest.currentQuest = null;
		QuestStep.currentStep = null;
		quest.updateStepStates(dayKey);
	}
	
	public static void failRequirementsForQuestStep(Quest quest, QuestStep questStep, CharacterWrapper character) {
		ArrayList<QuestStep> questSteps = quest.getSteps();
		String dayKey = character.getCurrentDayKey();
		questStep.preemptSteps(questSteps, dayKey);
		questStep.setState(QuestStepState.Failed, dayKey);
		quest.updateStepStates(dayKey);
	}
	
	public static void readyQuestStep(Quest quest, QuestStep questStep, CharacterWrapper character) {
		ArrayList<QuestStep> questSteps = quest.getSteps();
		String dayKey = character.getCurrentDayKey();
		questStep.preemptSteps(questSteps, dayKey);
		questStep.setState(QuestStepState.Ready, dayKey);
		quest.updateStepStates(dayKey);
	}
	
	public static void pendQuestStep(Quest quest, QuestStep questStep, CharacterWrapper character) {
		ArrayList<QuestStep> questSteps = quest.getSteps();
		String dayKey = character.getCurrentDayKey();
		questStep.preemptSteps(questSteps, dayKey);
		questStep.setState(QuestStepState.Pending, dayKey);
		quest.updateStepStates(dayKey);
	}
}
