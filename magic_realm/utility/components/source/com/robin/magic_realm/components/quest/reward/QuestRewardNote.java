package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestStepState;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardNote extends QuestReward {
	public static final String SOURCE = "_source";
	public static final String EVENT = "_event";
	public static final String NOTE = "_note";
	public static final String DELTE_OLD = "_deleteOld";
	
	public QuestRewardNote(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		character.addNote(getSource(),getEvent(),getNote(),deleteOld());
	}
	
	public String getDescription() {
		return "Add note from "+getSource()+" for the event "+getEvent()+".";
	}

	public RewardType getRewardType() {
		return RewardType.Note;
	}
	
	public String getSource() {
		return getString(SOURCE);
	}

	public String getEvent() {
		return getString(EVENT);
	}
	
	public String getNote() {
		return getString(NOTE);
	}
	
	public boolean deleteOld() {
		return getBoolean(DELTE_OLD);
	}
}