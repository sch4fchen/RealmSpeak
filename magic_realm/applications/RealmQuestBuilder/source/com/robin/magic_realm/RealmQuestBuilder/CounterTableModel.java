/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
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
					return counter.getStartCount();
			}
		}
		return null;
	}
}