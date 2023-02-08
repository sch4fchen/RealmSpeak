package com.robin.magic_realm.components.quest;

import java.awt.Color;

public enum QuestStepType {
	And, // All required steps MUST be complete before this step can proceed
	Or, // At least one required step MUST be complete before this step can proceed
	;
	public Color getColor() {
		switch(this) {
			case And:
				return Color.black;
			case Or:
				return Color.blue;
		}
		throw new IllegalStateException();
	}
}