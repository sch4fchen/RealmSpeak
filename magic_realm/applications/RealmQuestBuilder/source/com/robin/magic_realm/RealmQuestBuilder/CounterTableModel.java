package com.robin.magic_realm.RealmQuestBuilder;

import javax.swing.table.AbstractTableModel;

import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestCounter;

public class CounterTableModel extends AbstractTableModel {
	private static String[] CounterHeader = { "Name", "Initial count", };
	
	Quest quest;
	
	public CounterTableModel(Quest quest) {
		this.quest = quest;
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
		fireTableDataChanged();
	}

	public int getColumnCount() {
		return CounterHeader.length;
	}

	public String getColumnName(int index) {
		return CounterHeader[index];
	}

	public int getRowCount() {
		return quest.getCounters().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < getRowCount()) {
			QuestCounter counter = quest.getCounters().get(rowIndex);
			switch (columnIndex) {
				case 0:
					return counter.getName();
				case 1:
					return counter.getCount();
			}
		}
		return null;
	}
}