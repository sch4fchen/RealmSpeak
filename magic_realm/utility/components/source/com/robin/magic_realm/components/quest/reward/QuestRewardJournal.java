package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestStepState;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardJournal extends QuestReward {
	public static final String JOURNAL_KEY = "_key";
	public static final String ENTRY_TYPE = "_et";
	public static final String TEXT = "_txt";
	
	public QuestRewardJournal(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		Quest quest = getParentQuest();
		quest.addJournalEntry(getJournalKey(),getEntryType(),getText());
	}
	
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getJournalKey());
		sb.append("(");
		sb.append(getEntryType().toString());
		sb.append("): ");
		sb.append(getText());
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.Journal;
	}
	
	public QuestStepState getEntryType() {
		return QuestStepState.valueOf(getString(ENTRY_TYPE));
	}
	
	public String getJournalKey() {
		return getString(JOURNAL_KEY);
	}
	
	public String getText() {
		return getString(TEXT);
	}
}