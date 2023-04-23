package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardNote extends QuestReward {
	public static final String EVENT = "_event";
	public static final String NOTE = "_note";
	
	public QuestRewardNote(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		character.addNote(getParentStep().getGameObject(),getEvent(),getNote());
	}
	
	public String getDescription() {
		return "Add note for the event "+getEvent()+".";
	}

	public RewardType getRewardType() {
		return RewardType.Note;
	}
	
	public String getEvent() {
		return getString(EVENT);
	}
	
	public String getNote() {
		return getString(NOTE);
	}
}