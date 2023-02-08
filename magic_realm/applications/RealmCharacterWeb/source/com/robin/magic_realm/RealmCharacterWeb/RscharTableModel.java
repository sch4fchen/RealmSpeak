package com.robin.magic_realm.RealmCharacterWeb;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class RscharTableModel extends AbstractTableModel {
	
	private static final String[] COLUMN_NAME = {
		"Status",
		"File",
		"Creator",
		"Folder",
	};
	
	private ArrayList<RscharLayout> layoutRecords;
	
	public RscharTableModel() {
		layoutRecords = null;
	}
	public void setRecords(ArrayList<RscharLayout> layoutRecords) {
		this.layoutRecords = layoutRecords;
		fireTableDataChanged();
	}
	
	public Class getColumnClass() {
		return String.class;
	}

	public int getColumnCount() {
		return COLUMN_NAME.length;
	}
	
	public String getColumnName(int index) {
		return COLUMN_NAME[index];
	}

	public int getRowCount() {
		return layoutRecords==null?0:layoutRecords.size();
	}

	public Object getValueAt(int row,int col) {
		if (row<layoutRecords.size()) {
			RscharLayout rec = layoutRecords.get(row);
			switch(col) {
				case 0:
					return rec.getStatus();
				case 1:
					return rec.getFileName();
				case 2:
					return rec.getCharacter().getGameObject().getThisAttribute("creator");
				case 3:
					return rec.getWebFolder();
			}
		}
		return null;
	}
	public void formatColumns(JTable table) {
		table.getColumnModel().getColumn(0).setMaxWidth(50);
	}
}