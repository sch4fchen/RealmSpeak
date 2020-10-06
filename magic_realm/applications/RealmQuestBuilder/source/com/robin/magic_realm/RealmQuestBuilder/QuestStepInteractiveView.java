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
	
	public void fullFillRequirementsForQuestStep(Quest quest, QuestStep questStep, CharacterWrapper character) {
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
}
