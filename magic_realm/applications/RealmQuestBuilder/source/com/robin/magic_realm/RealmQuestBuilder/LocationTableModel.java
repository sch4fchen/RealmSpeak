package com.robin.magic_realm.RealmQuestBuilder;

import javax.swing.table.AbstractTableModel;

import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestLocation;

public class LocationTableModel extends AbstractTableModel {
	private static String[] LocationHeader = { "Name", "List", };
	
	Quest quest;
	
	public LocationTableModel(Quest quest) {
		this.quest = quest;
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
		fireTableDataChanged();
	}

	public int getColumnCount() {
		return LocationHeader.length;
	}

	public String getColumnName(int index) {
		return LocationHeader[index];
	}

	public int getRowCount() {
		return quest.getLocations().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < getRowCount()) {
			QuestLocation loc = quest.getLocations().get(rowIndex);
			switch (columnIndex) {
				case 0:
					return loc.getName();
				case 1:
					return loc.getDescription();
			}
		}
		return null;
	}
}