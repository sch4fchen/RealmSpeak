package com.robin.magic_realm.components.quest;

public enum TargetValueType {
	Game,		// Gains during the game (If multiple quest cards, then this increments for each completed quest of the same type!)
	Quest,		// Only gains during the quest (once activated)
	Step,		// Only gains during this step
	Day,		// Only gains during current day
	;
}