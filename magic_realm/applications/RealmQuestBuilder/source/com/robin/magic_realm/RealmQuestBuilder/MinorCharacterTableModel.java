package com.robin.magic_realm.RealmQuestBuilder;

import javax.swing.table.AbstractTableModel;

import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestMinorCharacter;

public class MinorCharacterTableModel extends AbstractTableModel {
	private static String[] MinorCharacterHeader = { "Name", "Description", };

	private Quest quest;
	
	public MinorCharacterTableModel(Quest quest) {
		this.quest = quest;
	}
	
	public void setQuest(Quest quest) {
		this.quest = quest;
		fireTableDataChanged();
	}

	public int getColumnCount() {
		return MinorCharacterHeader.length;
	}

	public String getColumnName(int index) {
		return MinorCharacterHeader[index];
	}

	public int getRowCount() {
		return quest.getMinorCharacters().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < getRowCount()) {
			QuestMinorCharacter mc = quest.getMinorCharacters().get(rowIndex);
			switch (columnIndex) {
				case 0:
					return mc.getName();
				case 1:
					return mc.getDescription();
			}
		}
		return null;
	}
}