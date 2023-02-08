package com.robin.magic_realm.RealmQuestBuilder;

import javax.swing.table.AbstractTableModel;

import com.robin.magic_realm.components.quest.Quest;

public class QuestRulesTableModel extends AbstractTableModel {
	private static String[] SpecialRulesHeader = { "Rule", "Description", };

	Quest quest;

	public QuestRulesTableModel(Quest quest) {
		this.quest = quest;
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
		fireTableDataChanged();
	}

	public int getColumnCount() {
		return SpecialRulesHeader.length;
	}

	public String getColumnName(int index) {
		return SpecialRulesHeader[index];
	}

	public int getRowCount() {
		return quest == null ? 0 : quest.getQuestRules().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < getRowCount()) {
			//QuestRule rule = quest.getQuestRules().get(rowIndex);
			// switch (columnIndex) {
			// case 0:
			// return loc.getName();
			// case 1:
			// return loc.getLocationType().toString();
			// case 2:
			// ArrayList list = loc.getChoiceAddresses();
			// return list == null ? "" :
			// StringUtilities.collectionToString(list, ",");
			// }
		}
		return null;
	}
}