package com.robin.magic_realm.components.quest;

public enum LocationType {
	Any,
	Lock,
	QuestChoice,
	StepChoice,
	QuestRandom,
	StepRandom
	;
	
	public String getDescriptionPrefix() {
		switch(this) {
			case Any:
				return "Any";
			case Lock:
				return "The first";
			case QuestChoice:
				return "At quest start, player selected";
			case QuestRandom:
				return "At quest start, randomly chosen";
			case StepChoice:
				return "At start of step, player selected";
			case StepRandom:
				return "At start of step, randomly chosen";
		}
		return "ERROR - No description!!";
	}
	
	public String getDescription() {
		switch(this) {
			case Any:
				return "Any location in the list is valid at any time during the quest.";
			case Lock:
				return "The first location in the list that a requirement is completed for becomes locked.";
			case QuestChoice:
				return "Player must pick a location from the list at the start of the quest.";
			case QuestRandom:
				return "A location is chosen at random from the list at the start of the quest.";
			case StepChoice:
				return "Player must pick a location from the list at the start of the first step that references it.";
			case StepRandom:
				return "A location is chosen at random from the list at the start of the first step that references it.";
		}
		return "ERROR - No description!!";
	}	
}