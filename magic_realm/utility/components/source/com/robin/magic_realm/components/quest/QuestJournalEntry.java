package com.robin.magic_realm.components.quest;

public class QuestJournalEntry {
	private QuestStepState entryType;
	private String text;
	public QuestJournalEntry(QuestStepState entryType,String text) {
		this.entryType = entryType;
		this.text = text;
	}
	public QuestStepState getEntryType() {
		return entryType;
	}
	public String getText() {
		return text;
	}
}